package com;

/**
 * @author Rex Joush
 * @time 2021.04.21
 */

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Tools for common
 */
public class Utils {


    public static DecimalFormat TimeFormat = new DecimalFormat("00");
    public static DecimalFormat doubleFormat = new DecimalFormat("#.00");

    // Check the file every line whether the format meets the requirements
    public static Pattern pTime = Pattern.compile("(Monday|monday|Tuesday|tuesday|Wednesday|wednesday|Thursday|thursday|Friday|friday|Saturday|saturday|Sunday|sunday|) \\d+:\\d+,[A-Za-z]+,[A-Za-z]+,\\d+,\\d+");

    // Check the file every line whether the format meets the requirements
    public static Pattern pLocation = Pattern.compile("[A-Za-z]+,[-]?\\d+.\\d+,[-]?\\d+.\\d+,[-]?[01](.\\d+)?");

    // Check the time whether the format meets the requirements
    public static Pattern p = Pattern.compile("(Monday|monday|Tuesday|tuesday|Wednesday|wednesday|Thursday|thursday|Friday|friday|Saturday|saturday|Sunday|sunday|) \\d+:\\d+");

    private static final double EARTH_RADIUS = 6371.0;

    public static double getDistance(double longitude1, double latitude1, double longitude2, double latitude2) {
        // latitude
        double lat1 = Math.toRadians(latitude1);
        double lat2 = Math.toRadians(latitude2);
        // longitude
        double lng1 = Math.toRadians(longitude1);
        double lng2 = Math.toRadians(longitude2);
        // difference of latitude
        double a = lat1 - lat2;
        // difference of longitude
        double b = lng1 - lng2;
        // calculate distance between two point
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(b / 2), 2)));
        // radians * earth radius, return kilometer
        s = s * EARTH_RADIUS;
        return s;
    }

    /*
        Get the time difference by two time string, return the number of total minute
        The time string formatter
            Week TimeString

            example:
                Friday 12:00
                Saturday 15:45
        return
            if time1 before time2 return value < 0
            if time1 after time2 return value > 0

     */
    public static int getTimeDifferenceByTimeString(String time1, String time2) {

        // get the week value and time value of time1
        String week1 = time1.split(" ")[0];
        String t1 = time1.split(" ")[1];

        // get the week value and time value of time1
        String week2 = time2.split(" ")[0];
        String t2 = time2.split(" ")[1];

        // get the day difference of thr two time
        int day = Week.valueOf(week1).getIndex() - Week.valueOf(week2).getIndex();

        int minute1 = Integer.parseInt(t1.split(":")[0]) * 60 + Integer.parseInt(t1.split(":")[1]);
        int minute2 = Integer.parseInt(t2.split(":")[0]) * 60 + Integer.parseInt(t2.split(":")[1]);

        // get the minute difference of thr two time
        int i = minute1 - minute2;

        // return the total minute difference of two time
        return i + day * 24 * 60;
    }

    // Get the time in a few minutes
    public static String getManyMinuteAfter(String time, int minute) {

        // get week and time value of parameter time
        String week = time.split(" ")[0];
        String t = time.split(" ")[1];


        // Sunday 21:00 200, 3h 20m

        // get now minute value, 1260
        int minuteNow = Integer.parseInt(t.split(":")[0]) * 60 + Integer.parseInt(t.split(":")[1]);

        // add pass minute, 1460
        int minuteAfter = minuteNow + minute;

        // get new minute value, 24
        int hour = minuteAfter / 60;
        String newMinute = Utils.TimeFormat.format(minuteAfter % 60); // 20

        String newWeek = Week.valueOf(week).getName();
        String hourString = "";

        if (hour >= 24) {
            newWeek = Week.getName((Week.valueOf(week).getIndex() + 1) % 7);
            hourString = Utils.TimeFormat.format(hour % 24);
        } else {
            hourString = Utils.TimeFormat.format(hour);
        }

        return newWeek + " " + hourString + ":" + newMinute;
    }


    /**
     * get print format time
     * example
     * time: Tuesday 20:20
     * return Tue 20:20
     *
     * @param time resource time string
     * @return format time string
     */
    public static String getPrintTime(String time) {
        return time.split(" ")[0].substring(0, 3) + " " + time.split(" ")[1];
    }

    /**
     * make the string first letter to upper case
     *
     * @param str wait for change string
     * @return change result
     */
    public static String captureName(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    /**
     * check str is or not a number
     * @param str check string
     * @return check result
     *          1.001 true
     *          128971 true
     *          21b31 false
     */
    public static boolean isNumeric(final String str) {
        // null or empty
        if (str == null || str.length() == 0) {
            return false;
        }

        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            try {
                Double.parseDouble(str);
                return true;
            } catch (NumberFormatException ex) {
                try {
                    Float.parseFloat(str);
                    return true;
                } catch (NumberFormatException exx) {
                    return false;
                }
            }
        }
    }

    public static boolean isTime(String time){

        return time.matches("\\d+:\\d+");
    }
}
