package service;

/**
 * @author Rex Joush
 * @time 2021.04.20
 */

import com.Flight;
import database.Data;
import entity.Week;
import util.CommonUtils;
import util.TimeUtils;

import java.io.*;
import java.util.*;

/**
 * flight service interface
 */
public class FlightService {

    /**
     * list all available flights ordered by departure time, then departure location name
     */
    public void getAllFlights() {

        System.out.println("-------------------------------------------------------");
        System.out.println("ID   Departure   Arrival     Source --> Destination");
        System.out.println("-------------------------------------------------------");
        if (Data.flights.size() == 0){
            System.out.println("(None)");
            return;
        }

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

        for (Flight flight : flights) {
            System.out.printf("%4s %-9s   %-9s   %s --> %s\n", flight.getId(),
                    TimeUtils.getPrintTime(flight.getTime()),
                    TimeUtils.getPrintTime(flight.getArrivedTime()),
                    flight.getSource(), flight.getDestination());
        }

    }

    /**
     * add flight
     *
     * @param options add command options
     */
    public void addFlight(String[] options) {

        // check parameter number
        if (options.length != 7) {
            throw new RuntimeException("Usage: FLIGHT ADD <departure time> <from> <to> <capacity>\n" +
                    "Example: FLIGHT ADD Monday 18:00 Sydney Melbourne 120");
        }

        int id = Data.flights.size();
        String time = options[2] + " " + options[3];

        // check time format
        if (!TimeUtils.p.matcher(time).matches()) {
            throw new RuntimeException("Invalid departure time. Use the format <day_of_week> <hour:minute>, with 24h time.");
        }

        // check runways
        checkRunways(time, options[4]);

        // check source location
        String source = options[4];
        if (!Data.locations.containsKey(source)) {
            throw new RuntimeException("Invalid starting location.");
        }

        // check destination location
        String destination = options[5];
        if (!Data.locations.containsKey(destination)) {
            throw new RuntimeException("Invalid ending location.");
        }

        // source and destination not allowed to be the same
        if (source.equals(destination)) {
            throw new RuntimeException("Source and destination cannot be the same place.");
        }

        // check capacity
        int capacity = 0;
        try {
            capacity = Integer.parseInt(options[6]);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid positive integer capacity.");
        }

        Flight flight = new Flight(id, time, source, destination, capacity, 0);

        Data.flights.put(id, flight);
        System.out.println("Successfully added Flight " + id + ".");

    }

    /**
     * check the runways is it occupied
     *
     * @param time   flight time, departing and arriving
     * @param source source location name
     */
    public void checkRunways(String time, String source) {
        // TODO check runways
        List<Flight> flights = new ArrayList<>();

        for (Map.Entry<Integer, Flight> entry : Data.flights.entrySet()) {
            flights.add(entry.getValue());
        }
        // flight add Tuesday 12:00 Beijing Dubai 120
        // 0 Tue 12:00   Tue 20:08   Beijing --> Dubai
        // flight add Tuesday 19:10 Dubai Beijing 120
        for (Flight flight : flights) {
            // if source is same and time difference less than 60, false
            if (Math.abs(TimeUtils.getTimeDifferenceByTimeString(flight.getTime(), time)) <= 60 && source.equals(flight.getSource())) {
                throw new RuntimeException("Scheduling conflict! This flight clashes with Flight " + flight.getId() + " departing from " + flight.getSource() + " on " + flight.getArrivedTime() + ".");
            }
            // if arrived time is same and time difference less than 60, false
            if (Math.abs(TimeUtils.getTimeDifferenceByTimeString(flight.getArrivedTime(), time)) <= 60 && source.equals(flight.getDestination())) {
                throw new RuntimeException("Scheduling conflict! This flight clashes with Flight " + flight.getId() + " arriving at " + flight.getDestination() + " on " + flight.getArrivedTime() + ".");
            }
        }
    }

    /**
     * book a certain number of passengers for the flight at the current ticket price,
     * and then adjust the ticket price to reflect the reduced capacity remaining. If no number is given, book 1
     * passenger. If the given number of bookings is more than the remaining capacity, only accept bookings
     * until the capacity is full.
     *
     * @param options command options
     */
    public void book(String[] options) {

        if (options.length < 3) {
            throw new RuntimeException("not enough arguments");
        }

        int id = Integer.parseInt(options[1]);
        int number;

        // given the number
        if (options.length > 3) {
            try {
                number = Integer.parseInt(options[3]);
            } catch (Exception e) {
                throw new RuntimeException("Invalid number of passengers to book.");
            }
        } else {
            number = 1;
        }
        Flight flight = Data.flights.get(id);

        // record the booked before new book
        int bookedBefore = flight.getBooked();

        // calculate the total cost
        double cost = flight.book(number);

        // get after booked number
        int bookedAfter = flight.getBooked();

        System.out.println("Booked " + (bookedAfter - bookedBefore) + " passengers on flight 0 for a total cost of $" + CommonUtils.doubleFormat.format(cost));

        // if the flight is full
        if (bookedAfter == flight.getCapacity()) {
            System.out.println("Flight is now full.");
        }

    }

    /**
     * remove a flight from the schedule
     *
     * @param options command options
     */
    public void remove(String[] options) {

        if (options.length < 2) {
            throw new RuntimeException("not enough arguments");
        }

        int id = Integer.parseInt(options[1]);
        Flight flight = Data.flights.get(id);
        Data.flights.remove(Integer.parseInt(options[1]));
        // Removed Flight 0, Mon 08:00 Mumbai --> NewDelhi, from the flight schedule.
        System.out.println("Removed Flight " + id + ", " + TimeUtils.getPrintTime(flight.getTime()) + " " + flight.getSource() + " --> " + flight.getDestination() + ", from the flight schedule");
    }

    /**
     * reset the number of passengers booked to 0, and the ticket price to its original state.
     *
     * @param options command options
     */
    public void reset(String[] options) {

        if (options.length < 2) {
            throw new RuntimeException("not enough arguments");
        }

        int id = Integer.parseInt(options[1]);
        Flight flight = Data.flights.get(id);
        flight.setBooked(0);
        Data.flights.put(id, flight);

        // Reset passengers booked to 0 for Flight 0, Mon 08:00 Mumbai --> NewDelhi.
        System.out.println("Reset passengers booked to 0 for Flight " + id + ", " + TimeUtils.getPrintTime(flight.getTime()) + " " + flight.getSource() + " --> " + flight.getDestination() + ".");

    }

    /**
     * view information about a flight (from->to, departure arrival times, current ticket price,
     * capacity, passengers booked)
     *
     * @param options command options
     */
    public void getFlightById(String[] options) {
        if (options.length < 2) {
            throw new RuntimeException("not enough arguments");
        }

        int id = Integer.parseInt(options[1]);
        Flight flight = Data.flights.get(id);

        System.out.println("Flight " + id);
        System.out.printf("%-14s%s %s\n", "Departure: ", TimeUtils.getPrintTime(flight.getTime()), flight.getSource());
        System.out.printf("%-14s%s %s\n", "Arrival: ", TimeUtils.getPrintTime(flight.getArrivedTime()), flight.getDestination());
        System.out.printf("%-14s%,dkm\n", "Distance: ", Math.round(flight.getDistance()));
        System.out.printf("%-14s%dh %dm\n", "Duration: ", flight.getDuration() / 60, flight.getDuration() % 60);
        System.out.printf("%-14s%s\n", "Ticket Cost: ", "$" + CommonUtils.doubleFormat.format(flight.getTicketPrice()));
        System.out.printf("%-14s%d/%d\n", "Passengers: ", flight.getBooked(), flight.getCapacity());

    }

    /**
     * import flights from csv file
     *
     * @param options command options
     */
    public void importFlight(String[] options) {
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
                if (CommonUtils.pTime.matcher(line).matches()) {
                    // add flight
                    String[] split = line.split(",");
                    Flight flight = new Flight(Data.flights.size(), split[0], split[1], split[2], Integer.parseInt(split[3]), Integer.parseInt(split[4]));

                    // check location is in the database
                    if (Data.locations.containsKey(split[1]) && Data.locations.containsKey(split[2])) {
                        Data.flights.put(Data.flights.size(), flight);
                        // right add 1
                        right++;
                    } else {
                        error++;
                    }

                } else {
                    // error add 1
                    error++;
                }
            }

            System.out.println("Imported " + right + " flights.");
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
     * export flights to csv file
     *
     * @param options command options
     */
    public void exportFlight(String[] options) {
        if (options.length < 3) {
            throw new RuntimeException("not enough arguments");
        }

        String path = options[2];

        try {
            // create buffer write, use path
            BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));

            int right = 0;
            // write file
            for (Map.Entry<Integer, Flight> integerFlightEntry : Data.flights.entrySet()) {
                Flight flight = integerFlightEntry.getValue();
                // format style
                String line = flight.getTime() + "," + flight.getSource() + "," + flight.getDestination() + "," + flight.getCapacity() + "," + flight.getBooked();
                writer.write(line);
                writer.newLine();
            }
            writer.close();
            System.out.println("Exported " + Data.flights.size() + " flight.");
        } catch (IOException e) {
            // error process
            throw new RuntimeException("Error writing file.");
        }
    }
}

