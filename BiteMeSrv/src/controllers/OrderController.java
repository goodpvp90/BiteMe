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

/**
 * Manages order-related operations for the server.
 **/
public class OrderController {
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
     * Constructor for OrderController.
     *
     * @param server               the server instance
     * @param notificationController the notification controller instance
     * @param dbController         the database controller instance
     */
    public OrderController(Server server, NotificationController notificationController, DBController dbController) {
		
    	this.server = server;
		this.notificationController = notificationController;
		this.dbController = dbController;
	}
     
    /**
     * Handles the insertion of a new order.
     *
     * @param client  the client connection
     * @param message the message containing order details
     */
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
    
    /**
     * Creates a new order.
     *
     * @param order the order to create
     * @param dishesInOrder the list of dishes in the order
     * @param client the client connection
     * @return true if the order was created successfully, false otherwise
     */    private boolean createOrder(Order order, List<Dish> dishesInOrder, ConnectionToClient client) {
        try {
            dbController.createOrder(order, dishesInOrder);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

     /**
      * Handles the update of an order's status.
      *
      * @param client  the client connection
      * @param message the message containing order status details
      */
	public void handleUpdateOrderStatus(ConnectionToClient client, Object[] message) {
        int orderId = (int) message[1];
        EnumOrderStatus newStatus = (EnumOrderStatus) message[2];
        String update_msg = (String)message[3];
        boolean isDelivery = (boolean)message[4];
        updateOrderStatus(orderId, newStatus, update_msg, isDelivery);
	}   
    
    /**
     * Updates the status of an existing order.
     *
     * @param orderId the order ID
     * @param status the new status of the order
     * @param msg the update message for the client
     * @param isDelivery flag indicating if the order is for delivery
     * @return a message indicating the result of the update
     */    
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

    /**
     * Handles the retrieval of dishes in a specific order.
     *
     * @param client  the client connection
     * @param message the message containing the order ID
     */
	public void handleDishesInOrder(ConnectionToClient client, Object[] message) {
    	int orderid = (int)message[1];
    	List<DishInOrder> dishes = dbController.getDishesInOrder(orderid);
	    //encapsulate the list to avoid suppress warnings
    	ListContainer dishesContainer = new ListContainer();
    	dishesContainer.setListDishInOrder(dishes);
    	server.sendMessageToClient(EnumClientOperations.DISHES_IN_ORDER, client, dishesContainer);
	}   
	
    /**
     * Handles the check if an order arrived on time.
     *
     * @param client  the client connection
     * @param message the message containing the order arrival ID
     */
	public void handleOrderInTime(ConnectionToClient client, Object[] message) {
    	int orderarriveid = (int)message[1];
    	server.sendMessageToClient(EnumClientOperations.ORDER_ON_TIME, client, dbController.isOrderArrivedOnTime(orderarriveid));
	}   

}
