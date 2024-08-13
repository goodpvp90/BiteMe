package reports;

import java.io.Serializable;
import java.sql.Date;

/**
 * Represents a daily performance report for a restaurant in the BiteMe application.
 * This class encapsulates information about the total number of orders and 
 * the number of orders completed on time for a specific date.
 * It implements Serializable to allow for easy storage and transmission of report data.
 */
public class DailyPerformanceReport implements Serializable {

	/**
     * for Serializable use.
     */
	private static final long serialVersionUID = 1L;
	
    /**
     * The date for which this performance report is generated.
     * It represents a specific day's performance data.
     */
	private Date date;
	
	/**
     * The total number of orders received on the date of this report.
     * This includes all orders, regardless of their completion status.
     */
    private int totalOrders;
    
    /**
     * The number of orders that were completed on time on the date of this report.
     * This represents a subset of the totalOrders that met the expected delivery or completion time.
     */
    private int ordersCompletedInTime;

    /**
     * Constructs a new DailyPerformanceReport with the specified date, total orders, and orders completed on time.
     *
     * @param date The date of the performance report.
     * @param totalOrders The total number of orders for the day.
     * @param ordersCompletedInTime The number of orders completed on time for the day.
     */
    public DailyPerformanceReport(Date date, int totalOrders, int ordersCompletedInTime) {
        this.date = date;
        this.totalOrders = totalOrders;
        this.ordersCompletedInTime = ordersCompletedInTime;
    }

    /**
     * Retrieves the date of the performance report.
     *
     * @return The date of the report.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the date of the performance report.
     *
     * @param date The new date to set for the report.
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Retrieves the total number of orders for the day.
     *
     * @return The total number of orders.
     */
    public int getTotalOrders() {
        return totalOrders;
    }

    /**
     * Sets the total number of orders for the day.
     *
     * @param totalOrders The new total number of orders to set.
     */
    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    /**
     * Retrieves the number of orders completed on time for the day.
     *
     * @return The number of orders completed on time.
     */
    public int getOrdersCompletedInTime() {
        return ordersCompletedInTime;
    }

    /**
     * Sets the number of orders completed on time for the day.
     *
     * @param ordersCompletedInTime The new number of orders completed on time to set.
     */
    public void setOrdersCompletedInTime(int ordersCompletedInTime) {
        this.ordersCompletedInTime = ordersCompletedInTime;
    }
}
