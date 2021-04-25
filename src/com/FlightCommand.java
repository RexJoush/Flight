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
 * FLIGHTS
 * FLIGHT ADD <departure time> <from> <to> <capacity>
 * FLIGHT IMPORT/EXPORT <filename>
 * FLIGHT <id>
 * FLIGHT <id> BOOK <num>
 * FLIGHT <id> REMOVE
 * FLIGHT <id> RESET
 */
public class FlightCommand {

    /**
     * FLIGHT ADD <departure time> <from> <to> <capacity>
     * FLIGHT IMPORT/EXPORT <filename>
     * FLIGHT <id>
     * FLIGHT <id> BOOK <num>
     * FLIGHT <id> REMOVE
     * FLIGHT <id> RESET
     *
     * @param options command line
     */
    public void flightCommand(String[] options) {

        try {
            switch (options.length) {
                case 0:
                    throw new RuntimeException("Usage:\n" +
                            "FLIGHT <id> [BOOK/REMOVE/RESET] [num]\n" +
                            "FLIGHT ADD <departure time> <from> <to> <capacity>\n" +
                            "FLIGHT IMPORT/EXPORT <filename>");
                case 1:
                    throw new RuntimeException("Usage:   FLIGHT ADD <departure time> <from> <to> <capacity>\n" +
                            "Example: FLIGHT ADD Monday 18:00 Sydney Melbourne 120");
                    // flight id
                case 2:
                    getFlightById(options[1]);
                    return;
                /*
                    flight id remove
                    flight id reset
                    flight import file.csv
                    flight export file.csv
                 */
                case 3:
                    switch (options[1].toLowerCase()){
                        case "import":
                            importFlights(options);
                            return;
                        case "export":
                            exportFlight(options[2]);
                            return;
                        default:
                            break;
                    }
                    switch (options[2].toLowerCase()) {
                        case "remove":
                            remove(options[1]);
                            return;
                        case "reset":
                            reset(options[1]);
                            return;
                        case "book":
                            book(options[1], 1);
                            return;
                        default:
                            throw new RuntimeException("Invalid command. Type 'help' for a list of commands");
                    }

                    // flight id book num
                case 4:
                    if ("book".equalsIgnoreCase(options[2])) {
                        if (!Utils.isNumeric(options[3])) {
                            throw new RuntimeException("Invalid number of passengers to book.");
                        }
                        book(options[1], Integer.parseInt(options[3]));
                        return;
                    } else {
                        throw new RuntimeException("Invalid command. Type 'help' for a list of commands");
                    }
                case 5:
                case 6:
                    throw new RuntimeException("Usage:   FLIGHT ADD <departure time> <from> <to> <capacity>\n" +
                            "Example: FLIGHT ADD Monday 18:00 Sydney Melbourne 120");
                    // FLIGHT ADD <departure time> <from> <to> <capacity>
                case 7:
                    addFlight(options[2], options[3], options[4], options[5], options[6], 0);
                    return;
                default:
                    throw new RuntimeException("Invalid command. Type 'help' for a list of commands");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * list all available flights ordered by departure time, then departure location name
     */
    public void getAllFlights() {

        System.out.println("Flights");
        System.out.println("-------------------------------------------------------");
        System.out.println("ID   Departure   Arrival     Source --> Destination");
        System.out.println("-------------------------------------------------------");

        if (FlightScheduler.flights.size() == 0) {
            System.out.println("(None)");
            return;
        }

        List<Flight> flights = new ArrayList<>();
        for (Map.Entry<Integer, Flight> entry : FlightScheduler.flights.entrySet()) {
            flights.add(entry.getValue());
        }
        flights.sort(new Comparator<>() {
            @Override
            public int compare(Flight f1, Flight f2) {
                // asc by week
                String week1 = f1.getWeek();
                String week2 = f2.getWeek();

                int result = Week.valueOf(week1).getIndex() - Week.valueOf(week2).getIndex();

                // if week same
                if (result == 0) {
                    result = f1.getTime().compareTo(f2.getTime());
                }
                return result;
            }
        });

        for (Flight flight : flights) {
            System.out.printf("%4s %-9s   %-9s   %s --> %s\n", flight.getId(),
                    Utils.getPrintTime(flight.getWeek() + " " + flight.getTime()),
                    Utils.getPrintTime(Utils.captureName(flight.getArrivedTime())),
                    Utils.captureName(flight.getSource()), flight.getDestination());
        }

    }

    /**
     * Add a flight to the database
     * handle error cases and return status negative if error
     * (different status codes for different messages)
     * do not print out anything in this function
     *
     * @param week        week
     * @param time        time
     * @param source      resource location
     * @param destination destination location
     * @param capacity    pass
     * @param booked      check
     * @return add status
     */
    public void addFlight(String week, String time, String source, String destination, String capacity, int booked) {

        // check start
        if (!FlightScheduler.locations.containsKey(source.toLowerCase())) {
            throw new RuntimeException("Invalid starting location.");
        }

        // check destination
        if (!FlightScheduler.locations.containsKey(destination.toLowerCase())) {
            throw new RuntimeException("Invalid ending location.");
        }

        // check source and destination is the same
        if (source.equalsIgnoreCase(destination)) {
            throw new RuntimeException("Source and destination cannot be the same place.");
        }

        // check time
        if (!(Week.contains(week) && Utils.isTime(time))) {
            throw new RuntimeException("Invalid departure time. Use the format <day_of_week> <hour:minute>, with 24h time.");
        }

        if (time.length() < 5) {
            time = "0" + time;
        }

        // check capacity
        if (!Utils.isNumeric(capacity)) {
            throw new RuntimeException("Invalid positive integer capacity.");
        }

        // check runways
        checkRunways(week + " " + time, source);


        // add flight
        Flight flight = new Flight();
        int id = FlightScheduler.flights.size();

        flight.setId(id);
        flight.setWeek(Utils.captureName(week));
        flight.setTime(time);
        flight.setSource(source);
        flight.setDestination(destination);
        flight.setCapacity(Integer.parseInt(capacity));
        flight.setBooked(booked);
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
            if (Math.abs(Utils.getTimeDifferenceByTimeString(flight.getWeek() + " " + flight.getTime(), time)) <= 60 && source.equals(flight.getSource())) {
                throw new RuntimeException("Scheduling conflict! This flight clashes with Flight " + flight.getId() + " departing from " + flight.getSource() + " on " + Utils.captureName(flight.getArrivedTime()) + ".");
            }
            // if arrived time is same and time difference less than 60, false
            if (Math.abs(Utils.getTimeDifferenceByTimeString(flight.getArrivedTime(), time)) <= 60 && source.equals(flight.getDestination())) {
                throw new RuntimeException("Scheduling conflict! This flight clashes with Flight " + flight.getId() + " arriving at " + flight.getDestination() + " on " + Utils.captureName(flight.getArrivedTime()) + ".");
            }
        }
    }

    /**
     * book a certain number of passengers for the flight at the current ticket price,
     * and then adjust the ticket price to reflect the reduced capacity remaining. If no number is given, book 1
     * passenger. If the given number of bookings is more than the remaining capacity, only accept bookings
     * until the capacity is full.
     *
     * @param id     flight id
     * @param booked booked number
     */
    public void book(String id, int booked) {

        Flight flight = checkId(id);

        // record the booked before new book
        int bookedBefore = flight.getBooked();

        // calculate the total cost
        double cost = flight.book(booked);

        // get after booked number
        int bookedAfter = flight.getBooked();

        if (bookedAfter - bookedBefore == 0){
            System.out.println("Booked 0 passengers on flight 0 for a total cost of $0.00");
        } else {
            System.out.println("Booked " + (bookedAfter - bookedBefore) + " passengers on flight 0 for a total cost of $" + Utils.doubleFormat.format(cost));
        }

        // if the flight is full
        if (bookedAfter == flight.getCapacity()) {
            System.out.println("Flight is now full.");
        }

    }

    /**
     * remove a flight from the schedule
     *
     * @param idString id
     */
    public void remove(String idString) {

        Flight flight = checkId(idString);

        FlightScheduler.flights.remove(flight.getId());
        // Removed Flight 0, Mon 08:00 Mumbai --> NewDelhi, from the flight schedule.
        System.out.println("Removed Flight " + flight.getId() + ", " + flight.getWeek().substring(0, 3) + " " + flight.getTime() + " " + flight.getSource() + " --> " + flight.getDestination() + ", from the flight schedule.");
    }

    /**
     * reset the number of passengers booked to 0, and the ticket price to its original state.
     *
     * @param idString id
     */
    public void reset(String idString) {

        Flight flight = checkId(idString);
        flight.setBooked(0);
        FlightScheduler.flights.put(flight.getId(), flight);

        // Reset passengers booked to 0 for Flight 0, Mon 08:00 Mumbai --> NewDelhi.
        System.out.println("Reset passengers booked to 0 for Flight " + flight.getId() + ", " + flight.getWeek().substring(0, 3) + " " + flight.getTime() + " " + flight.getSource() + " --> " + flight.getDestination() + ".");

    }

    /**
     * view information about a flight (from->to, departure arrival times, current ticket price,
     * capacity, passengers booked)
     *
     * @param idString flight id
     */
    public void getFlightById(String idString) {

        Flight flight = checkId(idString);

        System.out.printf("Flight %d\n", flight.getId());
        System.out.printf("%-14s%s %s %s\n", "Departure: ", flight.getWeek().substring(0, 3), flight.getTime(), flight.getSource());
        System.out.printf("%-14s%s %s\n", "Arrival: ", Utils.getPrintTime(flight.getArrivedTime()), flight.getDestination());
        System.out.printf("%-14s%,dkm\n", "Distance: ", Math.round(flight.getDistance()));
        System.out.printf("%-14s%dh %dm\n", "Duration: ", flight.getDuration() / 60, flight.getDuration() % 60);
        System.out.printf("%-14s%s\n", "Ticket Cost: ", "$" + Utils.doubleFormat.format(flight.getTicketPrice()));
        System.out.printf("%-14s%d/%d\n", "Passengers: ", flight.getBooked(), flight.getCapacity());

    }

    /**
     * import flights from csv file
     *
     * @param command command options
     */
    public void importFlights(String[] command) {
        try {
            if (command.length < 2) throw new FileNotFoundException();
            BufferedReader br = new BufferedReader(new FileReader(new File(command[2])));
            String line;
            int count = 0;
            int err = 0;

            while ((line = br.readLine()) != null) {
                String[] lparts = line.split(",");
                if (lparts.length < 5) continue;
                String[] dparts = lparts[0].split(" ");
                if (dparts.length < 2) continue;
                int booked = 0;

                try {
                    booked = Integer.parseInt(lparts[4]);

                } catch (NumberFormatException e) {
                    continue;
                }

                int status = addFlightByExport(dparts[0], dparts[1], lparts[1], lparts[2], lparts[3], booked);
                if (status < 0) {
                    err++;
                    continue;
                }
                count++;
            }
            br.close();
            System.out.println("Imported " + count + " flight" + (count != 1 ? "s" : "") + ".");
            if (err > 0) {
                if (err == 1) System.out.println("1 line was invalid.");
                else System.out.println(err + " lines were invalid.");
            }
        } catch (IOException e) {
            System.out.println("Error reading file.");
            return;
        }
    }

    private int addFlightByExport(String week, String time, String source, String destination, String capacity, int booked) {
        // check start
        if (!FlightScheduler.locations.containsKey(source.toLowerCase())) {
            return -1;
        }

        // check destination
        if (!FlightScheduler.locations.containsKey(destination.toLowerCase())) {
            return -1;
        }

        // check source and destination is the same
        if (source.equalsIgnoreCase(destination)) {
            return -1;
        }

        // check time
        if (!(Week.contains(week) && Utils.isTime(time))) {
            return -1;
        }

        if (time.length() < 5) {
            time = "0" + time;
        }

        // check capacity
        if (!Utils.isNumeric(capacity)) {
            return -1;
        }

        // check runways
        try {
            checkRunways(week + " " + time, source);
        } catch (Exception e){
            return -1;
        }


        // add flight
        Flight flight = new Flight();
        int id = FlightScheduler.flights.size();

        try {
            flight.setId(id);
            flight.setWeek(Utils.captureName(week));
            flight.setTime(time);
            flight.setSource(source);
            flight.setDestination(destination);
            flight.setCapacity(Integer.parseInt(capacity));
            flight.setBooked(booked);
        } catch (Exception e) {
            return -1;
        }
        FlightScheduler.flights.put(id, flight);
        return 1;
    }

    /**
     * export flights to csv file
     *
     * @param path command options
     */
    public void exportFlight(String path) {

        try {
            // create buffer write, use path
            BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));

            int right = 0;
            // write file
            for (Map.Entry<Integer, Flight> integerFlightEntry : FlightScheduler.flights.entrySet()) {
                Flight flight = integerFlightEntry.getValue();
                // format style
                String line = flight.getWeek() + " " + flight.getTime() + "," + flight.getSource() + "," + flight.getDestination() + "," + flight.getCapacity() + "," + flight.getBooked();
                writer.write(line);
                writer.newLine();
            }
            writer.close();
            if (FlightScheduler.flights.size() == 1) {
                System.out.println("Exported " + FlightScheduler.flights.size() + " flight.");
            } else {
                System.out.println("Exported " + FlightScheduler.flights.size() + " flights.");
            }

        } catch (IOException e) {
            // error process
            throw new RuntimeException("Error writing file.");
        }
    }

    /**
     * check id is exist or id is a number
     *
     * @param idString check str
     * @return if is id return id
     * else return flight
     */
    public Flight checkId(String idString) {

        // check id
        if (!Utils.isNumeric(idString)) {
            throw new RuntimeException("Invalid Flight ID.");
        }

        int id = Integer.parseInt(idString);
        Flight flight = FlightScheduler.flights.get(id);

        // check result
        if (flight == null) {
            throw new RuntimeException("Invalid Flight ID.");
        }
        return flight;
    }
}