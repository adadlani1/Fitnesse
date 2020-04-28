package and.coursework.fitnesse;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class AddActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private static final double DECIMAL_PLACES_LOCATION = 100000.0;
    private static final int PERMISSION_ID = 44;

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
    private Calendar calendar;
    private FusedLocationProviderClient mFusedLocationClient;


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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        showCoordinates(false);

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveActivityInformation();
            }
        });

    }

    private void saveInformationToFirebase() {
        progressBar.setVisibility(View.VISIBLE);
        String activityChosen = activities.getSelectedItem().toString();
        String minutesExercised = minutes.getText().toString();
        String description = descriptionText.getText().toString();

        String date = getDate();

        activity.setActivity(activityChosen);
        activity.setMinutes(minutesExercised);
        activity.setDescription(description);
        activity.setLatitude(latitudeStr);
        activity.setLongitude(longitudeStr);
        activity.setDateAdded(date);

        user.setActivities(activityChosen);
        mDatabase.push().setValue(activity);

        Toast.makeText(AddActivity.this, "Saved", Toast.LENGTH_SHORT).show();
    }

    private String getDate() {
        calendar = Calendar.getInstance();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(calendar.getTime());
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
        saveActivityInformation();
    }

    private void saveActivityInformation() {
        progressBar.setVisibility(View.VISIBLE);
        saveInformationToFirebase();
        startActivity(new Intent(getApplicationContext(), PerformedActivity.class));
        overridePendingTransition(100, R.anim.fade_in);
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

    private void showCoordinates(Boolean locAvailable) {
        TextView locationView = findViewById(R.id.locationTextView);
        String locationText;

        if (locAvailable) {
            locationText = "Current Location: " + latitudeStr + ", " + longitudeStr;
        } else {
            locationText = "Current Location Not Available";
            latitudeStr = "Not Available";
            longitudeStr = "Not Available";

        }
        locationView.setText(locationText);

    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    double roundLongitude = Math.round(location.getLongitude() * DECIMAL_PLACES_LOCATION) / DECIMAL_PLACES_LOCATION;
                                    double roundLatitude = Math.round(location.getLatitude() * DECIMAL_PLACES_LOCATION) / DECIMAL_PLACES_LOCATION;
                                    latitudeStr = String.valueOf(roundLatitude);
                                    longitudeStr = String.valueOf(roundLongitude);
                                    showCoordinates(true);
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            double roundLongitude = Math.round(mLastLocation.getLongitude() * DECIMAL_PLACES_LOCATION) / DECIMAL_PLACES_LOCATION;
            double roundLatitude = Math.round(mLastLocation.getLatitude() * DECIMAL_PLACES_LOCATION) / DECIMAL_PLACES_LOCATION;
            latitudeStr = String.valueOf(roundLatitude);
            longitudeStr = String.valueOf(roundLongitude);
            showCoordinates(true);
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else
            showCoordinates(false);
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    }


}
