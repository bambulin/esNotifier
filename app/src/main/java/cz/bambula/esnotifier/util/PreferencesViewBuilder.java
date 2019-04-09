package cz.bambula.esnotifier.util;

import android.graphics.drawable.Drawable;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;

import cz.bambula.esnotifier.R;
import cz.bambula.esnotifier.forecast.ERiskType;
import cz.bambula.esnotifier.presetarea.EPresetAreaGroup;
import cz.bambula.esnotifier.presetarea.PresetArea;
import cz.bambula.esnotifier.presetarea.PresetAreasList;

//import android.support.v7.preference.SwitchPreferenceCompat;

/**
 * Created by tkozel on 10/21/16.
 */

public class PreferencesViewBuilder {
    public static final String AREA_GROUP_SCREEN_ID_PREFIX = "pref_area_group_";
    public static final String AREA_GROUP_SCREEN_ID_POSTFIX = "_screen";

    private PreferenceActivity context;

    public PreferencesViewBuilder(PreferenceActivity context) {
        this.context = context;
    }

    public void setGlobalWarningLevelIcon() {
        String warningLevelName = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.pref_globalWarningLevel_id), null);
        ListPreference globalWarningLevelPref = (ListPreference) context.getPreferenceManager()
                .findPreference(context.getString(R.string.pref_globalWarningLevel_id));
        globalWarningLevelPref.setIcon(R.mipmap.ic_earth);
        Drawable riskIcon = ResourcesUtils.getERiskTypeResourceIcon(warningLevelName, ERiskType.THUNDER_15, context);
        globalWarningLevelPref.setIcon(riskIcon);
    }

    public void setLastUpdatedTitle() {
        Preference lastUpdatedPref = context.findPreference(context.getString(R.string.pref_lastSuccessfulForecastDwnld_id));
        lastUpdatedPref.setTitle(context.getString(R.string.pref_lastSuccessfulForecastDwnld_text) + " " +
                PreferencesManager.getLastSuccessfulDwnldReadable(context));
    }

    public void setSoundIcon() {
        SwitchPreference soundPref = (SwitchPreference) context.findPreference(context.getString(R.string.pref_useNotificationSound_id));
        if (PreferencesManager.isSoundOn(context)) {
            soundPref.setIcon(R.mipmap.ic_speaker);
        } else {
            soundPref.setIcon(R.mipmap.ic_mute);
        }
    }

    public void setVibrationIcon() {
        SwitchPreference soundPref = (SwitchPreference) context.findPreference(context.getString(R.string.pref_useNotificationVibration_id));
        if (PreferencesManager.isVibrationOn(context)) {
            soundPref.setIcon(R.mipmap.ic_vibration);
        } else {
            soundPref.setIcon(R.mipmap.ic_vibration_off);
        }
    }

    public void buildCountriesSettingsView() {
        PreferenceScreen presetAreasScreen = (PreferenceScreen) context.getPreferenceScreen()
                .findPreference(context.getString(R.string.pref_presetAreasScreen_id));
        if (presetAreasScreen == null) return;
        PreferenceScreen groupScreen;
        StringBuilder groupSummaryBuilder;
        ListPreference areaPreference;
        PresetAreasList presetAreas = PresetAreasList.getPresetAreas(context);
        for (EPresetAreaGroup group : EPresetAreaGroup.values()) {
            PresetAreasList presetAreasInGroup = presetAreas.getByGroup(group);
            if (presetAreasInGroup.isEmpty()) {
                continue;
            }
            groupScreen = presetAreasScreen.getPreferenceManager().createPreferenceScreen(context);
            groupScreen.setKey(getAreaGroupScreenId(group));
            groupScreen.setTitle(ResourcesUtils.getEnumResourceTextId(group, context));
            //groupSummaryBuilder = new StringBuilder();
            groupScreen.setSummary(presetAreasInGroup.getCodesString());
            for (PresetArea area : presetAreasInGroup) {
                areaPreference = new ListPreference(context);
                areaPreference.setKey(PreferencesManager.getPresetAreaId(area));
                areaPreference.setTitle(area.getName());
                areaPreference.setDialogTitle(area.getName());
                areaPreference.setIcon(R.mipmap.ic_bell_off);
                String warningLevelName = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferencesManager.getPresetAreaId(area), ERiskType.NO_RISK.name());
                Drawable riskIcon = ResourcesUtils.getERiskTypeResourceIcon(warningLevelName, ERiskType.NO_RISK, context);
                areaPreference.setIcon(riskIcon);
                areaPreference.setEntries(R.array.pref_warningLevel_entries);
                areaPreference.setEntryValues(R.array.pref_warningLevel_values);
                if (warningLevelName.equals(context.getString(R.string.ERiskType_NO_RISK_name))) {
                    areaPreference.setSummary("Off");
                    areaPreference.setValueIndex(0);
                } else {
                    areaPreference.setSummary("%s");
                }
                groupScreen.addPreference(areaPreference);
            }
            presetAreasScreen.addPreference(groupScreen);
        }
    }

    private static String getAreaGroupScreenId(EPresetAreaGroup group) {
        return AREA_GROUP_SCREEN_ID_PREFIX + group.name() + AREA_GROUP_SCREEN_ID_POSTFIX;
    }
}
