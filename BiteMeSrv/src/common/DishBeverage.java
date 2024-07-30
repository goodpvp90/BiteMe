package common;

import java.util.ArrayList;
import java.util.Arrays;

public class DishBeverage extends Dish {


	private static final long serialVersionUID = -5732423076855052222L;
	
	private ArrayList<String> optionals;
	
	
	public DishBeverage() {
		super();
	}

	public DishBeverage(String dishName, double price, int menuId) {
		super(dishName, price, menuId);
		this.setDishType(EnumDish.BEVERAGE);
        optionals = new ArrayList<>(Arrays.asList("Big", "Small"));
	}
	
    // Method to get the optionals list
    public ArrayList<String> getOptionals() {
        return optionals;
    }
	
    // Method to add an optional value to the list
    public boolean addOptional(String optional) {
        if (optional != null && !optionals.contains(optional)) {
            optionals.add(optional);
            return true; // Indicate that the value was successfully added
        }
        return false; // Indicate that the value was not added (either null or already exists)
    }
}
