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

public class RestaurantController {
	private Server server;
	private DBController dbController;
	private NotificationController notificationController;
	
	public RestaurantController(Server server, DBController dbController, NotificationController notificationController) {
		this.server = server;		
		this.dbController = dbController;
		this.notificationController = notificationController;
	}
	
	public void handleAddDish(ConnectionToClient client, Object[] message) {
        Dish dish = (Dish) message[1];
        boolean addResult = addDish(dish);
        server.sendMessageToClient(EnumClientOperations.ADD_DISH, client, addResult);
	}
	
    private boolean addDish(Dish dish) {
    	boolean result = dbController.addDish(dish);
    	if (result)
    		notificationController.notifyUpdatedMenu();
        return result;
    }
    
	public void handleUpdateDish(ConnectionToClient client, Object[] message) {
        boolean updateResult = updateDish((Dish) message[1]);
        server.sendMessageToClient(EnumClientOperations.UPDATE_DISH, client, updateResult);
	}
    
    private boolean updateDish(Dish dish) {
    	boolean result =  dbController.updateDish(dish);
    	if (result)
    		notificationController.notifyUpdatedMenu();
    	return result;
    }
    
	public void handleDeleteDish(ConnectionToClient client, Object[] message) {
        Dish dishO = (Dish)message[1];
        boolean deleteResult = deleteDish(dishO);
        server.sendMessageToClient(EnumClientOperations.DELETE_DISH, client, deleteResult);
	}
    
    private boolean deleteDish(Dish dish) {
    	boolean result =  dbController.deleteDish(dish);
    	if (result)
    		notificationController.notifyUpdatedMenu();
    	return result;
    }
    
	public void viewMenu(ConnectionToClient client, Object[] message, EnumServerOperations operation) {
        int menuId = (int) message[1];
        List<Dish> menu = dbController.getMenu(menuId);
	    //encapsulate the list to avoid suppress warnings
        ListContainer menuContainer = new ListContainer();
        menuContainer.setListDish(menu);
        server.sendMessageToClient(EnumClientOperations.valueOf(operation.toString()), client, menuContainer);
	}
	
	public void handlePendingOrders(ConnectionToClient client, Object[] message) {
        List<Order> pendingOrders = getPendingOrdersByBranch((int) message[1]);
	    //encapsulate the list to avoid suppress warnings
        ListContainer pendingOrdersContainer = new ListContainer();
        pendingOrdersContainer.setlistOrder(pendingOrders);
        server.sendMessageToClient(EnumClientOperations.PENDING_ORDER, client, pendingOrdersContainer);
	}
	
    // Retrieve pending orders for a specific branch
    private List<Order> getPendingOrdersByBranch(int branchId) {
        try {
            return dbController.showPendingOrdersByBranch(branchId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
