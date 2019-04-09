package cz.bambula.esnotifier;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import cz.bambula.esnotifier.updateservice.ForecastUpdateService;
import cz.bambula.esnotifier.util.PreferencesManager;


/**
 * Created by tom on 5/13/15.
 */
public class Scheduler extends BroadcastReceiver {
    private static boolean inProgress = false;

    private static final String TAG = Scheduler.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            return;
        }
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d(TAG,"onReceive invoked - boot completed");
            scheduleUpdateAlarm(PreferencesManager.getUpdateInterval(context), context);
        } else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            Log.d(TAG,"onReceive invoked - connectivity change");
            if (PreferencesManager.isLastUpdateMissing(context)) {
                ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected()) {
                    Log.w(TAG, "Cannot get the latest forecast, network is not available.");
                    return;
                }
                if (!inProgress) {
                    try {
                        inProgress = true;
                        scheduleUpdateAlarm(PreferencesManager.getUpdateInterval(context), context);
                    } finally {
                        inProgress = false;
                    }
                } else {
                    Log.d(TAG,"Previous onReceive still in progress - skipping this one");
                }
            }
        }
    }

    public void scheduleUpdateAlarmIfNotScheduled(int interval, Context context) {
        PendingIntent alarmPendingIntent = getAlarmPendingIntent(PendingIntent.FLAG_NO_CREATE, context);
        if (alarmPendingIntent == null) {
            scheduleUpdateAlarm(interval, context);
        }
    }

    public void scheduleUpdateAlarm(int interval, Context context) {
        PendingIntent alarmPendingIntent =  getAlarmPendingIntent(PendingIntent.FLAG_CANCEL_CURRENT, context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Application.ALARM_SERVICE);
        alarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                0,
                interval * 1000,
                alarmPendingIntent);

        Log.d(TAG, "scheduleUpdateAlarm: alarm reset " + interval + " seconds");
    }

    public PendingIntent getAlarmPendingIntent(int flag, Context context) {
        Intent alarmIntent = new Intent(context, ForecastUpdateService.class);
        return PendingIntent.getBroadcast(context, 0, alarmIntent, flag);
    }
}
