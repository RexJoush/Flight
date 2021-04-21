package database;

import com.Flight;
import com.Location;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Rex Joush
 * @time 2021.04.20
 */

public class Data {

    // all location in this list
    public static Map<String, Location> locations;

    // all flight in this list
    public static Map<Integer, Flight> flights;


    // init the database
    static {
        locations = new HashMap<>();
        Location location = new Location("Beijing",39.9385466, 116.1172733, 0.465);
        locations.put("Beijing", location);
        Location location2 = new Location("Dubai",25.0750853, 54.9475542, 0.04923);
        locations.put("Dubai",location2);
        flights = new HashMap<>();
    }


}
