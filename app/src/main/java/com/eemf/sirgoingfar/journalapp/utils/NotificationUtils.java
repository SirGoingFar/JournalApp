
package com.eemf.sirgoingfar.journalapp.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.eemf.sirgoingfar.journalapp.R;
import com.eemf.sirgoingfar.journalapp.activities.SplashActivity;

public class NotificationUtils {

    private static final int JOURNAL_PREVIEW_NOTIFICATION_ID = 1001;
    private static final int JOURNAL_PREVIEW_PENDING_INTENT_ID = 3003;
    private static final String JOURNAL_PREVIEW_NOTIFICATION_CHANNEL_ID = "preview_notification_channel";

    public static void remindUserToPreviewJournal(Context context) {

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    JOURNAL_PREVIEW_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, JOURNAL_PREVIEW_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setSmallIcon(R.drawable.ic_note_add)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.journal_preview_notification_title))
                .setContentText(context.getString(R.string.journal_preview_notification_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.journal_preview_notification_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(notifPendingIntent(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(JOURNAL_PREVIEW_NOTIFICATION_ID, notificationBuilder.build());
    }

    private static PendingIntent notifPendingIntent(Context context) {
        Intent startActivityIntent = new Intent(context, SplashActivity.class);
        return PendingIntent.getActivity(
                context,
                JOURNAL_PREVIEW_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context) {
        Resources resources = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_note_add);
        return largeIcon;
    }
}