package reports;

import java.io.Serializable;

import enums.EnumBranch;

/**
 * Represents a generic Monthly Report for a specific branch, month, and year.
 * Implements Serializable for data persistence.
 */
public class MonthlyReport implements Serializable{

	/**
     * for Serializable use.
     */
	private static final long serialVersionUID = 1L;
	
	/**
     * The branch for which the report is generated.
     */
	private EnumBranch loc;
	
    /**
     * The month of the report.
     */
    private int month;
    
    /**
     * The year of the report.
     */
    private int year;
    
    /**
     * Constructs a new MonthlyReport.
     *
     * @param loc The branch for which the report is generated.
     * @param month The month of the report.
     * @param year The year of the report.
     */
	public MonthlyReport(EnumBranch loc, int month, int year) {
		this.loc = loc;
		this.month = month;
		this.year = year;
	}

	/**
     * Gets the branch for which the report is generated.
     *
     * @return The branch of the report.
     */
	public EnumBranch getRestaurant() {
		return loc;
	}

	/**
     * Sets the branch for which the report is generated.
     *
     * @param loc The branch to set.
     */
	public void setRestaurant(EnumBranch loc) {
		this.loc = loc;
	}

	/**
     * Gets the month of the report.
     *
     * @return The month of the report.
     */
	public int getMonth() {
		return month;
	}

	/**
     * Sets the month of the report.
     *
     * @param month The month to set.
     */
	public void setMonth(int month) {
		this.month = month;
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
    
}
