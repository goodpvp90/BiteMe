package restaurantEntities;

import java.awt.Menu;
import java.io.Serializable;

import enums.EnumBranch;

/**
 * The Restaurant class represents a restaurant entity with its menu, name, branch ID, and location.
 */
public class Restaurant implements Serializable {

	/**
     * for Serializable use.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The menu of the restaurant.
     */
    private Menu menu;

    /**
     * The name of the restaurant.
     */
    private String name;

    /**
     * The unique identifier for the restaurant's branch.
     */
    private int BranchID;

    /**
     * The location of the restaurant represented by an EnumBranch.
     */
    private EnumBranch location;

    /**
     * Constructs a new Restaurant with the specified menu, name, branch ID, and location.
     *
     * @param menu The menu of the restaurant.
     * @param name The name of the restaurant.
     * @param branchID The unique identifier for the restaurant's branch.
     * @param location The location of the restaurant.
     */
    public Restaurant(Menu menu, String name, int branchID, EnumBranch location) {
        this.menu = menu;
        this.name = name;
        this.BranchID = branchID;
        this.location = location;
    }

    /**
     * Returns the menu of the restaurant.
     *
     * @return The menu.
     */
    public Menu getMenu() {
        return menu;
    }

    /**
     * Sets the menu of the restaurant.
     *
     * @param menu The menu to set.
     */
    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    /**
     * Returns the name of the restaurant.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the restaurant.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the unique identifier for the restaurant's branch.
     *
     * @return The branch ID.
     */
    public int getBranchID() {
        return BranchID;
    }

    /**
     * Sets the unique identifier for the restaurant's branch.
     *
     * @param branchID The branch ID to set.
     */
    public void setBranchID(int branchID) {
        this.BranchID = branchID;
    }

    /**
     * Returns the location of the restaurant.
     *
     * @return The location.
     */
    public EnumBranch getLocation() {
        return location;
    }

    /**
     * Sets the location of the restaurant.
     *
     * @param location The location to set.
     */
    public void setLocation(EnumBranch location) {
        this.location = location;
    }
}
