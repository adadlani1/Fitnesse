package and.coursework.fitnesse.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import and.coursework.fitnesse.R;
import and.coursework.fitnesse.manager.NotificationHelper;

public class NotificationReceiver extends BroadcastReceiver {

    /*Builds the notification and sends it out to the phone*/
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder notificationBuilder = notificationHelper.getChannelNotification(String.valueOf(R.string.NOTIFICATION_TITLE), String.valueOf(R.string.NOTIFICATION_MESSAGE));
        notificationHelper.getManager().notify(1, notificationBuilder.build());
    }
}
