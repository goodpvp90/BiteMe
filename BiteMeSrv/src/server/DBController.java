package server;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import common.DailyPerformanceReport;
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
import common.Restaurant.Location;
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
	
	public boolean checkCustomerRegistered(User user) {
		boolean isRegistered = false;
        String query = "SELECT registered FROM customers WHERE username = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, user.getUsername());
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                isRegistered = rs.getBoolean("registered"); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isRegistered;
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
	
	public void updateOrderStatus(int orderId, EnumOrderStatus status) throws SQLException {
	    String checkDeliveryQuery = "SELECT delivery FROM orders WHERE order_id = ?";
	    String updateStatusQuery = "UPDATE orders SET status = ? WHERE order_id = ?";
	    String updateReceiveTimeQuery = "UPDATE orders SET order_receive_time = ? WHERE order_id = ?";
	    
	    try (PreparedStatement checkStmt = connection.prepareStatement(checkDeliveryQuery)) {
	        checkStmt.setInt(1, orderId);
	        try (ResultSet rs = checkStmt.executeQuery()) {
	            if (rs.next()) {
	                int delivery = rs.getInt("delivery");
	                
	                try (PreparedStatement updateStmt = connection.prepareStatement(updateStatusQuery)) {
	                    updateStmt.setString(1, status.toString());
	                    updateStmt.setInt(2, orderId);
	                    updateStmt.executeUpdate();
	                }
	                
	                if (delivery == 0 && status == EnumOrderStatus.READY) {
	                    try (PreparedStatement updateTimeStmt = connection.prepareStatement(updateReceiveTimeQuery)) {
	                        updateTimeStmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
	                        updateTimeStmt.setInt(2, orderId);
	                        updateTimeStmt.executeUpdate();
	                    }
	                }
	            }
	        }
	    }
	}

	
	// Update order start time when worker accepts the order.
	public void updateOrderStartTime(int orderId) {
	    String selectQuery = "SELECT order_ready_time FROM orders WHERE order_id = ?";
	    String updateQuery = "UPDATE orders SET order_ready_time = ? WHERE order_id = ?";
	    
	    try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
	         PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
	        
	        // Check the current order_ready_time
	        selectStmt.setInt(1, orderId);
	        ResultSet rs = selectStmt.executeQuery();
	        
	        if (rs.next()) {
	            Timestamp currentOrderReadyTime = rs.getTimestamp("order_ready_time");
	            Timestamp newOrderReadyTime;
	            
	            if (currentOrderReadyTime == null) {
	                // If order_ready_time is null, set it to the current time plus 1 hour
	                LocalDateTime nowPlusOneHour = LocalDateTime.now().plus(1, ChronoUnit.HOURS);
	                newOrderReadyTime = Timestamp.valueOf(nowPlusOneHour);
	            } else {
	                // If order_ready_time is not null, add 20 minutes to the existing time
	                LocalDateTime updatedTime = currentOrderReadyTime.toLocalDateTime().plus(20, ChronoUnit.MINUTES);
	                newOrderReadyTime = Timestamp.valueOf(updatedTime);
	            }
	            
	            // Set the new order_ready_time and orderId, then execute the update statement
	            updateStmt.setTimestamp(1, newOrderReadyTime);
	            updateStmt.setInt(2, orderId);
	            updateStmt.executeUpdate();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	//GET DISCOUNT FOR USERNAME
	public double getCurrentDiscountAmount(String username) {
	    String query = "SELECT discount_amount FROM discounts WHERE username = ?";
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setString(1, username);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            return rs.getDouble("discount_amount");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return 0.0;
	}
	
	//UPDATE NEW DISCOUNT AMOUNT FOR USER
	public void updateDiscountAmount(String username, double discountAmount) {
	    String query = "INSERT INTO discounts (username, discount_amount) VALUES (?, ?) ON DUPLICATE KEY UPDATE discount_amount = ?";
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setString(1, username);
	        stmt.setDouble(2, discountAmount);
	        stmt.setDouble(3, discountAmount);
	        stmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	// Update order receive time, used when user accepts he received the order and insert the correct discount.
	public void updateOrderReceiveTimeAndInsertDiscount(int orderId, Timestamp orderReceiveTime) {
	    String selectQuery = "SELECT order_ready_time, total_price, username FROM orders WHERE order_id = ?";
	    String updateOrderQuery = "UPDATE orders SET order_receive_time = ? WHERE order_id = ?";
	    String getDiscountQuery = "SELECT discount_amount FROM discounts WHERE username = ?";
	    String updateDiscountQuery = "INSERT INTO discounts (username, discount_amount) VALUES (?, ?) ON DUPLICATE KEY UPDATE discount_amount = ?";
	    
	    try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
	         PreparedStatement updateOrderStmt = connection.prepareStatement(updateOrderQuery);
	         PreparedStatement getDiscountStmt = connection.prepareStatement(getDiscountQuery);
	         PreparedStatement updateDiscountStmt = connection.prepareStatement(updateDiscountQuery)) {
	        
	        // Select the current order details
	        selectStmt.setInt(1, orderId);
	        ResultSet rs = selectStmt.executeQuery();
	        
	        if (rs.next()) {
	            Timestamp orderReadyTime = rs.getTimestamp("order_ready_time");
	            double totalPrice = rs.getDouble("total_price");
	            String username = rs.getString("username");
	            
	            // Update the order receive time
	            updateOrderStmt.setTimestamp(1, orderReceiveTime);
	            updateOrderStmt.setInt(2, orderId);
	            updateOrderStmt.executeUpdate();
	            
	            if (orderReadyTime != null && orderReceiveTime != null && orderReceiveTime.after(orderReadyTime)) {
	                // Calculate the discount amount
	                double newDiscountAmount = totalPrice * 0.50;
	                
	                // Get the current discount amount
	                double currentDiscountAmount = 0.0;
	                getDiscountStmt.setString(1, username);
	                ResultSet discountRs = getDiscountStmt.executeQuery();
	                if (discountRs.next()) {
	                    currentDiscountAmount = discountRs.getDouble("discount_amount");
	                }
	                
	                // Calculate the total discount amount
	                double totalDiscountAmount = currentDiscountAmount + newDiscountAmount;
	                
	                // Update the discount table
	                updateDiscountStmt.setString(1, username);
	                updateDiscountStmt.setDouble(2, totalDiscountAmount);
	                updateDiscountStmt.setDouble(3, totalDiscountAmount);
	                updateDiscountStmt.executeUpdate();
	            }
	        }
	    } catch (SQLException e) {
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
                    Order order = new Order(rs.getString("username"), rs.getInt("branch_id"),
                            rs.getTimestamp("order_date"), rs.getTimestamp("order_request_time"), rs.getDouble("total_price"), rs.getBoolean("delivery"));

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
                    //int orderId = resultSet.getInt("order_id");
                    String username = resultSet.getString("username");
                    Timestamp orderDate = resultSet.getTimestamp("order_date"); // Adjust the type if necessary

                    Timestamp orderRequestTime = resultSet.getTimestamp("order_request_time"); // Adjust the type if necessary

                    double totalPrice = resultSet.getDouble("total_price");
                    boolean delivery = resultSet.getBoolean("delivery");
                    //EnumOrderStatus status = EnumOrderStatus.valueOf(resultSet.getString("status"));
                   

                    // Create and add the Order object to the list
                    Order order = new Order(username, branchId, orderDate, orderRequestTime, totalPrice, delivery);
                    pendingOrders.add(order);
                }
            }
        }
        return pendingOrders;
    }
    
    
    
    public void createOrder(Order order, List<Dish> dishes) throws SQLException {
        String insertOrderQuery = "INSERT INTO orders (username, branch_id, order_date, order_ready_time, order_receive_time, total_price, delivery, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String insertDishInOrderQuery = "INSERT INTO dish_in_order (order_id, dish_id, optional, comment, dish_name) VALUES (?, ?, ?, ?, ?)";

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
                            for (Dish dishInOrder : dishes) {
                                dishStmt.setInt(1, orderId);
                                dishStmt.setInt(2, dishInOrder.getDishId());
                                dishStmt.setString(3, dishInOrder.getComments());
                                dishStmt.setString(4, dishInOrder.getOptionalPick());
                                dishStmt.setString(5, dishInOrder.getDishName());
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
    
    public List<DishInOrder> getDishesInOrder(int orderId) {
        List<DishInOrder> dishesInOrder = new ArrayList<>();
        String sql = "SELECT * FROM dish_in_order WHERE order_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int dishId = rs.getInt("dish_id");
                String optional = rs.getString("optional");
                String comment = rs.getString("comment");
                String dishName = rs.getString("dish_name");
                
                // Retrieve the Dish object based on dish_id
                Dish dish = getDishById(dishId);
                
                // Create a DishInOrder object
                DishInOrder ndish = new DishInOrder(dishName, dishId, comment, optional);
                ndish.setOrderId(orderId);
                dishesInOrder.add(ndish);
            }
        } catch (SQLException e) {
			e.printStackTrace();
		}
        return dishesInOrder;
    }
    
    private Dish getDishById(int dishId){
        String sql = "SELECT * FROM dishes WHERE dish_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, dishId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String dishName = rs.getString("dish_name");
                double price = rs.getDouble("price");
                int menuId = rs.getInt("menu_id");
                boolean isGrill = rs.getBoolean("is_grill");
                EnumDish dishType = EnumDish.valueOf(rs.getString("dish_type"));
                Dish dish = null;
                switch(dishType) {
                case BEVERAGE:
                	dish = new DishBeverage(dishName,isGrill, price, menuId);
                	break;
                case SALAD:
                	dish = new DishSalad(dishName,isGrill, price, menuId);
                	break;
                case APPETIZER:
                	dish = new DishAppetizer(dishName,isGrill, price, menuId);
                	break;
                case DESSERT:
                	dish = new DishDessert(dishName,isGrill, price, menuId);
                	break;
                case MAIN_COURSE:
                	dish = new DishMainCourse(dishName,isGrill, price, menuId);        
                	break;
                }
                return dish;
            }
        } catch (SQLException e) {
		}
		return null;
    }


    
    /**
     * Adds a new dish to the database. Before adding, it checks if a dish with
     * the same name, menu ID, and dish type already exists.
     * 
     * @param dish the dish to be added
     * @return true if the dish was added successfully, false if a dish with the
     *         same name, menu ID, and dish type already exists or if an error
     *         occurred
     */
    public boolean addDish(Dish dish) {
        if (dishExists(dish)) {
            return false;
        }

        String insertSql = "INSERT INTO dishes (menu_id, dish_type, dish_name, price, is_grill) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
            insertStmt.setInt(1, dish.getMenuId());
            insertStmt.setString(2, dish.getDishType().toString());
            insertStmt.setString(3, dish.getDishName());
            insertStmt.setDouble(4, dish.getPrice());
            insertStmt.setBoolean(5, dish.isGrill());

            int rowsAffected = insertStmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if a dish with the same name, menu ID, and dish type already exists
     * in the database.
     * 
     * @param dish the dish to be checked
     * @return true if a dish with the same name, menu ID, and dish type exists,
     *         false otherwise
     */
    private boolean dishExists(Dish dish) {
        String checkSql = "SELECT COUNT(*) FROM dishes WHERE menu_id = ? AND dish_name = ? AND dish_type = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, dish.getMenuId());
            checkStmt.setString(2, dish.getDishName());
            checkStmt.setString(3, dish.getDishType().toString());

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Dish with the same name, menu_id, and dish_type already exists
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Deletes a dish from the database based on the menu ID, dish type, dish name,
     * and price.
     * 
     * @param dish the dish to be deleted
     * @return true if the dish was deleted successfully, false if no matching dish
     *         was found or if an error occurred
     */
    public boolean deleteDish(Dish dish) {
        String sql = "DELETE FROM dishes WHERE menu_id = ? AND dish_type = ? AND dish_name = ? AND price = ? AND is_grill = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, dish.getMenuId());
            preparedStatement.setString(2, dish.getDishType().toString());
            preparedStatement.setString(3, dish.getDishName());
            preparedStatement.setDouble(4, dish.getPrice());
            preparedStatement.setBoolean(5, dish.isGrill());

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
                boolean isGrill = resultSet.getBoolean("is_grill");
                switch(dishType){
                case BEVERAGE:
                	DishBeverage dish = new DishBeverage(dishName,isGrill, price, menuId);
                	dish.setDishId(dishId);
                	dishes.add(dish);
                	break;
                case SALAD:
                	DishSalad dishs = new DishSalad(dishName,isGrill, price, menuId);
                	dishs.setDishId(dishId);
                	dishes.add(dishs);
                	break;
                case APPETIZER:
                	DishAppetizer dishdi = new DishAppetizer(dishName,isGrill, price, menuId);
                	dishdi.setDishId(dishId);
                	dishes.add(dishdi);
                	break;
                case DESSERT:
                	DishDessert dishd = new DishDessert(dishName,isGrill, price, menuId);
                	dishd.setDishId(dishId);
                	dishes.add(dishd);
                	break;
                case MAIN_COURSE:
                	DishMainCourse dishm = new DishMainCourse(dishName,isGrill, price, menuId);
                	dishm.setDishId(dishId);
                	dishes.add(dishm);
                	break;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dishes;
    }
    
    public boolean updateDish(Dish dish) {
        String updateSql = "UPDATE dishes SET dish_type = ?, dish_name = ?, price = ?, is_grill = ? WHERE dish_id = ?";

        try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
            updateStmt.setString(2, dish.getDishType().toString());
            updateStmt.setString(3, dish.getDishName());
            updateStmt.setDouble(4, dish.getPrice());
            updateStmt.setBoolean(5, dish.isGrill());
            updateStmt.setInt(6, dish.getDishId());

            int rowsAffected = updateStmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
        String query = "INSERT INTO performancereport (branch_id, totalOrders, ordersCompletedInTime, date) " +
                       "SELECT o.branch_id AS branch_id, " +
                       "COUNT(*) AS totalOrders, " +
                       "SUM(CASE WHEN o.order_receive_time <= o.order_ready_time THEN 1 ELSE 0 END) AS ordersCompletedInTime, " +
                       "DATE(o.order_date) AS date " +
                       "FROM orders o " +
                       "WHERE o.order_date >= DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH), '%Y-%m-01') " +
                       "AND o.order_date < DATE_FORMAT(CURDATE(), '%Y-%m-01') " +
                       "GROUP BY o.branch_id, DATE(o.order_date) " +
                       "ON DUPLICATE KEY UPDATE totalOrders = VALUES(totalOrders), ordersCompletedInTime = VALUES(ordersCompletedInTime)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected + " rows affected. Performance report for the last month generated successfully.");
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
            int restaurantId = getRestaurantIdByLocation(report.getRestaurant());
            pstmt.setInt(1, restaurantId);
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
    
    // Method to retrieve restaurant ID from its location
    private int getRestaurantIdByLocation(Location location){
        String query = "SELECT branch_id FROM restaurants WHERE location = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, location.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("branch_id");
            }
        } catch (SQLException e) {
			e.printStackTrace();
		}
        } catch (SQLException e1) {
        	e1.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Retrieves the performance report for a given restaurant, month, and year.
     * This function queries the database to get all daily performance reports for the specified
     * parameters and adds them to the provided PerformanceReport object.
     *
     * @param report The PerformanceReport object containing the restaurant location, month, and year.
     * @return The updated PerformanceReport object with the daily reports added, or a string
     * indicating that no such report exists, or an error message if an exception occurs.
     */
    public Object getPerformanceReport(PerformanceReport report) {
        String query = "SELECT totalOrders, ordersCompletedInTime, date FROM performancereport " +
                       "WHERE branch_id = ? AND MONTH(date) = ? AND YEAR(date) = ?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            // Set the parameters for the query
            int restaurantId = getRestaurantIdByLocation(report.getRestaurant());
            pstmt.setInt(1, restaurantId);
            pstmt.setInt(2, report.getMonth());
            pstmt.setInt(3, report.getYear());

            // Execute the query
            try (ResultSet rs = pstmt.executeQuery()) {
                boolean reportExists = false;
                while (rs.next()) {
                    int totalOrders = rs.getInt("totalOrders");
                    int ordersCompletedInTime = rs.getInt("ordersCompletedInTime");
                    Date date = rs.getDate("date");

                    DailyPerformanceReport dailyReport = new DailyPerformanceReport(date, totalOrders, ordersCompletedInTime);
                    report.addDailyReport(dailyReport);
                    reportExists = true;
                }

                if (!reportExists) {
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
            int restaurantId = getRestaurantIdByLocation(report.getRestaurant());
            pstmt.setInt(1, restaurantId);
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
                        rs.getString("username"),
                        rs.getInt("branch_id"),
                        rs.getTimestamp("order_date"),
                        rs.getTimestamp("order_request_time"),
                        rs.getDouble("total_price"),
                        rs.getBoolean("delivery")
                    );
                }
            }
        }
        return null;
    }

}