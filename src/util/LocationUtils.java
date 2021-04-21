package util;

/**
 * @author Rex Joush
 * @time 2021.04.20
 */

/**
 * Tools on location
 */
public class LocationUtils {

    private static final double EARTH_RADIUS = 6378.137;

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
        s =  s * EARTH_RADIUS;
        return s;
    }

}
