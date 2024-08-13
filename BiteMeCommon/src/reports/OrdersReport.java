package reports;

import java.util.HashMap;
import java.util.Map;

import enums.EnumBranch;
import enums.EnumDish;

/**
 * Represents an Orders Report for a specific branch, month, and year.
 * Implements Serializable for data persistence.
 */
public class OrdersReport extends MonthlyReport{

	/**
     * for Serializable use.
     */
	private static final long serialVersionUID = 1L;
	
    /**
     * A map storing the amount of each dish type ordered.
     */
	private Map<EnumDish, Integer> dishTypeAmountMap;

	/**
     * Constructs a new OrdersReport.
     *
     * @param restaurant The branch for which the report is generated.
     * @param month The month of the report.
     * @param year The year of the report.
     */
    public OrdersReport(EnumBranch restaurant, int month, int year) {
		super(restaurant, month, year);
        this.dishTypeAmountMap = new HashMap<>();
    }

    /**
     * Gets the map of dish types and their ordered amounts.
     *
     * @return The map of dish types and amounts.
     */
	public Map<EnumDish, Integer> getDishTypeAmountMap() {
		return dishTypeAmountMap;
	}

	/**
     * Sets the map of dish types and their ordered amounts.
     *
     * @param dishTypeAmountMap The map to set.
     */
	public void setDishTypeAmountMap(Map<EnumDish, Integer> dishTypeAmountMap) {
		this.dishTypeAmountMap = dishTypeAmountMap;
	}
	
	/**
     * Adds or updates the amount for a specific dish type in the map.
     *
     * @param dish The dish type to add or update.
     * @param amount The amount to add.
     */
	public void addToDishTypeAmountMap(EnumDish dish, int amount) {
	    if (dishTypeAmountMap.containsKey(dish)) {
	        int currentAmount = dishTypeAmountMap.get(dish);
	        dishTypeAmountMap.put(dish, currentAmount + amount);
	    } else {
	        dishTypeAmountMap.put(dish, amount);
	    }
	}
    
}
