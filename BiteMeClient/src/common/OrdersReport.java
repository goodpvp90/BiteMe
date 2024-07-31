package common;

import java.util.HashMap;
import java.util.Map;

import common.Restaurant.Location;

public class OrdersReport extends MonthlyReport{
    private Map<EnumDish, Integer> dishTypeAmountMap;


    // Constructors
    public OrdersReport(Location restaurant, int month, int year) {
		super(restaurant, month, year);
        this.dishTypeAmountMap = new HashMap<>();
    }

	public Map<EnumDish, Integer> getDishTypeAmountMap() {
		return dishTypeAmountMap;
	}


	public void setDishTypeAmountMap(Map<EnumDish, Integer> dishTypeAmountMap) {
		this.dishTypeAmountMap = dishTypeAmountMap;
	}
	
	public void addToDishTypeAmountMap(EnumDish dish, int amount) {
	    if (dishTypeAmountMap.containsKey(dish)) {
	        int currentAmount = dishTypeAmountMap.get(dish);
	        dishTypeAmountMap.put(dish, currentAmount + amount);
	    } else {
	        dishTypeAmountMap.put(dish, amount);
	    }
	}
    
}
