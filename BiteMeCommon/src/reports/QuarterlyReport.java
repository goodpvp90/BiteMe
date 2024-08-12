package reports;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import enums.EnumBranch;

public class QuarterlyReport implements Serializable{

	private static final long serialVersionUID = 1L;
	private EnumBranch restaurant;
	private int quarter, year;
	private int income;
	private Map<String,Integer> daysInRanges;
	public QuarterlyReport(EnumBranch restaurant, int quarter, int year) {
		this.restaurant = restaurant;
		this.quarter = quarter;
		this.year = year;
		
		daysInRanges = new HashMap<>();
	}
	public EnumBranch getRestaurant() {
		return restaurant;
	}
	public void setRestaurant(EnumBranch restaurant) {
		this.restaurant = restaurant;
	}
	public int getQuarter() {
		return quarter;
	}
	public void setQuarter(int quarter) {
		this.quarter = quarter;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getIncome() {
		return income;
	}
	public void setIncome(int income) {
		this.income = income;
	}
	public Map<String, Integer> getDaysInRanges() {
		return daysInRanges;
	}
	public void setDaysInRanges(Map<String, Integer> daysInRanges) {
		this.daysInRanges = daysInRanges;
	}
	
	public void addDaysInRanges(String range, int numOfDays) {
		daysInRanges.put(range, numOfDays);
	}
	
	
}
