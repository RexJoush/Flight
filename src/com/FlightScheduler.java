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

            try {
                // if command contains flight, use Flight Command
                if (s.toLowerCase().contains("flight")) {
                    FlightCommand.flightCommand(s);
                }
                // if command contains location, use Location Command
                else if (s.toLowerCase().contains("location")) {
                    LocationCommand.locationCommand(s);
                }
                // if command contains travel, use Travel Command
                else if (s.toLowerCase().contains("travel")) {
                    TravelCommand.travelCommand(s);
                }
                else if ("exit".equals(s.toLowerCase())) {
                    CommonCommand.commonCommand("exit");
                }
                else if ("help".equals(s.toLowerCase())) {
                    CommonCommand.commonCommand("help");
                } else {
                    throw new RuntimeException("Invalid command. Type 'help' for a list of commands");
                }
            } catch (RuntimeException e){
                System.out.println(e.getMessage());
            }

        }
    }
}
