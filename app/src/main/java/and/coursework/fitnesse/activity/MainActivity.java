package and.coursework.fitnesse.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import and.coursework.fitnesse.R;
import and.coursework.fitnesse.adaptor.ActivityAdaptor;
import and.coursework.fitnesse.manager.PreferenceManager;
import and.coursework.fitnesse.objects.Activity;
import and.coursework.fitnesse.services.FirebaseBackgroundService;
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
    private ImageView nextMonth;

    private List<Activity> activityList = new ArrayList<>();
    private String currentMonth;
    private String currentMonthSelected;
    private String currentYear;
    private String currentYearSelected;
    private String currentMonthSelectedName;

    private FirebaseUser mUser;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assert mUser != null;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid()).child("Activities");

        recyclerView = findViewById(R.id.recyclerViewMain);
        progressBar = findViewById(R.id.progressBarMainPage);
        monthTextView = findViewById(R.id.monthTextView);
        nextMonth = findViewById(R.id.nextMonthImage);
        ImageView previousMonth = findViewById(R.id.LastMonthImage);
        ImageView account = findViewById(R.id.accountImageView);
        ImageView addActivity = findViewById(R.id.addActivityImageView);
        ImageView viewActivities = findViewById(R.id.listOfAllActivities);
        LineChartView chartView = findViewById(R.id.lineChart);
        TextView welcomeMsg = findViewById(R.id.welcome);

        welcomeMsg.setText(mUser.getDisplayName());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar.setVisibility(View.VISIBLE);
        chartView.setVisibility(View.INVISIBLE);

        currentYear = getCurrent("yyyy");
        currentYearSelected = currentYear;

        currentMonth = getCurrent("MM");
        currentMonthSelected = currentMonth;
        currentMonthSelectedName = getMonthName(Integer.parseInt(getCurrent("MM")));
        updateTextView();
        loadActivities();

        showVerfied();

        checkIfCurrentTimeIsShownToUser();

        if (new PreferenceManager(this).areNotificationsEnabled()){
            Intent intent = new Intent(MainActivity.this, FirebaseBackgroundService.class);
            startService(intent);
        }


        viewActivities.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), PerformedActivity.class)));

        addActivity.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AddActivity.class)));

        account.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ProfileActivity.class)));

        previousMonth.setOnClickListener(v -> {
            int monthInt = Integer.parseInt(currentMonthSelected);
            int yearInt = Integer.parseInt(currentYearSelected);
            monthInt -= 1;
            if (monthInt > 0) {
                updateMonth(monthInt);
            } else {
                monthInt = 12;
                currentYearSelected = String.valueOf(yearInt - 1);
                updateMonth(monthInt);
            }
            checkIfCurrentTimeIsShownToUser();
        });

        nextMonth.setOnClickListener(v -> {
            int monthInt = Integer.parseInt(currentMonthSelected);
            int yearInt = Integer.parseInt(currentYearSelected);
            monthInt += 1;
            if ( monthInt < 13) {
                updateMonth(monthInt);
            } else if (monthInt == 13){
                currentYearSelected = String.valueOf(yearInt + 1);
                monthInt = 1;
                updateMonth(monthInt);
            }
            checkIfCurrentTimeIsShownToUser();
        });

    }

    private void showVerfied() {
        ImageView verified = findViewById(R.id.verifiedImage);
        if (mUser.isEmailVerified()) {
            verified.setVisibility(View.VISIBLE);
        } else
            verified.setVisibility(View.INVISIBLE);
    }

    private void checkIfCurrentTimeIsShownToUser() {
        if (currentMonthSelected.equals(currentMonth) && currentYearSelected.equals(currentYear)){
            nextMonth.setVisibility(View.INVISIBLE);
        } else
            nextMonth.setVisibility(View.VISIBLE);

    }

    /*Gets Current Time */

    private String getCurrent(String time) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(time);
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
        monthTextView.setText(currentMonthSelectedName + " " + currentYearSelected);
    }

    public String getMonthName(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

    private void updateMonth(int monthInt) {
        currentMonthSelectedName = getMonthName(monthInt);
        if (monthInt > 9){
            currentMonthSelected = String.valueOf(monthInt);
        } else
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
                if (Integer.parseInt(activity.getDayAdded()) == Integer.parseInt(day)) {
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
