package util;

/**
 * @author Rex Joush
 * @time 2021.04.20
 */

import entity.Week;

import java.util.regex.Pattern;

/**
 * Tools on time
 */
public class TimeUtils {

    // Check the time whether the format meets the requirements
    public static Pattern p = Pattern.compile("(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday) [0-2][0-9]:[0-5][0-9]");

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
        String newMinute = CommonUtils.TimeFormat.format(minuteAfter % 60); // 20

        String newWeek = Week.valueOf(week).getName();
        String hourString = "";

        if (hour >= 24) {
            newWeek = Week.getName((Week.valueOf(week).getIndex() + 1) % 7);
            hourString = CommonUtils.TimeFormat.format(hour % 24);
        } else {
            hourString = CommonUtils.TimeFormat.format(hour);
        }

        return newWeek + " " + hourString + ":" + newMinute;
    }


    /**
     * get print format time
     * example
     *  time: Tuesday 20:20
     *  return Tue 20:20
     * @param time resource time string
     * @return format time string
     */
    public static String getPrintTime(String time) {
        return time.split(" ")[0].substring(0, 3) + " " + time.split(" ")[1];
    }

}
