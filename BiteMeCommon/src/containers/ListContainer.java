package containers;

import java.io.Serializable;
import java.util.List;

import restaurantEntities.Dish;
import restaurantEntities.DishInOrder;
import restaurantEntities.Order;

/**
 * A container class that holds lists of various types of objects. 
 * This class implements Serializable to allow its instances to be serialized.
 */
public class ListContainer implements Serializable {
    /**
	 * Serializable auto parameter
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The list of Dish objects.
	 */
	private List<Dish> listDish;

	/**
	 * The list of Object objects.
	 */
	private List<Object> listObject;

	/**
	 * The list of Order objects.
	 */
	private List<Order> listOrder;

	/**
	 * The list of DishInOrder objects.
	 */
	private List<DishInOrder> listDishInOrder;

	/**
	 * The list of String objects.
	 */
	private List<String> listString;

	/**
	 * The list of Double objects.
	 */
	private List<Double> listDouble;
	
	
    /**
     * Returns the list of Double objects.
     * 
     * @return the list of doubles
     */
    public List<Double> getListDouble() {
		return listDouble;
	}

    /**
     * Sets the list of Double objects.
     * 
     * @param listDouble the list of doubles to set
     */
	public void setListDouble(List<Double> listDouble) {
		this.listDouble = listDouble;
	}

    /**
     * Returns the list of String objects.
     * 
     * @return the list of strings
     */
	public List<String> getListString() {
		return listString;
	}

    /**
     * Sets the list of String objects.
     * 
     * @param listString the list of strings to set
     */
	public void setListString(List<String> listString) {
		this.listString = listString;
	}

    /**
     * Returns the list of DishInOrder objects.
     * 
     * @return the list of dishes in order
     */
	public List<DishInOrder> getListDishInOrder() {
		return listDishInOrder;
	}

    /**
     * Sets the list of DishInOrder objects.
     * 
     * @param listDishInOrder the list of dishes in order to set
     */
	public void setListDishInOrder(List<DishInOrder> listDishInOrder) {
		this.listDishInOrder = listDishInOrder;
	}

    /**
     * Returns the list of Dish objects.
     * 
     * @return the list of dishes
     */
	public List<Dish> getListDish() {
        return listDish;
    }

    /**
     * Sets the list of Dish objects.
     * 
     * @param listDish the list of dishes to set
     */
    public void setListDish(List<Dish> listDish) {
        this.listDish = listDish;
    }
    
    /**
     * Returns the list of Object objects.
     * 
     * @return the list of objects
     */
    public List<Object> getlistObject() {
        return listObject;
    }

    /**
     * Sets the list of Object objects.
     * 
     * @param listObject the list of objects to set
     */
    public void setlistObject(List<Object> listObject) {
        this.listObject = listObject;
    }
    
    /**
     * Returns the list of Order objects.
     * 
     * @return the list of orders
     */
    public List<Order> getlistOrder() {
        return listOrder;
    }

    /**
     * Sets the list of Order objects.
     * 
     * @param listOrder the list of orders to set
     */
    public void setlistOrder(List<Order> listOrder) {
        this.listOrder = listOrder;
    }
}
