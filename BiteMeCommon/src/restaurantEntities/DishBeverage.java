package restaurantEntities;

import java.util.ArrayList;
import java.util.Arrays;

import enums.EnumDish;

/**
 * The DishBeverage class represents a beverage dish in the restaurant's menu.
 */
public class DishBeverage extends Dish {

	/**
	 * For Serializable propose 
	 */
    private static final long serialVersionUID = -5732423076855052222L;

    /**
     * Parameterized constructor for the DishBeverage class.
     * Initializes a new instance with the specified dish name, grilling status, price, and menu ID.
     * Sets the dish type to BEVERAGE and initializes the list of optionals with "Big" and "Small".
     *
     * @param dishName The name of the beverage dish.
     * @param isGrill Indicates whether the dish is grilled.
     * @param price The price of the dish.
     * @param menuId The ID associated with the menu to which the dish belongs.
     */
    public DishBeverage(String dishName, boolean isGrill, double price, int menuId) {
        super(dishName, price, menuId, isGrill);
        this.setDishType(EnumDish.BEVERAGE);
        optionals = new ArrayList<>(Arrays.asList("Big", "Small"));
    }

    /**
     * Returns the list of optional items or choices associated with the beverage dish.
     *
     * @return The list of optionals.
     */
    @Override
    public ArrayList<String> getOptionals() {
        return optionals;
    }

    /**
     * Adds an optional item to the list of optionals for the beverage dish.
     *
     * @param optional The optional item to add.
     * @return true if the item was successfully added, false if the item was null or already exists in the list.
     */
    public boolean addOptional(String optional) {
        if (optional != null && !optionals.contains(optional)) {
            optionals.add(optional);
            return true;
        }
        return false;
    }
}
