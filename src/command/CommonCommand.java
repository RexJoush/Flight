package command;

/**
 * @author Rex Joush
 * @time 2021.04.20
 */

/**
 * process command about common
 *      HELP
 *      EXIT
 */
public class CommonCommand {
    public static void commonCommand(String command){
        if ("help".equals(command.toLowerCase())) {
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
        } else if ("exit".equals(command.toLowerCase())) {
            System.out.println("Application closed.");
            System.exit(0);
        }
    }
}
