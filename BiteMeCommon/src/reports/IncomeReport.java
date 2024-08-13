package reports;

import enums.EnumBranch;

/**
 * Represents an Income Report for a specific branch, month, and year.
 * Implements Serializable for data persistence.
 */
public class IncomeReport extends MonthlyReport{

	/**
     * for Serializable use.
     */
	private static final long serialVersionUID = 1L;
	
    /**
     * The total income for the report period.
     */
	private double income;

    /**
     * Constructs a new IncomeReport.
     *
     * @param restaurant The branch for which the report is generated.
     * @param month The month of the report.
     * @param year The year of the report.
     */
    public IncomeReport(EnumBranch restaurant, int month, int year) {
		super(restaurant, month, year);
    }

    /**
     * Gets the total income for the report period.
     *
     * @return The total income.
     */
	public double getIncome() {
		return income;
	}

	/**
     * Sets the total income for the report period.
     *
     * @param income The total income to set.
     */
	public void setIncome(double income) {
		this.income = income;
	}
    
}
