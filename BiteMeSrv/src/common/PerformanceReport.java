package common;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import common.Restaurant.Location;

public class PerformanceReport extends MonthlyReport {
    
    private List<DailyPerformanceReport> dailyReports;

    public PerformanceReport(Location restaurant, int month, int year) {
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