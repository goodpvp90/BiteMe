package server;

import common.Dish;
import common.DishInOrder;
import common.EnumClientOperations;
import common.Order;
import ocsf.server.ConnectionToClient;
import common.EnumOrderStatus;
import common.EnumServerOperations;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class OrderController {


    private Server server;

    
    public OrderController(Server server) {
		this.server = server;
	}

	// Create a new order
    public String createOrder(Order order, List<Dish> dishesInOrder) {
        try {
            server.dbController.createOrder(order, dishesInOrder);
            return "Order created successfully";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error creating order: " + e.getMessage();
        }
    }

    // Update an existing order's status
    public String updateOrderStatus(int orderId, EnumOrderStatus status, String msg) {
        try {
            server.dbController.updateOrderStatus(orderId, status);
            notifyUser(orderId, msg);
            
            switch(status) {
            case IN_PROGRESS:
                // Check if status is IN_PROGRESS and update the start time
                server.dbController.updateOrderStartTime(orderId);
                notifyUser(orderId, msg);
            	break;
            case READY:
                notifyUser(orderId, msg);
            	break;
            case COMPLETED:
            	//TO EXPLAIN WHEN COMPELED HAPPENS IF PICKUP OR DELIVERY
                server.dbController.updateOrderReceiveTimeAndInsertDiscount(orderId, new Timestamp(System.currentTimeMillis()));
                //LOGIC OF DISCOUNT TO INSERT INTO DB
                notifyUser(orderId, msg);
            	break;
            }
            // Check if status is IN_PROGRESS and update the start time
            if (status == EnumOrderStatus.IN_PROGRESS) {

            }
            if (status == EnumOrderStatus.READY) {
                //server.dbController.updateOrderReceiveTimeAndInsertDiscount(orderId, new Timestamp(System.currentTimeMillis()));
                notifyUser(orderId, msg);
            }
            // Check if status is COMPLETED and update the receive time
            if (status == EnumOrderStatus.COMPLETED) {
                server.dbController.updateOrderReceiveTimeAndInsertDiscount(orderId, new Timestamp(System.currentTimeMillis()));
                //LOGIC OF DISCOUNT TO INSERT INTO DB
                notifyUser(orderId, msg);
            }
            
            return "Order status updated successfully";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error updating order status: " + e.getMessage();
        }
    }

    
    private void notifyUser(int orderId, String message) {
        try {
            Order order = server.dbController.getOrderById(orderId);
            String username = order.getUsername();
            ConnectionToClient client = server.getClientByUsername(username);
            System.out.println("???????????????????");
            if (client != null) {
            	List<String> not = new ArrayList<String>();
            	not.add(message);
            	System.out.println("does it do it");
            	server.sendMessageToClient(EnumClientOperations.NOTIFICATION, client, not);
            }else 
                server.dbController.savePendingNotification(username, orderId, message);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Retrieve orders for a specific username
    public List<Order> getOrdersByUsername(String username) {
        try {
            return server.dbController.getOrdersByUsername(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Retrieve pending orders for a specific branch
    public List<Order> getPendingOrdersByBranch(int branchId) {
        try {
            return server.dbController.showPendingOrdersByBranch(branchId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    // Retrieve dishes in a specific order
    public List<DishInOrder> getDishesInOrder(int orderId) {
        return server.dbController.getDishesInOrder(orderId);
    }
    
    public boolean addDish(Dish dish) {
        return server.dbController.addDish(dish);
    }
    
    public boolean deleteDish(Dish dish) {
        return server.dbController.deleteDish(dish);
    }
    
    public boolean updateDish(Dish dish) {
    	return server.dbController.updateDish(dish);
    }

	public List<Dish> viewMenu(int menuId) {
		List<Dish> menu = server.dbController.getMenu(menuId);
		return menu;
	}
}
