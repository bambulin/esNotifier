package cz.bambula.esnotifier;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

import cz.bambula.esnotifier.forecast.ERiskType;
import cz.bambula.esnotifier.util.PreferencesManager;
import cz.bambula.esnotifier.util.PreferencesViewBuilder;
import cz.bambula.esnotifier.util.ResourcesUtils;
import cz.bambula.esnotifier.util.SettingsFactory;


public class MainActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private PreferencesViewBuilder prefViewBuilder;

    public MainActivity() {
        super();
        prefViewBuilder = new PreferencesViewBuilder(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        prefViewBuilder.setGlobalWarningLevelIcon();
        prefViewBuilder.setLastUpdatedTitle();
        prefViewBuilder.setSoundIcon();
        prefViewBuilder.setVibrationIcon();
        PreferenceScreen presetAreasScreen = (PreferenceScreen) getPreferenceScreen()
                .findPreference(getString(R.string.pref_presetAreasScreen_id));
        if (presetAreasScreen != null && presetAreasScreen.getPreferenceCount() == 0) {
            prefViewBuilder.buildCountriesSettingsView();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged");

        Preference pref = findPreference(key);
        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            String entry = listPref.getEntry().toString();
            if (entry.contains("%")) {
                entry = new StringBuilder(entry).insert(entry.indexOf('%'),'%').toString();
            }

            listPref.setSummary(entry);
            Log.d(TAG, "onSharedPreferenceChanged: setting summary  " + listPref.getEntry());

            if (key.endsWith("WarningLevel")) {
                ERiskType defaultLevel = key.equals(getString(R.string.pref_globalWarningLevel_id)) ? ERiskType.THUNDER_15 : ERiskType.NO_RISK;
                ERiskType level = SettingsFactory.buildERiskType(listPref.getValue(), defaultLevel);
                if (level == ERiskType.NO_RISK) {
                    listPref.setIcon(null);
                } else {
                    Drawable icon = getResources().getDrawable(ResourcesUtils.getERiskTypeResourceIconId(level));
                    listPref.setIcon(icon);
                }
                PreferencesManager.clearLastForecast(this);
            }
        }

        if (key.equals(getString(R.string.pref_useNotificationSound_id))) {
            prefViewBuilder.setSoundIcon();
        }

        if (key.equals(getString(R.string.pref_useNotificationVibration_id))) {
            prefViewBuilder.setVibrationIcon();
        }

        if (key.equals(getString(R.string.pref_lastSuccessfulForecastDwnld_id))) {
            prefViewBuilder.setLastUpdatedTitle();
        }

        // this must be the last block in the method, because it starts the update
        if (key.equals(getString(R.string.pref_updateInterval_id))) {
            Scheduler scheduler = new Scheduler();
            int interval = PreferencesManager.getUpdateInterval(this);
            Log.d(TAG, "onSharedPreferenceChanged: update interval = " + interval);
            scheduler.scheduleUpdateAlarm(interval, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        prefViewBuilder.setLastUpdatedTitle();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return false;
    }
}
