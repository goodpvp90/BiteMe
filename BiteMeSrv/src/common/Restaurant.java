package common;

import java.io.Serializable;

public class Restaurant implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Menu menu;
	private String name;
	private int BranchID;
	private EnumBranch location;
	
	public Restaurant(Menu menu, String name, int branchID, EnumBranch location) {
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

	public EnumBranch getLocation() {
		return location;
	}

	public void setLocation(EnumBranch location) {
		this.location = location;
	}
	
	

}
