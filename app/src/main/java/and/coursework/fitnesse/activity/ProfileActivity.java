package and.coursework.fitnesse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import and.coursework.fitnesse.manager.PreferenceManager;

public class ProfileActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private static final int swipeThreshold = 100;
    private static final int swipeVelocityThreshold = 100;

    GestureDetector gestureDetectorProfile = new GestureDetector(this);

    Button signOut;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressBar progressBar;
    TextView email;
    TextView name;
    CheckBox notificationsCheckBox;
    TimePicker timePicker;

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
                setSavedNotificationTimeToTimePicker();
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

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        Log.d("Anmol", "onFling: called");

        boolean result = false;
        float diffY = moveEvent.getY() - downEvent.getY();
        float diffX = moveEvent.getX() - downEvent.getX();

        if (Math.abs(diffX) > Math.abs(diffY)){
            // right or left swipe
            if (Math.abs(diffX) > swipeThreshold && Math.abs(velocityX) > swipeVelocityThreshold){
                if (diffX > 0)
                    onSwipeRight();
                else
                    onSwipeLeft();
                result = true;
            }
        } else{
            // up or down swipe
            if (Math.abs(diffY) > swipeThreshold && Math.abs(velocityY) > swipeVelocityThreshold){
                if (diffY > 0)
                    onSwipeBottom();
                else
                    onSwipeUp();
                result = true;
            }
        }


        return result;
    }

    private void onSwipeUp() {
        saveChanges();
    }

    private void onSwipeBottom() {
    }

    private void onSwipeLeft() {
    }

    private void onSwipeRight() {
        saveChanges();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetectorProfile.onTouchEvent(event);
        return super.onTouchEvent(event);
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
