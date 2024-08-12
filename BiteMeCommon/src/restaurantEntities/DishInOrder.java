package restaurantEntities;

import java.io.Serializable;

/**
 * The DishInOrder class represents a dish included in an order.
 */
public class DishInOrder implements Serializable {

	/**
     * for Serializable use.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for the order the dish belong to.
     */
    private int orderId;

    /**
     * Additional comments or notes supplied from order.
     */
    private String comment;

    /**
     * The selected optional item for the dish.
     */
    private String optionalPick;

    /**
     * Unique identifier for the dish.
     */
    private int dishId;

    /**
     * The name of the dish.
     */
    private String dishName;

    /**
     * Parameterized constructor for the DishInOrder class.
     * Initializes a new instance with the specified dish name, dish ID, comment, and optional pick.
     *
     * @param dishName The name of the dish.
     * @param dishId The unique identifier for the dish.
     * @param comment Additional comments or notes about the dish.
     * @param opt The selected optional choice for the dish.
     */
    public DishInOrder(String dishName, int dishId, String comment, String opt) {
        this.dishName = dishName;
        this.dishId = dishId;
        this.comment = comment;
        this.optionalPick = opt;
    }

    /**
     * Returns the unique identifier for the order the dish belong to.
     *
     * @return The order ID.
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * Sets the unique identifier for the order the dish belong to.
     *
     * @param orderId The order ID to set.
     */
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    /**
     * Returns additional comments or notes about the dish.
     *
     * @return The comments.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets additional comments or notes about the dish.
     *
     * @param comment The comments to set.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Returns the selected optional item for the dish.
     *
     * @return The optional item.
     */
    public String getOptionalPick() {
        return optionalPick;
    }

    /**
     * Sets the selected optional choice for the dish.
     *
     * @param optionalPick The optional item to set.
     */
    public void setOptionalPick(String optionalPick) {
        this.optionalPick = optionalPick;
    }

    /**
     * Returns the name of the dish.
     *
     * @return The dish name.
     */
    public String getDishName() {
        return dishName;
    }

    /**
     * Sets the name of the dish.
     *
     * @param dishName The dish name to set.
     */
    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    
}
