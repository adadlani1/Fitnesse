package and.coursework.fitnesse.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PreferenceManager {

    Context context;

    public PreferenceManager(Context context) {
        this.context = context;
    }

    public void saveNotificationPreference(boolean notificationsPreference){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Notifications", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("areNotificationsEnabled", notificationsPreference);
        editor.apply();
    }

    public boolean areNotificationsEnabled(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Notifications", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("areNotificationsEnabled", false);
    }

}
