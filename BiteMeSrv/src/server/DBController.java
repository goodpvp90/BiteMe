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

import common.Dish;
import common.DishAppetizer;
import common.DishBeverage;
import common.DishDessert;
import common.DishInOrder;
import common.DishMainCourse;
import common.DishSalad;
import common.EnumBranch;
import common.EnumDish;
import common.EnumOrderStatus;
import common.EnumType;
import common.IncomeReport;
import common.Order;
import common.OrdersReport;
import common.PerformanceReport;
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
	
	
    public boolean logout(String username) {
        String query = "UPDATE users SET isLogged = 0 WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


	public boolean createUser(String username, String password, String email, String phone, String firstName, String lastName, EnumBranch enumBranch, EnumType type) {
	    String sql = "INSERT INTO users (username, password, email, phone, firstname, lastname, home_branch, type, isLogged) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

	    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
	        preparedStatement.setString(1, username);
	        preparedStatement.setString(2, password);  // Ensure you hash passwords before storing them
	        preparedStatement.setString(3, email);
	        preparedStatement.setString(4, phone);
	        preparedStatement.setString(5, firstName);
	        preparedStatement.setString(6, lastName);
	        preparedStatement.setString(7, enumBranch.toString());
	        preparedStatement.setString(8, type.toString());
	        preparedStatement.setInt(9, 0);  // isLogged defaults to 0 (false)

	        int rowsAffected = preparedStatement.executeUpdate();
	        return rowsAffected > 0;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
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
    
    public void createOrder(Order order, List<DishInOrder> dishesInOrder) throws SQLException {
        String insertOrderQuery = "INSERT INTO orders (username, branch_id, order_date, order_request_time, order_receive_time, total_price, delivery, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String insertDishInOrderQuery = "INSERT INTO dish_in_order (order_id, dish_id, quantity) VALUES (?, ?, ?)";

        try {
            // Start transaction
            connection.setAutoCommit(false);

            // Insert the new order
            try (PreparedStatement stmt = connection.prepareStatement(insertOrderQuery, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, order.getUsername());
                stmt.setInt(2, order.getBranchId());
                stmt.setTimestamp(3, order.getOrderDate());
                stmt.setTimestamp(4, order.getOrderRequestTime());
                stmt.setTimestamp(5, order.getOrderReceiveTime());
                stmt.setDouble(6, order.getTotalPrice());
                stmt.setBoolean(7, order.isDelivery());
                stmt.setString(8, order.getStatus().toString());
                stmt.executeUpdate();

                // Retrieve the generated order ID
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int orderId = generatedKeys.getInt(1);

                        // Insert the dishes for this order
                        try (PreparedStatement dishStmt = connection.prepareStatement(insertDishInOrderQuery)) {
                            for (DishInOrder dishInOrder : dishesInOrder) {
                                dishStmt.setInt(1, orderId);
                                dishStmt.setInt(2, dishInOrder.getDishId());
                                dishStmt.setString(3, dishInOrder.getComment());
                                dishStmt.addBatch();
                            }
                            dishStmt.executeBatch();
                        }
                    } else {
                        throw new SQLException("Creating order failed, no ID obtained.");
                    }
                }
            }

            // Commit transaction
            connection.commit();
        } catch (SQLException e) {
            // Rollback transaction on error
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                // Log rollback error
                rollbackEx.printStackTrace();
            }
            throw e;
        } finally {
            // Reset auto-commit mode
            connection.setAutoCommit(true);
        }
    }

    
    public void addDishToOrder(DishInOrder dishinorder) throws SQLException {
        String sql = "INSERT INTO dish_in_order (order_id, dish_id, comment) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, dishinorder.getOrderId());
            pstmt.setInt(2, dishinorder.getDishId());
            pstmt.setString(3, dishinorder.getComment());
            pstmt.executeUpdate();
        }
    }
    
    public List<DishInOrder> getDishesInOrder(int orderId) throws SQLException {
        List<DishInOrder> dishesInOrder = new ArrayList<>();
        String sql = "SELECT * FROM dish_in_order WHERE order_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int dishId = rs.getInt("dish_id"); 
                String comment = rs.getString("comment");
                DishInOrder ndish = new DishInOrder(dishId, comment);
                ndish.setOrderId(orderId);
                dishesInOrder.add(ndish);
            }
        }
        return dishesInOrder;
    }

    public void updateOrderStatus(int orderId, EnumOrderStatus status) throws SQLException {
        String query = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status.toString());
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
        }
    }
    
    public boolean addDish(Dish dish) {
        String sql = "INSERT INTO dishes (menu_id, dish_type, dish_name, price) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, dish.getMenuId());
            preparedStatement.setString(2, dish.getDishType().toString());
            preparedStatement.setString(3, dish.getDishName());
            preparedStatement.setDouble(4, dish.getPrice());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteDish(Dish dish) {
        String sql = "DELETE FROM dishes WHERE menu_id = ? AND dish_type = ? AND dish_name = ? AND price = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, dish.getMenuId());
            preparedStatement.setString(2, dish.getDishType().toString());
            preparedStatement.setString(3, dish.getDishName());
            preparedStatement.setDouble(4, dish.getPrice());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public List<Dish> getMenu(int menuId) {
        String sql = "SELECT * FROM dishes WHERE menu_id = ?";
        List<Dish> dishes = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, menuId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int dishId = resultSet.getInt("dish_id");
                EnumDish dishType = EnumDish.valueOf(resultSet.getString("dish_type"));
                String dishName = resultSet.getString("dish_name");
                double price = resultSet.getDouble("price");
                switch(dishType){
                case BEVERAGE:
                	dishes.add(new DishBeverage(dishName, price, menuId));
                case SALAD:
                	dishes.add(new DishSalad(dishName, price, menuId));
                case APPETIZER:
                	dishes.add(new DishAppetizer(dishName, price, menuId));
                case DESSERT:
                	dishes.add(new DishDessert(dishName, price, menuId));
                case MAIN_COURSE:
                	dishes.add(new DishMainCourse(dishName, price, menuId));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dishes;
    }
    
    /**
     * Generates the orders report that collects the Total amount of each dish type sold for the previous month 
     * and inserts it into the ordersreport table.
     * If a report for the same restaurant and month already exists, it updates the amount field.
     */
    //===================================add completed
    public void generateOrdersReport() {
        String query = "INSERT INTO ordersreport (restaurant, dish_type, amount, month, year) " +
                "SELECT o.branch_id AS restaurant, d.dish_type, COUNT(*) AS amount, " +
                "MONTH(o.order_date) AS month, YEAR(o.order_date) AS year " +
                "FROM dish_in_order dio " +
                "JOIN dishes d ON dio.dish_id = d.dish_id " +
                "JOIN orders o ON dio.order_id = o.order_id " +
                "WHERE MONTH(o.order_date) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH) " +
                "AND YEAR(o.order_date) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH) " +
                "GROUP BY o.branch_id, d.dish_type, MONTH(o.order_date), YEAR(o.order_date) " +
                "ON DUPLICATE KEY UPDATE amount = VALUES(amount)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.executeUpdate();
            System.out.println("Monthly report generated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Generates the income report for the previous month based on the total income of a branch for the month
     * and inserts it into the incomereport table.
     * If a report for the same restaurant and month already exists, it updates the income field.
     */
    public void generateIncomeReport() {
        String query = "INSERT INTO incomereport (restaurant, month, year, income) " +
                       "SELECT branch_id AS restaurant, " +
                       "MONTH(order_date) AS month, " +
                       "YEAR(order_date) AS year, " +
                       "SUM(total_price) AS income " +
                       "FROM orders " +
                       "WHERE MONTH(order_date) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH) " +
                       "AND YEAR(order_date) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH) " +
                       "GROUP BY branch_id, MONTH(order_date), YEAR(order_date)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.executeUpdate();
            System.out.println("Monthly income report generated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Generates the performance report for the previous month collects the amount of orders and amount of orders that were completed in time
     * and inserts it into the performancereport table.
     * If a report for the same branch and month already exists, it updates the totalOrders and ordersCompletedInTime fields.
     */
    public void generatePerformanceReport() {
        String query = "INSERT INTO performancereport (branch_id, month, year, totalOrders, ordersCompletedInTime) " +
                       "SELECT o.branch_id AS branch_id, MONTH(o.order_date) AS month, YEAR(o.order_date) AS year, " +
                       "COUNT(*) AS totalOrders, " +
                       "SUM(CASE WHEN TIMESTAMPDIFF(MINUTE, o.order_request_time, o.order_receive_time) <= 60 THEN 1 ELSE 0 END) AS ordersCompletedInTime " +
                       "FROM orders o " +
                       "WHERE MONTH(o.order_date) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH) " +
                       "AND YEAR(o.order_date) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH) " +
                       "GROUP BY o.branch_id, MONTH(o.order_date), YEAR(o.order_date) " +
                       "ON DUPLICATE KEY UPDATE totalOrders = VALUES(totalOrders), ordersCompletedInTime = VALUES(ordersCompletedInTime)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.executeUpdate();
            System.out.println("Monthly income report generated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieves an income report from the database for the specified restaurant, month, and year.
     * If no report exists, returns a string indicating that no such report exists.
     * If a database error occurs, returns the error message.
     *
     * @param report an IncomeReport object with the restaurant, month, and year set
     * @return the updated IncomeReport object with the income set if found,
     *         otherwise a string indicating that no such report exists or an error message
     */
    public Object getIncomeReport(IncomeReport report) {
        String query = "SELECT income FROM incomereport WHERE restaurant = ? AND month = ? AND year = ?";
        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            // Set the parameters for the query
            pstmt.setInt(1, report.getRestaurant().getBranchID());
            pstmt.setInt(2, report.getMonth());
            pstmt.setInt(3, report.getYear());

            // Execute the query
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double income = rs.getDouble("income");
                    report.setIncome(income);      
                }
                else {
                	return (Object)"no such report exists";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return (Object)e.toString();
        }
        return (Object)report;
    }
    
    /**
     * Retrieves an performance report from the database for the specified restaurant, month, and year.
     * If no report exists, returns a string indicating that no such report exists.
     * If a database error occurs, returns the error message.
     *
     * @param report an PerformanceReport object with the restaurant, month, and year set
     * @return the updated PerformanceReport object with the income set if found,
     *         otherwise a string indicating that no such report exists or an error message
     */
    public Object getPerformanceReport(PerformanceReport report) {
        String query = "SELECT totalOrders, ordersCompletedInTime FROM performancereport"+
                "WHERE branch_id = ? AND month = ? AND year = ?";
        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            // Set the parameters for the query
            pstmt.setInt(1, report.getRestaurant().getBranchID());
            pstmt.setInt(2, report.getMonth());
            pstmt.setInt(3, report.getYear());

            // Execute the query
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int totalOrders = rs.getInt("totalOrders");
                    int ordersCompletedInTime = rs.getInt("ordersCompletedInTime");
                    report.setTotalOrders(totalOrders);
                    report.setOrdersCompletedInTime(ordersCompletedInTime);
                }
                else {
                	return (Object)"no such report exists";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return (Object)e.toString();
        }
        return (Object)report;
    }
    
    /**
     * Retrieves the orders report from the database for the specified restaurant, month, and year.
     *
     * @param report the OrdersReport object containing the restaurant, month, and year to query
     * @return the OrdersReport object populated with the retrieved data or an error message if an error occurs
     */
    public Object getOrdersReport(OrdersReport report) {
        String query = "SELECT dish_type, amount FROM ordersreport WHERE restaurant = ? AND month = ? AND year = ?";
        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            // Set the parameters for the query
            pstmt.setInt(1, report.getRestaurant().getBranchID());
            pstmt.setInt(2, report.getMonth());
            pstmt.setInt(3, report.getYear());

            // Execute the query
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    EnumDish dishType = EnumDish.valueOf(rs.getString("dish_type"));
                    int amount = rs.getInt("amount");
                    report.getDishTypeAmountMap().put(dishType, amount);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return (Object) e.toString();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return (Object) e.toString();
        }
        return (Object) report;
    }


    
    public void savePendingNotification(String username, int orderId, String status) throws SQLException {
        String query = "INSERT INTO pendingnotifications (username, orderNumber, status) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setInt(2, orderId);
            stmt.setString(3, status);
            stmt.executeUpdate();
        }
    }
    
    public List<String> getPendingNotifications(String username) throws SQLException {
        String query = "SELECT orderNumber, status FROM pendingnotifications WHERE username = ?";
        List<String> notifications = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add("Order " + rs.getInt("orderNumber") + ": " + rs.getString("status"));
                }
            }
        }
        return notifications;
    }
    
    public void deletePendingNotifications(String username) throws SQLException {
        String query = "DELETE FROM pendingnotifications WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }
    
    public Order getOrderById(int orderId) throws SQLException {
        String query = "SELECT * FROM orders WHERE order_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Order(
                        rs.getInt("order_id"),
                        rs.getString("username"),
                        rs.getInt("branch_id"),
                        rs.getTimestamp("order_date"),
                        rs.getTimestamp("order_request_time"),
                        rs.getTimestamp("order_receive_time"),
                        rs.getDouble("total_price"),
                        rs.getBoolean("delivery"),
                        EnumOrderStatus.valueOf(rs.getString("status"))
                    );
                }
            }
        }
        return null;
    }

}