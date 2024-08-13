package controllers;

import java.sql.SQLException;
import java.util.List;

import containers.ListContainer;
import enums.EnumBranch;
import enums.EnumClientOperations;
import enums.EnumType;
import ocsf.server.ConnectionToClient;
import restaurantEntities.Order;
import server.Server;
import userEntities.User;

/**
 * Manages user-related operations for the server.
 **/
public class UserController {
    /**
     * The server instance.
     */
    private Server server;
    
    /**
     * The notification controller instance.
     */
    private NotificationController notificationController;
    
    /**
     * The database controller instance.
     */
    private DBController dbController;

    /**
     * Constructor for UserController.
     *
     * @param server the server instance
     * @param notificationController the notification controller instance
     * @param dbController the database controller instance
     */
    public UserController(Server server, NotificationController notificationController, DBController dbController) {
		this.server = server;
		this.notificationController = notificationController;
		this.dbController = dbController;
	}
    
    /**
     * Handles user login.
     *
     * @param client  the client connection
     * @param message the message containing user login details
     */
    public void handleLogin(ConnectionToClient client, Object[] message) {
    	User user = (User)message[1];
        String username = user.getUsername();
        List<String> notifications = null;
        boolean loginReuslt = login(client, (Object[]) message);
        if (loginReuslt) {
        	client.setInfo("user", username);
        	try {
                notifications = dbController.getPendingNotifications(username);
                dbController.deletePendingNotifications(username);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if(!notifications.isEmpty()) {
        	    //encapsulate the list to avoid suppress warnings
            	ListContainer notificationsContainter = new ListContainer();
            	notificationsContainter.setListString(notifications);
            	server.sendMessageToClient(EnumClientOperations.NOTIFICATION, client, notificationsContainter);
            }
        }
	}
    
    /**
     * Performs the login operation.
     *
     * @param client  the client connection
     * @param message the message containing user login details
     * @return true if login was successful, false otherwise
     */
	private boolean login(ConnectionToClient client, Object[] message) {
    	User user = (User)message[1];
        Object result = dbController.validateLogin(user);
		if (result instanceof String) {
			server.sendMessageToClient(EnumClientOperations.USER,client, result);
			return false;
		}
		else {
			ListContainer userDetailsContainer = (ListContainer)result;
			List<Object> details = userDetailsContainer.getlistObject();
      
			user.setFirstName((String)details.get(0));
			user.setLastName((String)details.get(1));
			user.setEmail((String)details.get(2));
			user.setPhone((String) details.get(3));
			user.setHomeBranch((EnumBranch)details.get(4));
			user.setLogged((boolean)details.get(7));
			user.setType((EnumType)details.get(8));
			
			if (user.getType() == EnumType.CUSTOMER)
				user.setCustomerType(dbController.getCustomerType(user.getUsername()));
			//client.setInfo("user", user); //Store information into client object
			notificationController.addToClients(user.getUsername(), client);
			server.sendMessageToClient(EnumClientOperations.USER,client,(Object)user);
			return true;
		}
    }
    
    /**
     * Handles user logout.
     *
     * @param client  the client connection
     * @param message the message containing user logout details
     */
    public void logout(ConnectionToClient client, Object[] message) {
        User user = (User) message[1];
        boolean logoutSuccess = dbController.logout(user.getUsername());

        if (logoutSuccess) {
            user.setLogged(false);
            notificationController.removeFromClients(user.getUsername());
        }

    }
    
    /**
     * Handles checking if a username exists.
     *
     * @param client  the client connection
     * @param message the message containing the username to check
     */
	public void handleCheckUser(ConnectionToClient client, Object[] message) {
    	String usern = (String) message[1];
    	checkUserForCreation(client,usern);
	}   

    /**
     * Checks if a username exists in the database.
     *
     * @param client   the client connection
     * @param username the username to check
     */
    private void checkUserForCreation(ConnectionToClient client, String username) {
        try {
            User result = dbController.searchUsername(username);
            if (result != null) {
                server.sendMessageToClient(EnumClientOperations.CHECK_USER, client, result);
            } else {
                server.sendMessageToClient(EnumClientOperations.CHECK_USER, client, false); // No username found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            server.sendMessageToClient(EnumClientOperations.CHECK_USER, client, false); // Send false in case of error
        }
    }
    
    /**
     * Handles account creation.
     *
     * @param client  the client connection
     * @param message the message containing user account details
     */
    public void createAccount(ConnectionToClient client, Object[] message) {
        User user = (User)message[1];
        boolean result = false;
        try {
            result = dbController.createUser(user.getId(), user.getUsername(), user.getPassword(), user.getEmail(), user.getPhone(),
                    user.getFirstName(), user.getLastName(), user.getHomeBranch(), user.getType(), user.getCustomerType(), user.getCreditCard());
        } catch (Exception e) {
            e.printStackTrace();
        }
    	if (result) 
    		server.sendMessageToClient(EnumClientOperations.CREATED_ACCOUNT,client, (Object)user);
    	
    }
    
    /**
     * Handles changing a user's home branch.
     *
     * @param client  the client connection
     * @param message the message containing user and new home branch details
     */
	public void handleChangeHomeBranch(ConnectionToClient client, Object[] message) {
    	boolean changeResult = dbController.changeHomeBranch((User)message[1]);
    	server.sendMessageToClient(EnumClientOperations.CHANGE_HOME_BRANCH, client, changeResult);
	}   
    
    /**
     * Handles retrieving the current discount amount for a user.
     *
     * @param client  the client connection
     * @param message the message containing the username
     */
	public void handleGetDiscount(ConnectionToClient client, Object[] message) {
    	String username = (String)message[1];
    	double amount = dbController.getCurrentDiscountAmount(username);
    	server.sendMessageToClient(EnumClientOperations.GET_DISCOUNT_AMOUNT, client, amount);
	}   
	
    /**
     * Handles setting the discount amount for a user.
     *
     * @param client  the client connection
     * @param message the message containing the username and discount amount
     */
	public void handleSetDiscount(ConnectionToClient client, Object[] message) {
    	String username1 = (String)message[1];
    	double amount1 = (double)message[2];
    	dbController.updateDiscountAmount(username1, amount1);
	}   
	
    /**
     * Handles retrieving the orders for a specific user.
     *
     * @param client  the client connection
     * @param message the message containing the username
     */	public void handleUserOrders(ConnectionToClient client, Object[] message) {
    	List<Order> orders = dbController.getOrdersByUsername((String)message[1]);
    	//encapsulate the list to avoid suppress warnings
    	ListContainer ordersContainer = new ListContainer();
    	ordersContainer.setlistOrder(orders);
    	server.sendMessageToClient(EnumClientOperations.USERS_ORDERS, client, ordersContainer);

	}   	

}