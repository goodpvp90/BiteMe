package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import common.EnumBranch;
import common.EnumOrderStatus;
import common.EnumType;
import common.Order;
import common.User;

public class DBController {
	//EXAMPLE OF JDBC URL:
	//private static String JDBC_URL = "jdbc:mysql://localhost:3306/biteme?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";


	private Connection connection;

	public void connect(String jdbc_url, String username, String password) throws ClassNotFoundException, SQLException {
		String url = jdbc_url + "?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
		// Load MySQL JDBC Driver
		Class.forName("com.mysql.cj.jdbc.Driver");
		// Establish the connection
		connection = DriverManager.getConnection(url, username, password);
		System.out.println("Connected to the database successfully!");
	}

	public void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
				System.out.println("Database connection closed.");
			} catch (SQLException e) {
				System.out.println("Failed to close the database connection.");
				e.printStackTrace();
			}
		}
	}

	public String updateOrder(int orderNumber, String toChange, Object newParam) {
		String query = "UPDATE orders SET " + toChange + " = ? WHERE OrderNumber = ?";
        String message;

		try { 
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			if (newParam instanceof Double)
				preparedStatement.setDouble(1, (double)newParam);
			else
				preparedStatement.setString(1, (String)newParam);
			preparedStatement.setInt(2, orderNumber);
			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected == 0)
				message = "nothing";
			else
				message = "updated successfully";
		} catch (SQLException e) {
            message = e.getMessage();
		}
		return message;
	}

	public String insertOrder(int orderNumber, String restaurantName, double totalPrice, int orderListNumber, String orderAddress) {
        String query = "INSERT INTO orders (OrderNumber, name_of_restaurant, Total_price, order_list_number, order_address) VALUES (?, ?, ?, ?, ?)";
        String message;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, orderNumber);
            preparedStatement.setString(2, restaurantName);
            preparedStatement.setDouble(3, totalPrice);
            preparedStatement.setInt(4, orderListNumber);
            preparedStatement.setString(5, orderAddress);
            preparedStatement.executeUpdate();
            message = "inserted successfully";
        } catch (SQLException e) {
            message = e.getMessage();
        }
        return message;
    }
	
	public Object validateLogin(User user) {
        List<Object> userDetails = new ArrayList<>();
        String username = user.getUsername(), password = user.getPassword();
        // Define the SQL query
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        // Establish the database connection
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            // Set the parameters for the query
            statement.setString(1, username);
            statement.setString(2, password);

            // Execute the query
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    // user exists in the DataBase
                	//if got true meaning user is already logged in
                	if(updateIsLoggedStatus(username))
                		return (Object)"user is already logged in";
                	
                    String Email = rs.getString("email");
                    String phone = rs.getString("phone");
                    String firstName = rs.getString("firstname");
                    String lastName = rs.getString("lastname");
                    EnumBranch homeBranch = EnumBranch.valueOf(rs.getString("home_branch"));
                    EnumType type = EnumType.valueOf(rs.getString("type"));
                    boolean isLogged = rs.getBoolean("isLogged");

                    userDetails.add(firstName);
                    userDetails.add(lastName);
                    userDetails.add(Email);
                    userDetails.add(phone);
                    userDetails.add(homeBranch);
                    userDetails.add(username);
                    userDetails.add(password);
                    userDetails.add(isLogged);
                    userDetails.add(type);
                    
                }
                else {
                	return (Object)"username or password are incorrect";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return (Object)e.toString();
        }
        return (Object)userDetails;
    }
	
//	private boolean checkIfLogged(String username) {   
//        String sql = "SELECT isLogged FROM users WHERE username = ?";
//        try {
//            PreparedStatement statement = connection.prepareStatement(sql);
//            // Set the parameter for the query
//            statement.setString(1, username);
//
//            // Execute the query
//            try (ResultSet resultSet = statement.executeQuery()) {
//                if (resultSet.next()) {
//                    boolean isLogged = resultSet.getBoolean("isLogged");
//                    if (isLogged) {
//                        return true;
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
	
	private boolean updateIsLoggedStatus(String username) {
		String query = "UPDATE users SET isLogged = 1 WHERE username = ?";
		boolean logged = false;
		try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0)
				logged = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logged;
	}
    
    // Update order receive time, used when user accepts he recied the order
    public void updateOrderReceiveTime(int orderId, Timestamp orderReceiveTime){
        String query = "UPDATE orders SET order_receive_time = ? WHERE order_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setTimestamp(1, orderReceiveTime);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    // Get orders for a specific username
    public List<Order> getOrdersByUsername(String username) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(rs.getInt("order_id"), rs.getString("username"), rs.getInt("branch_id"),
                            rs.getTimestamp("order_date"), rs.getTimestamp("order_request_time"), rs.getTimestamp("order_receive_time"), rs.getDouble("total_price"), rs.getBoolean("delivery"),EnumOrderStatus.valueOf(rs.getString("home_branch")));
                    orders.add(order);
                }
            }
        }
        return orders;
    }
    
    /**
     * Retrieves all pending orders for a specific branch.
     *
     * @param branchId The ID of the branch to retrieve orders for.
     * @return A list of pending orders for the specified branch.
     * @throws SQLException If there is an error querying the database.
     */
    public List<Order> showPendingOrdersByBranch(int branchId) throws SQLException {
        String query = "SELECT * FROM orders WHERE branch_id = ? AND status = 'PENDING'";
        List<Order> pendingOrders = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, branchId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int orderId = resultSet.getInt("order_id");
                    String username = resultSet.getString("username");
                    Timestamp orderDate = resultSet.getTimestamp("order_date"); // Adjust the type if necessary
                    Timestamp orderRequestTime = resultSet.getTimestamp("order_request_time"); // Adjust the type if necessary
                    Timestamp orderReceiveTime = resultSet.getTimestamp("order_receive_time"); // Adjust the type if necessary
                    double totalPrice = resultSet.getDouble("total_price");
                    boolean delivery = resultSet.getBoolean("delivery");
                    EnumOrderStatus status = EnumOrderStatus.valueOf(resultSet.getString("status"));
                   

                    // Create and add the Order object to the list
                    Order order = new Order(orderId, username, branchId, orderDate, orderRequestTime, orderReceiveTime, totalPrice, delivery, status);
                    pendingOrders.add(order);
                }
            }
        }

        return pendingOrders;
    }
    
    // Create an order
    public void createOrder(Order order) throws SQLException {
        String query = "INSERT INTO orders (username, branch_id, order_date, order_request_time, total_price, delivery) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, order.getUsername());
            stmt.setInt(2, order.getBranchId());
            stmt.setTimestamp(3, order.getOrderDate());
            stmt.setTimestamp(4, order.getOrderRequestTime());
            stmt.setTimestamp(5, order.getOrderReceiveTime());
            stmt.setDouble(6, order.getTotalPrice());
            stmt.setBoolean(7, order.isDelivery());
            stmt.setString(8, order.getStatus().toString());
            stmt.executeUpdate();
        }
    }

}