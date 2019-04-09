package cz.bambula.esnotifier.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by tkozel on 10/22/16.
 */

public class TimeUtils {
    public static final String ZERO_TIME = "1970010100";
    public static final String FORECAST_TIME_FORMAT = "yyyyMMddHH";
    public static final String PREF_TIME_FORMAT = "yyyyMMddHHmmss";
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    public static boolean isForecastValidToInFuture(String validToString) {
        Date now = Calendar.getInstance().getTime();
        try {
            Date validTo = simpleDateFormat(FORECAST_TIME_FORMAT, UTC).parse(validToString);
            return now.before(validTo);
        } catch (ParseException ex) {
            return true;
        }
    }

    public static String nowInUTC() {
        Date now = Calendar.getInstance().getTime();
        return simpleDateFormat(PREF_TIME_FORMAT, UTC).format(now);
    }

    public static String getReadable(String prefValue) {
        try {
            Date date = simpleDateFormat(PREF_TIME_FORMAT, UTC).parse(prefValue);
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
            return df.format(date);
        } catch (ParseException ex) {
            return prefValue;
        }
    }

    public static Date getDateValue(String prefValue) {
        try {
            return simpleDateFormat(PREF_TIME_FORMAT, UTC).parse(prefValue);
        } catch (ParseException ex) {
            return null;
        }
    }

    private static SimpleDateFormat simpleDateFormat(String pattern, TimeZone timeZone) {
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        sf.setTimeZone(timeZone);
        return sf;
    }
}
