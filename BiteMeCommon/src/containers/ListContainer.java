package containers;

import java.io.Serializable;
import java.util.List;

import restaurantEntities.Dish;
import restaurantEntities.DishInOrder;
import restaurantEntities.Order;

public class ListContainer implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Dish> listDish;
	private List<Object> listObject;
	private List<Order> listOrder;
	private List<DishInOrder> listDishInOrder;
	private List<String> listString;
	private List<Double> listDouble;
	
	
	
    public List<Double> getListDouble() {
		return listDouble;
	}

	public void setListDouble(List<Double> listDouble) {
		this.listDouble = listDouble;
	}

	public List<String> getListString() {
		return listString;
	}

	public void setListString(List<String> listString) {
		this.listString = listString;
	}

	public List<DishInOrder> getListDishInOrder() {
		return listDishInOrder;
	}

	public void setListDishInOrder(List<DishInOrder> listDishInOrder) {
		this.listDishInOrder = listDishInOrder;
	}

	public List<Dish> getListDish() {
        return listDish;
    }

    public void setListDish(List<Dish> listDish) {
        this.listDish = listDish;
    }
    
    public List<Object> getlistObject() {
        return listObject;
    }

    public void setlistObject(List<Object> listObject) {
        this.listObject = listObject;
    }
    
    public List<Order> getlistOrder() {
        return listOrder;
    }

    public void setlistOrder(List<Order> listOrder) {
        this.listOrder = listOrder;
    }
}
