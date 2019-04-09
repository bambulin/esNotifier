package cz.bambula.esnotifier.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.bambula.esnotifier.R;
import cz.bambula.esnotifier.forecast.ERiskType;
import cz.bambula.esnotifier.forecast.Forecast;
import cz.bambula.esnotifier.presetarea.PresetArea;
import cz.bambula.esnotifier.presetarea.PresetAreasList;


/**
 * Created by tkozel on 10/22/16.
 */

public class PreferencesManager {

    private static final String TAG = PreferencesManager.class.getSimpleName();
    public static final String PREF_AREA_ID_PREFIX = "pref_area_";
    public static final String PREF_AREA_ID_POSTFIX = "_localWarningLevel";

    public static void saveActualForecast(Forecast forecast, Context context) {
        SharedPreferences.Editor editor = getPrefEditor(context);
        editor.putString(context.getString(R.string.forecast_issued), forecast.getIssued());
        editor.putString(context.getString(R.string.forecast_valid_from), forecast.getValidFrom());
        editor.putString(context.getString(R.string.forecast_valid_to), forecast.getValidTo());
        editor.apply();
        Log.d(TAG, "Actual forecast saved: issued=" + forecast.getIssued() + ", from=" + forecast.getValidFrom() + ", to=" + forecast.getValidTo());
    }

    public static void clearLastForecast(Context context) {
        SharedPreferences.Editor editor = getPrefEditor(context);
        editor.putString(context.getString(R.string.forecast_issued), TimeUtils.ZERO_TIME);
        editor.putString(context.getString(R.string.forecast_valid_from), TimeUtils.ZERO_TIME);
        editor.putString(context.getString(R.string.forecast_valid_to), TimeUtils.ZERO_TIME);
        editor.apply();
        Log.d(TAG, "Last forecast cleared.");
    }

    public static void setLastSuccessfulDwnld(Context context) {
        SharedPreferences.Editor editor = getPrefEditor(context);
        String now = TimeUtils.nowInUTC();
        editor.putString(context.getString(R.string.pref_lastSuccessfulForecastDwnld_id), now);
        editor.apply();
        Log.d(TAG, "Last successful forecast download saved: " + now);
    }

    public static String getLastSuccessfulDwnldReadable(Context context) {
        String lastSuccDwnldPrefValue = getLastSuccesfullDwnldPrefValue(context);
        String defaultVal = context.getString(R.string.pref_lastSuccessfulForecastDwnld_default);
        return SettingsFactory.buildLastSuccessfulDwnldReadable(lastSuccDwnldPrefValue, defaultVal);
    }

    public static Date getLastSuccessfulDwnldDate(Context context) {
        String lastSuccDwnldPrefValue = getLastSuccesfullDwnldPrefValue(context);
        return SettingsFactory.buildLastSuccessfulDwnldDate(lastSuccDwnldPrefValue, null);
    }

    private static String getLastSuccesfullDwnldPrefValue(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(context.getString(R.string.pref_lastSuccessfulForecastDwnld_id),
                null);
    }

    private static SharedPreferences.Editor getPrefEditor(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.edit();
    }

    public static int getUpdateInterval(Context context) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String updateIntervalPrefValue = sharedPref.getString(context.getString(R.string.pref_updateInterval_id),
                context.getString(R.string.pref_updateInterval_default));
        return SettingsFactory.buildUpdateInterval(updateIntervalPrefValue);
    }

    public static ERiskType getGlobalWarningLevel(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String globalWarningLevelPrefValue = sharedPref.getString(context.getString(R.string.pref_globalWarningLevel_id),
                context.getString(R.string.pref_globalWarningLevel_default));
        return SettingsFactory.buildERiskType(globalWarningLevelPrefValue, ERiskType.THUNDER_15);
    }

    public static boolean isSoundOn(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean s = sharedPref.getBoolean(context.getString(R.string.pref_useNotificationSound_id), true);
        return sharedPref.getBoolean(context.getString(R.string.pref_useNotificationSound_id), true);
    }

    public static boolean isVibrationOn(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(context.getString(R.string.pref_useNotificationVibration_id), true);
    }

    public static boolean isLastUpdateMissing(Context context) {
        int updateInterval = getUpdateInterval(context);
        Date lastUpdated = getLastSuccessfulDwnldDate(context);
        Calendar now = Calendar.getInstance();
        now.add(Calendar.SECOND, -updateInterval);
        return now.getTime().after(lastUpdated);
    }

    public static ERiskType getLocalWarningLevel(PresetArea presetArea, Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String localWarningLevelPrefValue = sharedPref.getString(getPresetAreaId(presetArea), ERiskType.NO_RISK.name());
        return SettingsFactory.buildERiskType(localWarningLevelPrefValue, ERiskType.NO_RISK);
    }

    public static List<Pair<PresetArea, ERiskType>> getLocalWarningLevelSettings(Context context) {
        List<Pair<PresetArea, ERiskType>> localWarningLevelSettings = new ArrayList<>();
        for (PresetArea presetArea : PresetAreasList.getPresetAreas(context)) {
            ERiskType localWarningLevel = getLocalWarningLevel(presetArea, context);
            if (localWarningLevel != ERiskType.NO_RISK) {
                localWarningLevelSettings.add(new Pair<>(presetArea, localWarningLevel));
            }
        }
        return localWarningLevelSettings;
    }

    public static String getPresetAreaId(PresetArea area) {
        return PREF_AREA_ID_PREFIX + area.getCode() + PREF_AREA_ID_POSTFIX;
    }
}
