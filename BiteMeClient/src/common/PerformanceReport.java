package common;

public class PerformanceReport extends MonthlyReport{
    private int totalOrders;
    private int ordersCompletedInTime;

    public PerformanceReport(Restaurant restaurant, int month, int year) {
		super(restaurant, month, year);
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
