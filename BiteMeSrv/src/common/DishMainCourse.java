package common;

import java.util.ArrayList;
import java.util.Arrays;

public class DishMainCourse extends Dish{

	
	private ArrayList<String> optionals;
	
	public DishMainCourse() {
		super();
	}

	public DishMainCourse(String dishName, double price, int menuId) {
		super(dishName, price, menuId);
		this.setDishType(EnumDish.MAIN_COURSE);
        optionals = new ArrayList<>(Arrays.asList("Medium","Well"));
	}
	
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
