package server;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import enums.EnumClientOperations;
import enums.EnumOrderStatus;
import ocsf.server.ConnectionToClient;
import restaurantEntities.Dish;
import restaurantEntities.DishInOrder;
import restaurantEntities.Order;

public class OrderController {
    private Server server;
    private NotificationController notificationController;
	private DBController dbController;
    
    public OrderController(Server server, NotificationController notificationController, DBController dbController) {
		this.server = server;
		this.notificationController = notificationController;
		this.dbController = dbController;
	}

	// Create a new order
    public boolean createOrder(Order order, List<Dish> dishesInOrder, ConnectionToClient client) {
        try {
            dbController.createOrder(order, dishesInOrder);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update an existing order's status
    public String updateOrderStatus(int orderId, EnumOrderStatus status, String msg) {
        try {
            dbController.updateOrderStatus(orderId, status);            
            switch(status) {
            case IN_PROGRESS:
                 dbController.updateOrderStartTime(orderId);
                notificationController.notifyUser(orderId, msg);
            	break;
            case READY:
            	notificationController.notifyUser(orderId, msg);
            	break;
            case COMPLETED:
                dbController.updateOrderReceiveTimeAndInsertDiscount(orderId, new Timestamp(System.currentTimeMillis()));
            	break;
            }
            
            return "Order status updated successfully";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error updating order status: " + e.getMessage();
        }
    }

    // Retrieve orders for a specific username
    public List<Order> getOrdersByUsername(String username) {
        try {
            return dbController.getOrdersByUsername(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Retrieve pending orders for a specific branch
    public List<Order> getPendingOrdersByBranch(int branchId) {
        try {
            return dbController.showPendingOrdersByBranch(branchId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    // Retrieve dishes in a specific order
    public List<DishInOrder> getDishesInOrder(int orderId) {
        return dbController.getDishesInOrder(orderId);
    }
    
    public boolean addDish(Dish dish) {
    	boolean result = dbController.addDish(dish);
    	if (result)
    		notificationController.notifyUpdatedMenu();
        return result;
    }
    
    public boolean deleteDish(Dish dish) {
    	boolean result =  dbController.deleteDish(dish);
    	if (result)
    		notificationController.notifyUpdatedMenu();
    	return result;
    }
    
    public boolean updateDish(Dish dish) {
    	boolean result =  dbController.updateDish(dish);
    	if (result)
    		notificationController.notifyUpdatedMenu();
    	return result;
    }

	public List<Dish> viewMenu(int menuId) {
		List<Dish> menu = dbController.getMenu(menuId);
		return menu;
	}
}
