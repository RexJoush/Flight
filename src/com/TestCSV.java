package com;

import database.Data;
import util.CommonUtils;

import java.io.*;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Rex Joush
 * @time 2021.04.20
 */

public class TestCSV {

    // Check the file every line whether the format meets the requirements
    public static Pattern p = Pattern.compile("(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday) [0-2][0-9]:[0-5][0-9]");

    public static String path = "test.csv";
    public static void main(String[] args) throws IOException {

        Flight flight = new Flight(0, "Tuesday 20:30", "beijing", "dubai", 100, 0);
        Flight flight2 = new Flight(1, "Tuesday 20:35", "beijing", "shanghai", 100, 0);
        Data.flights.put(0, flight);
        Data.flights.put(1, flight2);
        new TestCSV().readCSV();


    }

    private void writeCSV() {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));

            for (Map.Entry<Integer, Flight> integerFlightEntry : Data.flights.entrySet()) {
                Flight flight = integerFlightEntry.getValue();
                String line = flight.getTime() + "," + flight.getSource() + "," + flight.getDestination() + "," + flight.getCapacity() + "," + flight.getBooked();
                writer.write(line);
                writer.newLine();
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void readCSV() throws IOException {
        // 1.创建 FileReader 对象，构造方法中绑定要读取的数据源
        FileReader fr = new FileReader("locations.csv");
        // 2.buffer reader
        BufferedReader br = new BufferedReader(fr);

        String line = "";

        int index  = 0;
        while ((line = br.readLine()) != null) {
            if (CommonUtils.pLocation.matcher(line).matches()) {
                System.out.println(line);
                index++;
            } else {
                System.out.println("error");
            }
        }
        System.out.println(index);

        // 3.释放资源
        br.close();
        fr.close();
    }



}
