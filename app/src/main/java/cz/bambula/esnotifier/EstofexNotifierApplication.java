package cz.bambula.esnotifier;

import android.app.Application;

import cz.bambula.esnotifier.notification.NotificationChannelRegistrator;
import cz.bambula.esnotifier.util.PreferencesManager;

/**
 * Created by tkozel on 6.5.15.
 */
public class EstofexNotifierApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NotificationChannelRegistrator.createNotificationChannel(this);
        new Scheduler().scheduleUpdateAlarmIfNotScheduled(PreferencesManager.getUpdateInterval(this), this);
    }
}
