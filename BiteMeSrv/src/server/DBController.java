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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import common.QuarterlyReport;
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
	                boolean isLogged = rs.getBoolean("isLogged");
	                if (isLogged) {
	                    return (Object) "User is already logged in";
	                }

	                String Email = rs.getString("email");
	                String phone = rs.getString("phone");
	                String firstName = rs.getString("firstname");
	                String lastName = rs.getString("lastname");
	                EnumType type = null;
	                if (rs.getString("type") != null) {
	                    try {
	                        type = EnumType.valueOf(rs.getString("type"));
	                    } catch (IllegalArgumentException e) {
	                        type = null;
	                    }
	                }

	                EnumBranch homeBranch = null;
	                if (rs.getString("home_branch") != null) {
	                    try {
	                        homeBranch = EnumBranch.valueOf(rs.getString("home_branch"));
	                    } catch (IllegalArgumentException e) {
	                        homeBranch = null;
	                    }
	                }

	                userDetails.add(firstName);
	                userDetails.add(lastName);
	                userDetails.add(Email);
	                userDetails.add(phone);
	                userDetails.add(homeBranch);
	                userDetails.add(username);
	                userDetails.add(password);
	                userDetails.add(isLogged);
	                userDetails.add(type);

	                // Update the isLogged status
	                if (!updateIsLoggedStatus(username)) {
	                    return (Object) "Error updating login status";
	                }
	            } else {
	                return (Object) "username or password are incorrect";
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return (Object) e.toString();
	    }
	    return (Object) userDetails;
	}

	private boolean updateIsLoggedStatus(String username) {
	    String query = "UPDATE users SET isLogged = 1 WHERE username = ?";
	    try {
	        PreparedStatement preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setString(1, username);
	        int rowsAffected = preparedStatement.executeUpdate();
	        return rowsAffected > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
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

 // Create or update user by managers
    public boolean createUser(String id, String username, String password, String email, String phone, String firstName, String lastName, EnumBranch enumBranch, EnumType type, EnumType customerType, String creditCard) {
        String sqlUpdateUsers = "UPDATE users SET password = ?, email = ?, phone = ?, firstname = ?, lastname = ?, home_branch = ?, type = ?, id = ? WHERE username = ?";
        String sqlInsertCustomers = "INSERT INTO customers (username, credit_card, type_of_customer) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE credit_card = VALUES(credit_card), type_of_customer = VALUES(type_of_customer)";
        try {
            // Start transaction
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatementUpdateUsers = connection.prepareStatement(sqlUpdateUsers);
                 PreparedStatement preparedStatementInsertCustomers = connection.prepareStatement(sqlInsertCustomers)) {
                // Update users table
                preparedStatementUpdateUsers.setString(1, password);  // Ensure you hash passwords before storing them
                preparedStatementUpdateUsers.setString(2, email);
                preparedStatementUpdateUsers.setString(3, phone);
                preparedStatementUpdateUsers.setString(4, firstName);
                preparedStatementUpdateUsers.setString(5, lastName);
                preparedStatementUpdateUsers.setString(6, enumBranch != null ? enumBranch.toString() : null);
                //TODO Added null
                preparedStatementUpdateUsers.setString(7, type != null ? type.toString() : null);
                preparedStatementUpdateUsers.setString(8, id);
                preparedStatementUpdateUsers.setString(9, username);

                int rowsAffected = preparedStatementUpdateUsers.executeUpdate();
                // If the user is of type CUSTOMER, insert or update in customers table
                if (type == EnumType.CUSTOMER) {
                    preparedStatementInsertCustomers.setString(1, username);
                    preparedStatementInsertCustomers.setString(2, creditCard);
                    preparedStatementInsertCustomers.setString(3, customerType.toString());

                    preparedStatementInsertCustomers.executeUpdate();
                }

                // Commit transaction
                connection.commit();
                return rowsAffected > 0;
            } catch (SQLException e) {
                // Rollback transaction on error
                connection.rollback();
                e.printStackTrace();
                return false;
            } finally {
                // Reset auto-commit mode
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean changeHomeBranch(User user) {
        PreparedStatement preparedStatement = null;
    	 // SQL query to update the user's home branch
        String sql = "UPDATE users SET home_branch = ? WHERE username = ?";
        try {
	        // Prepare the statement
	        preparedStatement = connection.prepareStatement(sql);
	
	        // Set the parameters
	        preparedStatement.setString(1, user.getHomeBranch().toString());
	        preparedStatement.setString(2, user.getUsername());
	
	        // Execute the update
	        int rowsAffected = preparedStatement.executeUpdate();
	        return rowsAffected > 0;
        }catch (SQLException e) {
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
                            rs.getTimestamp("order_date"), rs.getTimestamp("order_ready_time"), rs.getDouble("total_price"), rs.getBoolean("delivery"),EnumOrderStatus.valueOf(rs.getString("status")));

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
	    String query = "SELECT o.*, d.receiver_name AS delivery_receiver_name, d.phone_number AS delivery_phone_number " +
	                   "FROM orders o " +
	                   "LEFT JOIN delivery d ON o.order_id = d.order_id AND o.delivery = 1 " +
	                   "WHERE o.branch_id = ? AND (o.status = 'PENDING' OR o.status = 'IN_PROGRESS')";	    
	    List<Order> pendingOrders = new ArrayList<>();

	    try (PreparedStatement statement = connection.prepareStatement(query)) {
	        statement.setInt(1, branchId);
	        try (ResultSet resultSet = statement.executeQuery()) {
	            while (resultSet.next()) {
	            	int orderid = resultSet.getInt("order_id");
	                String username = resultSet.getString("username");
	                Timestamp orderDate = resultSet.getTimestamp("order_date");
	                Timestamp orderRequestTime = resultSet.getTimestamp("order_ready_time");
	                double totalPrice = resultSet.getDouble("total_price");
	                boolean delivery = resultSet.getBoolean("delivery");
	                EnumOrderStatus status = EnumOrderStatus.valueOf(resultSet.getString("status"));
	                Order order;
	                
	                if (delivery) {
	                    String receiverName = resultSet.getString("delivery_receiver_name");
	                    String phoneNumber = resultSet.getString("delivery_phone_number");
	                    order = new Order(username, branchId, orderDate, orderRequestTime, totalPrice, delivery, status);
	                    order.setReceiverName(receiverName);
	                    order.setOrderId(orderid);
	                } else {
	                    order = new Order(username, branchId, orderDate, orderRequestTime, totalPrice, delivery, status);
	                    order.setOrderId(orderid);
	                    // Fetch the first name and last name from the users table
	                    String userQuery = "SELECT firstname, lastname FROM users WHERE username = ?";
	                    try (PreparedStatement userStatement = connection.prepareStatement(userQuery)) {
	                        userStatement.setString(1, username);
	                        try (ResultSet userResultSet = userStatement.executeQuery()) {
	                            if (userResultSet.next()) {
	                                String firstName = userResultSet.getString("firstname");
	                                String lastName = userResultSet.getString("lastname");
	                                order.setReceiverName(firstName + " " + lastName);
	                            }
	                        }
	                    }
	                }

	                pendingOrders.add(order);
	            }
	        }
	    }
	    return pendingOrders;
	}

    
    
    
    public void createOrder(Order order, List<Dish> dishes) throws SQLException {
        String insertOrderQuery = "INSERT INTO orders (username, branch_id, order_date, order_ready_time, order_receive_time, total_price, delivery, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String insertDishInOrderQuery = "INSERT INTO dish_in_order (order_id, dish_id, optional, comment, dish_name) VALUES (?, ?, ?, ?, ?)";
        String insertDeliveryQuery = "INSERT INTO delivery (order_id, City, street_and_number, receiver_name, phone_number) VALUES (?, ?, ?, ?, ?)";

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

                        // Insert the delivery details if it's a delivery order
                        if (order.isDelivery()) {
                            try (PreparedStatement deliveryStmt = connection.prepareStatement(insertDeliveryQuery)) {
                                deliveryStmt.setInt(1, orderId);
                                deliveryStmt.setString(2, order.getCity());
                                deliveryStmt.setString(3, order.getStreetAndNum());
                                deliveryStmt.setString(4, order.getReceiverName());
                                deliveryStmt.setString(5, String.valueOf(order.getPhoneNum()));
                                deliveryStmt.executeUpdate();
                            }
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
    
    
    public User searchUsername(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String firstName = rs.getString("firstname");
                    String lastName = rs.getString("lastname");
                    String email = rs.getString("email");
                    String phone = rs.getString("phone");
                    String dbUsername = rs.getString("username");
                    String password = rs.getString("password");
                    boolean isLogged = rs.getBoolean("isLogged");
                    
                    EnumType type = null;
                    if (rs.getString("type") != null) {
                        try {
                            type = EnumType.valueOf(rs.getString("type"));
                        } catch (IllegalArgumentException e) {
                            // Handle the case where the type value is invalid
                            type = null;
                        }
                    }

                    EnumBranch homeBranch = null;
                    if (rs.getString("home_branch") != null) {
                        try {
                            homeBranch = EnumBranch.valueOf(rs.getString("home_branch"));
                        } catch (IllegalArgumentException e) {
                            // Handle the case where the home_branch value is invalid
                            homeBranch = null;
                        }
                    }

                    return new User(firstName, lastName, email, phone, homeBranch, dbUsername, password, isLogged, type);
                } else {
                    return null; // User not found
                }
            }
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
   
    /**
     * Retrieves a quarterly report for a specific branch, year, and quarter.
     * If the report exists, it populates the {@code qreport} object with the report data.
     * If the report does not exist, it returns a message indicating that the report is not available.
     * 
     * @param qreport the {@link QuarterlyReport} object containing the details for which the report is to be fetched.
     *                It is assumed to have the branch ID, year, and quarter set.
     * @return an {@code Object} which is either the populated {@link QuarterlyReport} object if the report exists,
     *         or a {@code String} message indicating that the report does not exist or an error occurred.
     */
    public Object getQuarterlyReport(QuarterlyReport qreport){
        String selectReportSQL = "SELECT * FROM quarterly_report WHERE branch_id = ? AND year = ? AND quarter = ?";
    	int restaurantId = getRestaurantIdByLocation(qreport.getRestaurant());
        try (PreparedStatement selectStmt = connection.prepareStatement(selectReportSQL)) {
            selectStmt.setInt(1, restaurantId);
            selectStmt.setInt(2, qreport.getYear());
            selectStmt.setInt(3, qreport.getQuarter());

            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                // Report exists
            	qreport.setIncome(rs.getInt("income"));
                qreport.addDaysInRanges("0_20", rs.getInt("0_20"));
                qreport.addDaysInRanges("21_40", rs.getInt("21_40"));
                qreport.addDaysInRanges("41_60", rs.getInt("41_60"));
                qreport.addDaysInRanges("61_80", rs.getInt("61_80"));
                qreport.addDaysInRanges("81_100", rs.getInt("81_100"));
                qreport.addDaysInRanges("101_120", rs.getInt("101_120"));
                qreport.addDaysInRanges("121_140", rs.getInt("121_140"));
                qreport.addDaysInRanges("141_160", rs.getInt("141_160"));
                qreport.addDaysInRanges("161_180", rs.getInt("161_180"));
                qreport.addDaysInRanges("181_200", rs.getInt("181_200"));
                qreport.addDaysInRanges("201_plus", rs.getInt("201_plus"));
                return (Object)qreport;
            }
        } catch (SQLException e) {
        	e.printStackTrace();
        	return (Object)e.toString();
		}
        //if got here meaning report don't exist
        return (Object)"Report dont exist";
    }
    
    /**
     * Creates a quarterly report for a specific branch, year, and quarter.
     * If the report already exists, it will not be created again.
     * This method populates the new report with income and the number of days in each specified range.
     * 
     * @param qreport the {@link QuarterlyReport} object containing the details for the report to be created.
     *                It should have the branch ID, year, and quarter set.
     * @return {@code true} if the report was successfully created; {@code false} otherwise.
     */
    public boolean createQuarterlyReport(QuarterlyReport qreport) {
    	int restaurantId = getRestaurantIdByLocation(qreport.getRestaurant());
    	int income = getIncomeForQuarter(restaurantId, qreport.getYear(), qreport.getQuarter());
        Object result = getDaysInRanges(qreport);
        if (result == null)
        	return false;
		@SuppressWarnings("unchecked")
		Map<String, Integer> daysInRanges = (Map<String, Integer>)result;

        // Insert new report
        String insertReportSQL = "INSERT INTO quarterly_report (branch_id, quarter, year, income, `0_20`, `21_40`, `41_60`, `61_80`, `81_100`, `101_120`, `121_140`, `141_160`, `161_180`, `181_200`, `201_plus`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertReportSQL)) {
            insertStmt.setInt(1, restaurantId);
            insertStmt.setInt(2, qreport.getQuarter());
            insertStmt.setInt(3, qreport.getYear());
            insertStmt.setInt(4, income);
            insertStmt.setInt(5, daysInRanges.get("0_20"));
            insertStmt.setInt(6, daysInRanges.get("21_40"));
            insertStmt.setInt(7, daysInRanges.get("41_60"));
            insertStmt.setInt(8, daysInRanges.get("61_80"));
            insertStmt.setInt(9, daysInRanges.get("81_100"));
            insertStmt.setInt(10, daysInRanges.get("101_120"));
            insertStmt.setInt(11, daysInRanges.get("121_140"));
            insertStmt.setInt(12, daysInRanges.get("141_160"));
            insertStmt.setInt(13, daysInRanges.get("161_180"));
            insertStmt.setInt(14, daysInRanges.get("181_200"));
            insertStmt.setInt(15, daysInRanges.get("201_plus"));

            insertStmt.executeUpdate();
            System.out.println("Quarterly Report created.");
            return true;
        } catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error generating new quarterly report");
			return false;
		}
    }
        
    /**
     * Retrieves the total income for a specific branch, year, and quarter.
     * The income is calculated by summing the income for the months that fall within the given quarter.
     * 
     * @param branchId the ID of the branch for which the income is to be retrieved.
     * @param year the year for which the income is to be calculated.
     * @param quarter the quarter (1 to 4) for which the income is to be calculated.
     * @return the total income for the specified branch, year, and quarter.
     *         Returns 0 if there is an error or no income is found.
     */
    private int getIncomeForQuarter(int branchId, int year, int quarter){
        String incomeSQL = "SELECT SUM(income) FROM incomereport WHERE restaurant = ? AND year = ? AND month IN (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(incomeSQL)) {
            stmt.setInt(1, branchId);
            stmt.setInt(2, year);
            stmt.setInt(3, quarter * 3 - 2); // 1st month of the quarter
            stmt.setInt(4, quarter * 3 - 1); // 2nd month of the quarter
            stmt.setInt(5, quarter * 3);     // 3rd month of the quarter

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error getting income for quarterly report");
		}
        return 0;
    }

    /**
     * Retrieves the number of days that fall within specific ranges of total orders for a given branch, year, and quarter.
     * The ranges are predefined and include: 0-20, 21-40, 41-60, 61-80, 81-100, 101-120, 121-140, 141-160, 161-180, 181-200, and 201+.
     * 
     * @param qreport the {@link QuarterlyReport} object containing the details for which the range counts are to be fetched.
     *                It should have the branch ID, year, and quarter set.
     * @return an {@code Object} that contains a {@link Map} with the number of days in each range.
     *         The keys of the map are the range labels (e.g., "0_20", "21_40"), and the values are the counts of days within those ranges.
     *         Returns {@code null} if there is an error executing the SQL query.
     */
    private Object getDaysInRanges(QuarterlyReport qreport){
        // SQL query to get the number of days in each range of total orders
        String performanceSQL = "SELECT COUNT(*) AS days, " +
                "SUM(totalOrders BETWEEN 0 AND 20) AS `0_20`, " +
                "SUM(totalOrders BETWEEN 21 AND 40) AS `21_40`, " +
                "SUM(totalOrders BETWEEN 41 AND 60) AS `41_60`, " +
                "SUM(totalOrders BETWEEN 61 AND 80) AS `61_80`, " +
                "SUM(totalOrders BETWEEN 81 AND 100) AS `81_100`, " +
                "SUM(totalOrders BETWEEN 101 AND 120) AS `101_120`, " +
                "SUM(totalOrders BETWEEN 121 AND 140) AS `121_140`, " +
                "SUM(totalOrders BETWEEN 141 AND 160) AS `141_160`, " +
                "SUM(totalOrders BETWEEN 161 AND 180) AS `161_180`, " +
                "SUM(totalOrders BETWEEN 181 AND 200) AS `181_200`, " +
                "SUM(totalOrders > 200) AS `201_plus` " +
                "FROM performancereport " +
                "WHERE branch_id = ? AND YEAR(date) = ? AND QUARTER(date) = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(performanceSQL)) {
        	int restaurantId = getRestaurantIdByLocation(qreport.getRestaurant());
            stmt.setInt(1, restaurantId);
            stmt.setInt(2, qreport.getYear());
            stmt.setInt(3, qreport.getQuarter());

            ResultSet rs = stmt.executeQuery();
            Map<String, Integer> daysInRanges = new HashMap<>();
            if (rs.next()) {
                daysInRanges.put("0_20", rs.getInt("0_20"));
                daysInRanges.put("21_40", rs.getInt("21_40"));
                daysInRanges.put("41_60", rs.getInt("41_60"));
                daysInRanges.put("61_80", rs.getInt("61_80"));
                daysInRanges.put("81_100", rs.getInt("81_100"));
                daysInRanges.put("101_120", rs.getInt("101_120"));
                daysInRanges.put("121_140", rs.getInt("121_140"));
                daysInRanges.put("141_160", rs.getInt("141_160"));
                daysInRanges.put("161_180", rs.getInt("161_180"));
                daysInRanges.put("181_200", rs.getInt("181_200"));
                daysInRanges.put("201_plus", rs.getInt("201_plus"));
            }
            return (Object)daysInRanges;
        } catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error getting Days by range for creating quartyely report");
		}
        return null;
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
                        rs.getTimestamp("order_ready_time"),
                        rs.getDouble("total_price"),
                        rs.getBoolean("delivery"),
                        EnumOrderStatus.valueOf(rs.getString("status"))
                    );
                }
            }
        }
        return null;
    }

    //TODO USED WHEN DISCONNECTING THE SERVER.
    public void resetAllUserLoggedStatus() {
        String query = "UPDATE users SET isLogged = 0";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected + " users' logged status reset.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error resetting user logged status.");
        }
    }
    
    
    
}