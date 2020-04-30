package and.coursework.fitnesse;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import java.util.Date;
import java.util.Objects;

public class AddActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    /*Constants*/
    private static final double DECIMAL_PLACES_LOCATION = 100000.0;
    private static final int PERMISSION_ID = 44;
    private final int SWIPE_THRESHOLD = 100;
    private final int SWIPE_VELOCITY_THRESHOLD = 100;

    /*For Gestures*/
    private GestureDetector gestureDetector;

    /*String variable for location*/
    private String longitudeStr;
    private String latitudeStr;
    private FusedLocationProviderClient mFusedLocationClient;

    /*Variables used in XML file*/
    private ProgressBar progressBar;
    private EditText descriptionText;
    private EditText minutes;
    private Spinner activities;

    /*Variable for objects*/
    private User user;
    private Activity activity;

    /*Accessing Firebase Variables*/
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    /*Variables to save entered values*/
    private Calendar calendar;
    private String userUID;
    private String minutesExercised;
    private String description;
    private String activityChosen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Activity");

        /*Saving the display items to variables*/
        progressBar = findViewById(R.id.addActivityProgressBar);
        descriptionText = findViewById(R.id.descriptionEditText);
        minutes = findViewById(R.id.minutesEditText);
        activities = findViewById(R.id.activitiesChooser);
        Button saveButton = findViewById(R.id.saveButton);

        progressBar.setVisibility(View.INVISIBLE);

        /*Initialising objects*/
        user = new User();
        activity = new Activity();
        gestureDetector = new GestureDetector(this, this);

        /*Getting Firebase Information*/
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assert mUser != null;
        userUID = mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID).child("Activities");

        /*Getting Location information*/
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        /*When Save Button is Clicked*/
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });

    }

    /*Method checks if all of the required boxes are filled in*/
    private void validation() {
        minutesExercised = minutes.getText().toString();
        description = descriptionText.getText().toString();
        activityChosen = activities.getSelectedItem().toString();

        if (minutesExercised.equals("") || description.equals("") || activityChosen.equals("Select")) {
            checkIfFieldsFilledIn();
        } else
            saveActivityInformation();
    }

    /*Saves information into activity object and then adds that activity to Database*/
    private void saveInformationToFirebase() {
        progressBar.setVisibility(View.VISIBLE);
        String day = getDate("dd");
        String month = getDate("MM");
        String year = getDate("yyyy");

        activity.setActivity(activityChosen);
        activity.setMinutes(minutesExercised);
        activity.setDescription(description);
        activity.setLatitude(latitudeStr);
        activity.setLongitude(longitudeStr);
        activity.setMonthAdded(month);
        activity.setYearAdded(year);
        activity.setDayAdded(day);

        user.setActivities(activityChosen);
        mDatabase.push().setValue(activity);

        /*Message shown to user that */
        Toast.makeText(AddActivity.this, "Saved", Toast.LENGTH_SHORT).show();
    }

    /*Gets Current date */
    private String getDate(String pattern) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        return dateFormat.format(date);
    }

    /*Adds a custom Action Bar from a different xml file in menu*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_activity, menu);
        return true;
    }

    /*Adds a Back button on Action Bar to go back to parent page*/
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

    /*Calculates which direction the swipe is and decides on which method to call*/
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

    /*Swipe up means save information. Validation method called*/
    private void onSwipeUp() {
        validation();
    }

    private void onSwipeLeft() {

    }

    /*Swipe Right means go back to previous page*/
    private void onSwipeRight() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(100, R.anim.fade_in);
    }

    /*Saves information and goes back to parent page*/
    private void saveActivityInformation() {
        progressBar.setVisibility(View.VISIBLE);
        saveInformationToFirebase();
        startActivity(new Intent(getApplicationContext(), PerformedActivity.class));
        overridePendingTransition(100, R.anim.fade_in);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /*Shows coordinates onto the page*/
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

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    }

    /*Requests if the permission has been enabled or not*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    /*gets the last recorded location by the phone*/
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
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
                                    /*gets location and assigns value to strings*/
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
                /*Starts Location settings if location disabled*/
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    /*Gets new Location Data*/
    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

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

    /*Requests permissions to access Location*/
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    /*Method when there is a location present*/
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

    /*Checks if the permission has been given*/
    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else
            showCoordinates(false);
        return false;
    }

    /*Checks if the location is enabled on the phone*/
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER);
    }

    /*checks if required fields are filled in*/
    private void checkIfFieldsFilledIn() {
        TextView blankFields = findViewById(R.id.blankFieldsErrorTextView);
        blankFields.setVisibility(View.VISIBLE);
        if (minutesExercised.equals(""))
            minutes.setError("Please Enter the Number of Minutes.");
        if (description.equals(""))
            descriptionText.setError("Please Enter More Information About Your Activity.");
        if (activityChosen.equals("Select"))
            activities.setBackgroundColor(Color.RED);
    }


}
