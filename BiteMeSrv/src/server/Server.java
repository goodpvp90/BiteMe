package server;

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import ServerGUI.serverController;
import common.EnumClientOperations;
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

	private String createResponseForClient(String message) {
		String[] words = message.split("\\s+");
		if ("inserted".equals(words[0]))
			return "The order sent successfully";
		else if ("Duplicate".equals(words[0]))
			return "The order already exists";
		else if ("updated".equals(words[0]))
			return "The order updated successfully";
		else if ("nothing".equals(words[0]))
			return "Nothing has changed";
		else
			return "Something went wrong";
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		String result;
		EnumServerOperations operation = EnumServerOperations.NONE;
		if (msg instanceof Object[]) {
			Object[] message = (Object[]) msg;
			operation = (EnumServerOperations) message[0];
			switch (operation) {
			case USER_CONDITION:
				controller.displayClientDetails((String[]) message[1]);
				break;
//			case INSERT_ORDER:
//				insertOrder((Object[]) message[1], client);
//				break;
//			case UPDATE_ORDER:
//				result = updateOrder((Object[]) message[1]);
//				break;
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

//	// CHECK IF WORKS
//	private void insertOrder(Object[] message, ConnectionToClient client) {
//		String result;
//		Order order = (Order) message[1];
//		result = dbController.insertOrder(order.getOrderNumber(), order.getNameOfRestaurant(), order.getTotalPrice(),
//				order.getOrderListNumber(), order.getOrderAddress());
//		try {
//			client.sendToClient(createResponseForClient(result));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

//	// Update order
//	private String updateOrder(Object[] message) {
//		int orderNum = (int) message[1];
//		String toChange = message[2].toString();
//		if ("Order_address".equals(toChange)) {
//			return (dbController.updateOrder(orderNum, toChange, (String) message[3]));
//		} else
//			return (dbController.updateOrder(orderNum, toChange, (double) message[3]));
//	}

	// view orders
//	private void viewOrders(ConnectionToClient client) {
//		List<Object[]> orders = dbController.showOrders();
//		Object[] ordersObj = orders.toArray();
//		sendMessageToClient(EnumClientOperations.VIEW, client, (Object)ordersObj);
//	}

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
