package and.coursework.fitnesse.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import and.coursework.fitnesse.R;
import and.coursework.fitnesse.adaptor.ActivityAdaptor;
import and.coursework.fitnesse.listeners.OnSwipeTouchListener;
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

import static and.coursework.fitnesse.utils.AppUtils.getDate;

public class MainActivity extends AppCompatActivity {

    /*Views*/
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView monthTextView;
    private TextView welcomeMsg;
    private ImageView nextMonth;
    private ImageView previousMonth;
    private ImageView account;
    private ImageView addActivity;
    private ImageView viewActivities;
    private ImageView openSpotify;
    private LineChartView chartView;

    /*Variables*/
    private List<Activity> activityList = new ArrayList<>();
    private String currentMonth;
    private String currentMonthSelected;
    private String currentYear;
    private String currentYearSelected;
    private String currentMonthSelectedName;

    /*Firebase Variables*/
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;

    @SuppressLint({"SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Everything is initialised when activity is created*/
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        initialiseFirebase(mAuth);

        initialiseViews();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar.setVisibility(View.VISIBLE);
        chartView.setVisibility(View.INVISIBLE);

        /*Current year and month is saved into a variable to be used as a reference point*/
        currentYear = getDate("yyyy");
        currentYearSelected = currentYear;

        currentMonth = getDate("MM");
        currentMonthSelected = currentMonth;
        currentMonthSelectedName = getMonthName(Integer.parseInt(getDate("MM")));

        /*Month and name are added to the main page*/
        updateTextView();
        loadActivities();

        /*Checks if user has verified email, if they have then logo shown */
        showVerfied();

        /*Checks if the month shown on the graph is equal to current month*/
        checkIfCurrentMonthIsShownToUser();

        /*Firebase checking service is called so notifications can be triggered*/
        if (new PreferenceManager(this).areNotificationsEnabled()) {
            Intent intent = new Intent(MainActivity.this, FirebaseBackgroundService.class);
            startService(intent);
        }

        /*On Click Listeners for each button as they all start different Activities with intents*/
        viewActivities.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), PerformedActivity.class)));

        addActivity.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AddActivity.class)));

        account.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ProfileActivity.class)));

        openSpotify.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("spotify:playlist:37i9dQZF1DWSWA2pLcO5dt"));
            this.startActivity(intent);
            Toast.makeText(getApplicationContext(), "We recommend listening to music while exercising!", Toast.LENGTH_SHORT).show();
        });

        previousMonth.setOnClickListener(v -> previousMonthInfoRequested());

        nextMonth.setOnClickListener(v -> nextMonthInfoRequested());

        /*Gestures on the CardView*/
        View gestureView = findViewById(R.id.gestureViewMain);
        gestureView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeRight() {
                previousMonthInfoRequested();
            }

            public void onSwipeLeft() {
                if (!currentMonthSelected.equals(currentMonth) || !currentYearSelected.equals(currentYear)) {
                    nextMonthInfoRequested();
                }
            }
        });
    }

    /*Confirmation dialog when back button pressed*/
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm exit")
                .setMessage("Are you sure you want to exit the application?")
                .setPositiveButton("YES", (dialog, id) -> this.finishAffinity())
                .setNegativeButton("NO", (dialog, id) -> dialog.cancel())
                .setCancelable(false)
                .show();
    }

    /*Initialises firebase variables and database*/
    private void initialiseFirebase(FirebaseAuth mAuth) {
        mUser = mAuth.getCurrentUser();
        assert mUser != null;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid()).child("Activities");
    }

    /*Initialises Views in the XML document*/
    private void initialiseViews() {
        recyclerView = findViewById(R.id.recyclerViewMain);
        progressBar = findViewById(R.id.progressBarMainPage);
        monthTextView = findViewById(R.id.monthTextView);
        nextMonth = findViewById(R.id.nextMonthImage);
        previousMonth = findViewById(R.id.LastMonthImage);
        account = findViewById(R.id.accountImageView);
        addActivity = findViewById(R.id.addActivityImageView);
        viewActivities = findViewById(R.id.listOfAllActivities);
        openSpotify = findViewById(R.id.music);
        chartView = findViewById(R.id.lineChart);
        welcomeMsg = findViewById(R.id.welcome);
    }

    /*Previous month arrow button pressed*/
    private void previousMonthInfoRequested() {
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
        checkIfCurrentMonthIsShownToUser();
    }

    /*Next month arrow button pressed*/
    private void nextMonthInfoRequested() {
        int monthInt = Integer.parseInt(currentMonthSelected);
        int yearInt = Integer.parseInt(currentYearSelected);
        monthInt += 1;
        if (monthInt < 13) {
            updateMonth(monthInt);
        } else if (monthInt == 13) {
            currentYearSelected = String.valueOf(yearInt + 1);
            monthInt = 1;
            updateMonth(monthInt);
        }
        checkIfCurrentMonthIsShownToUser();
    }

    /*Checks if User has verified email*/
    private void showVerfied() {
        ImageView verified = findViewById(R.id.verifiedImage);
        if (mUser.isEmailVerified()) {
            verified.setVisibility(View.VISIBLE);
        } else
            verified.setVisibility(View.INVISIBLE);
    }

    /*Checks if the month shown on page is current month*/
    private void checkIfCurrentMonthIsShownToUser() {
        if (currentMonthSelected.equals(currentMonth) && currentYearSelected.equals(currentYear)) {
            nextMonth.setVisibility(View.INVISIBLE);
        } else
            nextMonth.setVisibility(View.VISIBLE);

    }

    /*Queries the database and gets the activities for the displayed month*/
    private void loadActivities() {
        Query query = mDatabase.orderByChild("monthAdded").equalTo((currentMonthSelected));

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    activityList.clear();
                    /*if there is no activity in the month this method is called*/
                    showNoResults();
                }
                if (dataSnapshot.exists()) {
                    activityList.clear();
                    /*when results exist*/
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Activity activity = snapshot.getValue(Activity.class);
                        activityList.add(activity);
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    if (activityList.size() != 0) {
                        /*Activities obtained and adaptor called to show results*/
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

    /*Updates month shown and welcome message*/
    @SuppressLint("SetTextI18n")
    private void updateTextView() {
        monthTextView.setText(currentMonthSelectedName + " " + currentYearSelected);
        welcomeMsg.setText(mUser.getDisplayName());
    }

    public static String getMonthName(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

    /*Gets selected month and load activities for the month*/
    private void updateMonth(int monthInt) {
        currentMonthSelectedName = getMonthName(monthInt);
        if (monthInt > 9) {
            currentMonthSelected = String.valueOf(monthInt);
        } else
            currentMonthSelected = "0" + monthInt;
        loadActivities();
        updateTextView();
    }

    /*shows results on the chart*/
    private void showResultsOnChart() {
        LineChartView lineChartView = findViewById(R.id.lineChart);
        lineChartView.setInteractive(true);

        List<String> xAxisData = getXAxisData();
        List<Integer> yAxisData = getYAxisData(xAxisData);

        List<lecho.lib.hellocharts.model.PointValue> yAxisDayOfMonth = new ArrayList<>();
        List<AxisValue> xAxisMinuteValues = new ArrayList<>();


        Line minutesLine = new Line(yAxisDayOfMonth).setColor(Color.parseColor("#100db4"));

        /*Sets the x axis labels*/
        for (int i = 0; i < xAxisData.size(); i++) {
            xAxisMinuteValues.add(i, new AxisValue(i).setLabel(xAxisData.get(i)));
        }

        /*Sets the y axis labels*/
        int day = 0;
        for (int Minutes : yAxisData) {
            yAxisDayOfMonth.add(new PointValue(day, Minutes));
            day++;
        }

        /*Adds line to graph*/
        List<Line> lineArrayList = new ArrayList<>();
        lineArrayList.add(minutesLine);

        LineChartData lineChartData = new LineChartData();
        lineChartData.setLines(lineArrayList);

        initialiseChart(xAxisMinuteValues, lineChartData, lineChartView);

    }

    /*Gets y axis data from list of activities*/
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

    /*Gets x axis data by looking at month and working out length of the month*/
    private List<String> getXAxisData() {
        LocalDate date = LocalDate.of(2020, Integer.parseInt(currentMonthSelected), 1);
        int monthSize = date.lengthOfMonth();

        List<String> days = new ArrayList<>();

        for (int i = 1; i <= monthSize; i++) {
            days.add(String.valueOf(i));
        }
        return days;
    }

    /*initialises axis by setting variable values*/
    private void initialiseAxis(Axis axis, String nameOfAxis) {
        axis.setTextSize(16);
        axis.setName(nameOfAxis);
        axis.setTextColor(Color.parseColor("#08AFFF"));
    }

    /*initialises the chart by showing results to user*/
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

    /*If no activity is present, this is shown to user*/
    private void showNoResults() {
        progressBar.setVisibility(View.INVISIBLE);
        Activity noActivities = new Activity();
        noActivities.setActivity("No Activity In This Month");
        activityList.add(noActivities);
        ActivityAdaptor adaptor = new ActivityAdaptor(getApplicationContext(), activityList);
        recyclerView.setAdapter(adaptor);
        LineChartView chartView = findViewById(R.id.lineChart);
        chartView.setVisibility(View.INVISIBLE);
    }

}
