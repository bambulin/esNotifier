package cz.bambula.esnotifier.util;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cz.bambula.esnotifier.forecast.ERiskType;

/**
 * Created by tom on 5/5/15.
 */
public class SettingsFactory {

    private static final String TAG = SettingsFactory.class.getSimpleName();

    private static final String TEN_SECONDS = "-1";

    private static final List updateIntervals = Arrays.asList(15, 30, 60, 180);

    /**
     * Returns UpdateInterval seconds
     *
     * @param updateIntervalPrefValue
     * @return
     */
    public static int buildUpdateInterval(String updateIntervalPrefValue) {
        if (updateIntervalPrefValue.equals(TEN_SECONDS)) {
            return 10;
        }

        int interval = Integer.parseInt(updateIntervalPrefValue);

        if (!updateIntervals.contains(interval)) {
            Log.d(TAG, "Not supported update interval: " + interval + ", returning 30 mins");
            interval = 30;
        }

        return interval * 60;
    }

    public static ERiskType buildERiskType(String warningLevelPrefValue, ERiskType defaultRiskType) {
        if (StringUtils.isBlank(warningLevelPrefValue)) {
            Log.d(TAG, "Empty risk type, returning " + defaultRiskType);
            return defaultRiskType;
        }
        ERiskType riskType;
        try {
            riskType = ERiskType.valueOf(warningLevelPrefValue);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Not supported ERiskType: " + warningLevelPrefValue + ", returning " + defaultRiskType);
            riskType = defaultRiskType;
        }
        return riskType;
    }

    public static String buildLastSuccessfulDwnldReadable(String lastSuccessfulDwnldPrefValue, String defaultValue) {
        if (StringUtils.isBlank(lastSuccessfulDwnldPrefValue)) {
            Log.d(TAG, "Empty last successful download: " + lastSuccessfulDwnldPrefValue + ", returning " + defaultValue);
            return defaultValue;
        }
        return TimeUtils.getReadable(lastSuccessfulDwnldPrefValue);
    }

    public static Date buildLastSuccessfulDwnldDate(String lastSuccessfulDwnldPrefValue, Date defaultValue) {
        if (StringUtils.isBlank(lastSuccessfulDwnldPrefValue)) {
            Log.d(TAG, "Empty last successful download: " + lastSuccessfulDwnldPrefValue + ", returning " + defaultValue);
            return defaultValue;
        }
        return TimeUtils.getDateValue(lastSuccessfulDwnldPrefValue);
    }
}
