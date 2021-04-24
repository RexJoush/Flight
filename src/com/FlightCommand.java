package com;

/**
 * @author Rex Joush
 * @time 2021.04.20
 */

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * process command about flight
 *      FLIGHTS
 *      FLIGHT ADD <departure time> <from> <to> <capacity>
 *      FLIGHT IMPORT/EXPORT <filename>
 *      FLIGHT <id>
 *      FLIGHT <id> BOOK <num>
 *      FLIGHT <id> REMOVE
 *      FLIGHT <id> RESET
 */
public class FlightCommand {

    public void flightCommand(String command){
        // list all available flights ordered by departure time, then departure location name
        if ("flights".equals(command)){
            getAllFlights();
            return;
        }
        String[] options = command.split(" ");
        boolean flag = true;
        switch (options[1].toLowerCase()) {
            // add flight
            case "add" :
                flag = false;
                addFlight(options);
                break;
            // import csv file
            case "import" :
                flag = false;
                importFlight(options);
                break;
            // export csv file
            case "export" :
                flag = false;
                exportFlight(options);
                break;
            default:
                break;
        }

        // fix bug
        if (flag){
            if (options.length <= 2){
                getFlightById(options);
            } else {
                switch (options[2].toLowerCase()){
                    // book a certain number of passengers for the flight at the current ticket price
                    case "book" :
                        book(options);
                        break;
                    // remove flight
                    case "remove" :
                        remove(options);
                        break;
                    // reset flight
                    case "reset" :
                        reset(options);
                        break;
                    // find flight by id
                    default:
                        throw new RuntimeException("Invalid command. Type 'help' for a list of commands");
                }
            }

        }
    }

    /**
     * list all available flights ordered by departure time, then departure location name
     */
    public void getAllFlights() {

        if (FlightScheduler.flights.size() == 0){
            System.out.println("(None)");
            return;
        }

        System.out.println("-------------------------------------------------------");
        System.out.println("ID   Departure   Arrival     Source --> Destination");
        System.out.println("-------------------------------------------------------");

        List<Flight> flights = new ArrayList<>();
        for (Map.Entry<Integer, Flight> entry : FlightScheduler.flights.entrySet()) {
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
                    Utils.getPrintTime(flight.getTime()),
                    Utils.getPrintTime(flight.getArrivedTime()),
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

        // check source location
        String source = options[4];
        if (!FlightScheduler.locations.containsKey(source)) {
            throw new RuntimeException("Invalid starting location.");
        }

        // check destination location
        String destination = options[5];
        if (!FlightScheduler.locations.containsKey(destination)) {
            throw new RuntimeException("Invalid ending location.");
        }

        // source and destination not allowed to be the same
        if (source.equals(destination)) {
            throw new RuntimeException("Source and destination cannot be the same place.");
        }

        int id = FlightScheduler.flights.size();
        String time = options[2] + " " + options[3];

        // check time format
        if (!Utils.p.matcher(time).matches()) {
            throw new RuntimeException("Invalid departure time. Use the format <day_of_week> <hour:minute>, with 24h time.");
        }

        // check runways
        checkRunways(time, options[4]);

        // check capacity
        int capacity = 0;
        try {
            capacity = Integer.parseInt(options[6]);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid positive integer capacity.");
        }

        Flight flight = new Flight(id, time, source, destination, capacity, 0);

        FlightScheduler.flights.put(id, flight);
        System.out.println("Successfully added Flight " + id + ".");

    }

    /**
     * check the runways is it occupied
     *
     * @param time   flight time, departing and arriving
     * @param source source location name
     */
    public void checkRunways(String time, String source) {
        List<Flight> flights = new ArrayList<>();

        for (Map.Entry<Integer, Flight> entry : FlightScheduler.flights.entrySet()) {
            flights.add(entry.getValue());
        }
        // flight add Tuesday 12:00 Beijing Dubai 120
        // 0 Tue 12:00   Tue 20:08   Beijing --> Dubai
        // flight add Tuesday 19:10 Dubai Beijing 120
        for (Flight flight : flights) {
            // if source is same and time difference less than 60, false
            if (Math.abs(Utils.getTimeDifferenceByTimeString(flight.getTime(), time)) <= 60 && source.equals(flight.getSource())) {
                throw new RuntimeException("Scheduling conflict! This flight clashes with Flight " + flight.getId() + " departing from " + flight.getSource() + " on " + flight.getArrivedTime() + ".");
            }
            // if arrived time is same and time difference less than 60, false
            if (Math.abs(Utils.getTimeDifferenceByTimeString(flight.getArrivedTime(), time)) <= 60 && source.equals(flight.getDestination())) {
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
        Flight flight = FlightScheduler.flights.get(id);

        // record the booked before new book
        int bookedBefore = flight.getBooked();

        // calculate the total cost
        double cost = flight.book(number);

        // get after booked number
        int bookedAfter = flight.getBooked();

        System.out.println("Booked " + (bookedAfter - bookedBefore) + " passengers on flight 0 for a total cost of $" + Utils.doubleFormat.format(cost));

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
        Flight flight = FlightScheduler.flights.get(id);
        FlightScheduler.flights.remove(Integer.parseInt(options[1]));
        // Removed Flight 0, Mon 08:00 Mumbai --> NewDelhi, from the flight schedule.
        System.out.println("Removed Flight " + id + ", " + Utils.getPrintTime(flight.getTime()) + " " + flight.getSource() + " --> " + flight.getDestination() + ", from the flight schedule");
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
        Flight flight = FlightScheduler.flights.get(id);
        flight.setBooked(0);
        FlightScheduler.flights.put(id, flight);

        // Reset passengers booked to 0 for Flight 0, Mon 08:00 Mumbai --> NewDelhi.
        System.out.println("Reset passengers booked to 0 for Flight " + id + ", " + Utils.getPrintTime(flight.getTime()) + " " + flight.getSource() + " --> " + flight.getDestination() + ".");

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
        Flight flight = FlightScheduler.flights.get(id);

        if (flight == null) {
            throw new RuntimeException("Invalid Flight ID.");
        }

        System.out.println("Flight " + id);
        System.out.printf("%-14s%s %s\n", "Departure: ", Utils.getPrintTime(flight.getTime()), flight.getSource());
        System.out.printf("%-14s%s %s\n", "Arrival: ", Utils.getPrintTime(flight.getArrivedTime()), flight.getDestination());
        System.out.printf("%-14s%,dkm\n", "Distance: ", Math.round(flight.getDistance()));
        System.out.printf("%-14s%dh %dm\n", "Duration: ", flight.getDuration() / 60, flight.getDuration() % 60);
        System.out.printf("%-14s%s\n", "Ticket Cost: ", "$" + Utils.doubleFormat.format(flight.getTicketPrice()));
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
                if (Utils.pTime.matcher(line).matches()) {
                    // add flight
                    String[] split = line.split(",");
                    Flight flight = new Flight(FlightScheduler.flights.size(), split[0], split[1], split[2], Integer.parseInt(split[3]), Integer.parseInt(split[4]));

                    // check location is in the database
                    if (FlightScheduler.locations.containsKey(split[1]) && FlightScheduler.locations.containsKey(split[2])) {
                        FlightScheduler.flights.put(FlightScheduler.flights.size(), flight);
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
            for (Map.Entry<Integer, Flight> integerFlightEntry : FlightScheduler.flights.entrySet()) {
                Flight flight = integerFlightEntry.getValue();
                // format style
                String line = flight.getTime() + "," + flight.getSource() + "," + flight.getDestination() + "," + flight.getCapacity() + "," + flight.getBooked();
                writer.write(line);
                writer.newLine();
            }
            writer.close();
            System.out.println("Exported " + FlightScheduler.flights.size() + " flight.");
        } catch (IOException e) {
            // error process
            throw new RuntimeException("Error writing file.");
        }
    }
}
