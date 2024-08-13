package restaurantEntities;


import java.io.Serializable;
import java.util.ArrayList;
import enums.EnumDish;

/**
 * The Dish class represents a dish in the restaurant's menu, constructors, getters, setters.
 */
public abstract class Dish implements Serializable {
    
	/**
     * for Serializable use.
     */
	private static final long serialVersionUID = 1L;
	
	/**
     * Unique identifier for the dish, auto-incremented by the database.
     */
    private int dishId;

    /**
     * The type of the dish, defined by EnumDish.
     */
    private EnumDish dishType;

    /**
     * The name of the dish.
     */
    private String dishName;

    /**
     * The price of the dish.
     */
    private double price;

    /**
     * The ID associated with the menu to which the dish belongs.
     */
    private int menu_id;

    /**
     * Additional comments or notes notes supplied in the order.
     */
    private String comments;

    /**
     * A list of optional items or choices associated with the dish.
     */
    protected ArrayList<String> optionals;

    /**
     * Indicates whether the dish is grilled.
     */
    private boolean isGrill;

    /**
     * The selected optional item for the dish.
     */
    private String optionalPick;

   
    /**
     * Parameterized constructor for the Dish class.
     * Initializes a new instance with the specified dish name, price, menu ID, and grilling status.
     *
     * @param dishName The name of the dish.
     * @param price The price of the dish.
     * @param menuId The ID associated with the menu to which the dish belongs.
     * @param isGrill Indicates whether the dish is grilled.
     */
    public Dish(String dishName, double price, int menuId, boolean isGrill) {
        this.dishName = dishName;
        this.price = price;
        this.menu_id = menuId; // Initialize the menu_id
        this.comments = "Add Comment Here";
		this.isGrill = isGrill;
        this.optionalPick ="";
    }

    /**
     * Returns whether the dish is grilled.
     *
     * @return true if the dish is grilled, false otherwise.
     */
    public boolean isGrill() {
        return isGrill;
    }

    /**
     * Sets whether the dish is grilled.
     *
     * @param isGrill true if the dish should be grilled, false otherwise.
     */
    public void setGrill(boolean isGrill) {
        this.isGrill = isGrill;
    }

    /**
     * Returns the dish ID.
     *
     * @return The dish ID.
     */
    public int getDishId() {
        return dishId;
    }

    /**
     * Sets the dish ID.
     *
     * @param dishId The dish ID to set.
     */
    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    /**
     * Returns the type of the dish.
     *
     * @return The dish type, defined by EnumDish.
     */
    public EnumDish getDishType() {
        return dishType;
    }

    /**
     * Sets the type of the dish.
     *
     * @param dishType The dish type to set.
     */
    public void setDishType(EnumDish dishType) {
        this.dishType = dishType;
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

    /**
     * Returns the price of the dish.
     *
     * @return The dish price.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the price of the dish.
     *
     * @param price The dish price to set.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Returns the ID associated with the menu to which the dish belongs.
     *
     * @return The menu ID.
     */
    public int getMenuId() {
        return menu_id;
    }

    /**
     * Sets the ID associated with the menu to which the dish belongs.
     *
     * @param menuId The menu ID to set.
     */
    public void setMenuId(int menuId) {
        this.menu_id = menuId;
    }


    /**
     * Sets additional comments or notes notes supplied in the order.
     *
     * @param comments The comments to set.
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * Returns additional comments or notes supplied in the order.
     *
     * @return The comments.
     */
    public String getComments() {
        return comments;
    }

    /**
     * Returns the list of optionals or choices associated with the dish.
     *
     * @return The list of optionals.
     */
    public ArrayList<String> getOptionals() {
        return optionals;
    }

    /**
     * Sets the selected optional pick for the dish.
     *
     * @param optionalPick The optional pick to set.
     */
    public void setOptionalPick(String optionalPick) {
        this.optionalPick = optionalPick;
    }

    /**
     * Returns the selected optional pick for the dish.
     *
     * @return The selected optional pick.
     */
    public String getOptionalPick() {
        return optionalPick;
    }
    
}