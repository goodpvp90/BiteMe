package common;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class PerformanceReport extends MonthlyReport {
    
    private List<DailyPerformanceReport> dailyReports;

    public PerformanceReport(EnumBranch restaurant, int month, int year) {
        super(restaurant, month, year);
        this.dailyReports = new ArrayList<>();
    }

    public List<DailyPerformanceReport> getDailyReports() {
        return dailyReports;
    }

    public void addDailyReport(DailyPerformanceReport dailyReport) {
        this.dailyReports.add(dailyReport);
    }
}