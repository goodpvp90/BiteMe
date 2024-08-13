package reports;

import java.util.ArrayList;
import java.util.List;
import enums.EnumBranch;

/**
 * Represents a Performance Report for a specific branch, month, and year.
 * Extends the MonthlyReport class.
 */
public class PerformanceReport extends MonthlyReport {
    
	/**
     * for Serializable use.
     */
	private static final long serialVersionUID = 1L;
	
	/**
     * List of daily performance reports for the month.
     */
	private List<DailyPerformanceReport> dailyReports;

	/**
     * Constructs a new PerformanceReport.
     *
     * @param restaurant The branch for which the report is generated.
     * @param month The month of the report.
     * @param year The year of the report.
     */
    public PerformanceReport(EnumBranch restaurant, int month, int year) {
        super(restaurant, month, year);
        this.dailyReports = new ArrayList<>();
    }

    /**
     * Gets the list of daily performance reports.
     *
     * @return The list of daily performance reports.
     */
    public List<DailyPerformanceReport> getDailyReports() {
        return dailyReports;
    }

    /**
     * Adds a daily performance report to the list.
     *
     * @param dailyReport The daily performance report to add.
     */
    public void addDailyReport(DailyPerformanceReport dailyReport) {
        this.dailyReports.add(dailyReport);
    }
}