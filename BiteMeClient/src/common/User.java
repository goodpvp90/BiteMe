package common;

import java.io.Serializable;

public class User implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String firstName, lastName, Email, phone, username, password;
	private boolean isLogged;
	private EnumType type;
	private EnumBranch homeBranch;
	
	//constructor for validate login
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}


	//full constructor for register
	public User(String firstName, String lastName, String email, String phone, EnumBranch homeBranch,
			String username, String password, boolean isLogged, EnumType type) {
		this.firstName = firstName;
		this.lastName = lastName;
		Email = email;
		this.phone = phone;
		this.homeBranch = homeBranch;
		this.username = username;
		this.password = password;
		this.isLogged = isLogged;
		this.type = type;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getEmail() {
		return Email;
	}


	public void setEmail(String email) {
		Email = email;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public EnumBranch getHomeBranch() {
		return homeBranch;
	}


	public void setHomeBranch(EnumBranch homeBranch) {
		this.homeBranch = homeBranch;
	}


	public boolean isLogged() {
		return isLogged;
	}


	public void setLogged(boolean isLogged) {
		this.isLogged = isLogged;
	}


	public EnumType getType() {
		return type;
	}


	public void setType(EnumType type) {
		this.type = type;
	}


	public String getUsername() {
		return username;
	}


	public String getPassword() {
		return password;
	}
	
	
}
