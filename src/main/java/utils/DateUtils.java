package utils;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class DateUtils {
    private static SimpleDateFormat simpleDateFormat;

    public static SimpleDateFormat getDateFormatter() {
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        }
        return simpleDateFormat;
    }
}
