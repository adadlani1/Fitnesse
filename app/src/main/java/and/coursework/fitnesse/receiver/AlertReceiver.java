package and.coursework.fitnesse.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import and.coursework.fitnesse.manager.NotificationHelper;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder notificationBuilder = notificationHelper.getChannelNotification("Exercise Time!", "You haven't been active today");
        notificationHelper.getManager().notify(1, notificationBuilder.build());
    }
}
