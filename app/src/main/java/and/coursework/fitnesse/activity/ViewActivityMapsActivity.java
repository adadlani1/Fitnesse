package and.coursework.fitnesse.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

import and.coursework.fitnesse.R;

public class ViewActivityMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mMapView;
    private String activity;
    private String effortLevel;
    private Double longitude;
    private Double latitude;
    private String description;
    private String minutes;
    private String yearAdded;
    private String monthAdded;
    private String dayAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(String.valueOf(R.string.MAPVIEW_BUNDLE_KEY));
        }

        if (checkPermissions()) {

            /*initialises mapView */
            mMapView = findViewById(R.id.user_list_map);
            mMapView.onCreate(mapViewBundle);
            mMapView.getMapAsync(this);

            getIncomingIntent();

        } else {
            requestPermissions();
            Log.d("Anmol", "sup");
        }
        ImageView backArrow = findViewById(R.id.backArrowViewActivity);
        backArrow.setOnClickListener(v -> {
            onBackPressed();
            overridePendingTransition(100, R.anim.fade_in);
        });

    }

    /*Methods required as we implemented OnMapReadyCallback*/
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(String.valueOf(R.string.MAPVIEW_BUNDLE_KEY));
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(String.valueOf(R.string.MAPVIEW_BUNDLE_KEY), mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    /*Sets the latitude and longitude of the activity clicked on and zooms to that location*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Activity Location"));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));
        googleMap.setMyLocationEnabled(true);
    }

    /*Gets incoming intent*/
    private void getIncomingIntent() {
        if (getIntent().hasExtra("activity") && getIntent().hasExtra("effortLevel")) {
            instantiateActivity();
        }
    }

    private void instantiateActivity() {
        activity = getIntent().getStringExtra("activity");
        effortLevel = getIntent().getStringExtra("effortLevel");
        longitude = Double.valueOf((Objects.requireNonNull(getIntent().getStringExtra("longitude"))));
        latitude = Double.valueOf((Objects.requireNonNull(getIntent().getStringExtra("latitude"))));
        description = getIntent().getStringExtra("description");
        minutes = getIntent().getStringExtra("minutes");
        yearAdded = getIntent().getStringExtra("yearAdded");
        monthAdded = getIntent().getStringExtra("monthAdded");
        dayAdded = getIntent().getStringExtra("dayAdded");

        showInformationInActivity();
    }

    /*Shows information from intent to activity*/
    @SuppressLint("SetTextI18n")
    private void showInformationInActivity() {
        TextView activityTextView = findViewById(R.id.activityNameTextView);
        TextView descriptionTextView = findViewById(R.id.showDescriptionTextView);
        TextView minutesTextView = findViewById(R.id.showMinutesTextView);
        TextView dateTextView = findViewById(R.id.showDateTextView);
        RatingBar ratingBar = findViewById(R.id.ratingBar);

        String monthAddedName = MainActivity.getMonthName(Integer.parseInt(monthAdded));

        activityTextView.setText(activity);
        descriptionTextView.setText(description);
        minutesTextView.setText(minutes);
        dateTextView.setText(dayAdded + " " + monthAddedName + " " + yearAdded);
        ratingBar.setNumStars(Integer.parseInt(effortLevel) + 1);
        ratingBar.setRating(Integer.parseInt(effortLevel) + 1);
    }

    /*Checks if the permission has been given*/
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    /*Requests permissions to access Location*/
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                44
        );
    }

}

