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
            int rowsAffected = preparedStatement.executeUpdate();
            message = "inserted successfully";
        } catch (SQLException e) {
            message = e.getMessage();
        }
        return message;
    }
	
	public String validateLogin(String username, String password) {
        String result = "Invalid username or password.";
        
        // Define the SQL query
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        // Establish the database connection
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            // Set the parameters for the query
            statement.setString(1, username);
            statement.setString(2, password);

            // Execute the query
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Login is successful
                    result = "Welcome, " + username + "!";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result = "An error occurred while validating the login.";
        }

        return result;
    }

    public List<Object[]> showOrders() {
        String query = "SELECT * FROM orders";
        List<Object[]> orders = new ArrayList<>();

        try {
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query); 
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