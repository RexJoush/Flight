package command;

/**
 * @author Rex Joush
 * @time 2021.04.20
 */

import service.LocationService;

/**
 * process command about location
 *      LOCATIONS
 *      LOCATION ADD <name> <lat> <long> <demand_coefficient>
 *      LOCATION <name>
 *      LOCATION IMPORT/EXPORT <filename>
 *      SCHEDULE <location_name>
 *      DEPARTURES <location_name>
 *      ARRIVALS <location_name>
 */
public class LocationCommand {

    public static LocationService locationService = new LocationService();

    public static void locationCommand(String command){

        // list all available locations in alphabetical order
        if ("locations".equalsIgnoreCase(command)){
            locationService.getAllLocations();
            return;
        }
        String[] options = command.split(" ");
        if ("schedule".equalsIgnoreCase(options[0])){
            locationService.schedule(options);
        }
        else if ("departures".equalsIgnoreCase(options[0])){
            locationService.departures(options);
        }
        else if ("arrivals".equalsIgnoreCase(options[0])) {
            locationService.arrivals(options);
        }
        else {
            if (options.length < 3){
                if ("location".equals(options[1])){
                    locationService.getLocationByName(options[1]);
                } else {
                    throw new RuntimeException("Invalid command. Type 'help' for a list of commands");
                }
            } else {
                switch (options[1].toLowerCase()) {
                    // add location
                    case "add" :
                        locationService.addLocation(options);
                        break;
                    // import csv file
                    case "import" :
                        locationService.importLocation(options);
                        break;
                    // export csv file
                    case "export" :
                        locationService.exportLocation(options);
                        break;
                    default:
                        break;
                }
            }
        }

    }


}
