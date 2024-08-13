package controllers;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import containers.ListContainer;
import enums.EnumClientOperations;
import enums.EnumOrderStatus;
import ocsf.server.ConnectionToClient;
import restaurantEntities.Dish;
import restaurantEntities.DishInOrder;
import restaurantEntities.Order;
import server.Server;

public class OrderController {
    private Server server;
    private NotificationController notificationController;
	private DBController dbController;
    
    public OrderController(Server server, NotificationController notificationController, DBController dbController) {
		this.server = server;
		this.notificationController = notificationController;
		this.dbController = dbController;
	}
     
    
    public void handleInsertOrder(ConnectionToClient client, Object[] message) {
    	// Extract data from the message
        Order newOrder = (Order) message[1];
        ListContainer dishesInOrderContainer = (ListContainer)message[2];
		List<Dish> dishesInOrder = dishesInOrderContainer.getListDish();

        // Call the method to create the order
        try {
        	createOrder(newOrder, dishesInOrder, client);
        	notificationController.notifyWorker(dbController.getLocationByBranchId(newOrder.getBranchId()));
        } catch (Exception e) {
        	e.printStackTrace();;
        }
    }
    
	// Create a new order
    private boolean createOrder(Order order, List<Dish> dishesInOrder, ConnectionToClient client) {
        try {
            dbController.createOrder(order, dishesInOrder);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

	public void handleUpdateOrderStatus(ConnectionToClient client, Object[] message) {
        int orderId = (int) message[1];
        EnumOrderStatus newStatus = (EnumOrderStatus) message[2];
        String update_msg = (String)message[3];
        boolean isDelivery = (boolean)message[4];
        updateOrderStatus(orderId, newStatus, update_msg, isDelivery);
	}   
    
    // Update an existing order's status
    private String updateOrderStatus(int orderId, EnumOrderStatus status, String msg, boolean isDelivery) {
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
            	if (!isDelivery)
                    notificationController.notifyUser(orderId, msg);
                dbController.updateOrderReceiveTimeAndInsertDiscount(orderId, new Timestamp(System.currentTimeMillis()));
            	break;
			default:
				break;
            }
            
            return "Order status updated successfully";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error updating order status: " + e.getMessage();
        }
    }

    // Retrieve dishes in a specific order
	public void handleDishesInOrder(ConnectionToClient client, Object[] message) {
    	int orderid = (int)message[1];
    	List<DishInOrder> dishes = dbController.getDishesInOrder(orderid);
	    //encapsulate the list to avoid suppress warnings
    	ListContainer dishesContainer = new ListContainer();
    	dishesContainer.setListDishInOrder(dishes);
    	server.sendMessageToClient(EnumClientOperations.DISHES_IN_ORDER, client, dishesContainer);
	}   

	public void handleOrderInTime(ConnectionToClient client, Object[] message) {
    	int orderarriveid = (int)message[1];
    	server.sendMessageToClient(EnumClientOperations.ORDER_ON_TIME, client, dbController.isOrderArrivedOnTime(orderarriveid));
	}   

}
