package and.coursework.fitnesse;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.cardview.widget.CardView;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

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
        LineChartView chartView = findViewById(R.id.lineChart);
        context = getApplicationContext();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar.setVisibility(View.VISIBLE);
        chartView.setVisibility(View.INVISIBLE);

        currentMonthSelected = getCurrentMonth();
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
                        showResultsOnChart();
                    } else{
                        Activity noActivities = new Activity();
                        noActivities.setActivity("No Activity in This Month");
                        activityList.add(noActivities);
                        ActivityAdaptor adaptor = new ActivityAdaptor(getApplicationContext(), activityList);
                        recyclerView.setAdapter(adaptor);

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
        loadActivities();
        updateTextView();
    }

    private void showResultsOnChart() {
        LineChartView lineChartView = findViewById(R.id.lineChart);
        lineChartView.setInteractive(true);

        List<String> axisData = getXAxisData();

        List<Integer> yAxisData = getYAxisData(axisData);


        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();


        Line line = new Line(yAxisValues).setColor(Color.parseColor("#100db4"));

        for (int i = 0; i < axisData.size(); i++) {
            axisValues.add(i, new AxisValue(i).setLabel(axisData.get(i)));
        }

        for (int i = 0; i < yAxisData.size(); i++) {
            yAxisValues.add(new PointValue(i, yAxisData.get(i)));
        }

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setValues(axisValues);
        axis.setTextSize(16);
        axis.setName("Day of Month");
        axis.setTextColor(Color.parseColor("#10e6fc"));
        data.setAxisXBottom(axis);

        Axis yAxis = new Axis();
        yAxis.setName("Activity (Mins)");
        yAxis.setTextColor(Color.parseColor("#10e6fc"));
        yAxis.setTextSize(16);
        data.setAxisYLeft(yAxis);

        lineChartView.setLineChartData(data);
        Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
        viewport.top = Collections.max(yAxisData);
        lineChartView.setMaximumViewport(viewport);
        lineChartView.setCurrentViewport(viewport);

        lineChartView.setVisibility(View.VISIBLE);

    }

    private List<Integer> getYAxisData(List<String> axisData) {
        List<Integer> data = new ArrayList<>();

        for (String day : axisData){
            int minutes = 0;
            for (Activity activity: activityList){
                if (activity.getDayAdded().equals(day)){
                    minutes += Integer.parseInt(activity.getMinutes());
                }
            }
            data.add(minutes);
        }

        return data;
    }

    private List<String> getXAxisData() {
        LocalDate date = LocalDate.of(2020, Integer.parseInt(currentMonthSelected), 01);
        int monthSize = date.lengthOfMonth();

        List<String> days = new ArrayList<>();

        for (int i = 1; i <= monthSize; i++) {
            days.add(String.valueOf(i));
        }
        return days;
    }
}
