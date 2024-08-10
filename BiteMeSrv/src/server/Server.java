package server;

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import ServerGUI.serverController;
import common.Dish;
import common.DishInOrder;
import common.EnumClientOperations;
import common.EnumDish;
import common.EnumOrderStatus;
import common.EnumServerOperations;
import common.IncomeReport;
import common.Order;
import common.OrdersReport;
import common.PerformanceReport;
import common.QuarterlyReport;
import common.Restaurant.Location;
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
                boolean addResult = orderController.addDish(dish);
                sendMessageToClient(EnumClientOperations.ADD_DISH, client, addResult);
                break;
            case INSERT_ORDER:
                // Extract data from the message
                Order newOrder = (Order) message[1];
                List<Dish> dishesInOrder = (List<Dish>) message[2];
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
            case UPDATE_ORDER_STATUS:
                int orderId = (int) message[1];
                EnumOrderStatus newStatus = (EnumOrderStatus) message[2];
                orderController.updateOrderStatus(orderId, newStatus);
                break;
            case LOGIN:
            	orderController.updateOrderStatus(19, EnumOrderStatus.READY);
            	handleLogin(client, message);
                break;
            case LOG_OUT:
            	userController.logout(client,(Object[]) message);
            	break;
            case CHECK_USER:
            	String usern = (String) message[1];
            	userController.checkUserForCreation(client,usern);
            	break;
            case CREATE_ACCOUNT:
            	userController.createAccount(client, (Object[]) message);
            	break;
            case VIEW_MENU:
            	viewMenu(client, message, operation);
                break;
            case MENU_FOR_UPDATE:
            	viewMenu(client, message, operation);
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
            case QUARTERLY_REPORT:
            	//TODO SYSO REMOVE
            	System.out.println("Received quarterly report request from client"); 
            	reportController.getQuarterlyReport((QuarterlyReport)message[1], client);
            	System.out.println("Quarterly report processed and sent back to client"); 
            	//TODO SYSO REMOVE
            	break;
            case GET_DISCOUNT_AMOUNT:
            	String username = (String)message[1];
            	double amount = dbController.getCurrentDiscountAmount(username);
            	sendMessageToClient(EnumClientOperations.GET_DISCOUNT_AMOUNT, client, amount);
            	break;
            case SET_DISCOUNT_AMOUNT:
            	String username1 = (String)message[1];
            	double amount1 = (double)message[2];
            	dbController.updateDiscountAmount(username1, amount1);
            	//MAYBE ADD RESPONSE TO CLIENT IF UPDATED SUCCESFULLY.
            	break;
            case DISHES_IN_ORDER:
            	int orderid = (int)message[1];
            	List<DishInOrder> dishes = orderController.getDishesInOrder(orderid);
            	sendMessageToClient(EnumClientOperations.DISHES_IN_ORDER, client, dishes);
            	break;
            case CHANGE_HOME_BRANCH:
            	boolean changeResult = userController.changeHomeBranch((User)message[1]);
            	sendMessageToClient(EnumClientOperations.CHANGE_HOME_BRANCH, client, changeResult);
            	break;
			case NONE:
				System.out.println("No operation was received");
				break;
			}
		} else
			System.out.println("Received unknown message type from client: " + msg);
	}
	
	private void viewMenu(ConnectionToClient client, Object[] message, EnumServerOperations operation) {
        int menuId = (int) message[1];
        List<Dish> menu = orderController.viewMenu(menuId);
        sendMessageToClient(EnumClientOperations.valueOf(operation.toString()), client, menu);
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
		sendToAllClients(EnumClientOperations.SERVER_DISCONNECTED);
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
