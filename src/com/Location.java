package com;

public class Location {

	private String name;		// location name
	private double latitude;	// latitude
	private double longitude;	// longitude
	private double demand;		// demand number, the degree of passengers want to or not

	public Location(String name, double lat, double lon, double demand) {
		this.name = name;
		this.latitude = lat;
		this.longitude = lon;
		this.demand = demand;
	}

    //Implement the Haversine formula - return value in kilometres
    public static double distance(Location l1, Location l2) {
		return Utils.getDistance(l1.longitude, l1.latitude, l2.longitude, l2.latitude);
	}

    public void addArrival(Flight f) {

	}
	
	public void addDeparture(Flight f) {

	}
	
	/**
	 * Check to see if Flight f can depart from this location.
	 * If there is a clash, the clashing flight string is returned, otherwise null is returned.
	 * A conflict is determined by if any other flights are arriving or departing at this location within an hour of this flight's departure time.
	 * @param f The flight to check.
	 * @return "Flight <id> [departing/arriving] from <name> on <clashingFlightTime>". Return null if there is no clash.
	 */
	public String hasRunwayDepartureSpace(Flight f) {
		// TODO

		return "";
    }

    /**
	 * Check to see if Flight f can arrive at this location.
	 * A conflict is determined by if any other flights are arriving or departing at this location within an hour of this flight's arrival time.
	 * @param f The flight to check.
	 * @return String representing the clashing flight, or null if there is no clash. Eg. "Flight <id> [departing/arriving] from <name> on <clashingFlightTime>"
	 */
	public String hasRunwayArrivalSpace(Flight f) {
		// TODO
		return "";
    }


    // generate




	public Location() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getDemand() {
		return demand;
	}

	public void setDemand(double demand) {
		this.demand = demand;
	}
}
