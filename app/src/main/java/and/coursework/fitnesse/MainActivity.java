package and.coursework.fitnesse;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;
    private RelativeLayout relativeLayout;
    private Context context;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView monthTextView;
    private ImageView previousMonth;
    private ImageView nextMonth;

    private List<Activity> activityList = new ArrayList<>();
    private String currentMonthSelected;
    private String currentMonthSelectedName;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid()).child("Activities");

        relativeLayout = findViewById(R.id.relativeLayoutMainPage);
        recyclerView = findViewById(R.id.recyclerViewMain);
        progressBar = findViewById(R.id.progressBarMainPage);
        monthTextView = findViewById(R.id.monthTextView);
        previousMonth = findViewById(R.id.LastMonthImage);
        nextMonth = findViewById(R.id.nextMonthImage);

        context = getApplicationContext();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar.setVisibility(View.VISIBLE);

        currentMonthSelected = getCurrentMonth();
        Log.d("Anmol", currentMonthSelected);
        currentMonthSelectedName = getMonth(Integer.parseInt(getCurrentMonth()));
        updateTextView();
        loadActivities();

        previousMonth.setOnClickListener(v -> {
            int monthInt = Integer.parseInt(currentMonthSelected);
            monthInt-=1;
            updateMonth(monthInt);
        });

        nextMonth.setOnClickListener(v -> {
            int monthInt = Integer.parseInt(currentMonthSelected);
            monthInt+=1;
            updateMonth(monthInt);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                return true;

            case R.id.activity:
                startActivity(new Intent(this, PerformedActivity.class));
                return true;

            case R.id.profile:
                startActivity(new Intent(this, ProfileActivity.class));
                return true;

            case R.id.add_friend:
                startActivity(new Intent(this, AddFriendActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /*Gets Current Month */
    private String getCurrentMonth() {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void loadActivities() {
        Query query = mDatabase.orderByChild("monthAdded").equalTo((currentMonthSelected));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    activityList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Activity activity = snapshot.getValue(Activity.class);
                        activityList.add(activity);
                    }

                    progressBar.setVisibility(View.INVISIBLE);

                    if (activityList.size() != 0) {
                        ActivityAdaptor adapter = new ActivityAdaptor(getApplicationContext(), activityList);
                        recyclerView.setAdapter(adapter);
                    } else{
                        Activity noActivities = new Activity();
                        noActivities.setActivity("No Activity in This Month");
                        activityList.add(noActivities);
                        ActivityAdaptor adaptor = new ActivityAdaptor(getApplicationContext(), activityList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @SuppressLint("SetTextI18n")
    private void updateTextView() {
        monthTextView.setText(currentMonthSelectedName + " 2020");
    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month-1];
    }

    private void updateMonth(int monthInt) {
        currentMonthSelectedName = getMonth(monthInt);
        currentMonthSelected = "0" + monthInt;
        Log.d("ANMOL", currentMonthSelected);
        Log.d("ANMOL", currentMonthSelectedName);
        loadActivities();
        updateTextView();
    }
}
