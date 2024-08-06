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
    
    ///////////////////////// for delivery
    private String City;
    private String StreenAndNum;
    private int phoneNum;
    private String receiverName;
    private boolean isEarly; //if false below doesn't needed to fill 
    private Timestamp OrderReadyRequestTime; //when the delivery time
    ////////////////////////
    
    private int branchId;
    private Timestamp orderDate;
    private Timestamp orderRequestTime;
    private Timestamp orderReceiveTime;
    private Timestamp orderFutureTime;
    private double totalPrice;
    private boolean delivery; ///////////////Choose supply method 0 pickup
    private EnumOrderStatus status;

    public Order( String username, int branchId, Timestamp orderDate, Timestamp orderRequestTime, double totalPrice, boolean delivery) {
        this.username = username;
        this.branchId = branchId;
        this.orderDate = orderDate;
        this.orderRequestTime = orderRequestTime;
        this.totalPrice = totalPrice;
        this.delivery = delivery;
        status=EnumOrderStatus.PENDING;
    }
 
    
	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	
	public boolean getIseEarly() {
		return isEarly;
	}

	public void setIseEarly(boolean iseEarly) {
		this.isEarly = iseEarly;
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
	
	public String getCity() {
	    return City;
	}

	public String getStreetAndNum() {
	    return StreenAndNum;
	}

	public int getPhoneNum() {
	    return phoneNum;
	}

	public String getReceiverName() {
	    return receiverName;
	}

	public boolean isEarly() {
	    return isEarly;
	}

	public Timestamp getOrderReadyRequestTime() {
	    return OrderReadyRequestTime;
	}

    
    

    // Getters and setters
}