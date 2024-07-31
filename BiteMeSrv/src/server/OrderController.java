package server;

import common.Dish;
import common.DishInOrder;
import common.EnumClientOperations;
import common.Order;
import ocsf.server.ConnectionToClient;
import common.EnumOrderStatus;
import common.EnumServerOperations;

import java.sql.SQLException;
import java.util.List;

public class OrderController {


    private static Server server = Server.getInstance();

    // Create a new order
    public static String createOrder(Order order, List<DishInOrder> dishesInOrder) {
        try {
            server.dbController.createOrder(order, dishesInOrder);
            return "Order created successfully";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error creating order: " + e.getMessage();
        }
    }

    // Update an existing order's status
    public static String updateOrderStatus(int orderId, EnumOrderStatus status) {
        try {
            server.dbController.updateOrderStatus(orderId, status);
            if (status == EnumOrderStatus.COMPLETED) {
                notifyUser(orderId);
            }
            return "Order status updated successfully";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error updating order status: " + e.getMessage();
        }
    }
    
    private static void notifyUser(int orderId) {
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
    public static List<Order> getOrdersByUsername(String username) {
        try {
            return server.dbController.getOrdersByUsername(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Retrieve pending orders for a specific branch
    public static List<Order> getPendingOrdersByBranch(int branchId) {
        try {
            return server.dbController.showPendingOrdersByBranch(branchId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    // Retrieve dishes in a specific order
    public static List<DishInOrder> getDishesInOrder(int orderId) {
        return server.dbController.getDishesInOrder(orderId);
    }
    
    public static void addDish(Dish dish) {
        server.dbController.addDish(dish);
    }
    
    public static boolean deleteDish(Dish dish) {
        return server.dbController.deleteDish(dish);
    }

	public static List<Dish> viewMenu(int menuId) {
		List<Dish> menu = server.dbController.getMenu(menuId);
		return menu;
	}
}
