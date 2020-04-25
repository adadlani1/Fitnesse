package and.coursework.fitnesse;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class AddActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, LocationListener {
    private static final double DECIMAL_PLACES_LOCATION = 100000.0;

    private final int SWIPE_THRESHOLD = 100;
    private final int SWIPE_VELOCITY_THRESHOLD = 100;

    GestureDetector gestureDetector;

    LocationManager locationManager;

    private String longitudeStr;
    private String latitudeStr;

    private ProgressBar progressBar;

    private EditText descriptionText;
    private EditText minutes;

    private Spinner activities;

    User user;
    Activity activity;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String userUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gestureDetector = new GestureDetector(this, this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Activity");

        progressBar = findViewById(R.id.addActivityProgressBar);
        descriptionText = findViewById(R.id.descriptionEditText);
        minutes = findViewById(R.id.minutesEditText);
        activities = findViewById(R.id.activitiesChooser);

        progressBar.setVisibility(View.INVISIBLE);

        user = new User();
        activity = new Activity();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        assert mUser != null;
        userUID = mUser.getUid();


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID).child("Activities");


        if (ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            return;
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        assert location != null;
        onLocationChanged(location);

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Log.e("ERRORAN", "grteuiogr");
//                saveInformationToFirebase();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(100, R.anim.fade_in);
            }
        });

    }

    private void saveInformationToFirebase() {
        progressBar.setVisibility(View.VISIBLE);
        String activityChosen = activities.getSelectedItem().toString();
        String minutesExercised = minutes.getText().toString();
        String description = descriptionText.getText().toString();

        Log.d("Longitude:", longitudeStr);

        activity.setActivity(activityChosen);
        activity.setMinutes(minutesExercised);
        activity.setDescription(description);
        activity.setLatitude(latitudeStr);
        activity.setLongitude(longitudeStr);

        user.setActivities(activityChosen);
        mDatabase.push().setValue(activity);

        Toast.makeText(AddActivity.this, "Saved", Toast.LENGTH_SHORT).show();
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

    /*
     *  Gesture Detector Methods
     * */

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

        boolean result = false;
        float diffY = moveEvent.getY() - downEvent.getY();
        float diffX = moveEvent.getX() - downEvent.getX();

        if (Math.abs(diffX) > Math.abs(diffY)) {
            // right or left swipe
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0)
                    onSwipeRight();
                else
                    onSwipeLeft();
                result = true;
            }
        } else {
            // up or down swipe
            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0)
                    onSwipeBottom();
                else
                    onSwipeUp();
                result = true;
            }
        }


        return result;
    }

    private void onSwipeBottom() {
    }

    private void onSwipeUp() {

    }

    private void onSwipeLeft() {

    }

    private void onSwipeRight() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(100, R.anim.fade_in);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onLocationChanged(Location location) {
        double longitude = Math.round(location.getLongitude() * DECIMAL_PLACES_LOCATION) / DECIMAL_PLACES_LOCATION;
        double latitude = Math.round(location.getLatitude() * DECIMAL_PLACES_LOCATION) / DECIMAL_PLACES_LOCATION;
        longitudeStr = String.valueOf(longitude);
        latitudeStr = String.valueOf(latitude);
        showCoordinates();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void showCoordinates() {
        TextView locationView = findViewById(R.id.locationTextView);
        String locationText = "Current Location: " + latitudeStr + ", " + longitudeStr;
        locationView.setText(locationText);
    }
}
