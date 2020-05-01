package and.coursework.fitnesse;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import java.time.LocalDate;
import java.util.ArrayList;
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

    private DatabaseReference mDatabase;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView monthTextView;

    private List<Activity> activityList = new ArrayList<>();
    private String currentMonthSelected;
    private String currentMonthSelectedName;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        assert mUser != null;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid()).child("Activities");

        recyclerView = findViewById(R.id.recyclerViewMain);
        progressBar = findViewById(R.id.progressBarMainPage);
        monthTextView = findViewById(R.id.monthTextView);
        ImageView previousMonth = findViewById(R.id.LastMonthImage);
        ImageView nextMonth = findViewById(R.id.nextMonthImage);
        LineChartView chartView = findViewById(R.id.lineChart);

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
            monthInt -= 1;
            updateMonth(monthInt);
        });

        nextMonth.setOnClickListener(v -> {
            int monthInt = Integer.parseInt(currentMonthSelected);
            monthInt += 1;
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

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    activityList.clear();
                    showNoResults();
                }
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
        return new DateFormatSymbols().getMonths()[month - 1];
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

        List<String> xAxisData = getXAxisData();
        List<Integer> yAxisData = getYAxisData(xAxisData);


        List<lecho.lib.hellocharts.model.PointValue> yAxisDayOfMonth = new ArrayList<>();
        List<AxisValue> xAxisMinuteValues = new ArrayList<>();


        Line minutesLine = new Line(yAxisDayOfMonth).setColor(Color.parseColor("#100db4"));

        for (int i = 0; i < xAxisData.size(); i++) {
            xAxisMinuteValues.add(i, new AxisValue(i).setLabel(xAxisData.get(i)));
        }

        int day = 0;
        for (int Minutes: yAxisData) {
            yAxisDayOfMonth.add(new PointValue(day, Minutes));
            day++;
        }

        List<Line> lineArrayList = new ArrayList<>();
        lineArrayList.add(minutesLine);

        LineChartData lineChartData = new LineChartData();
        lineChartData.setLines(lineArrayList);

        initialiseChart(xAxisMinuteValues, lineChartData, lineChartView);

    }

    private List<Integer> getYAxisData(List<String> axisData) {
        List<Integer> data = new ArrayList<>();

        for (String day : axisData) {
            int minutes = 0;
            for (Activity activity : activityList) {
                if (activity.getDayAdded().equals(day)) {
                    minutes += Integer.parseInt(activity.getMinutes());
                }
            }
            data.add(minutes);
        }

        return data;
    }

    private List<String> getXAxisData() {
        LocalDate date = LocalDate.of(2020, Integer.parseInt(currentMonthSelected), 1);
        int monthSize = date.lengthOfMonth();

        List<String> days = new ArrayList<>();

        for (int i = 1; i <= monthSize; i++) {
            days.add(String.valueOf(i));
        }
        return days;
    }

    private void initialiseAxis(Axis axis, String nameOfAxis){
        axis.setTextSize(16);
        axis.setName(nameOfAxis);
        axis.setTextColor(Color.parseColor("#08AFFF"));
    }
    private void initialiseChart(List<AxisValue> xAxisMinuteValues, LineChartData lineChartData, LineChartView lineChartView) {
        Axis xAxis = new Axis();
        xAxis.setValues(xAxisMinuteValues);
        initialiseAxis(xAxis, "Day of Month");
        lineChartData.setAxisXBottom(xAxis);

        Axis yAxis = new Axis();
        initialiseAxis(yAxis, "Activity (mins)");
        lineChartData.setAxisYLeft(yAxis);

        lineChartView.setLineChartData(lineChartData);
        Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
        lineChartView.setMaximumViewport(viewport);
        lineChartView.setCurrentViewport(viewport);

        lineChartView.setVisibility(View.VISIBLE);
    }

    private void showNoResults() {
        Activity noActivities = new Activity();
        noActivities.setActivity("No Activity In This Month");
        activityList.add(noActivities);
        ActivityAdaptor adaptor = new ActivityAdaptor(getApplicationContext(), activityList);
        recyclerView.setAdapter(adaptor);
        LineChartView chartView = findViewById(R.id.lineChart);
        chartView.setVisibility(View.INVISIBLE);
    }
}
