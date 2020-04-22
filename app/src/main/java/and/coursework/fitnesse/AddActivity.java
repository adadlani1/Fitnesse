package and.coursework.fitnesse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.Objects;

public class AddActivity extends AppCompatActivity implements View.OnTouchListener, GestureDetector.OnGestureListener {
    GestureDetector gestureDetector;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
         gestureDetector = new GestureDetector(this, this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Activity");

        imageView = findViewById(R.id.imageView2);

        imageView.setOnTouchListener(this);

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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() == R.id.imageView2){
            //methods for touching image1
            gestureDetector.onTouchEvent(event);
            return true;
        }
        return true;
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
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d("Anmol", "onFling: called");
        return false;
    }
}
