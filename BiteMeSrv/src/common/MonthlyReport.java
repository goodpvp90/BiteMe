package common;

public class MonthlyReport {
	private Restaurant restaurant;
    private int month;
    private int year;
    
	public MonthlyReport(Restaurant restaurant, int month, int year) {
		this.restaurant = restaurant;
		this.month = month;
		this.year = year;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
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
