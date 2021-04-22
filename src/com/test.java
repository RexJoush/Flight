package com;

import database.Data;
import entity.Week;
import service.FlightService;
import service.LocationService;
import util.TimeUtils;

import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Rex Joush
 * @time 2021.04.20
 */

public class test {

    public static FlightService flightService = new FlightService();
    public static LocationService locationService = new LocationService();

    public static void main(String[] args) {
        locationService.importLocation(new String[]{"location", "import", "locations.csv"});
        flightService.importFlight(new String[]{"flight", "import", "flights6.csv"});
        int a = 0;
        switch (a){
            case 0:
                System.out.println(0);
                break;
            case 1:
                System.out.println(1);
            case 2:
                System.out.println(2);
            case 3:
                System.out.println(3);
        }

         //System.out.println(TimeUtils.getTimeDifferenceByTimeString("Monday 12:00", "Monday 11:00"));

//        System.out.println(new test().getAllPath2("Alta", "Oslo"));

    }

    // visited point list
    public static Set<String> visited = new HashSet<>();

    // probably path
    public static List<List<Flight>> answer = new ArrayList<>();

    public List<List<Flight>> getAllPath2(String source, String destination) {
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

        List<String> mid = new ArrayList<>();
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


    /**
     * find path core algorithm
     *
     * @param start current visit flight
     */
    public List<List<Flight>> getAllPath(String start, String destination) {

        Set<Integer> visited = new HashSet<>();
        List<List<Flight>> answer = new ArrayList<>();

        for (Map.Entry<Integer, Flight> entry : Data.flights.entrySet()) {
            // start is find
            if (entry.getValue().getSource().equals(start)) {

                List<Flight> path = new ArrayList<>();
                // if the flight destination is end, add path, continue
                if (!visited.contains(entry.getKey()) && entry.getValue().getDestination().equals(destination)) {
                    path.add(entry.getValue());
                    answer.add(path);
                    visited.add(entry.getKey()); // mark visited, flight id
                }
                else {
                    // is not end, find new destination
                    for (Map.Entry<Integer, Flight> entry1 : Data.flights.entrySet()) {

                        // if find start -> b -> end
                        if (visited.contains(entry1.getKey()) || !entry1.getValue().getSource().equals(entry.getValue().getDestination())) {
                            continue;
                        }
                        // find start -> b > xxx
                        else if (entry1.getValue().getSource().equals(entry.getValue().getDestination()) && entry1.getValue().getDestination().equals(destination)) {
                            // add first flight
                            path.add(entry.getValue());
                            // add second flight
                            path.add(entry1.getValue());
                            answer.add(path);
                            visited.add(entry1.getKey());
                        }
                        else {
                            for (Map.Entry<Integer, Flight> entry2 : Data.flights.entrySet()) {

                                // if find start -> b -> c -> end
                                if (entry2.getValue().getSource().equals(entry1.getValue().getDestination()) && entry2.getValue().getDestination().equals(destination)) {
                                    // add first flight
                                    List<Flight> path2 = new ArrayList<>();
                                    path2.add(entry.getValue());
                                    // add second flight
                                    path2.add(entry1.getValue());
                                    // add third flight
                                    path2.add(entry2.getValue());
                                    answer.add(path2);
                                    visited.add(entry2.getKey());
                                    break;
                                } else {
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
        }
        return answer;
    }
}
