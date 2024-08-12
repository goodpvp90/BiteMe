package server;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ServerGUI.serverController;
import common.Dish;
import common.DishInOrder;
import common.EnumBranch;
import common.EnumClientOperations;
import common.EnumDish;
import common.EnumOrderStatus;
import common.EnumServerOperations;
import common.EnumType;
import common.IncomeReport;
import common.Order;
import common.OrdersReport;
import common.PerformanceReport;
import common.QuarterlyReport;
import common.User;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;


public class Server extends AbstractServer {
	public DBController dbController;
	private serverController controller;
	private ReportController reportController;
	private OrderController orderController;
	private UserController userController;
	private Map<String, ConnectionToClient> clients = new HashMap<>();
	private List<ConnectionToClient> clientsInOrderCreation = new ArrayList<>();
	private Map<User,ConnectionToClient> workersInPendingOrders = new HashMap<>();
	
	
	public Server(int port, String url, String username, String password) {
		super(port);
		dbController = new DBController();
		reportController = new ReportController(this);
		orderController = new OrderController(this);
		userController = new UserController(this);
		//clients = new HashMap<>();
		try {
			dbController.connect(url, username, password);
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("Failed to connect to the database.");
			throw new NullPointerException();
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
        	System.out.println(areConnectionsEqual(clients.get("ben"),client));
			switch (operation) {
			case USER_CONDITION:
				controller.displayClientDetails((String[]) message[1]);
				break;
            case ADD_DISH:
                Dish dish = (Dish) message[1];
                boolean addResult = orderController.addDish(dish);
                sendMessageToClient(EnumClientOperations.ADD_DISH, client, addResult);
                break;
            case UPDATE_DISH:
                boolean updateResult = orderController.updateDish((Dish) message[1]);
                sendMessageToClient(EnumClientOperations.UPDATE_DISH, client, updateResult);
            	break;
            case IN_ORDER_CREATION:
            	addClientToClientsInOrderCreation(client);
            	break;
            case OUT_ORDER_CREATION:
            	removeClientsInOrderCreation(client);
            	break;
            case INSERT_ORDER:
                // Extract data from the message
                Order newOrder = (Order) message[1];
                List<Dish> dishesInOrder = (List<Dish>) message[2];
                // Call the method to create the order
                try {
                	boolean order = orderController.createOrder(newOrder, dishesInOrder, client);
                	notifyWorker(dbController.getLocationByBranchId(newOrder.getBranchId()));
                    sendMessageToClient(EnumClientOperations.INSERT_ORDER,client, order);
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
                String update_msg = (String)message[3];
                orderController.updateOrderStatus(orderId, newStatus, update_msg);
                break;
            case LOGIN:
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
            case USERS_ORDERS:
            	List<Order> orders = orderController.getOrdersByUsername((String)message[1]);
            	sendMessageToClient(EnumClientOperations.USERS_ORDERS, client, orders);
            	break;
            case ORDER_ON_TIME:
            	int orderarriveid = (int)message[1];
            	sendMessageToClient(EnumClientOperations.ORDER_ON_TIME, client, dbController.isOrderArrivedOnTime(orderarriveid)); 	
            	break;
            case IN_PENDING_ORDERS:
            	addToWorkersInPendingOrders((User)message[1], client);
                for (User userr : workersInPendingOrders.keySet())
                	System.out.println(userr.equals((User)message[1]));
            	break;
            case OUT_PENDING_ORDERS:
            	removeFromWorkersInPendingOrders((User)message[1]);
            	break;
			case NONE:
				System.out.println("No operation was received");
				break;
			}
		} else
			System.out.println("Received unknown message type from client: " + msg);
	}
	
	//=======================================================
    public boolean areConnectionsEqual(ConnectionToClient client1, ConnectionToClient client2) {
        return clients.entrySet().stream()
            .anyMatch(entry -> entry.getValue().equals(client1) && entry.getValue().equals(client2));
    }
    //==========================================================
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
        boolean loginReuslt = userController.login(client, (Object[]) message);
        if (loginReuslt) {
        	client.setInfo("user", username);
        	try {
                notifications = dbController.getPendingNotifications(username);
                dbController.deletePendingNotifications(username);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if(!notifications.isEmpty())
            	sendMessageToClient(EnumClientOperations.NOTIFICATION, client, notifications);
        }
	}
    
    public void addToClients(String key, ConnectionToClient client) {
        clients.put(key, client);
    }

    public void removeFromClients(String key) {
        clients.remove(key);
    }
    
    // Retrieve a client from the map by key
    public ConnectionToClient getClient(String key) {
        return clients.get(key);
    }
    
    public void addClientToClientsInOrderCreation(ConnectionToClient client) {
    	clientsInOrderCreation.add(client);
    }
    
    public void removeClientsInOrderCreation(ConnectionToClient client) {
    	clientsInOrderCreation.remove(client);
    }
    
    public void notifyUpdatedMenu() {
    	for (ConnectionToClient c : clientsInOrderCreation)
    		sendMessageToClient(EnumClientOperations.INTERRUPT_ORDER_CREATION, c, "STOP CREATING ORDER");
    }
    
    public void addToWorkersInPendingOrders(User key, ConnectionToClient client) {
    	System.out.println("ADDING");
    	workersInPendingOrders.put(key, client);
    }

    public void removeFromWorkersInPendingOrders(User key) {
    	workersInPendingOrders.remove(key);
    }
    
    public ConnectionToClient getWorkerInPendingOrders(User key) {
        return workersInPendingOrders.get(key);
    }
    
    public void notifyWorker(EnumBranch branchLoc) {
        for (User user : workersInPendingOrders.keySet()) {
            if(user.getHomeBranch() == branchLoc) {
            	sendMessageToClient(EnumClientOperations.INTERRUPT_PENDING_ORDERS, workersInPendingOrders.get(user), "RELOAD PENDING PAGE");
            }
        }
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
		dbController.resetAllUserLoggedStatus();
		sendToAllClients(EnumClientOperations.SERVER_DISCONNECTED);
		dbController.closeConnection();
		reportController.shutdown();
	}

	public void stopServer() {
		try {
			stopListening();
			close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setController(serverController controller) {
		this.controller = controller;
	}
}
