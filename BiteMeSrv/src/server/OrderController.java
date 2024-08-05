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
import java.util.List;

public class OrderController {


    private Server server;

    
    public OrderController(Server server) {
		this.server = server;
	}

	// Create a new order
    public String createOrder(Order order, List<DishInOrder> dishesInOrder) {
        try {
            server.dbController.createOrder(order, dishesInOrder);
            return "Order created successfully";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error creating order: " + e.getMessage();
        }
    }

    // Update an existing order's status
    public String updateOrderStatus(int orderId, EnumOrderStatus status) {
        try {
            server.dbController.updateOrderStatus(orderId, status);
            
            // Check if status is IN_PROGRESS and update the start time
            if (status == EnumOrderStatus.IN_PROGRESS) {
                server.dbController.updateOrderStartTime(orderId, new Timestamp(System.currentTimeMillis()));
            }
            
            // Check if status is COMPLETED and update the receive time
            if (status == EnumOrderStatus.COMPLETED) {
                server.dbController.updateOrderReceiveTime(orderId, new Timestamp(System.currentTimeMillis()));
                notifyUser(orderId);
            }
            
            return "Order status updated successfully";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error updating order status: " + e.getMessage();
        }
    }

    
    private void notifyUser(int orderId) {
        try {
            Order order = server.dbController.getOrderById(orderId);
            String username = order.getUsername();
            ConnectionToClient client = server.getClientByUsername(username);
            
            if (client != null)
            	server.sendMessageToClient(EnumClientOperations.NOTIFICATION, client, (Object) ("Your order " + orderId + " is ready!"));
            else 
                server.dbController.savePendingNotification(username, orderId, "Your order is ready!");
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
    
    public void addDish(Dish dish) {
        server.dbController.addDish(dish);
    }
    
    public boolean deleteDish(Dish dish) {
        return server.dbController.deleteDish(dish);
    }

	public List<Dish> viewMenu(int menuId) {
		List<Dish> menu = server.dbController.getMenu(menuId);
		return menu;
	}
}
