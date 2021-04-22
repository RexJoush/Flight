package com;

import command.CommonCommand;
import command.FlightCommand;
import command.LocationCommand;
import command.TravelCommand;

import java.util.Scanner;

public class FlightScheduler {

    private static FlightScheduler instance;
    private final Scanner scanner = new Scanner(System.in);

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
        while (true) {

            // get command
            System.out.print("User: ");
            String s = scanner.nextLine();
            String[] s1 = s.split(" ");
            try {
                switch (s1[0].toLowerCase()){
                    case "flight" :
                        FlightCommand.flightCommand(s);
                        break;
                    case "flights" :
                        FlightCommand.flightCommand("flights");
                        break;
                    case "departures":
                    case "arrivals":
                    case "schedule":
                    case "location":
                        LocationCommand.locationCommand(s);
                        break;
                    case "locations":
                        LocationCommand.locationCommand("locations");
                        break;
                    case "travel":
                        TravelCommand.travelCommand(s);
                        break;
                    case "exit":
                        CommonCommand.commonCommand("exit");
                        break;
                    case "help":
                        CommonCommand.commonCommand("help");
                        break;
                    default:
                        throw new RuntimeException("Invalid command. Type 'help' for a list of commands");
                }

                // if command contains flight, use Flight Command
//                if ("flight".equalsIgnoreCase(s1[0]) || "flights".equalsIgnoreCase(s1[0])) {
//                    FlightCommand.flightCommand(s);
//                }
//                // if command contains location, use Location Command
//                else if ("location".equalsIgnoreCase(s1[0]) || "locations".equalsIgnoreCase(s1[0])) {
//                    LocationCommand.locationCommand(s);
//                }
//                // if command contains travel, use Travel Command
//                else if ("travel".equalsIgnoreCase(s1[0])) {
//                    TravelCommand.travelCommand(s);
//                }
//                else if ("exit".equalsIgnoreCase(s1[0])) {
//                    CommonCommand.commonCommand("exit");
//                }
//                else if ("help".equalsIgnoreCase(s1[0])) {
//                    CommonCommand.commonCommand("help");
//                } else {
//                    throw new RuntimeException("Invalid command. Type 'help' for a list of commands");
//                }
            } catch (RuntimeException e){
                System.out.println(e.getMessage());
            }

        }
    }
}
