package service;

/**
 * @author Rex Joush
 * @time 2021.04.21
 */

import com.Flight;
import com.Location;
import database.Data;
import entity.Week;
import util.CommonUtils;
import util.TimeUtils;

import java.io.*;
import java.util.*;


/**
 * location service interface
 */
public class LocationService {

    /**
     * list all available locations in alphabetical order
     */
    public void getAllLocations() {

        List<String> locations = new ArrayList<>();

        for (Map.Entry<String, Location> entry : Data.locations.entrySet()) {
            locations.add(entry.getKey());
        }
        Collections.sort(locations);

        System.out.println("Locations ("+ Data.locations.size() +"):");
        System.out.println(String.join(", ", locations));
    }

    /**
     * add location
     * @param options command options
     */
    public void addLocation(String[] options) {
        // check parameter number
        if (options.length != 6) {
            throw new RuntimeException("Usage: LOCATION ADD <name> <lat> <long> <demand_coefficient>\n" +
                    "Example: LOCATION ADD Sydney -33.847927 150.651786 0.2");
        }

        // check location have or not
        String name = options[2];
        if (Data.locations.containsKey(name)) {
            throw new RuntimeException("This location already exists.");
        }

        // check latitude
        double latitude = 0.0;
        try {
            latitude = Double.parseDouble(options[3]);
            if (Math.abs(latitude) > 85.0){
                throw new RuntimeException("Invalid latitude. It must be a number of degrees between -85 and +85.");
            }
        } catch (Exception e){
            throw new RuntimeException("Invalid latitude. It must be a number of degrees between -85 and +85.");
        }

        // check longitude
        double longitude = 0.0;
        try {
            longitude = Double.parseDouble(options[4]);
            if (Math.abs(longitude) > 180.0){
                throw new RuntimeException("Invalid longitude. It must be a number of degrees between -180 and +180.");
            }
        } catch (Exception e){
            throw new RuntimeException("Invalid longitude. It must be a number of degrees between -180 and +180.");
        }

        // check demand
        double demand = 0.0;
        try {
            demand = Double.parseDouble(options[5]);
            if (Math.abs(demand) > 1.0){
                throw new RuntimeException("Invalid demand coefficient. It must be a number between -1 and +1.");
            }
        } catch (Exception e){
            throw new RuntimeException("Invalid demand coefficient. It must be a number between -1 and +1.");
        }

        // new location add to database
        Location location = new Location(name, latitude, longitude, demand);
        Data.locations.put(name, location);
        System.out.println("Successfully added location " + name);

    }

    /**
     * view details about a location (it’s name, coordinates, demand coefficient)
     * @param option command options
     */
    public void getLocationByName(String option) {
        if (!Data.locations.containsKey(option)) {
            throw new RuntimeException("This location does not exist in the system.");
        }
        Location location = Data.locations.get(option);
        System.out.printf("%-14s%s\n","Location: ", location.getName());
        System.out.printf("%-14s%s\n","Latitude: ", location.getLatitude());
        System.out.printf("%-14s%s\n","Longitude: ", location.getLongitude());
        System.out.printf("%-14s%s\n","Demand: ", location.getDemand());
    }

    /**
     * import locations from csv file
     *
     * @param options command options
     */
    public void importLocation(String[] options) {
        if (options.length < 3) {
            throw new RuntimeException("not enough arguments");
        }
        String path = options[2];
        FileReader fr;

        try {
            // 1.create FileReader, support datasource
            fr = new FileReader(path);
            // 2.buffer reader
            BufferedReader br = new BufferedReader(fr);

            String line = "";
            int right = 0;
            int error = 0;


            while ((line = br.readLine()) != null) {
                // contains the line format
                if (CommonUtils.pLocation.matcher(line).matches()) {
                    // add flight
                    String[] split = line.split(",");
                    Location location = new Location(split[0], Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));

                    // check location is in the database
                    if (!Data.locations.containsKey(split[0])) {
                        Data.locations.put(split[0], location);
                        right++;
                    } else {
                        error++;
                    }

                } else {
                    // error add 1
                    error++;
                }
            }

            System.out.println("Imported " + right + " locations.");
            if (error > 0) {
                System.out.println(error + " line was invalid.");
            }

            // 3.release
            br.close();
            fr.close();

        } catch (IOException e) {
            throw new RuntimeException("Error reading file.");
        }
    }

    /**
     * export locations to csv file
     *
     * @param options command options
     */
    public void exportLocation(String[] options) {
        if (options.length < 3) {
            throw new RuntimeException("not enough arguments");
        }

        String path = options[2];
        try {
            // create buffer write, use path
            BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));

            int right = 0;
            // write file
            for (Map.Entry<String, Location> entry : Data.locations.entrySet()) {
                Location location = entry.getValue();
                // format style
                String line = location.getName() + "," + location.getLatitude() + "," + location.getLatitude() + "," + location.getDemand();
                writer.write(line);
                writer.newLine();
            }
            writer.close();
            System.out.println("Exported " + Data.locations.size() + " locations.");
        } catch (IOException e) {
            // error process
            throw new RuntimeException("Error writing file.");
        }
    }

    /**
     * list all departing and arriving flights, in order of the time they arrive/depart
     *
     * @param options command options
     */
    public void schedule(String[] options) {
        // todo location schedule
        if (options.length < 2) {
            throw new RuntimeException("not enough arguments");
        }
        if (!Data.locations.containsKey(options[1])) {
            throw new RuntimeException("This location does not exist in the system.");
        }

        List<Flight> flights = getSortedFlight();

        System.out.println("-------------------------------------------------------");
        System.out.println("ID   Time        Departure/Arrival to/from Location");
        System.out.println("-------------------------------------------------------");

        for (Flight flight : flights) {
            // find All source from this place
            if (flight.getSource().equals(options[1])) {
                System.out.printf("%4s %-12sDeparture to %s\n", flight.getId(),
                        TimeUtils.getPrintTime(flight.getTime()),flight.getDestination());
            }
            // find All source from this place
            if (flight.getDestination().equals(options[1])) {
                System.out.printf("%4s %-12sArrival from %s\n", flight.getId(),
                        TimeUtils.getPrintTime(flight.getTime()),flight.getSource());
            }
        }
    }

    /**
     * list all departing flights, in order of departure time
     *
     * @param options command options
     */
    public void departures(String[] options) {

        if (options.length < 2) {
            throw new RuntimeException("not enough arguments");
        }
        if (!Data.locations.containsKey(options[1])) {
            throw new RuntimeException("This location does not exist in the system.");
        }

        List<Flight> flights = getSortedFlight();

        System.out.println("-------------------------------------------------------");
        System.out.println("ID   Time        Departure/Arrival to/from Location");
        System.out.println("-------------------------------------------------------");

        for (Flight flight : flights) {
            // find All source from this place
            if (flight.getSource().equals(options[1])) {
                System.out.printf("%4s %-12sDeparture to %s\n", flight.getId(),
                        TimeUtils.getPrintTime(flight.getTime()),flight.getDestination());
            }
        }
    }

    /**
     * list all arriving flights, in order of arrival time
     *
     * @param options command options
     */
    public void arrivals(String[] options) {
        // todo location arrivals
        if (options.length < 2) {
            throw new RuntimeException("not enough arguments");
        }
        if (!Data.locations.containsKey(options[1])) {
            throw new RuntimeException("This location does not exist in the system.");
        }

        List<Flight> flights = getSortedFlight();

        System.out.println("-------------------------------------------------------");
        System.out.println("ID   Time        Departure/Arrival to/from Location");
        System.out.println("-------------------------------------------------------");

        for (Flight flight : flights) {
            // find All source from this place
            if (flight.getDestination().equals(options[1])) {
                System.out.printf("%4s %-12sArrival from %s\n", flight.getId(),
                        TimeUtils.getPrintTime(flight.getTime()),flight.getSource());
            }
        }
    }

    /**
     * get the flight list have been sorted
     *
     * @return flight list
     */
    public List<Flight> getSortedFlight() {
        List<Flight> flights = new ArrayList<>();
        for (Map.Entry<Integer, Flight> entry : Data.flights.entrySet()) {
            flights.add(entry.getValue());
        }

        flights.sort(new Comparator<>() {
            @Override
            public int compare(Flight f1, Flight f2) {
                // asc by week
                String week1 = f1.getTime().split(" ")[0];
                String week2 = f2.getTime().split(" ")[0];

                int result = Week.valueOf(week1).getIndex() - Week.valueOf(week2).getIndex();

                // if week same
                if (result == 0) {
                    result = f1.getTime().split(" ")[1].compareTo(f2.getTime().split(" ")[1]);
                }
                return result;
            }
        });

        return flights;
    }
}