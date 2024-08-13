package controllers;

import java.sql.SQLException;

import java.util.List;

import containers.ListContainer;
import enums.EnumClientOperations;
import enums.EnumServerOperations;
import ocsf.server.ConnectionToClient;
import restaurantEntities.Dish;
import restaurantEntities.Order;
import server.Server;

/**
 * Manages restaurant-related operations for the server.
 **/
public class RestaurantController {
    /**
     * The server instance.
     */
    private Server server;
    
    /**
     * The database controller instance.
     */
    private DBController dbController;
    
    /**
     * The notification controller instance.
     */
    private NotificationController notificationController;
    
    /**
     * Constructor for RestaurantController.
     *
     * @param server               the server instance
     * @param dbController         the database controller instance
     * @param notificationController the notification controller instance
     */
	public RestaurantController(Server server, DBController dbController, NotificationController notificationController) {
		this.server = server;		
		this.dbController = dbController;
		this.notificationController = notificationController;
	}
	
    /**
     * Handles the addition of a new dish.
     *
     * @param client  the client connection
     * @param message the message containing dish details
     */
	public void handleAddDish(ConnectionToClient client, Object[] message) {
        Dish dish = (Dish) message[1];
        boolean addResult = addDish(dish);
        server.sendMessageToClient(EnumClientOperations.ADD_DISH, client, addResult);
	}
	
    /**
     * Adds a new dish to the menu.
     *
     * @param dish the dish to add
     * @return true if the dish was added successfully, false otherwise
     */
    private boolean addDish(Dish dish) {
    	boolean result = dbController.addDish(dish);
    	if (result)
    		notificationController.notifyUpdatedMenu();
        return result;
    }
    
    /**
     * Handles the update of an existing dish.
     *
     * @param client  the client connection
     * @param message the message containing updated dish details
     */
	public void handleUpdateDish(ConnectionToClient client, Object[] message) {
        boolean updateResult = updateDish((Dish) message[1]);
        server.sendMessageToClient(EnumClientOperations.UPDATE_DISH, client, updateResult);
	}
    
    /**
     * Updates an existing dish in the menu.
     *
     * @param dish the dish to update
     * @return true if the dish was updated successfully, false otherwise
     */
    private boolean updateDish(Dish dish) {
    	boolean result =  dbController.updateDish(dish);
    	if (result)
    		notificationController.notifyUpdatedMenu();
    	return result;
    }
    
    /**
     * Handles the deletion of a dish.
     *
     * @param client  the client connection
     * @param message the message containing dish details to delete
     */
	public void handleDeleteDish(ConnectionToClient client, Object[] message) {
        Dish dishO = (Dish)message[1];
        boolean deleteResult = deleteDish(dishO);
        server.sendMessageToClient(EnumClientOperations.DELETE_DISH, client, deleteResult);
	}
    
    /**
     * Deletes a dish from the menu.
     *
     * @param dish the dish to delete
     * @return true if the dish was deleted successfully, false otherwise
     */
    private boolean deleteDish(Dish dish) {
    	boolean result =  dbController.deleteDish(dish);
    	if (result)
    		notificationController.notifyUpdatedMenu();
    	return result;
    }
    
    /**
     * Retrieves and sends the menu to the client.
     *
     * @param client    the client connection
     * @param message   the message containing the menu ID
     * @param operation the operation to be performed
     */
	public void viewMenu(ConnectionToClient client, Object[] message, EnumServerOperations operation) {
        int menuId = (int) message[1];
        List<Dish> menu = dbController.getMenu(menuId);
	    //encapsulate the list to avoid suppress warnings
        ListContainer menuContainer = new ListContainer();
        menuContainer.setListDish(menu);
        server.sendMessageToClient(EnumClientOperations.valueOf(operation.toString()), client, menuContainer);
	}
	
    /**
     * Handles the retrieval of pending orders.
     *
     * @param client  the client connection
     * @param message the message containing branch ID
     */
	public void handlePendingOrders(ConnectionToClient client, Object[] message) {
        List<Order> pendingOrders = getPendingOrdersByBranch((int) message[1]);
	    //encapsulate the list to avoid suppress warnings
        ListContainer pendingOrdersContainer = new ListContainer();
        pendingOrdersContainer.setlistOrder(pendingOrders);
        server.sendMessageToClient(EnumClientOperations.PENDING_ORDER, client, pendingOrdersContainer);
	}
	
    /**
     * Retrieves pending orders for a specific branch.
     *
     * @param branchId the branch ID
     * @return the list of pending orders for the specified branch
     */
	private List<Order> getPendingOrdersByBranch(int branchId) {
        try {
            return dbController.showPendingOrdersByBranch(branchId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
