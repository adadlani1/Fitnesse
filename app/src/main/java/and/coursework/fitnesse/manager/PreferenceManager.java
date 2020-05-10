package and.coursework.fitnesse.manager;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private Context context;

    public PreferenceManager(Context context) {
        this.context = context;
    }

    /*Method that saves the boolean value when the Notification checkbox is clicked*/
    public void saveNotificationPreference(boolean notificationsPreference) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Notifications", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("areNotificationsEnabled", notificationsPreference);
        editor.apply();
    }

    /*Method that returns a boolean depending on if notifications are enabled*/
    public boolean areNotificationsEnabled() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Notifications", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("areNotificationsEnabled", false);
    }

    /*Saves the notification time when saved in the profile*/
    public void saveNotificationTimePreference(int hour, int minutes) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Notifications", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("notificationHour", hour);
        editor.putInt("notificationMinutes", minutes);
        editor.apply();
    }

    /*Returns the hour from Shared Preferences*/
    public int getNotificationHour() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Notifications", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("notificationHour", 0);
    }

    /*Returns the Minutes from Shared Preferences*/
    public int getNotificationMinutes() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Notifications", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("notificationMinutes", 0);
    }
}
