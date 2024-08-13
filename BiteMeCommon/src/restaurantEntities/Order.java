package restaurantEntities;

import java.io.Serializable;
import java.sql.Timestamp;
import enums.EnumOrderStatus;

/**
 * Represents an order in the restaurant data.
 * An order contains details about the customer, order times, delivery information, and status.
 */
public class Order implements Serializable {


	private static final long serialVersionUID = 1L;
    /** The unique identifier for the order. */
    private int orderId;

    /** The username of the customer who placed the order. */
    private String username;

    /** The city where the delivery is to be made. */
    private String city;

    /** The street address and number for delivery. */
    private String streetAndNum;

    /** The phone number of the customer. */
    private String phoneNum;

    /** The name of the person receiving the order. */
    private String receiverName;

    /** Indicates whether the order is early. */
    private boolean isEarly;

    /** The time when the order ready request was made. */
    private Timestamp orderReadyRequestTime;

    /** The ID of the branch where the order was placed. */
    private int branchId;

    /** The date when the order was placed. */
    private Timestamp orderDate;

    /** The time when the order was requested. */
    private Timestamp orderRequestTime;

    /** The time when the order was received. */
    private Timestamp orderReceiveTime;

    /** The total price of the order. */
    private double totalPrice;

    /** Indicates whether the order is for delivery. */
    private boolean delivery;

    /** The current status of the order. */
    private EnumOrderStatus status;

    /**
     * Creates an order with essential details.
     * 
     * @param username the username of the customer
     * @param branchId the ID of the branch
     * @param orderDate the date when the order was placed
     * @param orderRequestTime the time when the order was requested
     * @param totalPrice the total price of the order
     * @param delivery whether the order is for delivery or not
     * @param status the status of the order
     */
    public Order( String username, int branchId, Timestamp orderDate, Timestamp orderRequestTime, double totalPrice, boolean delivery, EnumOrderStatus status) {
        this.username = username;
        this.branchId = branchId;
        this.orderDate = orderDate;
        this.orderRequestTime = orderRequestTime;
        this.totalPrice = totalPrice;
        this.delivery = delivery;
        this.status=status;	
    }
    
    /**
     * Creates an order with additional delivery details.
     * 
     * @param username the username of the customer
     * @param branchId the ID of the branch
     * @param orderDate the date when the order was placed
     * @param orderRequestTime the time when the order was requested
     * @param totalPrice the total price of the order
     * @param delivery whether the order is for delivery or not
     * @param city the city for delivery
     * @param streetAndNum the street address for delivery
     * @param phoneNum the phone number of the customer
     * @param receiverName the name of the person receiving the order
     */
    public Order( String username, int branchId, Timestamp orderDate, Timestamp orderRequestTime, double totalPrice, boolean delivery,
    		String city, String streetAndNum, String phoneNum, String receiverName ) {
        this.username = username;
        this.branchId = branchId;
        this.orderDate = orderDate;
        this.orderRequestTime = orderRequestTime;
        this.totalPrice = totalPrice;
        this.delivery = delivery;
        status=EnumOrderStatus.PENDING;	
        this.city = city;
		this.streetAndNum = streetAndNum;
		this.phoneNum = phoneNum;
		this.receiverName = receiverName;
		
	
    }
 
    /**
    * Returns the order ID.
    * 
    * @return the order ID
    */
	public int getOrderId() {
		return orderId;
	}

	/**
     * Sets the order ID.
     * 
     * @param orderId the order ID to set
     */
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	/**
     * Returns whether the order is early order.
     * 
     * @return true if the order is early, false otherwise
     */
	public boolean getIseEarly() {
		return isEarly;
	}
	/**
     * Sets whether the order is early order.
     * 
     * @param isEarly true if the order is early, false otherwise
     */
	public void setIseEarly(boolean iseEarly) {
		this.isEarly = iseEarly;
	}
	
	/**
     * Returns the username of the customer.
     * 
     * @return the username of the customer
     */
	public String getUsername() {
		return username;
	}

	/**
     * Sets the username of the customer.
     * 
     * @param username the username to set
     */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
     * Returns the branch ID.
     * 
     * @return the branch ID
     */
	public int getBranchId() {
		return branchId;
	}

	/**
     * Sets the branch ID.
     * 
     * @param branchId the branch ID to set
     */
	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

	/**
     * Returns the date when the order was placed.
     * 
     * @return the order date
     */
	public Timestamp getOrderDate() {
		return orderDate;
	}

	/**
     * Sets the date when the order was placed.
     * 
     * @param orderDate the order date to set
     */
	public void setOrderDate(Timestamp orderDate) {
		this.orderDate = orderDate;
	}

	/**
     * Returns the time when the order was requested.
     * 
     * @return the order request time
     */
	public Timestamp getOrderRequestTime() {
		return orderRequestTime;
	}

	/**
     * Sets the time when the order was requested.
     * 
     * @param orderRequestTime the order request time to set
     */
	public void setOrderRequestTime(Timestamp orderRequestTime) {
		this.orderRequestTime = orderRequestTime;
	}

	/**
     * Returns the time when the order was received.
     * 
     * @return the order receive time
     */
	public Timestamp getOrderReceiveTime() {
		return orderReceiveTime;
	}

	/**
     * Sets the time when the order was received.
     * 
     * @param orderReceiveTime the order receive time to set
     */
	public void setOrderReceiveTime(Timestamp orderReceiveTime) {
		this.orderReceiveTime = orderReceiveTime;
	}
	
	/**
     * Returns the total price of the order.
     * 
     * @return the total price
     */
	public double getTotalPrice() {
		return totalPrice;
	}

	/**
     * Sets the total price of the order.
     * 
     * @param totalPrice the total price to set
     */
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	/**
     * Returns whether the order is for delivery.
     * 
     * @return true if the order is for delivery, false otherwise
     */
	public boolean isDelivery() {
		return delivery;
	}

	/**
     * Sets whether the order is for delivery.
     * 
     * @param delivery true if the order is for delivery, false otherwise
     */
	public void setDelivery(boolean delivery) {
		this.delivery = delivery;
	}
	
	/**
     * Returns the status of the order.
     * 
     * @return the status of the order
     */
	public EnumOrderStatus getStatus() {
		return status;
	}

	/**
     * Sets the status of the order.
     * 
     * @param status the status to set
     */
	public void setStatus(EnumOrderStatus status) {
		this.status = status;
	}
	
	/**
     * Returns the city for delivery.
     * 
     * @return the city
     */
	public String getCity() {
	    return city;
	}

	/**
     * Returns the street address and number for delivery.
     * 
     * @return the street address and number
     */
	public String getStreetAndNum() {
	    return streetAndNum;
	}
	
	/**
     * Sets the phone number of the customer.
     * 
     * @param phoneNum the phone number to set
     */
	public void setPhoneNum(String phoneNum) {
	    this.phoneNum = phoneNum;
	}
	
	/**
     * Returns the phone number of the customer.
     * 
     * @return the phone number
     */
	public String getPhoneNum() {
	    return phoneNum;
	}

	/**
     * Returns the name of the person receiving the order.
     * 
     * @return the receiver's name
     */
	public String getReceiverName() {
	    return receiverName;
	}
	
	/**
     * Sets the name of the person receiving the order.
     * 
     * @param receiverName the receiver's name to set
     */
	public void setReceiverName(String receiverName) {
	    this.receiverName = receiverName;
	}

	/**
     * Returns whether the order is early.
     * 
     * @return true if the order is early, false otherwise
     */
	public boolean isEarly() {
	    return isEarly;
	}
	
	/**
     * Returns the time when the order ready request was made.
     * 
     * @return the order ready request time
     */
	public Timestamp getOrderReadyRequestTime() {
	    return orderReadyRequestTime;
	}

}