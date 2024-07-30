package common;


import java.io.Serializable;

public abstract class Dish implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int dishId; // This field will be auto-incremented by the database
    private EnumDish dishType;
    private String dishName;
    private double price;
    private int menu_id; // Field to associate with the menu

    // Default constructor
    public Dish() {}

    // Parameterized constructor
    public Dish(String dishName, double price, int menuId) {
        this.dishName = dishName;
        this.price = price;
        this.menu_id = menuId; // Initialize the menu_id
    }

    // Getter and setter for dishId
    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    // Getter and setter for dishType
    public EnumDish getDishType() {
        return dishType;
    }

    public void setDishType(EnumDish dishType) { //CHECK IF NEEDED, not used now.
        this.dishType = dishType;
    }

    // Getter and setter for dishName
    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    // Getter and setter for price
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Getter and setter for menuId
    public int getMenuId() {
        return menu_id;
    }

    public void setMenuId(int menuId) {
        this.menu_id = menuId;
    }

    @Override
    public String toString() {
        return "Dish{" +
               "dishId=" + dishId +
               ", dishType=" + dishType +
               ", dishName='" + dishName + '\'' +
               ", price=" + price +
               ", menuId=" + menu_id +
               '}';
    }
}
