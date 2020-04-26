package and.coursework.fitnesse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private final int swipeThreshold = 100;
    private final int swipeVelocityThreshold = 100;

    GestureDetector gestureDetectorProfile = new GestureDetector(this);

    Button signOut;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressBar progressBar;
    TextView email;
    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        email = findViewById(R.id.Email);
        name = findViewById(R.id.Name);

        signOut = findViewById(R.id.signoutButton);
        final Button saveChanges = findViewById(R.id.saveChangesButton);

        CheckBox verified = findViewById(R.id.verifiedBox);

        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        progressBar.setVisibility(View.INVISIBLE);

        email.setText(mUser.getEmail());
        name.setText(mUser.getDisplayName());

        if (mUser.isEmailVerified()) {
            verified.setChecked(true);
        } else
            verified.setChecked(false);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutClicked();
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");


    }

    private void saveChanges() {
        progressBar.setVisibility(View.VISIBLE);
        String newName = getNewName();
        updateProfile(newName);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        overridePendingTransition(100, R.anim.fade_in);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            overridePendingTransition(100, R.anim.fade_in);
            return true;
        }
        return super.onOptionsItemSelected(item);
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

        mUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);

                }
            }
        });
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
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(100 , R.anim.fade_in);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetectorProfile.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
