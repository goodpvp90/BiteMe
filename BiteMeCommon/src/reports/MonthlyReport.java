package reports;

import java.io.Serializable;

import enums.EnumBranch;

public class MonthlyReport implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EnumBranch loc;
    private int month;
    private int year;
    
	public MonthlyReport(EnumBranch loc, int month, int year) {
		this.loc = loc;
		this.month = month;
		this.year = year;
	}

	public EnumBranch getRestaurant() {
		return loc;
	}

	public void setRestaurant(EnumBranch loc) {
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
