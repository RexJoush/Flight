package service;

/**
 * @author Rex Joush
 * @time 2021.04.21
 */

import com.Flight;
import database.Data;
import entity.TravelDefinition;
import util.CommonUtils;
import util.TimeUtils;

import java.util.*;

/**
 * travel service interface
 */
public class TravelService {


    /**
     * according to source and destination find the all probably path
     * @param source starting location
     * @param destination ending location
     * @return the path flight list
     */
    public List<List<Flight>> getPath(String source, String destination) {

        List<List<Flight>> answer = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        // find source -> destination
        for (Map.Entry<Integer, Flight> entry : Data.flights.entrySet()) {
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
        for (Map.Entry<Integer, Flight> entry : Data.flights.entrySet()) {

            if (!visited.contains(entry.getKey()) && entry.getValue().getSource().equals(source)){
                for (Map.Entry<Integer, Flight> entry1 : Data.flights.entrySet()) {
                    // the location is right
                    if (!visited.contains(entry1.getKey()) && entry1.getValue().getSource().equals(entry.getValue().getDestination()) && entry1.getValue().getDestination().equals(destination)){
                        // check time
                        if (TimeUtils.getTimeDifferenceByTimeString(entry.getValue().getArrivedTime(), entry1.getValue().getTime()) < 0) {
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
        for (Map.Entry<Integer, Flight> entry : Data.flights.entrySet()) {

            // beijing tu shanghai
            if (!visited.contains(entry.getKey()) && entry.getValue().getSource().equals(source)){

                for (Map.Entry<Integer, Flight> entry1 : Data.flights.entrySet()) {
                    // the location is right
                    if (!visited.contains(entry1.getKey()) && entry1.getValue().getSource().equals(entry.getValue().getDestination())){
                        // check time
                        if (TimeUtils.getTimeDifferenceByTimeString(entry.getValue().getArrivedTime(), entry1.getValue().getTime()) < 0) {

                            for (Map.Entry<Integer, Flight> entry2 : Data.flights.entrySet()) {
                                if (!visited.contains(entry2.getKey()) && entry2.getValue().getSource().equals(entry1.getValue().getDestination()) && entry2.getValue().getDestination().equals(destination)){
                                    if (TimeUtils.getTimeDifferenceByTimeString(entry1.getValue().getArrivedTime(), entry2.getValue().getTime()) < 0){
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

        for (Map.Entry<Integer, Flight> entry : Data.flights.entrySet()) {
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

        for (Map.Entry<Integer, Flight> entry : Data.flights.entrySet()) {
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
    public void printResult(List<TravelDefinition> ways) {
        for (TravelDefinition way : ways) {
            System.out.printf("%-18s%d\n", "Legs:", way.getFlights().size());
            System.out.printf("%-18s%dh %dm\n", "Total Duration:", way.getDuration() / 60, way.getDuration() % 60);
            System.out.printf("%-18s$%s\n", "Total Cost:", CommonUtils.doubleFormat.format(way.getCost()));
            System.out.println("" +
                    "-------------------------------------------------------------\n" +
                    "ID   Cost      Departure   Arrival     Source --> Destination\n" +
                    "-------------------------------------------------------------");
            List<Flight> flights = way.getFlights();

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

    public void printFlight(Flight flight){
        System.out.printf("%4s $%7s %s   %s   %s --> %s\n", flight.getId(), CommonUtils.doubleFormat.format(flight.getTicketPrice()),TimeUtils.getPrintTime(flight.getTime()), TimeUtils.getPrintTime(flight.getArrivedTime()), flight.getSource(), flight.getDestination());
    }

    public void printLayover(Flight flight1, Flight flight2) {
        int timeDifference = Math.abs(TimeUtils.getTimeDifferenceByTimeString(flight1.getArrivedTime(), flight2.getTime()));
        System.out.printf("LAYOVER %sh %sm at %s\n", timeDifference / 60, timeDifference % 60, flight1.getDestination());
    }
}
