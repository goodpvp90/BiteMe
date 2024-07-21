package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBController {
	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/bitemeproto?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "Aa123456";

	private Connection connection;

	public void connect() throws ClassNotFoundException, SQLException {
		// Load MySQL JDBC Driver
		Class.forName("com.mysql.cj.jdbc.Driver");
		// Establish the connection
		connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
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
			Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			
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
        	Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, orderNumber);
            preparedStatement.setString(2, restaurantName);
            preparedStatement.setDouble(3, totalPrice);
            preparedStatement.setInt(4, orderListNumber);
            preparedStatement.setString(5, orderAddress);

            int rowsAffected = preparedStatement.executeUpdate();
            message = "inserted successfully";
        } catch (SQLException e) {
            message = e.getMessage();
        }
        return message;
    }

    public List<Object[]> showOrders() {
        String query = "SELECT * FROM orders";
        List<Object[]> orders = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int ordernum = rs.getInt("OrderNumber");
                String name = rs.getString("name_of_restaurant");
                double price = rs.getDouble("Total_price");
                int order_lst = rs.getInt("order_list_number");
                String order_add = rs.getString("order_address");

                orders.add(new Object[]{ordernum, name, price, order_lst, order_add});
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve the orders.");
            e.printStackTrace();
        }

        return orders;
    }
}
//    public void getData() {
//    	try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)
//    			{
//    				
//    			}
//    }
