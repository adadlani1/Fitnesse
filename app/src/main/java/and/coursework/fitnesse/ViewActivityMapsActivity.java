package and.coursework.fitnesse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

public class ViewActivityMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        Bundle mapViewBundle = null;
        if (savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView = findViewById(R.id.user_list_map);
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);

        getIncomingIntent();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Activity Location"));
        googleMap.setMyLocationEnabled(true);
    }

    private void getIncomingIntent(){
        if (getIntent().hasExtra("activity")){
            instantiateActivity();
        }
    }

    private void instantiateActivity() {
         activity = ( getIntent().getStringExtra("activity"));
         effortLevel = (getIntent().getStringExtra("effortLevel"));
        longitude = Double.valueOf((Objects.requireNonNull(getIntent().getStringExtra("longitude"))));
        latitude = Double.valueOf((Objects.requireNonNull(getIntent().getStringExtra("latitude"))));
         description = ( getIntent().getStringExtra("description"));
        minutes = ( getIntent().getStringExtra("minutes"));
        yearAdded = ( getIntent().getStringExtra("year"));
        monthAdded = ( getIntent().getStringExtra("month"));
        dayAdded = ( getIntent().getStringExtra("day"));
    }
}

