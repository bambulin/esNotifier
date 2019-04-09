package cz.bambula.esnotifier.forecast;

import android.util.Log;
import android.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.bambula.esnotifier.presetarea.PresetArea;

/**
 * Created by tom on 5/1/15.
 */
public class Forecast implements Serializable {

    private static final String TAG = Forecast.class.getSimpleName();

    private String forecastType;
    private String issued;
    private String validFrom;
    private String validTo;

    private List<ForecastedArea> areas;

    private String forecaster;

    private boolean areasSorted;

    public Forecast() {
        areas = new ArrayList<>();
        areasSorted = false;
    }

    public String getIssued() {
        return issued;
    }

    public void setIssued(String issued) {
        this.issued = issued;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }

    public String getForecaster() {
        return forecaster;
    }

    public void setForecaster(String forecaster) {
        this.forecaster = forecaster;
    }

    public String getForecastType() {
        return forecastType;
    }

    public void setForecastType(String forecastType) {
        this.forecastType = forecastType;
    }

    public void addArea(ForecastedArea area) {
        this.areas.add(area);
    }

    public Pair<ERiskType, PresetArea> getRiskTypeForNotification(ERiskType global, List<Pair<PresetArea, ERiskType>> localSettings) {
        if (areas.isEmpty()) {
            return new Pair<>(ERiskType.NO_RISK, null);
        }
        if (!areasSorted) {
            this.sortAreas();
        }

        // global warning
        if (global != ERiskType.NO_RISK && global.compareTo(areas.get(0).getRiskType()) <= 0) {
            Log.d(TAG, "Global Warning issued, level: " + areas.get(0).getRiskType());
            return new Pair<>(areas.get(0).getRiskType(), null);
        }

        Pair<ERiskType, PresetArea> riskTypeForNotification = new Pair<>(ERiskType.NO_RISK, null);
        ERiskType local;
        PresetArea presetArea;
        for (Pair<PresetArea, ERiskType> localSetting : localSettings) {
            presetArea = localSetting.first;
            local = localSetting.second;
            if (local == ERiskType.NO_RISK) {
                continue;
            }
            for (ForecastedArea area : areas) {
                // forecasted areas are sorted by risk from highest to lowest so if riskTypeForNotification
                // is higher or equal to current forecasted area we can stop looping, because we know we
                // cannot find higher risk for current preset area
                if (area.getRiskType().compareTo(riskTypeForNotification.first) <= 0) {
                    break;
                }
                // if local risk setting is lower than or equals to forecasted area risk and the areas have intersection
                // then riskTypeForNotification can be set to forecasted area risk
                //
                if (local.compareTo(area.getRiskType()) <= 0 && area.hasIntersectionWith(presetArea.getArea())) {
                    Log.d(TAG, "Warning level " + area.getRiskType() + " for area " + presetArea.getCode());
                    riskTypeForNotification = new Pair<>(area.getRiskType(), presetArea);
                }
            }
        }
        if (riskTypeForNotification.first.equals(ERiskType.NO_RISK)) {
            Log.d(TAG, "No applicable warning area found.");
        }
        return riskTypeForNotification;
    }

    private void sortAreas() {
        Collections.sort(areas, Collections.<ForecastedArea>reverseOrder());
        areasSorted = true;
    }
}
