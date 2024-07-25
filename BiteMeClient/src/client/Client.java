package client;

import ocsf.client.*;


import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import common.Dish;
import common.DishInOrder;
import common.EnumClientOperations;
import common.EnumDish;
import common.EnumServerOperations;
import common.Order;
import common.User;
import ClientGUI.ClientLoginController;
import ClientGUI.clientController;

public class Client extends AbstractClient {
	// Default port to connect to the server
	final public static int DEFAULT_PORT = 8080;
	private static Client instance;
	// Controller for Client GUI functionality
	private clientController clientController;
	private ClientLoginController clientLoginController;
	
	// Constructor to initialize the client with host and port, and establish
	// connection
	public Client(String host, int port) throws IOException {
		super(host, port);
		openConnection();

		// Get client's IP address and host name
		String clientIP = InetAddress.getLocalHost().getHostAddress();
		String clientHostName = InetAddress.getLocalHost().getHostName();

		// Send initial connection message to the server

		sendToServer(new Object[] { EnumServerOperations.USER_CONDITION,
				new String[] { clientIP, clientHostName, "start" } });

	}
	
	// Public method to get the Server instance
	public static Client getInstance() {
		if (instance == null) {
			throw new IllegalStateException("Client not initialized. Call initialize() first.");
		}
		return instance;
	}
	
	// Public method to initialize the Server and get the instance
	public static void initialize(String host, int port) throws IOException {
		if (instance == null) {
			instance = new Client(host, port);
		}
	}

	// Sets the GUI controller for this client
	public void setGuiController(clientController clientController) {
		this.clientController = clientController;
	}

	// Handle messages received from the server
	@Override
	protected void handleMessageFromServer(Object msg) {
		EnumClientOperations operation = EnumClientOperations.NONE;
		if (msg instanceof Object[]) {
			Object[] message = (Object[]) msg;
			operation = (EnumClientOperations) message[0];
			switch (operation) {
			case DISPLAY_ORDERS:
				// Handle array of orders from the server
	            Object[] orders = (Object[]) message[1];
	            if (clientController != null) {
	                clientController.displayOrders(orders);
	            }
	            break;
			case USER:
				//SEND TO CLIENT CONTROLLER
	        	Object data = (Object)message[1];
	        	clientLoginController.updateUser(data);
	        	break;
			case UPDATE_WELOCME:
				// Handle non-array messages for updating the top label in clientController
	            if (clientController != null) {
	                clientController.updateWelcomeText("Message from server: " + message[1]);
	            }
	            break;
			case NONE:
				System.out.println("no operation was recived");
				break;
			}
		}
	}

	// Quit the client and close the connection
	public void quit() {
		try {
			// Get client's IP address and host name
			String clientIP = InetAddress.getLocalHost().getHostAddress();
			String clientHostName = InetAddress.getLocalHost().getHostName();

			// Send disconnection message to the server
			sendToServer(new Object[] { EnumServerOperations.USER_CONDITION,
					new String[] { clientIP, clientHostName, "end" } });

			// Close the connection
			closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Exit the application
		System.exit(0);
	}

	// Send a message to the server
	public void sendMessageToServer(Object msg) {
		try {
			sendToServer(msg);
		} catch (Exception e) {
			// Update the GUI with an error message if sending fails
			if (clientController != null) {
				clientController.updateWelcomeText("Failed to send message to server: " + e.getMessage());
			}
		}
	}
	
	public void getInstanceOfClientLoginController(ClientLoginController client) {
		this.clientLoginController = client;
	}
	
	// Send a request to update an order on the server
	public void sendUpdateOrderRequest(int orderNum, String colToChange, Object newVal) {
		Object[] arr = new Object[4];
		arr[0] = "updateOrder";
		arr[1] = orderNum;
		arr[2] = colToChange;
		arr[3] = newVal;
		sendMessageToServer(arr);
	}
	
	public void sendAddDishRequest(Dish dish) {
	    // Send a request to add a new dish
		sendMessageToServer(new Object[] { EnumServerOperations.ADD_DISH, dish });
	}
	
	public void sendCreateOrderRequest(Order order, List<DishInOrder> dishesInOrder) {
	    sendMessageToServer(new Object[] {
		        EnumServerOperations.INSERT_ORDER, order, dishesInOrder});
	}

	// Request to view orders from the database
	public void viewOrdersFromDB() {
		sendMessageToServer(new Object[] { EnumServerOperations.VIEW });
	}

	public void loginValidation(User user) {
		sendMessageToServer(new Object[] { EnumServerOperations.LOGIN, user });
	}
}
