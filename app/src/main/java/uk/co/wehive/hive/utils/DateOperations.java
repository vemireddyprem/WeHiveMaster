package uk.co.wehive.hive.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class DateOperations {

    public static String getDate(long milliSeconds, String format) {
        DateFormat formatter = new SimpleDateFormat(format);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String getHour(long milliSeconds, String format) {
        DateFormat formatter = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    /**
     * @param date date in format dd MMMM h:m:a
     * @return EXAMPLE 13 JUL 02:10 AM
     */
    public static String getDate(String date) {
        String[] parts = date.split(" ");
        String[] partsHour = parts[2].split(":");
        String hour = (partsHour[0].length() == 1 ? "0" + partsHour[0] : partsHour[0]) + ":" +
                (partsHour[1].length() == 1 ? "0" + partsHour[1] : partsHour[1]) + " " + partsHour[2];
        return parts[0] + " " + parts[1].substring(0, 3) + " " + hour;
    }

    public static Long getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }
}
