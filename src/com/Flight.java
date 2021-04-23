package com;

import util.Utils;

public class Flight {

    private int id;             // id
    private String time;        // departure time
    private String source;      // source location
    private String destination; // destination location
    private int capacity;       // the capacity of the flight
    private int booked;         // the number of passengers who have checked tickets

    @Override
    public String toString() {
        return source + " -> " + destination;
    }

    //get the number of minutes this flight takes (round to nearest whole number)
    public int getDuration() {
        // return the time
        return (int) (getDistance() / 720 * 60);
    }

    // get the flight arrived time
    public String getArrivedTime(){
        return Utils.getManyMinuteAfter(this.getTime(), this.getDuration());
    }

    //implement the ticket price formula
    public double getTicketPrice() {

        // x is proportion of seats filled (booked/capacity)
        double x = 1.0 * this.booked / this.capacity;
        // y is multiplier for ticket price to determine current value
        double y = 0.0;

        // set value of y
        if (x <= 0.5) {
            y = -0.4 * x + 1;
        } else if (x <= 0.7) {
            y = x + 0.3;
        } else if (x <= 1) {
            y = 0.2 / Math.PI * (1 / Math.tan(20 * x - 14)) + 1;
        }

        // get distance of source and destination
        double d = getDistance();

        // get demand of destination
        double DTo = Utils.locations.get(this.getDestination()).getDemand();

        // get demand of source
        double DFrom = Utils.locations.get(this.getSource()).getDemand();

        // calculate the ticket price
        return y * d / 100 * (30 + 4 * (DTo - DFrom));

    }

    //book the given number of passengers onto this flight, returning the total cost
    public double book(int num) {
        // calculate the total cost
        double totalCost = 0.0;
        for (int i = 0; i < num; i++){
            totalCost += this.getTicketPrice();
            if (!isFull()) {
                this.booked++;
            } else {
                return totalCost;
            }
        }
        return totalCost;
    }

    //return whether or not this flight is full
    public boolean isFull() {
        return this.booked == this.capacity;
    }

    //get the distance of this flight in km
    public double getDistance() {
        // get source location object
        Location sourceLocation = Utils.locations.get(this.source);

        // get destination location object
        Location destinationLocation = Utils.locations.get(this.destination);

        // calculate the distance of two location
        return Location.distance(sourceLocation, destinationLocation);
    }

    //get the layover time, in minutes, between two flights
    public static int layover(Flight x, Flight y) {
        return Utils.getTimeDifferenceByTimeString(x.getTime(), y.getTime());
    }



    /*
        generate
     */

    // constructor with id
    public Flight(int id, String time, String source, String destination, int capacity, int booked) {
        this.id = id;
        this.time = time;
        this.source = source;
        this.destination = destination;
        this.capacity = capacity;
        this.booked = booked;
    }

    // constructor without id
    public Flight(String time, String source, String destination, int capacity, int booked) {
        this.time = time;
        this.source = source;
        this.destination = destination;
        this.capacity = capacity;
        this.booked = booked;
    }

    public Flight() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getBooked() {
        return booked;
    }

    public void setBooked(int booked) {
        this.booked = booked;
    }
}
