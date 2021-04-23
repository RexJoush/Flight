package entity;

import com.Flight;
import util.Utils;

import java.util.List;

/**
 * @author Rex Joush
 * @time 2021.04.22
 */

public class TravelDefinition {

    private List<Flight> flights;   // flight list

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    public TravelDefinition(List<Flight> flights) {
        this.flights = flights;
    }


    // the total travel duration
    public int getDuration(){

        int duration = 0;
        switch (flights.size()) {
            case 1:
                duration = flights.get(0).getDuration();
                break;
            case 2:
                duration =  Math.abs(Utils.getTimeDifferenceByTimeString(flights.get(0).getTime(), flights.get(1).getArrivedTime()));
                break;
            case 3:
                duration =  Math.abs(Utils.getTimeDifferenceByTimeString(flights.get(0).getTime(), flights.get(2).getArrivedTime()));
                break;
        }
        return duration;
    }

    // the travel cost
    public double getCost(){
        double cost = 0.0;
        for (Flight flight : flights) {
            cost += flight.getTicketPrice();
        }
        return cost;
    }

    // get layover time
    public int getLayoverTime(){

        int layover = 0;
        switch (flights.size()) {
            case 1:
                break;
            case 2:
                layover = Utils.getTimeDifferenceByTimeString(flights.get(1).getTime(), flights.get(0).getArrivedTime());
                break;
            case 3:
                layover = Utils.getTimeDifferenceByTimeString(flights.get(1).getTime(), flights.get(0).getArrivedTime())
                        + Utils.getTimeDifferenceByTimeString(flights.get(2).getTime(), flights.get(1).getArrivedTime());
                break;
        }
        return layover;
    }

    // the flight time
    public int getFlightTime(){
        int flightTime = 0;
        for (Flight flight : flights) {
            flightTime += flight.getDuration();
        }
        return flightTime;
    }

    // the stopovers times
    public int getStopovers(){
        switch (flights.size()) {
            case 1:
                return 0;
            case 2:
                return 1;
            case 3:
                return 2;
        }
        return 0;
    }

}
