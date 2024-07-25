package server;

import common.Dish;
import common.DishInOrder;
import common.Order;
import common.EnumOrderStatus;

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
            return "Order status updated successfully";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error updating order status: " + e.getMessage();
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

    // Add a dish to an existing order
    public static String addDishToOrder(int orderId, int dishId, int quantity) {
        try {
            server.dbController.addDishToOrder(orderId, dishId, quantity);
            return "Dish added to order successfully";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error adding dish to order: " + e.getMessage();
        }
    }

    // Retrieve dishes in a specific order
    public static List<DishInOrder> getDishesInOrder(int orderId) {
        try {
            return server.dbController.getDishesInOrder(orderId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void addDish(Dish dish) {
        try {
            server.dbController.addDish(dish);
        } catch (SQLException e) {
            System.out.println("Error adding dish: " + e.getMessage());
        }
    }
}
