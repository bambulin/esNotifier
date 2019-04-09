package cz.bambula.esnotifier.updateservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;

import cz.bambula.esnotifier.BuildConfig;
import cz.bambula.esnotifier.R;
import cz.bambula.esnotifier.forecast.Forecast;

/**
 * Created by tom on 5/1/15.
 */
public class ForecastUpdateService extends BroadcastReceiver {
    private static final String TAG = ForecastUpdateService.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onReceive invoked");
        }
        new Thread(() -> startUpdate(context)).start();
    }

    public static void startUpdate(Context context) {
        Log.d(TAG, "Starting update process");
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Log.w(TAG, "Cannot get the latest forecast, network is not available.");
            return;
        }

        try {
            Forecast f = ForecastDownloader.getForecast(context.getString(R.string.forecast_xml_url));
            ForecastProcessor.processNewForecast(f, context);
        } catch (IOException e) {
            Log.e(TAG, "error", e);
        }
    }
}
