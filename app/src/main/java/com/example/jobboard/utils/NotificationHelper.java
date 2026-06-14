package com.example.jobboard.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.jobboard.MainActivity;
import com.example.jobboard.R;

public class NotificationHelper {

    public static final String CHANNEL_ID   = "jobboard_candidatures";
    public static final String CHANNEL_NAME = "Candidatures";
    public static final String CHANNEL_DESC = "Notifications de candidatures envoyées";

    private static int notificationId = 1;

    /**
     * Crée le NotificationChannel (obligatoire sur Android 8+).
     * Appeler une seule fois au démarrage de l'app.
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESC);
            channel.enableVibration(true);

            NotificationManager manager =
                    context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Envoie une notification locale "Candidature envoyée".
     */
    public static void sendApplicationNotification(Context context, String jobTitle) {
        // Intent pour ouvrir MainActivity au clic sur la notif
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("🎉 Candidature envoyée !")
                .setContentText("Votre candidature pour « " + jobTitle + " » a bien été enregistrée.")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Votre candidature pour « " + jobTitle
                                + " » a bien été enregistrée. Consultez l'onglet Candidatures pour le suivi."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(notificationId++, builder.build());
        }
    }
}

