package restaurantEntities;

import java.util.ArrayList;
import java.util.Arrays;

import enums.EnumDish;

/**
 * The DishDessert class represents a dessert dish in the restaurant's menu.
 */
public class DishDessert extends Dish {

    /**
     * Parameterized constructor for the DishDessert class.
     * Initializes a new instance with the specified dish name, grilling status, price, and menu ID.
     * Sets the dish type to DESSERT.
     *
     * @param dishName The name of the dessert dish.
     * @param isGrill Indicates whether the dish is grilled.
     * @param price The price of the dish.
     * @param menuId The ID associated with the menu to which the dish belongs.
     */
    public DishDessert(String dishName, boolean isGrill, double price, int menuId) {
        super(dishName, price, menuId, isGrill);
        this.setDishType(EnumDish.DESSERT);
        optionals = new ArrayList<>(Arrays.asList());
    }

    /**
     * Returns the list of optional items or choices associated with the dessert dish.
     *
     * @return The list of optionals.
     */
    @Override
    public ArrayList<String> getOptionals() {
        return optionals;
    }

    /**
     * Adds an optional item to the list of optionals for the dessert dish.
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
