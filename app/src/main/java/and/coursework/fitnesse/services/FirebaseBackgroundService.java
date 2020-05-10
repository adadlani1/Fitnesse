package and.coursework.fitnesse.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import and.coursework.fitnesse.activity.AddActivity;
import and.coursework.fitnesse.manager.PreferenceManager;
import and.coursework.fitnesse.objects.Activity;
import and.coursework.fitnesse.receiver.AlertReceiver;

import static and.coursework.fitnesse.utils.AppUtils.getDate;

/*A background service that checks the database for additions to the Activities of the user*/
public class FirebaseBackgroundService extends Service {
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        assert mUser != null;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid()).child("Activities");

        hasNewActivityBeenAddedToday();

    }

    /*Checks the database if new activity has been added*/
    private void hasNewActivityBeenAddedToday() {

        String day = getDate("dd");

        Query queryOrderedByDay = mDatabase.orderByChild("dayAdded").equalTo(day);

        List<Activity> activityList = queryEventListener(queryOrderedByDay);
        List<Activity> todaysActivities = checkForActivityToday(activityList);

        /*If no activities today are present*/
        if (todaysActivities.size() == 0){
            sendNotifications();
        }
    }

    /*Checks all of the activities and finds activities completed today*/
    private List<Activity> checkForActivityToday(List<Activity> activityList) {
        String month = getDate("MM");
        String year = getDate("yyyy");
        List<Activity> todaysActivities = new ArrayList<>();
        for (Activity activity: activityList){
            String savedMonth = activity.getMonthAdded();
            String savedYear = activity.getYearAdded();
            if (savedMonth.equals(month) && savedYear.equals(year)){
                todaysActivities.add(activity);
            }
        }
        return todaysActivities;
    }

    /*Gets results from the database*/
    private List<Activity> queryEventListener(Query query) {
        List<Activity> activityList = new ArrayList<>();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    sendNotifications();
                }
                if (dataSnapshot.exists()){
                    activityList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Activity activity = snapshot.getValue(Activity.class);
                        activityList.add(activity);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return activityList;
    }

    private void sendNotifications() {
        setTimeForNotification();
    }

    /*Time is set for the notification depending on what the user entered in Profile page*/
    private void setTimeForNotification() {
        int hour = new PreferenceManager(this).getNotificationHour();
        int minute = new PreferenceManager(this).getNotificationMinutes();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        broadcastNotification(calendar);
    }


    /*Broadcasts notification and sends it daily*/
    private void broadcastNotification(Calendar calendar) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent,0);

        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
