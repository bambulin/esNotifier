package cz.bambula.esnotifier.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Pair;

import java.util.List;

import cz.bambula.esnotifier.R;
import cz.bambula.esnotifier.forecast.ERiskType;
import cz.bambula.esnotifier.forecast.Forecast;
import cz.bambula.esnotifier.presetarea.PresetArea;
import cz.bambula.esnotifier.util.PreferencesManager;
import cz.bambula.esnotifier.util.ResourcesUtils;

public class ForecastNotificationManager {
    public static void forecastNotify(Forecast forecast, Context context) {
        String link = context.getString(R.string.forecast_page_url);
        ERiskType globalWarningLevel = PreferencesManager.getGlobalWarningLevel(context);
        List<Pair<PresetArea, ERiskType>> localWarningLevelsSettings = PreferencesManager.getLocalWarningLevelSettings(context);

        Pair<ERiskType, PresetArea> warningLevelForNotification = forecast.getRiskTypeForNotification(globalWarningLevel, localWarningLevelsSettings);

        // If no risk then no notification
        if (ERiskType.NO_RISK.equals(warningLevelForNotification.first)) {
            return;
        }

        String notificationTitle = context.getString(R.string.notification_title) + " " + forecast.getForecastType();
        String warningText = context.getString(ResourcesUtils.getEnumResourceTextId(warningLevelForNotification.first, context));
        String notificationText = warningText + " " + context.getString(R.string.notification_text);
        if (warningLevelForNotification.second != null) {
            notificationText += " for " + warningLevelForNotification.second.getName();
        }

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, NotificationChannelRegistrator.CHANNEL_ID)
                        .setAutoCancel(true)
                        .setSmallIcon(ResourcesUtils.getERiskTypeResourceIconId(warningLevelForNotification.first))
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationText)
                        .setLights(ResourcesUtils.getERiskTypeColor(warningLevelForNotification.first), 300, 1000)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (PreferencesManager.isSoundOn(context)) {
            notificationBuilder.setSound(notificationSound);
        }
        if (PreferencesManager.isVibrationOn(context)) {
            notificationBuilder.setVibrate(new long[]{0, 100, 500, 1000});
        }

        // pending intent is redirection using the deep-link
        Intent resultIntent = new Intent(Intent.ACTION_VIEW);
        resultIntent.setData(Uri.parse(link));

        PendingIntent pending = PendingIntent.getActivity(context, 0, resultIntent, 0);
        notificationBuilder.setContentIntent(pending);

        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(context);
        // using the same tag and Id causes the new notification to replace an existing one
        mNotificationManager.notify(context.getString(R.string.notification_tag), 0 , notificationBuilder.build());
    }
}
