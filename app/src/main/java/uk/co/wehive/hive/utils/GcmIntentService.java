package uk.co.wehive.hive.utils;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import uk.co.wehive.hive.R;
import uk.co.wehive.hive.view.activity.HomeActivity;

public class GcmIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification(extras);
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification(extras);
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                sendNotification(extras);
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Bundle bundle) {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, HomeActivity.class);
        String message = bundle.getString(AppConstants.MESSAGE_GCM);
        String origin = bundle.containsKey(AppConstants.ORIGIN_GCM) ? bundle.getString(AppConstants.ORIGIN_GCM) : "";
        String idOrigin = bundle.containsKey(AppConstants.ID_ORIGIN_GCM) ? bundle.getString(AppConstants.ID_ORIGIN_GCM) : "";

        String notifCounter = bundle.containsKey(AppConstants.BADGE) ? bundle.getString(AppConstants.BADGE) : "";
        ManagePreferences.setNotificationsCounterMenu(Integer.parseInt(notifCounter));

        intent.putExtra(AppConstants.ID_ORIGIN_GCM, idOrigin);
        intent.putExtra(AppConstants.ORIGIN_GCM, origin);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_action_bar)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message).setBigContentTitle(message))
                        .setAutoCancel(true)
                        .setTicker(message)
                        .setContentText(message);
        mBuilder.setContentIntent(contentIntent);
        ManagePreferences.setNumberOfNotifications(ManagePreferences.getNumberOfNotifications() + 1);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}