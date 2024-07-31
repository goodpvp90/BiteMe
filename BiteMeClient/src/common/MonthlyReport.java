package common;

import common.Restaurant.Location;

public class MonthlyReport {
	private Location loc;
    private int month;
    private int year;
    
	public MonthlyReport(Location loc, int month, int year) {
		this.loc = loc;
		this.month = month;
		this.year = year;
	}

	public Location getRestaurant() {
		return loc;
	}

	public void setRestaurant(Location loc) {
		this.loc = loc;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
    
}
