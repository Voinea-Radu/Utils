package com.voinearadu.file_manager.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    public static String getDate(String format) {
        return new SimpleDateFormat(format).format(Calendar.getInstance().getTime());
    }

    @SuppressWarnings("unused")
    public static String getDateAndTime() {
        return getDate("HH:mm:ss dd-MM-yyyy");
    }

    @SuppressWarnings("unused")
    public static String getDateOnly() {
        return getDate("dd-MM-yyyy");
    }

    @SuppressWarnings("unused")
    public static String getTimeOnly() {
        return getDate("HH:mm:ss");
    }

    @SuppressWarnings("unused")
    public static String convertUnixTimeToDate(long unixTimestamp) {
        Date date = new Date(unixTimestamp);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        return sdf.format(date);
    }

    @SuppressWarnings("unused")
    public static String convertToPeriod(long milliseconds) {
        if (milliseconds < 0) {
            return "0s";
        }

        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        milliseconds = milliseconds % 60;
        seconds = seconds % 60;
        minutes = minutes % 60;
        hours = hours % 24;

        String output = "";

        if (days > 0) {
            output += days + "d ";
            output += hours + "h ";
            output += minutes + "m ";
            output += seconds + "s ";

            return output;
        }

        if (hours > 0) {
            output += hours + "h ";
            output += minutes + "m ";
            output += seconds + "s ";

            return output;
        }

        if (minutes > 0) {
            output += minutes + "m ";
            output += seconds + "s ";

            return output;
        }

        if (seconds > 0) {
            output += seconds + "s ";

            return output;
        }

        return milliseconds + "ms ";
    }


}
