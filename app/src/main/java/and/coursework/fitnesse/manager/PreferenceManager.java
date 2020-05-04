package and.coursework.fitnesse.manager;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    Context context;

    public PreferenceManager(Context context) {
        this.context = context;
    }

    private void saveNotificationPreference(boolean notificationsPreference){
        SharedPreferences sharedPreferences = context.getSharedPreferences("areNotificationsEnabled", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Notifications", notificationsPreference);
        editor.apply();
    }

}
