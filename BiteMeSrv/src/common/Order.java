package common;

import java.io.Serializable;
import java.sql.Timestamp;

public class Order implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int orderId;
    private String username;
    private int branchId;
    private Timestamp orderDate;
    private Timestamp orderRequestTime;
    private Timestamp orderReceiveTime;
    private Timestamp orderFutureTime;
    private double totalPrice;
    private boolean delivery;
    private EnumOrderStatus status;

    public Order(int orderId, String username, int branchId, Timestamp orderDate, Timestamp orderRequestTime, Timestamp orderReceiveTime, double totalPrice, boolean delivery, EnumOrderStatus status) {
        this.orderId = orderId;
        this.username = username;
        this.branchId = branchId;
        this.orderDate = orderDate;
        this.orderRequestTime = orderRequestTime;
        this.orderReceiveTime = orderReceiveTime;
        this.totalPrice = totalPrice;
        this.delivery = delivery;
        this.setStatus(status);
        
    }

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getBranchId() {
		return branchId;
	}

	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

	public Timestamp getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Timestamp orderDate) {
		this.orderDate = orderDate;
	}

	public Timestamp getOrderRequestTime() {
		return orderRequestTime;
	}

	public void setOrderRequestTime(Timestamp orderRequestTime) {
		this.orderRequestTime = orderRequestTime;
	}

	public Timestamp getOrderReceiveTime() {
		return orderReceiveTime;
	}

	public void setOrderReceiveTime(Timestamp orderReceiveTime) {
		this.orderReceiveTime = orderReceiveTime;
	}
	
	public void setOrderFutureTime(Timestamp orderFutureTime) {
		this.orderFutureTime = orderFutureTime;
	}
	
	public Timestamp getOrderFutureTime() {
		return orderFutureTime;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public boolean isDelivery() {
		return delivery;
	}

	public void setDelivery(boolean delivery) {
		this.delivery = delivery;
	}

	public EnumOrderStatus getStatus() {
		return status;
	}

	public void setStatus(EnumOrderStatus status) {
		this.status = status;
	}
    
    

    // Getters and setters
}
