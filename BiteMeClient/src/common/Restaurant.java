package common;

import java.io.Serializable;

public class Restaurant implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum Location{
		NORTH,
		CENTER,
		SOUTH;
	}
	
	private Menu menu;
	private String name;
	private int BranchID;
	private Location location;
	
	public Restaurant(Menu menu, String name, int branchID, Location location) {
		this.menu = menu;
		this.name = name;
		BranchID = branchID;
		this.location = location;
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBranchID() {
		return BranchID;
	}

	public void setBranchID(int branchID) {
		BranchID = branchID;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	

}
