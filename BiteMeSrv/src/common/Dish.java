package common;


import java.io.Serializable;
import java.util.ArrayList;

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
    private String comments;//OFEK-------------------------
    protected ArrayList<String> optionals;//OFEK-------------------------
    private String optionalPick;//OFEK-------------------------
    private boolean isGrill;
    
    // Default constructor
    public Dish() {}

    // Parameterized constructor
    public Dish(String dishName, double price, int menuId, boolean isGrill) {
        this.dishName = dishName;
        this.price = price;
        this.menu_id = menuId; // Initialize the menu_id
        this.comments = "Add Comment Here";//OFEK-------------------------
        this.optionalPick ="";//OFEK-------------------------
		this.isGrill = isGrill;
    }

    public boolean isGrill() {
		return isGrill;
	}

	public void setGrill(boolean isGrill) {
		this.isGrill = isGrill;
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
    
    //OFEK-------------------------
    public void setComments(String comments) { 
    	this.comments = comments;
	} 
    //OFEK-------------------------
    public String getComments() 
    {
    	return comments; 
	}
    //OFEK-------------------------
    public ArrayList<String> getOptionals() {
        return optionals;
    }
    //OFEK-------------------------
    public void setOptionalPick(String optionalPick)
    {
    	this.optionalPick = optionalPick;
    }
    //OFEK-------------------------	
    public String getOptionalPick()
    {
    	return optionalPick;
    }
    
}