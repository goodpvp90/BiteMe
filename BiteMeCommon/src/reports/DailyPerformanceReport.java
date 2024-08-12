package reports;

import java.io.Serializable;
import java.sql.Date;

public class DailyPerformanceReport implements Serializable {
    private Date date;
    private int totalOrders;
    private int ordersCompletedInTime;

    public DailyPerformanceReport(Date date, int totalOrders, int ordersCompletedInTime) {
        this.date = date;
        this.totalOrders = totalOrders;
        this.ordersCompletedInTime = ordersCompletedInTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public int getOrdersCompletedInTime() {
        return ordersCompletedInTime;
    }

    public void setOrdersCompletedInTime(int ordersCompletedInTime) {
        this.ordersCompletedInTime = ordersCompletedInTime;
    }
}
