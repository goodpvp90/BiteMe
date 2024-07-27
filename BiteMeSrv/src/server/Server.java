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
import common.User;
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

    public void sendMessageToClient(EnumClientOperations op, ConnectionToClient client, Object msg) {
        try {
            Object message = new Object[]{op, msg};
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
            case DELETE_DISH:
                Dish dishO = (Dish)message[1];
                boolean deleteResult = OrderController.deleteDish(dishO);
                sendMessageToClient(EnumClientOperations.DELETE_DISH, client, deleteResult);
                break;
            case UPDATE_ORDER:
                int orderId = (int) message[1];
                EnumOrderStatus newStatus = (EnumOrderStatus) message[2];
                OrderController.updateOrderStatus(orderId, newStatus);
                break;
            case LOGIN:
                String username = (String) message[1];
                List<String> notifications = null;
                UserController.login(client, (Object[]) message);
                try {
                    notifications = dbController.getPendingNotifications(username);
                    dbController.deletePendingNotifications(username);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sendMessageToClient(EnumClientOperations.NOTIFICATION, client, notifications);
                break;
            case CREATE_ACCOUNT:
            	UserController.createAccount(client, (Object[]) message);
            case VIEW_MENU:
                int menuId = (int) message[1];
                List<Dish> menu = OrderController.viewMenu(menuId);
                sendMessageToClient(EnumClientOperations.VIEW_MENU, client, menu);
                break;
            case PENDING_ORDER:
                List<Order> pendingOrders = OrderController.getPendingOrdersByBranch((int) message[1]);
                sendMessageToClient(EnumClientOperations.PENDING_ORDER, client, pendingOrders);
                break;
            case UPDATE_DISH:
                Dish updatedDish = (Dish) message[1];
                boolean updateResult = OrderController.updateDish(updatedDish);
                sendMessageToClient(EnumClientOperations.UPDATE_DISH, client, updateResult);
                break;
			case NONE:
				System.out.println("no operation was recived");
				break;
			}
		} else
			System.out.println("Received unknown message type from client: " + msg);
	}

	
    public ConnectionToClient getClientByUsername(String username) {
        Thread[] clientThreadList = getClientConnections();
        for (Thread clientThread : clientThreadList) {
            ConnectionToClient client = (ConnectionToClient) clientThread;
            User user = (User) client.getInfo("user");
            if (user != null && user.getUsername().equals(username)) {
                return client;
            }
        }
        return null;
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
