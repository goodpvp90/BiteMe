package common;

import java.io.Serializable;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    private int orderNumber;
    private String nameOfRestaurant;
    private double totalPrice;
    private int orderListNumber;
    private String orderAddress;

    // Constructor
    public Order(int orderNumber, String nameOfRestaurant, double totalPrice, int orderListNumber, String orderAddress) {
        this.orderNumber = orderNumber;
        this.nameOfRestaurant = nameOfRestaurant;
        this.totalPrice = totalPrice;
        this.orderListNumber = orderListNumber;
        this.orderAddress = orderAddress;
    }

    // Getters and setters
    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getNameOfRestaurant() {
        return nameOfRestaurant;
    }

    public void setNameOfRestaurant(String nameOfRestaurant) {
        this.nameOfRestaurant = nameOfRestaurant;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getOrderListNumber() {
        return orderListNumber;
    }

    public void setOrderListNumber(int orderListNumber) {
        this.orderListNumber = orderListNumber;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    @Override
    public String toString() {
        return "Order [orderNumber=" + orderNumber + ", nameOfRestaurant=" + nameOfRestaurant + ", totalPrice="
                + totalPrice + ", orderListNumber=" + orderListNumber + ", orderAddress=" + orderAddress + "]";
    }
}
