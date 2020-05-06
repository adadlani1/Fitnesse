package and.coursework.fitnesse.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import and.coursework.fitnesse.R;
import and.coursework.fitnesse.listeners.OnSwipeTouchListener;
import and.coursework.fitnesse.manager.PreferenceManager;

public class ProfileActivity extends AppCompatActivity{

    Button signOut;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressBar progressBar;
    TextView email;
    TextView name;
    CheckBox notificationsCheckBox;
    TimePicker timePicker;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        email = findViewById(R.id.Email);
        name = findViewById(R.id.Name);
        signOut = findViewById(R.id.signoutButton);
        progressBar = findViewById(R.id.progressBar);
        notificationsCheckBox = findViewById(R.id.notificationsCheckBox);
        timePicker = findViewById(R.id.timePicker);
        Button saveChangesButton = findViewById(R.id.saveChangesButton);
        ImageView verified = findViewById(R.id.verifiedBox);
        ImageView aboutImageView = findViewById(R.id.about);
        RelativeLayout relativeLayout = findViewById(R.id.profileRelativeLayout);

        timePicker.setIs24HourView(true);
        progressBar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        assert mUser != null;
        email.setText(mUser.getEmail());
        name.setText(mUser.getDisplayName());

        if (mUser.isEmailVerified()) {
            verified.setVisibility(View.VISIBLE);
        } else
            verified.setVisibility(View.GONE);

        if (new PreferenceManager(this).areNotificationsEnabled()){
            notificationsCheckBox.setChecked(true);
            timePicker.setVisibility(View.VISIBLE);
            setSavedNotificationTimeToTimePicker();
        } else{
            notificationsCheckBox.setChecked(false);
            timePicker.setVisibility(View.GONE);
        }

        signOut.setOnClickListener(v -> signOutClicked());

        saveChangesButton.setOnClickListener(v -> saveChanges());

        aboutImageView.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),
                AboutActivity.class)));

        notificationsCheckBox.setOnClickListener(v -> {
            if (notificationsCheckBox.isEnabled()){
                timePicker.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Please set the time you would like to " +
                        "be reminded to complete your daily activity", Toast.LENGTH_SHORT).show();
                setSavedNotificationTimeToTimePicker();
            }
        });

        relativeLayout.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()){
            public void onSwipeTop() {
                saveChanges();
            }
            public void onSwipeRight() {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(100, R.anim.fade_in);
            }
            public void onSwipeLeft() {
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            }
            public void onSwipeBottom() {
                signOutClicked();
            }
        });
    }

    private void saveChanges() {
        progressBar.setVisibility(View.VISIBLE);
        String newName = getNewName();
        updateProfile(newName);
    }

    private String getNewName() {
        return name.getText().toString();
    }

    void signOutClicked() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        overridePendingTransition(100, R.anim.fade_in);
    }

    void updateProfile(String newName) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();

        mUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ProfileActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(100, R.anim.fade_in);

            }
        });

        String userID = mUser.getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Name");
        mDatabase.setValue(newName);

        saveNotificationPreference(notificationsCheckBox.isChecked());
        getSavedNotificationTime();
    }

    private void saveNotificationPreference(boolean enabled) {
        new PreferenceManager(this).saveNotificationPreference(enabled);
    }

    private void getSavedNotificationTime() {
        int hour = timePicker.getHour();
        int minutes = timePicker.getMinute();
        saveNotificationTimePreference(hour, minutes);
    }

    private void saveNotificationTimePreference(int hour, int minutes) {
        new PreferenceManager(this).saveNotificationTimePreference(hour, minutes);
    }

    private void setSavedNotificationTimeToTimePicker() {
        int hour = new PreferenceManager(this).getNotificationHour();
        int mins = new PreferenceManager(this).getNotificationMinutes();

        timePicker.setHour(hour);
        timePicker.setMinute(mins);
    }
}
