package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import common.EnumBranch;
import common.EnumType;
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
                    System.out.println("1");

                    String Email = rs.getString("email");
                    String phone = rs.getString("phone");
                    String firstName = rs.getString("firstname");
                    String lastName = rs.getString("lastname");
                    System.out.println("2");

                    EnumBranch homeBranch = EnumBranch.valueOf(rs.getString("home_branch"));
                    EnumType type = EnumType.valueOf(rs.getString("type"));
                    boolean isLogged = rs.getBoolean("isLogged");
                    System.out.println("3");

                    userDetails.add(firstName);
                    userDetails.add(lastName);
                    userDetails.add(Email);
                    userDetails.add(phone);
                    userDetails.add(homeBranch);
                    userDetails.add(username);
                    userDetails.add(password);
                    userDetails.add(isLogged);
                    userDetails.add(type);
                    System.out.println("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm");

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
            e.printStackTrace();
        }

        return orders;
    }
}