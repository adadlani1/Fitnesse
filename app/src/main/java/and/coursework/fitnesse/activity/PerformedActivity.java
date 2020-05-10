package and.coursework.fitnesse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

public class PerformedActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private List<Activity> activityList = new ArrayList<>();

    /*Views*/
    private RecyclerView recyclerView;
    private TextView noActivitiesTextView;
    private ProgressBar progressBar;
    private ImageView backArrow;
    private ImageView addActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performed);

        /*Variables Initialised*/
        initialiseVariables();
        initialiseFirebase();

        getActivitiesFromDatabase();

        backArrow.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            overridePendingTransition(100, R.anim.fade_in);
        });

        addActivity.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AddActivity.class)));

    }

    /*Firebase variables initialised*/
    private void initialiseFirebase() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        assert mUser != null;
        String userUid = mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userUid).child("Activities");
    }

    /*Views initialised*/
    private void initialiseVariables() {
        progressBar = findViewById(R.id.allActivitiesProgressBar);
        noActivitiesTextView = findViewById(R.id.noActivities);
        backArrow = findViewById(R.id.backArrowAllActivities);
        addActivity = findViewById(R.id.addActivityAllActivities);

        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
        Context context = getApplicationContext();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        noActivitiesTextView.setVisibility(View.INVISIBLE);
    }

    /*Finds unique activities saved by user in database*/
    private List<String> findUniqueActivities(List<Activity> activityList) {
        List<String> differentActivities = new ArrayList<>();
        for (Activity activity : activityList) {
            if (!differentActivities.contains(activity.getActivity())) {
                differentActivities.add(activity.getActivity());
            }
        }
        return differentActivities;
    }

    /*Analyses the information provided by the database, returns a list of categories with
     * information about each unique activity*/
    private List<ActivityCategory> analyseAndGetOverallCategoryInformation(List<Activity> activityList, List<String> uniqueActivities) {
        List<ActivityCategory> uniqueActivityAnalysis = new ArrayList<>();
        for (String activityName : uniqueActivities) {
            ActivityCategory activityCategory = new ActivityCategory();
            activityCategory.setName(activityName);

            /*Finds frequency of the activity*/
            int frequency = findFrequencyOfActivity(activityList, activityName);
            activityCategory.setFrequency(frequency);

            /*Finds average minutes exercised for the activity*/
            int averageMinutes = findAverage(activityList, activityName, frequency, "minutes");
            activityCategory.setAverageMinutes(averageMinutes);

            /*Finds average effort level for the exercise*/
            int averageEffortLevel = findAverage(activityList, activityName, frequency, "effort level");
            activityCategory.setAverageEffortLevel(averageEffortLevel);

            /*Adds the activity details to the list*/
            uniqueActivityAnalysis.add(activityCategory);
        }

        return uniqueActivityAnalysis;
    }

    /*Finds average when details passed*/
    private int findAverage(List<Activity> activityList, String activityName, int frequency, String attribute) {
        int total = 0;
        for (Activity activity : activityList) {
            String savedActivityNameInDatabase = activity.getActivity();
            if (savedActivityNameInDatabase.equals(activityName)) {
                if (attribute.equals("minutes"))
                    total += Integer.parseInt(activity.getMinutes());
                else if (attribute.equals("effort level"))
                    total += activity.getEffortLevel();
            }
        }

        return total / frequency;
    }

    /*Finds frequency of the activity name*/
    private int findFrequencyOfActivity(List<Activity> activityList, String activityName) {
        int frequencyOfActivity = 0;
        for (Activity activity : activityList) {
            String savedActivityNameInDatabase = activity.getActivity();
            if (savedActivityNameInDatabase.equals(activityName))
                frequencyOfActivity += 1;
        }
        return frequencyOfActivity;
    }

    /*Gets all of the activities performed by user from the database*/
    private void getActivitiesFromDatabase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.VISIBLE);
                if (dataSnapshot.exists()) {
                    activityList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Activity activity = snapshot.getValue(Activity.class);
                        activityList.add(activity);
                    }

                    progressBar.setVisibility(View.INVISIBLE);

                    /*When there is a list of activities obtained*/
                    if (activityList.size() != 0) {
                        List<String> uniqueActivities = findUniqueActivities(activityList);

                        /*Adaptor called to show the analysis of the performed activities*/
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
    }
}
