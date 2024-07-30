package common;

public class DishInOrder {
    private int orderId; //Check if needed, currently unused
    private int dishId;
    private String comment;

    // Constructors
    public DishInOrder(int dishId, String comment) {
        this.dishId = dishId;
        this.comment = comment;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "DishInOrder{" +
                "orderId=" + orderId +
                ", dishId=" + dishId +
                ", comment=" + comment +
                '}';
    }
}
