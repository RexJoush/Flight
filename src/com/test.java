package com;

import database.Data;
import entity.Week;
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

    public static void main(String[] args) {

        String s = "Monday 34:00";

        DecimalFormat decimalFormat = new DecimalFormat("00");

        String command = "flight add Tuestday 20:20 beijing shanghai 80";

        List<Flight> flights = new ArrayList<>();

        Flight flight = new Flight(0, "Tuesday 20:30", "beijing", "dubai", 100, 0);
        Flight flight2 = new Flight(1, "Tuesday 20:35", "beijing", "shanghai", 100, 0);
        Flight flight3 = new Flight(2, "Friday 20:20", "beijing", "shanghai", 100, 0);

//        flights.add(flight1);
        flights.add(flight2);
        flights.add(flight3);


        System.out.println(TimeUtils.getManyMinuteAfter("Sunday 23:20", 60));



    }
}
