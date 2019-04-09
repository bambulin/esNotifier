package cz.bambula.esnotifier.updateservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import cz.bambula.esnotifier.R;
import cz.bambula.esnotifier.forecast.Forecast;
import cz.bambula.esnotifier.notification.ForecastNotificationManager;
import cz.bambula.esnotifier.util.PreferencesManager;
import cz.bambula.esnotifier.util.TimeUtils;

public class ForecastProcessor {
    private static final String TAG = ForecastProcessor.class.getSimpleName();

    public static void processNewForecast(Forecast forecast, Context context) {
        if (forecast == null) {
            return;
        }
        PreferencesManager.setLastSuccessfulDwnld(context);
        Log.d(TAG, "Forecast received: issued=" + forecast.getIssued() + ", from=" + forecast.getValidFrom() + ", to=" + forecast.getValidTo());

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String prevForecastIssued = sharedPref.getString(context.getString(R.string.forecast_issued), TimeUtils.ZERO_TIME);
        String prevForecastValidFrom = sharedPref.getString(context.getString(R.string.forecast_valid_from), TimeUtils.ZERO_TIME);
        String prevForecastValidTo = sharedPref.getString(context.getString(R.string.forecast_valid_to), TimeUtils.ZERO_TIME);
        Log.d(TAG, "Forecast compared with: issued=" + prevForecastIssued + ", from=" + prevForecastValidFrom + ", to=" + prevForecastValidTo);

        if ( !prevForecastIssued.equals(forecast.getIssued()) ||
                !prevForecastValidFrom.equals(forecast.getValidFrom()) ||
                !prevForecastValidTo.equals(forecast.getValidTo())) {
            Log.d(TAG, "Downloaded forecast is different. Processing...");
            if (TimeUtils.isForecastValidToInFuture(forecast.getValidTo()))  {
                ForecastNotificationManager.forecastNotify(forecast, context);
            } else {
                Log.d(TAG, "Forecast was valid to " + forecast.getValidTo() + ", no notification");
            }
            PreferencesManager.saveActualForecast(forecast, context);
        } else {
            Log.d(TAG, "Downloaded forecast is the same. Ignoring...");
        }
    }
}
