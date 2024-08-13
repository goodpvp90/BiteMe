package reports;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import enums.EnumBranch;

/**
 * Represents a Quarterly Report for a specific branch, quarter, and year.
 * Implements Serializable for data persistence.
 */
public class QuarterlyReport implements Serializable{

    /**
     * Serialization version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The branch for which the report is generated.
     */
    private EnumBranch restaurant;

    /**
     * The quarter of the report.
     */
    private int quarter;

    /**
     * The year of the report.
     */
    private int year;

    /**
     * The total income for the quarter.
     */
    private int income;

    /**
     * A map storing the number of days in each order count range.
     */
    private Map<String, Integer> daysInRanges;
    
    /**
     * Constructs a new QuarterlyReport.
     *
     * @param restaurant The branch for which the report is generated.
     * @param quarter The quarter of the report.
     * @param year The year of the report.
     */
	public QuarterlyReport(EnumBranch restaurant, int quarter, int year) {
		this.restaurant = restaurant;
		this.quarter = quarter;
		this.year = year;
		
		daysInRanges = new HashMap<>();
	}
	
	/**
     * Gets the branch for which the report is generated.
     *
     * @return The branch of the report.
     */
	public EnumBranch getRestaurant() {
		return restaurant;
	}
	
	/**
     * Sets the branch for which the report is generated.
     *
     * @param restaurant The branch to set.
     */
	public void setRestaurant(EnumBranch restaurant) {
		this.restaurant = restaurant;
	}
	
	/**
     * Gets the quarter of the report.
     *
     * @return The quarter of the report.
     */
	public int getQuarter() {
		return quarter;
	}
	
	/**
     * Sets the quarter of the report.
     *
     * @param quarter The quarter to set.
     */
	public void setQuarter(int quarter) {
		this.quarter = quarter;
	}
	
	/**
     * Gets the year of the report.
     *
     * @return The year of the report.
     */
	public int getYear() {
		return year;
	}
	
	/**
     * Sets the year of the report.
     *
     * @param year The year to set.
     */
	public void setYear(int year) {
		this.year = year;
	}
	
	/**
     * Gets the total income for the quarter.
     *
     * @return The total income.
     */
	public int getIncome() {
		return income;
	}
	
	/**
     * Sets the total income for the quarter.
     *
     * @param income The income to set.
     */

	public void setIncome(int income) {
		this.income = income;
	}
	
	/**
     * Gets the map of days in each order count range.
     *
     * @return The map of days in ranges.
     */
	public Map<String, Integer> getDaysInRanges() {
		return daysInRanges;
	}
	
	/**
     * Sets the map of days in each order count range.
     *
     * @param daysInRanges The map of days in ranges to set.
     */
	public void setDaysInRanges(Map<String, Integer> daysInRanges) {
		this.daysInRanges = daysInRanges;
	}
	
	/**
     * Adds the number of days for a specific order count range.
     *
     * @param range The order count range.
     * @param numOfDays The number of days to add for the range.
     */
	public void addDaysInRanges(String range, int numOfDays) {
		daysInRanges.put(range, numOfDays);
	}		
}
