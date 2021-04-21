package command;

/**
 * @author Rex Joush
 * @time 2021.04.20
 */

import service.FlightService;

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

    public static FlightService flightService = new FlightService();

    public static void flightCommand(String command){
        // list all available flights ordered by departure time, then departure location name
        if ("flights".equalsIgnoreCase(command)){
            flightService.getAllFlight();
            return;
        }
        String[] options = command.split(" ");
        boolean flag = true;
        switch (options[1].toLowerCase()) {
            // add flight
            case "add" :
                flag = false;
                flightService.addFlight(options);
                break;
            // import csv file
            case "import" :
                flag = false;
                flightService.importFlight(options);
                break;
            // export csv file
            case "export" :
                flag = false;
                flightService.exportFlight(options);
                break;
            default:
                break;
        }

        // fix bug
        if (flag){
            if (options.length <= 2){
                flightService.getFlightById(options);
            } else {
                switch (options[2].toLowerCase()){
                    // book a certain number of passengers for the flight at the current ticket price
                    case "book" :
                        flightService.book(options);
                        break;
                    // remove flight
                    case "remove" :
                        flightService.remove(options);
                        break;
                    // reset flight
                    case "reset" :
                        flightService.reset(options);
                        break;
                    // find flight by id
                    default:

                }
            }

        }
    }
}
