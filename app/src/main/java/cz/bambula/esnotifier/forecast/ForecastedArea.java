package cz.bambula.esnotifier.forecast;

import android.support.annotation.NonNull;

import java.util.List;

import cz.bambula.esnotifier.trigonometry.Area;
import cz.bambula.esnotifier.trigonometry.Point;

/**
 * Created by tom on 5/1/15.
 */
public class ForecastedArea extends Area implements Comparable<ForecastedArea> {

    private ERiskType riskType;

    public ForecastedArea(ERiskType riskType, List<Point> corners) {
        super(corners);
        this.riskType = riskType;
    }

    public ERiskType getRiskType() {
        return this.riskType;
    }

    @Override
    public int compareTo(@NonNull ForecastedArea a) {
        // Enum risk type is comparable by default
        return riskType.compareTo(a.riskType);
    }
}
