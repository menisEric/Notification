package com.ericmenis.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Build;


public class NotificationHandler extends ContextWrapper {

    private NotificationManager manager;

    public static final String CHANNEL_HIGH_ID = "1";
    private final String CHANNEL_HIGH_NAME = "HIGH CHANNEL";
    public static final String CHANNEL_LOW_ID = "2";
    private final String CHANNEL_LOW_NAME = "LOW CHANNEL";
    private final int SUMMARY_GROUP_ID = 1001;
    private final String SUMMARY_GROUP_NAME = "GROUPING_NOTIFICATION";

    public NotificationHandler(Context context) {
        super(context);
        createChannels();
    }

    public NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    private void createChannels(){
        if (Build.VERSION.SDK_INT >= 26){
            //Creando high channel
            NotificationChannel highchannel = new NotificationChannel(
                    CHANNEL_HIGH_ID, CHANNEL_HIGH_NAME, NotificationManager.IMPORTANCE_HIGH);

            //...Extra config...
            //Luces
            highchannel.enableLights(true);
            highchannel.setLightColor(Color.YELLOW);
            highchannel.setShowBadge(true);
            //Vibración
            highchannel.enableVibration(true);
            //highchannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            //Sonido personalizado
            // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            // highChannel.setSound(defaultSoundUri, null);

            //Visibilidad de la notificación
            highchannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationChannel lowchannel = new NotificationChannel(
                    CHANNEL_LOW_ID, CHANNEL_LOW_NAME, NotificationManager.IMPORTANCE_LOW);
            lowchannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            getManager().createNotificationChannel(highchannel);
            getManager().createNotificationChannel(lowchannel);
        }
    }

    public Notification.Builder createdNotification(String title, String message, boolean isHighImportance){
        if (Build.VERSION.SDK_INT >= 26){
            if (isHighImportance){
                return this.createdNotificationWithChannel(title,message, CHANNEL_HIGH_NAME);
            }
            return this.createdNotificationWithChannel(title,message, CHANNEL_LOW_NAME);
        }
        return this.createdNotificationWithoutChannel(title, message);
    }

    private Notification.Builder createdNotificationWithChannel(String title, String message, String channelId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("message", message);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            Notification.Action action =
                    new Notification.Action.Builder(Icon.createWithResource
                            (this, android.R.drawable.ic_menu_send), "See Details", pIntent).build();


            return new Notification.Builder(getApplicationContext(), channelId)
                    .setContentTitle(title)
                    .setContentText(message)
                    .addAction(action)
                    .setColor(getColor(R.color.colorPrimary))
                    .setSmallIcon(android.R.drawable.stat_notify_chat)
                    .setGroup(SUMMARY_GROUP_NAME)
                    .setAutoCancel(true);
        }
        return null;
    }

    private Notification.Builder createdNotificationWithoutChannel(String title, String message){
            return new Notification.Builder(getApplicationContext())
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(android.R.drawable.stat_notify_chat)
                    .setAutoCancel(true);
    }

    public void publishNotificationSummaryGroup(boolean isHighImportance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = (isHighImportance) ? CHANNEL_HIGH_ID : CHANNEL_LOW_ID;
            Notification summaryNotification = new Notification.Builder(getApplicationContext(), channelId)
                    .setSmallIcon(android.R.drawable.stat_notify_chat)
                    .setGroup(SUMMARY_GROUP_NAME)
                    .setGroupSummary(true)
                    .build();
            getManager().notify(SUMMARY_GROUP_ID, summaryNotification);
        }
    }
}
