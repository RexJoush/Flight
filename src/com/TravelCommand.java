package com;

/**
 * @author Rex Joush
 * @time 2021.04.20
 */

import java.util.*;

/**
 * process command about location
 * TRAVEL <from> <to> [sort] [n]
 * TRAVEL <from> <to> duration      sortType = 0
 * TRAVEL <from> <to> cost          sortType = 1
 * TRAVEL <from> <to> stopovers     sortType = 2
 * TRAVEL <from> <to> layover       sortType = 3
 * TRAVEL <from> <to> flight_time   sortType = 4
 */
public class TravelCommand {

    public static void travelCommand(String command) {
        String[] options = command.split(" ");

        int sortType = 0;

        if (options.length < 3) {
            throw new RuntimeException("" +
                    "Usage: TRAVEL <from> <to> [cost/duration/stopovers/layover/flight_time]");
        }

        // sorted
        if (options.length == 4){
            // sorted by arguments
            switch (options[3].toLowerCase()) {
                case "duration":
                    sortType = 0;
                case "cost":
                    sortType = 1;
                    break;
                case "stopovers":
                    sortType = 2;
                    break;
                case "layover":
                    sortType = 3;
                    break;
                case "flight_time":
                    sortType = 4;
                    break;
                default:
                    throw new RuntimeException("Invalid sorting property: must be either cost, duration, stopovers, layover, or flight_time.");
            }
        }


        // source is not in database
        if (!FlightScheduler.locations.containsKey(options[1])) {
            throw new RuntimeException("Starting location not found.");
        }

        // destination is not in database
        if (!FlightScheduler.locations.containsKey(options[2])) {
            throw new RuntimeException("Ending location not found.");
        }

        List<List<Flight>> path = getPath(options[1], options[2]);

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
        switch (sortType) {
            case 0:
                ways.sort(Comparator.comparingInt(TravelDefinition::getDuration));
            case 1:
                ways.sort((o1, o2) -> (int) (o1.getCost() - o2.getCost()));
                break;
            case 2:
                ways.sort(Comparator.comparingInt(TravelDefinition::getStopovers));
                break;
            case 3:
                ways.sort(Comparator.comparingInt(TravelDefinition::getLayoverTime));
                break;
            case 4:
                ways.sort(Comparator.comparingInt(TravelDefinition::getFlightTime));
                break;
            default:
                throw new RuntimeException("Invalid sorting property: must be either cost, duration, stopovers, layover, or flight_time.");
        }

        // find n
        if (options.length == 5){
            printResult(ways, Integer.parseInt(options[4]));
        }
        else {
            printResult(ways, 1);
        }


    }

    /**
     * according to source and destination find the all probably path
     * @param source starting location
     * @param destination ending location
     * @return the path flight list
     */
    public static List<List<Flight>> getPath(String source, String destination) {

        List<List<Flight>> answer = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        // find source -> destination
        for (Map.Entry<Integer, Flight> entry : FlightScheduler.flights.entrySet()) {
            if (entry.getValue().getSource().equals(source) && entry.getValue().getDestination().equals(destination)){
                List<Flight> path = new ArrayList<>();
                path.add(entry.getValue());
                visited.add(entry.getKey());
                answer.add(path);
            }
        }

        /*
            find source -> x -> destination
         */

        // get all x
        for (Map.Entry<Integer, Flight> entry : FlightScheduler.flights.entrySet()) {

            if (!visited.contains(entry.getKey()) && entry.getValue().getSource().equals(source)){
                for (Map.Entry<Integer, Flight> entry1 : FlightScheduler.flights.entrySet()) {
                    // the location is right
                    if (!visited.contains(entry1.getKey()) && entry1.getValue().getSource().equals(entry.getValue().getDestination()) && entry1.getValue().getDestination().equals(destination)){
                        // check time
                        if (Utils.getTimeDifferenceByTimeString(entry.getValue().getArrivedTime(), entry1.getValue().getTime()) < 0) {
                            List<Flight> path = new ArrayList<>();
                            path.add(entry.getValue());
                            path.add(entry1.getValue());
                            answer.add(path);
                        }
                    }
                }
            }
        }


        /*
            find source -> x -> y -> destination
         */
        for (Map.Entry<Integer, Flight> entry : FlightScheduler.flights.entrySet()) {

            // beijing tu shanghai
            if (!visited.contains(entry.getKey()) && entry.getValue().getSource().equals(source)){

                for (Map.Entry<Integer, Flight> entry1 : FlightScheduler.flights.entrySet()) {
                    // the location is right
                    if (!visited.contains(entry1.getKey()) && entry1.getValue().getSource().equals(entry.getValue().getDestination())){
                        // check time
                        if (Utils.getTimeDifferenceByTimeString(entry.getValue().getArrivedTime(), entry1.getValue().getTime()) < 0) {

                            for (Map.Entry<Integer, Flight> entry2 : FlightScheduler.flights.entrySet()) {
                                if (!visited.contains(entry2.getKey()) && entry2.getValue().getSource().equals(entry1.getValue().getDestination()) && entry2.getValue().getDestination().equals(destination)){
                                    if (Utils.getTimeDifferenceByTimeString(entry1.getValue().getArrivedTime(), entry2.getValue().getTime()) < 0){
                                        List<Flight> path = new ArrayList<>();
                                        path.add(entry.getValue());
                                        path.add(entry1.getValue());
                                        path.add(entry2.getValue());
                                        visited.add(entry.getKey());
                                        visited.add(entry1.getKey());
                                        visited.add(entry2.getKey());
                                        answer.add(path);
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }

        return answer;
    }

    // get flight graph
    public Map<String, Integer> graph = getInitGraph();

    // visited point list
    public Set<Flight> visited = new HashSet<>();

    // probably path
    public List<Flight> path = new ArrayList<>();


    /**
     * find path core algorithm
     * @param flight current visit flight
     */
    public void dfs(Flight flight){
        // add visited flight
        visited.add(flight);

        for (Map.Entry<Integer, Flight> entry : FlightScheduler.flights.entrySet()) {
            if (entry.getValue().getSource().equals(flight.getSource())){
                if (entry.getValue().getDestination().equals(flight.getDestination())){
                    path.add(entry.getValue());
                    continue;
                }
                if (!visited.contains(entry.getValue())){
                    dfs(flight);
                }
            }
        }

        visited.remove(flight);
    }



    /**
     * init flight graph
     *
     * if beijing --> shanghai has a flight
     * then graph will have an item (beijing,shanghai) -> <flight_id>
     * @return graph
     */
    public Map<String, Integer> getInitGraph() {
        Map<String, Integer> graph = new HashMap<>();

        for (Map.Entry<Integer, Flight> entry : FlightScheduler.flights.entrySet()) {
            String source = entry.getValue().getSource();
            String destination = entry.getValue().getDestination();
            Integer id = entry.getKey();
            graph.put(source + "," + destination, id);
        }

        return graph;
    }


    /**
     * print the result
     * @param ways way list
     */
    public static void printResult(List<TravelDefinition> ways, int n) {

        int low = 0;
        if (n > ways.size()){
            low = ways.size() - 1;
        }

        for (int i = low; i < n; i++) {
            System.out.printf("%-18s%d\n", "Legs:", ways.get(i).getFlights().size());
            System.out.printf("%-18s%dh %dm\n", "Total Duration:", ways.get(i).getDuration() / 60, ways.get(i).getDuration() % 60);
            System.out.printf("%-18s$%s\n", "Total Cost:", Utils.doubleFormat.format(ways.get(i).getCost()));
            System.out.println("" +
                    "-------------------------------------------------------------\n" +
                    "ID   Cost      Departure   Arrival     Source --> Destination\n" +
                    "-------------------------------------------------------------");
            List<Flight> flights = ways.get(i).getFlights();

            printFlight(flights.get(0));

            if (flights.size() == 2){
                printLayover(flights.get(0), flights.get(1));
                printFlight(flights.get(1));
            }
            if (flights.size() == 3){
                printLayover(flights.get(0), flights.get(1));
                printFlight(flights.get(1));
                printLayover(flights.get(1), flights.get(2));
                printFlight(flights.get(2));
            }
            System.out.println();
        }
    }

    public static void printFlight(Flight flight){
        System.out.printf("%4s $%7s %s   %s   %s --> %s\n", flight.getId(), Utils.doubleFormat.format(flight.getTicketPrice()), Utils.getPrintTime(flight.getTime()), Utils.getPrintTime(flight.getArrivedTime()), flight.getSource(), flight.getDestination());
    }

    public static void printLayover(Flight flight1, Flight flight2) {
        int timeDifference = Math.abs(Utils.getTimeDifferenceByTimeString(flight1.getArrivedTime(), flight2.getTime()));
        System.out.printf("LAYOVER %sh %sm at %s\n", timeDifference / 60, timeDifference % 60, flight1.getDestination());
    }
}
