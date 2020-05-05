package and.coursework.fitnesse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import and.coursework.fitnesse.R;
import and.coursework.fitnesse.adaptor.CategoryAdaptor;
import and.coursework.fitnesse.objects.Activity;
import and.coursework.fitnesse.objects.ActivityCategory;

public class PerformedActivity extends AppCompatActivity implements GestureDetector.OnGestureListener{

    private static final float SWIPE_THRESHOLD = 100;
    private static final float SWIPE_VELOCITY_THRESHOLD = 100;

    GestureDetector gestureDetectorProfile = new GestureDetector(this);
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    RecyclerView recyclerView;

    List<Activity> activityList = new ArrayList<>();

    Context context;
    TextView noActivitiesTextView;
    RelativeLayout relativeLayout;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performed);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assert mUser != null;
        String userUid = mUser.getUid();

        progressBar = findViewById(R.id.allActivitiesProgressBar);
        noActivitiesTextView = findViewById(R.id.noActivities);
        ImageView backArrow = findViewById(R.id.backArrowAllActivities);
        ImageView addActivity = findViewById(R.id.addActivityAllActivities);

        noActivitiesTextView.setVisibility(View.INVISIBLE);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userUid).child("Activities");

        relativeLayout = findViewById(R.id.relativeLayout);
        context = getApplicationContext();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.VISIBLE);
                if (dataSnapshot.exists()){
                    activityList.clear();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Activity activity = snapshot.getValue(Activity.class);
                        activityList.add(activity);
                    }

                    progressBar.setVisibility(View.INVISIBLE);

                    if (activityList.size()!= 0) {
                        List<String> uniqueActivities = findUniqueActivities(activityList);
                        List<ActivityCategory> activityCategoriesList = analyseAndGetOverallCategoryInformation(activityList, uniqueActivities);
                        CategoryAdaptor adapter = new CategoryAdaptor(getApplicationContext(), activityCategoriesList);
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                    activityList.clear();
                    progressBar.setVisibility(View.INVISIBLE);
                    noActivitiesTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        backArrow.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            overridePendingTransition(100, R.anim.fade_in);
        });

        addActivity.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AddActivity.class)));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.performed_activities, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_activity) {
            startActivity(new Intent(this, AddActivity.class));
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            overridePendingTransition(100, R.anim.fade_in);
            return true;
        }
        return super.onOptionsItemSelected(item);
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

        boolean result = false;
        float diffY = moveEvent.getY() - downEvent.getY();
        float diffX = moveEvent.getX() - downEvent.getX();

        if (Math.abs(diffX) > Math.abs(diffY)){
            // right or left swipe
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD){
                if (diffX > 0)
                    onSwipeRight();
                else
                    onSwipeLeft();
                result = true;
            }
        } else{
            // up or down swipe
            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD){
                if (diffY > 0)
                    onSwipeBottom();
                else
                    onSwipeUp();
                result = true;
            }
        }

        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetectorProfile.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void onSwipeUp() {
    }

    private void onSwipeBottom() {
    }

    private void onSwipeLeft() {
    }

    private void onSwipeRight() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(100 , R.anim.fade_in);
    }

    private List<String> findUniqueActivities(List<Activity> activityList) {
        List<String> differentActivities = new ArrayList<>();
        for (Activity activity: activityList){
            if (!differentActivities.contains(activity.getActivity())){
                differentActivities.add(activity.getActivity());
            }
        }
        return differentActivities;
    }

    private List<ActivityCategory> analyseAndGetOverallCategoryInformation(List<Activity> activityList, List<String> uniqueActivities) {
        List<ActivityCategory> uniqueActivityAnalysis = new ArrayList<>();
        for (String activityName : uniqueActivities){
            ActivityCategory activityCategory = new ActivityCategory();
            activityCategory.setName(activityName);
            int frequency = findFrequencyOfActivity(activityList, activityName);
            activityCategory.setFrequency(frequency);
            int averageMinutes = findAverage(activityList, activityName, frequency, "minutes");
            activityCategory.setAverageMinutes(averageMinutes);
            int averageEffortLevel = findAverage(activityList, activityName, frequency, "effort level");
            activityCategory.setAverageEffortLevel(averageEffortLevel);
            uniqueActivityAnalysis.add(activityCategory);
        }

        return uniqueActivityAnalysis;
    }

    private int findAverage(List<Activity> activityList, String activityName, int frequency, String attribute) {
        int total = 0;
        for (Activity activity : activityList){
            String savedActivityNameInDatabase = activity.getActivity();
            if (savedActivityNameInDatabase.equals(activityName)){
                if (attribute.equals("minutes"))
                    total += Integer.parseInt(activity.getMinutes());
                else if (attribute.equals("effort level"))
                    total += activity.getEffortLevel();
            }
        }

        return total/frequency;
    }

    private int findFrequencyOfActivity(List<Activity> activityList, String activityName) {
        int frequencyOfActivity = 0;
        for (Activity activity : activityList){
            String savedActivityNameInDatabase = activity.getActivity();
            if (savedActivityNameInDatabase.equals(activityName))
                frequencyOfActivity+=1;
        }
        return frequencyOfActivity;
    }
}
