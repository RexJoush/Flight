package command;

/**
 * @author Rex Joush
 * @time 2021.04.20
 */

import com.Flight;
import database.Data;
import entity.TravelDefinition;
import service.TravelService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * process command about location
 * TRAVEL <from> <to> [sort] [n]
 * TRAVEL <from> <to> cost
 * TRAVEL <from> <to> stopovers
 * TRAVEL <from> <to> layover
 * TRAVEL <from> <to> flight_time
 */
public class TravelCommand {

    public static TravelService travelService = new TravelService();

    public static void travelCommand(String command) {
        String[] options = command.split(" ");

        if (options.length < 3) {
            throw new RuntimeException("not enough arguments");
        }

        // source is not in database
        if (!Data.locations.containsKey(options[1])) {
            throw new RuntimeException("Starting location not found.");
        }

        // destination is not in database
        if (!Data.locations.containsKey(options[2])) {
            throw new RuntimeException("Ending location not found.");
        }

        List<List<Flight>> path = travelService.getPath(options[1], options[2]);

        // if no way
        if (path.isEmpty()) {
            throw new RuntimeException("Sorry, no flights with 3 or less stopovers are available from " + options[1] + " to " + options[2] + ".");
        }

        // format ways
        List<TravelDefinition> ways = new ArrayList<>();
        for (List<Flight> flights : path) {
            TravelDefinition travelDefinition = new TravelDefinition(flights);
            ways.add(travelDefinition);
        }

        // sorted by arguments
        switch (options[3].toLowerCase()) {
            case "cost":
                ways.sort((o1, o2) -> (int) (o1.getCost() - o2.getCost()));
                break;
            case "stopovers":
                ways.sort(Comparator.comparingInt(TravelDefinition::getStopovers));
                break;
            case "layover":
                ways.sort(Comparator.comparingInt(TravelDefinition::getLayoverTime));
                break;
            case "flight_time":
                ways.sort(Comparator.comparingInt(TravelDefinition::getFlightTime));
                break;
            default:
                throw new RuntimeException("Invalid sorting property: must be either cost, duration, stopovers, layover, or flight_time.");
        }

        travelService.printResult(ways);

    }
}
