package common;

import common.Restaurant.Location;

public class IncomeReport extends MonthlyReport{
    private double income;

    // Constructors
    public IncomeReport(Location restaurant, int month, int year) {
		super(restaurant, month, year);
    }

	public double getIncome() {
		return income;
	}

	public void setIncome(double income) {
		this.income = income;
	}
    
}
