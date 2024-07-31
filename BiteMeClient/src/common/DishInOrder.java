package common;

public class DishInOrder {
    private int orderId; //Check if needed, currently unused
    private Dish dish;
    private String comment;

    // Constructors
    public DishInOrder(Dish dish, String comment) {
        this.dish = dish;
        this.comment = comment;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
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
                ", dishId=" + dish +
                ", comment=" + comment +
                '}';
    }
}
