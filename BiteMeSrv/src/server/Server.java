package server;

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import ServerGUI.serverController;
import common.Dish;
import common.DishInOrder;
import common.EnumClientOperations;
import common.EnumOrderStatus;
import common.EnumServerOperations;
import common.Order;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Server extends AbstractServer {
	private static Server instance;
	public DBController dbController;
	private serverController controller;

	// Private constructor
	private Server(int port, String url, String username, String password) {
		super(port);
		dbController = new DBController();
		try {
			dbController.connect(url, username, password);
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("Failed to connect to the database.");
			e.printStackTrace();
		}
	}

	// Public method to initialize the Server and get the instance
	public static void initialize(int port, String url, String username, String password) {
		if (instance == null) {
			instance = new Server(port, url, username, password);
		}
	}

	// Public method to get the Server instance
	public static Server getInstance() {
		if (instance == null) {
			throw new IllegalStateException("Server not initialized. Call initialize() first.");
		}
		return instance;
	}

	public void sendMessageToClient(EnumClientOperations op,ConnectionToClient client, Object msg) {
		try {
			Object message = new Object[] { op, msg };
			client.sendToClient(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		String result;
		EnumServerOperations operation = EnumServerOperations.NONE;
		if (msg instanceof Object[]) {
			Object[] message = (Object[]) msg;
			operation = (EnumServerOperations) message[0];
        	System.out.println(operation);
			switch (operation) {
			case USER_CONDITION:
				controller.displayClientDetails((String[]) message[1]);
				break;
            case ADD_DISH:
                Dish dish = (Dish) message[1];
                OrderController.addDish(dish);
                break;
            case INSERT_ORDER:
                // Extract data from the message
                Order newOrder = (Order) message[1];
                List<DishInOrder> dishesInOrder = (List<DishInOrder>) message[2];
                // Call the method to create the order
                try {
                    OrderController.createOrder(newOrder, dishesInOrder);
                    result = "Order created successfully.";
                } catch (Exception e) {
                    result = "Error creating order: " + e.getMessage();
                }
                break;
            case UPDATE_ORDER:
                // Extract data from the message
                int orderId = (int) message[1];
                EnumOrderStatus newStatus = (EnumOrderStatus) message[2];
                // Call the method to update the order status
                try {
                    OrderController.updateOrderStatus(orderId, newStatus);
                    result = "Order status updated successfully.";
                } catch (Exception e) {
                    result = "Error updating order status: " + e.getMessage();
                }
                break;
			case LOGIN:
				UserController.login(client, (Object[]) message);
				break;
//			case VIEW:
//				viewOrders(client);
//				break;
			case NONE:
				System.out.println("no operation was recived");
				break;
			}
		} else
			System.out.println("Received unknown message type from client: " + msg);
	}


	@Override
	protected void clientDisconnected(ConnectionToClient client) {
		controller.displayClientDetails((new String[] { "Client disconnected: " + client }));
	}

	@Override
	protected void serverStarted() {
		controller.updateStatus("Server listening for connections on port " + getPort());
	}

	@Override
	protected void serverStopped() {
		controller.updateStatus("Server has stopped listening for connections.");
		dbController.closeConnection();
	}

	public void stopServer() {
		try {
			stopListening();
			close();
			instance = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setController(serverController controller) {
		this.controller = controller;
	}
}
