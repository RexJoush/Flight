package com;

import java.util.Scanner;

public class FlightScheduler {

    private static FlightScheduler instance;
    private final Scanner scanner = new Scanner(System.in);

    private final LocationCommand locationService = new LocationCommand();
    private final FlightCommand flightService = new FlightCommand();

    public static void main(String[] args) {
        instance = new FlightScheduler(args);
        instance.run();
    }

    public static FlightScheduler getInstance() {
        return instance;
    }

    public FlightScheduler(String[] args) {}

    public void run() {
        // Do not use System.exit() anywhere in your code,
        // otherwise it will also exit the auto test suite.
        // Also, do not use static attributes otherwise
        // they will maintain the same values between testcases.

        // START YOUR CODE HERE

        boolean exit = false;

        while (!exit) {

            // get command
            System.out.print("User: ");
            String s = scanner.nextLine();
            String[] s1 = s.split(" ");
            try {
                switch (s1[0].toLowerCase()){
                    case "flight" :
                        flightService.flightCommand(s);
                        break;
                    case "flights" :
                        flightService.flightCommand("flights");
                        break;
                    case "departures":
                    case "arrivals":
                    case "schedule":
                    case "location":
                        locationService.locationCommand(s);
                        break;
                    case "locations":
                        locationService.locationCommand("locations");
                        break;
                    case "travel":
                        TravelCommand.travelCommand(s);
                        break;
                    case "exit":
                        System.out.println("Application closed.");
                        exit = true;
                        break;
                    case "help":
                        System.out.println(
                            "FLIGHTS                                            - list all available flights ordered by departure time, then departure location name\n" +
                            "FLIGHT ADD <departure time> <from> <to> <capacity> - add a flight\n" +
                            "FLIGHT IMPORT/EXPORT <filename>                    - import/export flights to csv file\n" +
                            "FLIGHT <id>                                        - view information about a flight (from->to, departure arrival times, current ticket price, capacity, passengers booked)\n" +
                            "FLIGHT <id> BOOK <num>                             - book a certain number of passengers for the flight at the current ticket price, and then adjust the ticket price to reflect the reduced capacity remaining. " +
                            "                                                     If no number is given, book 1 passenger. If the given number of bookings is more than the remaining capacity, only accept bookings\n" +
                            "                                                     until the capacity is full.\n" +
                            "FLIGHT <id> REMOVE                                 - remove a flight from the schedule\n" +
                            "FLIGHT <id> RESET                                  - reset the number of passengers booked to 0, and the ticket price to its original state.\n" +
                            "\n" +
                            "LOCATIONS                                              - list all available locations in alphabetical order\n" +
                            "LOCATION ADD <name> <lat> <long> <demand_coefficient>  - add a location\n" +
                            "LOCATION <name>                                        - view details about a location (it’s name, coordinates, demand coefficient)\n" +
                            "LOCATION IMPORT/EXPORT <filename>                      - import/export locations to csv file\n" +
                            "\n" +
                            "SCHEDULE <location_name>                               - list all departing and arriving flights, in order of the time they arrive/depart\n" +
                            "DEPARTURES <location_name>                             - list all departing flights, in order of departure time\n" +
                            "ARRIVALS <location_name>                               - list all arriving flights, in order of arrival time\n" +
                            "TRAVEL <from> <to> [sort] [n]                          - list the nth possible flight route between a starting location and destination, with a maximum of 3 stopovers. Default ordering is for shortest overall duration. If n is not\n" +
                            "                                                         provided, display the first one in the order. If n is larger than the number of flights available, display the\n" +
                            "                                                         last one in the ordering.\n" +

                            "\n" +
                            "can have other orderings:\n" +
                            "TRAVEL <from> <to> cost            - minimum current cost\n" +
                            "TRAVEL <from> <to> duration        - minimum total duration\n" +
                            "TRAVEL <from> <to> stopovers       - minimum stopovers\n" +
                            "TRAVEL <from> <to> layover         - minimum layover time\n" +
                            "TRAVEL <from> <to> flight_time     - minimum flight time\n" +
                            "HELP                               – outputs this help string.\n" +
                            "EXIT                               – end the program.");
                        break;
                    default:
                        throw new RuntimeException("Invalid command. Type 'help' for a list of commands");
                }
            } catch (RuntimeException e){
                System.out.println(e.getMessage());
            }

        }
    }
}
