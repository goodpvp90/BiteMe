package common;

import java.io.Serializable;

public class DishInOrder implements Serializable {
    private int orderId; //Check if needed, currently unused
    private String comment;
    private String optionalPick;
    private int dishId;
    private String dishName;
    
    // Constructors
    public DishInOrder(String dishName, int dishId, String comment, String opt) {
    	this.dishName = dishName;
    	this.dishId = dishId;
        this.comment = comment;
        this.optionalPick = opt;
    }

    // Getters and Setters//CHECK IF NEED
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {//CHECK IF NEED
        this.orderId = orderId;
    }


    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    

    public void setOptionalPick(String optionalPick)
    {
    	this.optionalPick = optionalPick;
    }

    public String getOptionalPick()
    {
    	return optionalPick;
    }
    
    // Getter and setter for dishName
    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    @Override
    public String toString() {
        return "DishInOrder{" +
                "orderId=" + orderId +
                ", dishId=" + dishId +
                ", dishName=" + dishName +
                ", comment=" + comment +
                '}';
    }
}
