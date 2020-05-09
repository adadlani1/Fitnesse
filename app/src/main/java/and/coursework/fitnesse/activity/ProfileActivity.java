package and.coursework.fitnesse.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

    private FirebaseUser mUser;
    
    private ProgressBar progressBar;
    private TextView email;
    private TextView name;
    private CheckBox notificationsCheckBox;
    private TimePicker timePicker;
    private ImageView verified;
    private ImageView aboutImageView;
    private ImageView backArrow;
    private RelativeLayout relativeLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initialiseViews();

        timePicker.setIs24HourView(true);
        progressBar.setVisibility(View.INVISIBLE);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        assert mUser != null;
        email.setText(mUser.getEmail());
        name.setText(mUser.getDisplayName());

        /*Checks if user is verified and if so, shows icon*/
        if (mUser.isEmailVerified()) {
            verified.setVisibility(View.VISIBLE);
        } else
            verified.setVisibility(View.GONE);

        /*Gets value of notification preference and shows with the check box ticked if
        * notifications enabled */
        showSharedPreferences();

        backArrow.setOnClickListener(v -> saveChanges());

        aboutImageView.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),
                AboutActivity.class)));

        /*When the notifications checkbox is checked, time picker shows*/
        notificationsCheckBox.setOnClickListener(v -> {
            if (notificationsCheckBox.isChecked()){
                timePicker.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Please set the time you would like to " +
                        "be reminded to complete your daily activity", Toast.LENGTH_SHORT).show();
                setSavedNotificationTimeToTimePicker();
            } else
                timePicker.setVisibility(View.INVISIBLE);
        });

        /*Gestures*/
        relativeLayout.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()){
            public void onSwipeRight() {
                saveChanges();
            }
            public void onSwipeLeft() {
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            }
            public void onSwipeBottom() {
                signOutClicked();
            }
        });
    }

    /*Gets and shows notification preferences*/
    private void showSharedPreferences() {
        if (new PreferenceManager(this).areNotificationsEnabled()){
            notificationsCheckBox.setChecked(true);
            timePicker.setVisibility(View.VISIBLE);
            setSavedNotificationTimeToTimePicker();
        } else{
            notificationsCheckBox.setChecked(false);
            timePicker.setVisibility(View.GONE);
        }
    }

    /*Initialises Views*/
    private void initialiseViews() {
        email = findViewById(R.id.Email);
        name = findViewById(R.id.Name);
        progressBar = findViewById(R.id.progressBar);
        notificationsCheckBox = findViewById(R.id.notificationsCheckBox);
        timePicker = findViewById(R.id.timePicker);
        verified = findViewById(R.id.verifiedBox);
        aboutImageView = findViewById(R.id.about);
        backArrow = findViewById(R.id.backArrowProfile);
        relativeLayout = findViewById(R.id.profileRelativeLayout);
    }

    /*Saves changes and updates profile*/
    private void saveChanges() {
        progressBar.setVisibility(View.VISIBLE);
        String newName = getNewName();
        updateProfile(newName);
    }

    /*Gets name from input box */
    private String getNewName() {
        return name.getText().toString();
    }

    /*Signs user out when corresponding button or gesture used*/
    void signOutClicked() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        overridePendingTransition(100, R.anim.fade_in);
    }

    /*Updates profile in firebase and locally on the app*/
    void updateProfile(String newName) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();

        mUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ProfileActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                onBackPressed();
                overridePendingTransition(100, R.anim.fade_in);

            }
        });

        String userID = mUser.getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Name");
        mDatabase.setValue(newName);

        /*Checks notification checkbox*/
        saveNotificationPreference(notificationsCheckBox.isChecked());
        getSavedNotificationTime();
    }

    /*Saves notification preference*/
    private void saveNotificationPreference(boolean enabled) {
        new PreferenceManager(this).saveNotificationPreference(enabled);
    }

    /*Gets time set in time picker*/
    private void getSavedNotificationTime() {
        int hour = timePicker.getHour();
        int minutes = timePicker.getMinute();
        saveNotificationTimePreference(hour, minutes);
    }

    /*Saves the time set in notification time*/
    private void saveNotificationTimePreference(int hour, int minutes) {
        new PreferenceManager(this).saveNotificationTimePreference(hour, minutes);
    }

    /*Sets the time saved in shared preferences to time picker*/
    private void setSavedNotificationTimeToTimePicker() {
        int hour = new PreferenceManager(this).getNotificationHour();
        int mins = new PreferenceManager(this).getNotificationMinutes();

        timePicker.setHour(hour);
        timePicker.setMinute(mins);
    }
}
