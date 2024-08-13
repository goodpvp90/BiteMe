package userEntities;

import java.io.Serializable;
import enums.EnumBranch;
import enums.EnumType;

/**
 * The User class represents a user entity with personal details, login information, and account type.
 */
public class User implements Serializable {

	/**
     * for Serializable use.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The first name of the user.
     */
    private String firstName;

    /**
     * The last name of the user.
     */
    private String lastName;

    /**
     * The email address of the user.
     */
    private String Email;

    /**
     * The phone number of the user.
     */
    private String phone;

    /**
     * The username of the user.
     */
    private String username;

    /**
     * The password of the user.
     */
    private String password;

    /**
     * The credit card information of the user.
     */
    private String creditCard;

    /**
     * The ID of the user.
     */
    private String id;

    /**
     * Indicates whether the user is logged in.
     */
    private boolean isLogged;

    /**
     * The type of the user's account.
     */
    private EnumType type;

    /**
     * The type of the user's customer account.
     */
    private EnumType customerType;

    /**
     * The home branch of the user.
     */
    private EnumBranch homeBranch;

    /**
     * Constructs a new User for login validation.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Constructs a new User for registration with full details.
     *
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param email The email address of the user.
     * @param phone The phone number of the user.
     * @param homeBranch The home branch of the user.
     * @param username The username of the user.
     * @param password The password of the user.
     * @param isLogged Indicates whether the user is logged in.
     * @param type The type of the user's account.
     */
    public User(String firstName, String lastName, String email, String phone, EnumBranch homeBranch,
                String username, String password, boolean isLogged, EnumType type) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.Email = email;
        this.phone = phone;
        this.homeBranch = homeBranch;
        this.username = username;
        this.password = password;
        this.isLogged = isLogged;
        this.type = type;
    }

    /**
     * Returns the first name of the user.
     *
     * @return The first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the user.
     *
     * @param firstName The first name to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the last name of the user.
     *
     * @return The last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the user.
     *
     * @param lastName The last name to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the email address of the user.
     *
     * @return The email address.
     */
    public String getEmail() {
        return Email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email The email address to set.
     */
    public void setEmail(String email) {
        this.Email = email;
    }

    /**
     * Returns the phone number of the user.
     *
     * @return The phone number.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number of the user.
     *
     * @param phone The phone number to set.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns the home branch of the user.
     *
     * @return The home branch.
     */
    public EnumBranch getHomeBranch() {
        return homeBranch;
    }

    /**
     * Sets the home branch of the user.
     *
     * @param homeBranch The home branch to set.
     */
    public void setHomeBranch(EnumBranch homeBranch) {
        this.homeBranch = homeBranch;
    }

    /**
     * Returns whether the user is logged in.
     *
     * @return true if the user is logged in, false otherwise.
     */
    public boolean isLogged() {
        return isLogged;
    }

    /**
     * Sets the login status of the user.
     *
     * @param isLogged The login status to set.
     */
    public void setLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }

    /**
     * Returns the type of the user's account.
     *
     * @return The account type.
     */
    public EnumType getType() {
        return type;
    }

    /**
     * Sets the type of the user's account.
     *
     * @param type The account type to set.
     */
    public void setType(EnumType type) {
        this.type = type;
    }

    /**
     * Returns the type of the user's customer account.
     *
     * @return The customer account type.
     */
    public EnumType getCustomerType() {
        return customerType;
    }

    /**
     * Sets the type of the user's customer account.
     *
     * @param customerType The customer account type to set.
     */
    public void setCustomerType(EnumType customerType) {
        this.customerType = customerType;
    }

    /**
     * Returns the username of the user.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password of the user.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the credit card information of the user.
     *
     * @return The credit card information.
     */
    public String getCreditCard() {
        return creditCard;
    }

    /**
     * Sets the credit card information of the user.
     *
     * @param creditCard The credit card information to set.
     */
    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    /**
     * Returns the ID of the user.
     *
     * @return The user ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the user.
     *
     * @param id The user ID to set.
     */
    public void setId(String id) {
        this.id = id;
    }
}
