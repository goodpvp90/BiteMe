package common;

import java.util.ArrayList;
import java.util.Arrays;

public class DishMainCourse extends Dish{

	
	private ArrayList<String> optionals;
	private boolean isGrill;
	
	public DishMainCourse() {
		super();
	}

	public DishMainCourse(String dishName,boolean isGrill, double price, int menuId) {
		super(dishName, price, menuId);
		this.setDishType(EnumDish.MAIN_COURSE);
		this.isGrill = isGrill;
		if (isGrill)
			optionals = new ArrayList<>(Arrays.asList("Medium","Well"));
		else
			optionals = new ArrayList<String>();
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
