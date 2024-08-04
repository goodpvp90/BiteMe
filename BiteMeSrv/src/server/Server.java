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
import common.IncomeReport;
import common.Order;
import common.OrdersReport;
import common.PerformanceReport;
import common.User;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Server extends AbstractServer {
	private static Server instance;
	public DBController dbController;
	private serverController controller;
	private ReportController reportController;
	private OrderController orderController;
	private UserController userController;
	
	// Private constructor
	public Server(int port, String url, String username, String password) {
		super(port);
		dbController = new DBController();
		reportController = new ReportController(this);
		orderController = new OrderController(this);
		userController = new UserController(this);
		try {
			dbController.connect(url, username, password);
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("Failed to connect to the database.");
			e.printStackTrace();
		}
	}

    public void sendMessageToClient(EnumClientOperations op, ConnectionToClient client, Object msg) {
        try {
            Object message = new Object[]{op, msg};
            System.out.println(op.toString());
            System.out.println("SENDING TO CLIENT1");
            client.sendToClient(message);
            System.out.println("SENDING TO CLIENT2");
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
                orderController.addDish(dish);
                break;
            case INSERT_ORDER:
                // Extract data from the message
                Order newOrder = (Order) message[1];
                List<DishInOrder> dishesInOrder = (List<DishInOrder>) message[2];
                // Call the method to create the order
                try {
                    sendMessageToClient(EnumClientOperations.INSERT_ORDER,client, orderController.createOrder(newOrder, dishesInOrder));
                } catch (Exception e) {
                    result = "Error creating order: " + e.getMessage();
                }
                break;
            case DELETE_DISH:
                Dish dishO = (Dish)message[1];
                boolean deleteResult = orderController.deleteDish(dishO);
                sendMessageToClient(EnumClientOperations.DELETE_DISH, client, deleteResult);
                break;
            case UPDATE_ORDER:
                int orderId = (int) message[1];
                EnumOrderStatus newStatus = (EnumOrderStatus) message[2];
                orderController.updateOrderStatus(orderId, newStatus);
                break;
            case LOGIN:
            	handleLogin(client, message);
                break;
            case LOG_OUT:
            	userController.logout(client,(Object[]) message);
            	break;
            case CREATE_ACCOUNT:
            	userController.createAccount(client, (Object[]) message);
            	break;
            case VIEW_MENU:
                int menuId = (int) message[1];
                List<Dish> menu = orderController.viewMenu(menuId);
                sendMessageToClient(EnumClientOperations.VIEW_MENU, client, menu);
                break;
            case PENDING_ORDER:
                List<Order> pendingOrders = orderController.getPendingOrdersByBranch((int) message[1]);
                sendMessageToClient(EnumClientOperations.PENDING_ORDER, client, pendingOrders);
                break;
            case INCOME_REPORT:
            	reportController.getIncomeReport((IncomeReport)message[1], client);
            	break;
            case ORDERS_REPORT:
            	reportController.getOrdersReport((OrdersReport)message[1], client);
            	break;
            case PERFORMANCE_REPORT:
            	reportController.getPerformanceReport((PerformanceReport)message[1], client);
            	break;
			case NONE:
				System.out.println("no operation was recived");
				break;
			}
		} else
			System.out.println("Received unknown message type from client: " + msg);
	}
	
	private void handleLogin(ConnectionToClient client, Object[] message) {
    	User user = (User)message[1];
        String username = user.getUsername();
        List<String> notifications = null;
       	System.out.println("LOGIN SHOWED ON SERVER");
        userController.login(client, (Object[]) message);
        try {
            notifications = dbController.getPendingNotifications(username);
            dbController.deletePendingNotifications(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sendMessageToClient(EnumClientOperations.NOTIFICATION, client, notifications);
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
		reportController.shutdown();
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
