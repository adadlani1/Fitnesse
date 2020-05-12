package and.coursework.fitnesse.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import and.coursework.fitnesse.R;
import and.coursework.fitnesse.listeners.OnSwipeTouchListener;
import and.coursework.fitnesse.objects.Activity;
import and.coursework.fitnesse.objects.User;

import static and.coursework.fitnesse.utils.AppUtils.getDate;

public class AddActivity extends AppCompatActivity {
    /*Constants*/
    private static final double DECIMAL_PLACES_LOCATION = 100000.0;
    private static final int PERMISSION_ID = 44;
    /*String variable for location*/
    private String longitudeStr;
    private String latitudeStr;
    private FusedLocationProviderClient mFusedLocationClient;

    /*Views*/
    private ProgressBar progressBar;
    private EditText descriptionText;
    private EditText minutes;
    private Spinner activities;
    private SeekBar effortLevel;
    private ImageView saveButton;
    private ImageView backButton;

    /*Variable for objects*/
    private User user;
    private Activity activity;

    /*Accessing Firebase Variables*/
    private DatabaseReference mDatabase;

    private String minutesExercised;
    private String description;
    private String activityChosen;
    private int effortLevelValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        /*Saving the views to variables*/
        initialiseViews();

        /*Initialising objects*/
        user = new User();
        activity = new Activity();

        /*Getting Firebase Information*/
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        assert mUser != null;
        String userUID = mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID).child("Activities");

        /*Getting Location information*/
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        /*When Save Button is Clicked*/
        saveButton.setOnClickListener(v -> validation());

        backButton.setOnClickListener(v -> goBackToMainPage());

        View gestureView = findViewById(R.id.gestureViewAdd);
        gestureView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            /*Swipe up means save information. Validation method called*/
            public void onSwipeTop() {
                validation();
            }

            /*Swipe Right means go back to previous page*/
            public void onSwipeRight() {
                goBackToMainPage();
            }
        });

    }

    private void initialiseViews() {
        progressBar = findViewById(R.id.addActivityProgressBar);
        descriptionText = findViewById(R.id.descriptionEditText);
        minutes = findViewById(R.id.minutesEditText);
        activities = findViewById(R.id.activitiesChooser);
        effortLevel = findViewById(R.id.effortLevelSlider);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backArrow);

        progressBar.setVisibility(View.INVISIBLE);
    }

    /*Method checks if all of the required boxes are filled in*/
    private void validation() {
        minutesExercised = minutes.getText().toString();
        description = descriptionText.getText().toString();
        activityChosen = activities.getSelectedItem().toString();
        effortLevelValue = effortLevel.getProgress();

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
        activity.setEffortLevel(effortLevelValue);

        user.setActivities(activityChosen);
        mDatabase.push().setValue(activity);

        /*Message shown to user that */
        Toast.makeText(AddActivity.this, this.getResources().getString(R.string.SAVED_ACTIVITY_MESSAGE), Toast.LENGTH_SHORT).show();
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

    /*Saves information and goes back to parent page*/
    private void saveActivityInformation() {
        progressBar.setVisibility(View.VISIBLE);
        saveInformationToFirebase();
        startActivity(new Intent(getApplicationContext(), PerformedActivity.class));
        overridePendingTransition(100, R.anim.fade_in);
    }

    /*Shows coordinates onto the page*/
    private void showCoordinates(Boolean locAvailable) {
        TextView locationView = findViewById(R.id.locationTextView);
        String locationText;

        if (locAvailable) {
            locationText = "Current Location: " + latitudeStr + ", " + longitudeStr;
        } else {
            locationText = this.getResources().getString(R.string.CURRENT_NOT_AVAILABLE);
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
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        task -> {
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
                );
            } else {
                /*Starts Location settings if location disabled*/
                Toast.makeText(this, R.string.TURN_ON_LOCATION_MESSAGE, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    /*Gets new Location Data*/
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

    /*Checks if required fields are filled in*/
    private void checkIfFieldsFilledIn() {
        TextView blankFields = findViewById(R.id.blankFieldsErrorTextView);
        blankFields.setVisibility(View.VISIBLE);
        if (minutesExercised != null && minutesExercised.equals("")) {
            minutes.setError(this.getResources().getString(R.string.ENTER_NUMBER_OF_MINS_MESSAGE));
        }
        if (description != null && description.equals(""))
            descriptionText.setError(this.getResources().getString(R.string.ENTER_DESCRIPTION_MESSAGE));
        if (activityChosen.equals("Select"))
            activities.setBackgroundColor(Color.RED);
    }

    /*Starts the main Activity*/
    private void goBackToMainPage() {
        onBackPressed();
        overridePendingTransition(100, R.anim.fade_in);
    }
}
