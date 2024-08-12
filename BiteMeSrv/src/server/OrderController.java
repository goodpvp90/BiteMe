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
     
    
    public void handleInsertOrder(ConnectionToClient client, Object[] message) {
    	// Extract data from the message
        Order newOrder = (Order) message[1];
        @SuppressWarnings("unchecked")
		List<Dish> dishesInOrder = (List<Dish>) message[2];
        // Call the method to create the order
        try {
        	boolean order = createOrder(newOrder, dishesInOrder, client);
        	notificationController.notifyWorker(dbController.getLocationByBranchId(newOrder.getBranchId()));
            server.sendMessageToClient(EnumClientOperations.INSERT_ORDER,client, order);
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
    	server.sendMessageToClient(EnumClientOperations.DISHES_IN_ORDER, client, dishes);
	}   

	public void handleOrderInTime(ConnectionToClient client, Object[] message) {
    	int orderarriveid = (int)message[1];
    	server.sendMessageToClient(EnumClientOperations.ORDER_ON_TIME, client, dbController.isOrderArrivedOnTime(orderarriveid));
	}   

}
