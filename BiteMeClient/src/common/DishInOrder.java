package common;

public class DishInOrder {
    private int orderId;
    private int dishId;
    private int quantity;

    // Constructors
    public DishInOrder(int orderid, int dishId, int d) {
        this.orderId = orderid;
        this.dishId = dishId;
        this.quantity = d;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "DishInOrder{" +
                "orderId=" + orderId +
                ", dishId=" + dishId +
                ", quantity=" + quantity +
                '}';
    }
}
