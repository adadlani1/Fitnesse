package and.coursework.fitnesse;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;

import com.google.android.gms.location.LocationServices;

import java.util.Objects;

public class AddActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private final int swipeThreshold = 100;
    private final int swipeVelocityThreshold = 100;

    GestureDetector gestureDetector;

    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gestureDetector = new GestureDetector(this, this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Activity");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(AddActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);

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
     *  Gesture Detector
     * */

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d("TAG", "onDown: called");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d("TAG", "onShowPress: called");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d("Anmol", "onSingleTapUp: called");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d("Anmol", "onScroll: called");
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d("Anmol", "onLongPress: called");

    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        Log.d("Anmol", "onFling: called");

        boolean result = false;
        float diffY = moveEvent.getY() - downEvent.getY();
        float diffX = moveEvent.getX() - downEvent.getX();

        if (Math.abs(diffX) > Math.abs(diffY)) {
            // right or left swipe
            if (Math.abs(diffX) > swipeThreshold && Math.abs(velocityX) > swipeVelocityThreshold) {
                if (diffX > 0)
                    onSwipeRight();
                else
                    onSwipeLeft();
                result = true;
            }
        } else {
            // up or down swipe
            if (Math.abs(diffY) > swipeThreshold && Math.abs(velocityY) > swipeVelocityThreshold) {
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
        Toast.makeText(this, "Swipe Up", Toast.LENGTH_LONG).show();
    }

    private void onSwipeLeft() {
        Toast.makeText(this, "Swipe Left", Toast.LENGTH_LONG).show();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getMyLocation();


        }
    }

    private void getMyLocation() {

    }
}
