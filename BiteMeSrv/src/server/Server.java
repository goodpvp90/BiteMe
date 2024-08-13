package server;

import java.io.IOException;
import java.sql.SQLException;

import ServerGUI.serverController;
import controllers.DBController;
import controllers.NotificationController;
import controllers.OrderController;
import controllers.ReportController;
import controllers.RestaurantController;
import controllers.UserController;
import enums.EnumClientOperations;
import enums.EnumServerOperations;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;
import reports.IncomeReport;
import reports.OrdersReport;
import reports.PerformanceReport;
import reports.QuarterlyReport;
import userEntities.User;

public class Server extends AbstractServer {
	private DBController dbController;
	private serverController controller;
	private ReportController reportController;
	private OrderController orderController;
	private UserController userController;
	private NotificationController notificationController;
	private RestaurantController restaurantController;
	
	public Server(int port, String url, String username, String password) {
		super(port);
		dbController = new DBController();
		notificationController = new NotificationController(this, dbController);
		reportController = new ReportController(this, dbController);
		userController = new UserController(this, notificationController, dbController);
		orderController = new OrderController(this, notificationController, dbController);
		restaurantController = new RestaurantController(this, dbController, notificationController);
		
		try {
			dbController.connect(url, username, password);
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("Failed to connect to the database.");
			throw new NullPointerException();
		}
	}
	
	public DBController getDBController() {
		return this.dbController;
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
		EnumServerOperations operation;
		if (msg instanceof Object[]) {
			Object[] message = (Object[]) msg;
			operation = (EnumServerOperations) message[0];
        	System.out.println(operation);
        	System.out.println(notificationController.areConnectionsEqual(notificationController.getClient("ben"),client));
			switch (operation) {
			case CLIENT_CONDITION:
				controller.displayClientDetails((String[]) message[1]);
				break;
            case ADD_DISH:
            	restaurantController.handleAddDish(client, message);
                break;
            case UPDATE_DISH:
            	restaurantController.handleUpdateDish(client, message);
            	break;
            case DELETE_DISH:
            	restaurantController.handleDeleteDish(client, message);
                break;
            case IN_ORDER_CREATION:
            	notificationController.addClientToClientsInOrderCreation(client);
            	break;
            case OUT_ORDER_CREATION:
            	notificationController.removeClientsInOrderCreation(client);
            	break;
            case INSERT_ORDER:
            	orderController.handleInsertOrder(client, message);
                break;
            case UPDATE_ORDER_STATUS:
            	orderController.handleUpdateOrderStatus(client, message);
                break;
            case LOGIN:
            	userController.handleLogin(client, message);
                break;
            case LOG_OUT:
            	userController.logout(client,(Object[]) message);
            	break;
            case CHECK_USER:
            	userController.handleCheckUser(client, message);
            	break;
            case CREATE_ACCOUNT:
            	userController.createAccount(client, (Object[]) message);
            	break;
            case VIEW_MENU:
            	restaurantController.viewMenu(client, message, operation);
                break;
            case MENU_FOR_UPDATE:
            	restaurantController.viewMenu(client, message, operation);
            	break;
            case PENDING_ORDER:
            	restaurantController.handlePendingOrders(client, message);
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
            	reportController.getQuarterlyReport((QuarterlyReport)message[1], client);
            	break;
            case GET_DISCOUNT_AMOUNT:
            	userController.handleGetDiscount(client, message);
            	break;
            case SET_DISCOUNT_AMOUNT:
            	userController.handleSetDiscount(client, message);
            	break;
            case DISHES_IN_ORDER:
            	orderController.handleDishesInOrder(client, message);
            	break;
            case CHANGE_HOME_BRANCH:
            	userController.handleChangeHomeBranch(client, message);
            	break;
            case USERS_ORDERS:
            	userController.handleUserOrders(client, message);
            	break;
            case ORDER_ON_TIME:
            	orderController.handleOrderInTime(client, message);
            	break;
            case IN_PENDING_ORDERS:
            	notificationController.addToWorkersInPendingOrders((User)message[1], client);
            	break;
            case OUT_PENDING_ORDERS:
            	notificationController.removeFromWorkersInPendingOrders((User)message[1]);
            	break;
			default:
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
		dbController.resetAllUserLoggedStatus();
		dbController.closeConnection();
		reportController.shutdown();
	}
	
	//stop
	public void stopServer() {
		try {
			sendToAllClients(new Object[]{EnumClientOperations.SERVER_DISCONNECTED});
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
