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

    public void flightCommand(String command) {
        // list all available flights ordered by departure time, then departure location name
        if ("flights".equals(command)) {
            getAllFlights();
            return;
        }
        String[] options = command.split(" ");
        if (options.length < 2) {
            throw new RuntimeException("Usage:\n" +
                    "FLIGHT <id> [BOOK/REMOVE/RESET] [num]\n" +
                    "FLIGHT ADD <departure time> <from> <to> <capacity>\n" +
                    "FLIGHT IMPORT/EXPORT <filename>");
        }
        boolean flag = true;
        switch (options[1].toLowerCase()) {
            // add flight
            case "add":
                flag = false;
                addFlight(options);
                break;
            // import csv file
            case "import":
                flag = false;
                importFlights(options);
                break;
            // export csv file
            case "export":
                flag = false;
                exportFlight(options);
                break;
            default:
                break;
        }

        // fix bug
        if (flag) {
            if (options.length <= 2) {
                getFlightById(options);
            } else {
                switch (options[2].toLowerCase()) {
                    // book a certain number of passengers for the flight at the current ticket price
                    case "book":
                        book(options);
                        break;
                    // remove flight
                    case "remove":
                        remove(options);
                        break;
                    // reset flight
                    case "reset":
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

        System.out.println("Flights");
        System.out.println("-------------------------------------------------------");
        System.out.println("ID   Departure   Arrival     Source --> Destination");
        System.out.println("-------------------------------------------------------");

        if (FlightScheduler.flights.size() == 0) {
            System.out.println("(None)");
            System.out.println();
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
                    Utils.getPrintTime(Utils.captureName(flight.getTime())),
                    Utils.getPrintTime(Utils.captureName(flight.getArrivedTime())),
                    Utils.captureName(flight.getSource()), flight.getDestination());
        }

    }

    /**
     * add flight
     *
     * @param options add command options
     */
    public void addFlight(String[] options) {

        // check parameter number
        if (options.length < 7) {
            throw new RuntimeException("Usage:   FLIGHT ADD <departure time> <from> <to> <capacity>\n" +
                    "Example: FLIGHT ADD Monday 18:00 Sydney Melbourne 120");
        }

        // check source location
        String source = options[4];
        if (!FlightScheduler.locations.containsKey(source.toLowerCase())) {
            throw new RuntimeException("Invalid starting location.");
        }

        // check destination location
        String destination = options[5];
        if (!FlightScheduler.locations.containsKey(destination.toLowerCase())) {
            throw new RuntimeException("Invalid ending location.");
        }

        // source and destination not allowed to be the same
        if (source.equalsIgnoreCase(destination)) {
            throw new RuntimeException("Source and destination cannot be the same place.");
        }

        int id = FlightScheduler.flights.size();

        String hour_minute = "";
        if (options[3].length() < 5) {
            hour_minute = "0" + options[3];
        } else {
            hour_minute = options[3];
        }

        String time = options[2].toLowerCase() + " " + hour_minute;
        Utils.TimeFormat.format(options[3].split(":")[0]);
        Utils.TimeFormat.format(options[3].split(":")[1]);

        // check time format
        if (!Utils.p.matcher(time.toLowerCase()).matches()) {
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
        System.out.printf("%-14s%s %s\n", "Departure: ", Utils.getPrintTime(Utils.captureName(flight.getTime())), flight.getSource());
        System.out.printf("%-14s%s %s\n", "Arrival: ", Utils.getPrintTime(flight.getArrivedTime()), flight.getDestination());
        System.out.printf("%-14s%,dkm\n", "Distance: ", Math.round(flight.getDistance()));
        System.out.printf("%-14s%dh %dm\n", "Duration: ", flight.getDuration() / 60, flight.getDuration() % 60);
        System.out.printf("%-14s%s\n", "Ticket Cost: ", "$" + Utils.doubleFormat.format(flight.getTicketPrice()));
        System.out.printf("%-14s%d/%d\n", "Passengers: ", flight.getBooked(), flight.getCapacity());

    }

    public void importFlights(String[] command) {
        try {
            if (command.length < 3) throw new FileNotFoundException();
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

                int status = addFlight(dparts[0], dparts[1], lparts[1], lparts[2], lparts[3], booked);
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

    /**
     * Add a flight to the database
     * handle error cases and return status negative if error
     * (different status codes for different messages)
     * do not print out anything in this function
     *
     * @param date1    week
     * @param date2    time
     * @param start    resource location
     * @param end      destination location
     * @param capacity pass
     * @param booked   check
     * @return add status
     */
    public int addFlight(String date1, String date2, String start, String end, String capacity, int booked) {
        Flight flight = new Flight();
        int id = FlightScheduler.flights.size();
        try {
            flight.setId(id);
            flight.setTime(date1 + " " + date2);
            flight.setSource(start);
            flight.setDestination(end);
            flight.setCapacity(Integer.parseInt(capacity));
            flight.setBooked(booked);
            FlightScheduler.flights.put(id, flight);
        } catch (Exception e) {
            return -1;
        }
        return 1;
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
                    if (FlightScheduler.locations.containsKey(split[1].toLowerCase()) && FlightScheduler.locations.containsKey(split[2].toLowerCase())) {
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

            if (right == 1) {
                System.out.println("Imported " + right + " flight.");
            }
            if (right != 1) {
                System.out.println("Imported " + right + " flights.");
            }
            if (error == 1) {
                System.out.println(error + " line was invalid.");
            }
            if (error > 1) {
                System.out.println(error + " lines were invalid.");
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
}