package client;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import ClientGUI.ClientLoginController;
import ClientGUI.CustomerCheckout;
import ClientGUI.CustomerInformationUpdateController;
import ClientGUI.CustomerOrderCreation;
import ClientGUI.HomeBranchChange;
import ClientGUI.MyOrders;
import ClientGUI.RegisterUserPageController;
import ClientGUI.ReportsPageController;
import ClientGUI.UpdateAddDish;
import ClientGUI.UpdateDeleteMenu;
import ClientGUI.UserHomePageController;
import ClientGUI.WorkerPendingOrders;
import enums.EnumClientOperations;
import enums.EnumOrderStatus;
import enums.EnumPageForDishInOrder;
import enums.EnumServerOperations;
import ocsf.client.AbstractClient;
import reports.IncomeReport;
import reports.OrdersReport;
import reports.PerformanceReport;
import reports.QuarterlyReport;
import restaurantEntities.Dish;
import restaurantEntities.DishInOrder;
import restaurantEntities.Order;
import userEntities.User;
/**
 * Client class represents the client-side of the application, responsible
 * for managing the connection with the server and handling various client 
 * operations and interactions.
 */
public class Client extends AbstractClient {
	/**
     * The default port number to connect to the server.
     */
	final public static int DEFAULT_PORT = 8080;
	/**
     * The singleton instance of the Client.
     */
	private static Client instance;
	/**
     * The page context for displaying dishes in orders.
     */
	private EnumPageForDishInOrder pageForDishInOrder;
	/**
     * The controller for the client login.
     */
	private ClientLoginController clientLoginController;
	/**
     * The controller for customer order creation.
     */
	private CustomerOrderCreation  CustomerOrderCreation;
	/**
     * The controller for the reports page.
     */
	private ReportsPageController reportsPageController;
	/**
     * The controller for user registration page.
     */
	private RegisterUserPageController registerUserPageController;
	/**
     * The controller for updating customer information.
     */
	private CustomerInformationUpdateController customerInformationUpdateController;
	/**
     * The controller for customer checkout.
     */
	private CustomerCheckout customerCheckout;
	/**
     * The controller for updating or deleting menu items.
     */
	private UpdateDeleteMenu updateDeleteMenu;
	/**
     * The controller for managing worker's pending orders.
     */
	private WorkerPendingOrders workerPendingOrders;
	/**
     * The controller for updating or adding new dishes.
     */
	private UpdateAddDish updateAddDish;
	/**
     * The controller for changing the home branch.
     */
	private HomeBranchChange homeBranchChange;
	/**
     * The controller for the user home page.
     */
	private UserHomePageController userHomePageController;
	/**
     * The lock for synchronization on pop-up dialogs.
     */
	private final Lock lock = new ReentrantLock();
	/**
     * The condition for synchronizing controller setup.
     */
	private final Condition condition = lock.newCondition();
	/**
     * The controller for managing the user's orders.
     */
	private MyOrders myOrders;
	/**
     * The user associated with the client.
     */
	private User user;
	/**
     * Initializes the client with host and port, and establishes connection.
     * 
     * @param host the server host
     * @param port the server port
     * @throws IOException if an I/O error occurs
     */
	private Client(String host, int port) throws IOException {
		super(host, port);
		openConnection();
		String clientIP = InetAddress.getLocalHost().getHostAddress();
		String clientHostName = InetAddress.getLocalHost().getHostName();
		sendToServer(new Object[] { EnumServerOperations.CLIENT_CONDITION,
				new String[] { clientIP, clientHostName, "start" } });
	}

	/**
     * Returns the instance of the Client.
     * 
     * @return the Client instance
     * @throws IllegalStateException if the client is not initialized
     */
	public static Client getInstance() {
		if (instance == null) {
			throw new IllegalStateException("Client not initialized. Call initialize() first.");
		}
	    if (!instance.isConnected()) {
	        try {
	            instance.openConnection();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return instance;
	}	
	/**
     * Initializes the Client with the given host and port.
     * 
     * @param host the server host
     * @param port the server port
     * @throws IOException if an I/O error occurs
     */
	public static void initialize(String host, int port) throws IOException {
		if (instance == null) {
			instance = new Client(host, port);
		}		
	}
	/**
     * Sets the user associated with this client.
     * 
     * @param user the user to set
     */
	public void setUser(User user) {
		this.user = user;
	}
	/**
     * Sets the WorkerPendingOrders instance.
     * 
     * @param workerPendingOrders the WorkerPendingOrders instance to set
     */
	public void setWorkerPendingOrders(WorkerPendingOrders WorkerPendingOrders) {
		this.workerPendingOrders = WorkerPendingOrders;
	}
	/**
     * Sets the CustomerOrderCreation instance.
     * 
     * @param customerOrderCreation the CustomerOrderCreation instance to set
     */
	public void setCustomerOrderCreation(CustomerOrderCreation CustomerOrderCreation) {
		this.CustomerOrderCreation = CustomerOrderCreation;
	}
	 /**
     * Sets the RegisterUserPageController instance.
     * 
     * @param controller the RegisterUserPageController instance to set
     */
	public void setRegisterUserPageController(RegisterUserPageController controller) {
	    this.registerUserPageController = controller;
	}
	/**
     * Sets the CustomerInformationUpdateController instance.
     * 
     * @param controller the CustomerInformationUpdateController instance to set
     */
	public void setCustomerInformationUpdateController(CustomerInformationUpdateController controller) {
	    this.customerInformationUpdateController = controller;
	}
	/**
     * Sets the ReportsPageController instance.
     * 
     * @param controller the ReportsPageController instance to set
     */
    public void setReportsPageController(ReportsPageController controller) {
        this.reportsPageController = controller;
    }
    /**
     * Sets the ClientLoginController instance.
     * 
     * @param client the ClientLoginController instance to set
     */
	public void getInstanceOfClientLoginController(ClientLoginController client) {
		this.clientLoginController = client;
	}
	/**
     * Sets the CustomerCheckout instance.
     * 
     * @param customerCheckout the CustomerCheckout instance to set
     */
	public void getInstanceOfCustomerCheckout(CustomerCheckout customerCheckout) {
		this.customerCheckout = customerCheckout;
	}
	/**
     * Sets the UpdateDeleteMenu instance.
     * 
     * @param updateDeleteMenu the UpdateDeleteMenu instance to set
     */
	public void getInstanceOfUpdateDeleteMenu(UpdateDeleteMenu updateDeleteMenu) {
		this.updateDeleteMenu = updateDeleteMenu;
	}
	/**
     * Sets the UpdateAddDish instance.
     * 
     * @param updateAddDish the UpdateAddDish instance to set
     */
	public void getInstanceOfUpdateAddDish(UpdateAddDish updateAddDish) {
		this.updateAddDish = updateAddDish;
	}
	/**
     * Sets the HomeBranchChange instance.
     * 
     * @param homeBranchChange the HomeBranchChange instance to set
     */
	public void getInstanceOfHomeBranchChange(HomeBranchChange homeBranchChange) {
		this.homeBranchChange=homeBranchChange;
	}
	/**
     * Sets the MyOrders instance.
     * 
     * @param myOrders the MyOrders instance to set
     */
	public void getInstanceOfMyOrders(MyOrders myOrders) {
		this.myOrders=myOrders;
	}
	/**
     * Sets the UserHomePageController instance.
     * 
     * @param userHomePageController the UserHomePageController instance to set
     */
	public void getUserHomePageController(UserHomePageController userHomePageController) {
		 lock.lock();
	        try {
	            this.userHomePageController = userHomePageController;
	            condition.signalAll();
	        } finally {
	            lock.unlock();
	        }
	}
	/**
     * Handles messages received from the server.
     * Each case relates to a different outcome on the client, usually for different UI screens
     * @param msg the message from the server
     */
	@Override
	protected void handleMessageFromServer(Object msg) {
		EnumClientOperations operation;
		if (msg instanceof Object[]) {
			Object[] message = (Object[]) msg;
			operation = (EnumClientOperations) message[0];
			switch (operation) {	
			case USERS_ORDERS:
				List<Order> UserOrders = (List<Order>)message[1];			
				myOrders.setOrders(UserOrders);			
	            break;
			case PENDING_ORDER:
				//TODO nadir
				@SuppressWarnings("unchecked")
				List<Order> pendingOrders = (List<Order>)message[1];
				workerPendingOrders.SetPendingOrdersFromDB(pendingOrders);
				break;
			case DISHES_IN_ORDER:
	        	List<DishInOrder> dishes = (List<DishInOrder>)message[1];		
				switch(pageForDishInOrder) {
					case EnumPageForDishInOrder.WORKER:
						workerPendingOrders.SetDishInOrdersFromDB(dishes);
						break;
					case EnumPageForDishInOrder.CUSTOMER:
						myOrders.SetDishInOrdersFromDB(dishes);
						break;
				}
	        	break;
			case USER:
				handleLogin(message);
	        	break;
            case NOTIFICATION:          	
            	List<String> notifications = (List<String>) message[1];              
                waitForController();//Synchronization
                userHomePageController.showNotificationDialog(notifications);               	                
                break;
            case CHECK_USER:
                Object res = message[1];                
                if (registerUserPageController != null) {
                    registerUserPageController.handleServerResponse(res);
                }
                break;
            case CREATED_ACCOUNT:
            	Object dataUser = (Object)message[1];
                    if (customerInformationUpdateController != null) {
                        customerInformationUpdateController.handleServerResponse(dataUser);
                    }
                break;
            case VIEW_MENU:
            	List<Dish> menu = (List<Dish>) message[1];
           	 	CustomerOrderCreation.SettempMenuFromDB(menu);    
                 break;
            case MENU_FOR_UPDATE:
            	//TODO nadir
            	@SuppressWarnings("unchecked")
            	List<Dish> menuupdate = (List<Dish>) message[1];
            	updateDeleteMenu.setMenuDishes(menuupdate);            	
            	break;
            case ADD_DISH:
            	updateAddDish.setSucceededAdd((boolean) message[1]);
                break;
            case DELETE_DISH:
            	updateDeleteMenu.SetSuccessDelete((boolean) message[1]);
                break;
            case REPORT_ERROR:
            	String errorMsg = (String)message[1];        	
                reportsPageController.handleIncomeReportResponse(errorMsg);               
            	break;
            case INCOME_REPORT:
                IncomeReport receivedReport = (IncomeReport) message[1];
                reportsPageController.handleIncomeReportResponse(receivedReport);
                break;
            case ORDERS_REPORT:
            	OrdersReport ordersReport = (OrdersReport)message[1];
                reportsPageController.handleOrdersReportResponse(ordersReport);               
                break;
            case PERFORMANCE_REPORT:
            	PerformanceReport performanceReport = (PerformanceReport)message[1];
                reportsPageController.handlePerformanceReportResponse(performanceReport);
                break;
            case QUARTERLY_REPORT:
                Object[] data = (Object[]) message[1];
                QuarterlyReport qreport = (QuarterlyReport) data[0];
                //TODO nadir
                @SuppressWarnings("unchecked")
                List<Double> monthlyIncomes = (List<Double>) data[1];
                reportsPageController.handleQuarterlyReportResponse(qreport, monthlyIncomes);
                break;
            case UPDATE_DISH:
            	updateDeleteMenu.SetSuccessEdit((boolean) message[1]);
            	break;
            case GET_DISCOUNT_AMOUNT: 
            	customerCheckout.setCompensation((double)message[1]);	
            	break;           
            case CHANGE_HOME_BRANCH:
            	homeBranchChange.checkSuccessChangeHomeBranch((boolean)message[1]);
            	break;
            case INTERRUPT_ORDER_CREATION:
            	userHomePageController.showCreateOrderDuringUpdateMenuDialog();
            	break;
            case INTERRUPT_PENDING_ORDERS:
            	userHomePageController.showPendingOrderDuringOrderCreationDialog();
            	break;
            case SERVER_DISCONNECTED:
            	if(user == null)
            		quit();
            	else
                	userLogout(user,true);
            	break;
            case ORDER_ON_TIME:           	
            	myOrders.OrderCompleteHandle((boolean)message[1]);
            	break;
			}
		}
	}
	/**
     * Waits for the UserHomePageController to be set.
     */	
	public void waitForController() {
        lock.lock();
        try {
            while (userHomePageController == null) {
                condition.await();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
	/**
     * Handles the login response from the server.
     * 
     * @param message the login response message
     */
	private void handleLogin(Object[] message) {
        Object messagePart = message[1];
    	clientLoginController.updateUser(messagePart);
	}
	/**
     * Quits the client and closes the connection.
     */
	public void quit() {
		try {
			String clientIP = InetAddress.getLocalHost().getHostAddress();
			String clientHostName = InetAddress.getLocalHost().getHostName();
			sendToServer(new Object[] { EnumServerOperations.CLIENT_CONDITION,
			new String[] { clientIP, clientHostName, "end" } });
			closeConnection();
		} catch (IOException e) {}
		System.exit(0);
	}
	/**
     * Sends a message to the server.
     * 
     * @param msg the message to send
     */
	public void sendMessageToServer(Object msg) {
		try {
			sendToServer(msg);
		} catch (Exception e) {}		
	}
	/**
     * Sends a request to show dishes in an order.
     * 
     * @param orderid the order ID
     * @param page the page to show dishes
     */
	public void sendShowDishesInOrder(int orderid, EnumPageForDishInOrder page) {
		pageForDishInOrder = page;
		sendMessageToServer(new Object[] { EnumServerOperations.DISHES_IN_ORDER, orderid });
	}
	/**
     * Sends a request to mark an order as arriving on time.
     * 
     * @param orderId the order ID
     */
	public void sendOrderArriveOnTime(int orderId) {
		sendMessageToServer(new Object[] { EnumServerOperations.ORDER_ON_TIME, orderId });
	}
	 /**
     * Sends a request to show pending orders.
     * 
     * @param branchId the branch ID
     */
	public void sendShowPending(int branchId) {
		sendMessageToServer(new Object[] { EnumServerOperations.PENDING_ORDER, branchId });
	}
	/**
     * Sends a request to create an account.
     * 
     * @param user the user to create
     */
	public void sendCreateAccout(User user) {
		sendMessageToServer(new Object[] { EnumServerOperations.CREATE_ACCOUNT, user });
	}
	/**
     * Sends a request to search for a user.
     * 
     * @param username the username to search
     */
	public void sendSearchUser(String username) {
		sendMessageToServer(new Object[] { EnumServerOperations.CHECK_USER, username });
	}
	/**
     * Sends a request to add a dish.
     * 
     * @param dish the dish to add
     */
	public void sendAddDishRequest(Dish dish) {
		sendMessageToServer(new Object[] { EnumServerOperations.ADD_DISH, dish });
	}
	/**
     * Sends a request to delete a dish.
     * 
     * @param dish the dish to delete
     */
	public void sendDeleteDishRequest(Dish dish) {
		sendMessageToServer(new Object[] { EnumServerOperations.DELETE_DISH, dish });
	}
	/**
	 * Sends a request to create a new order.
	 * 
	 * @param order the order to be created
	 * @param dishesInOrder the list of dishes associated with the order
	 */
	public void sendCreateOrderRequest(Order order, List<Dish> dishesInOrder) {
	    sendMessageToServer(new Object[] { EnumServerOperations.INSERT_ORDER, order, dishesInOrder});		       
	}
	/**
	 * Sends a request to validate user login.
	 * 
	 * @param user the user to be validated
	 */
	public void loginValidation(User user) {
		sendMessageToServer(new Object[] { EnumServerOperations.LOGIN, user });
	}
	/**
	 * Sends a request to log out a user.
	 * 
	 * @param user the user to be logged out
	 * @param kill flag indicating whether to terminate the application
	 */
	public void userLogout(User user, boolean kill) {
		sendMessageToServer(new Object[] { EnumServerOperations.LOG_OUT, user });
		if (kill)
			quit();
	}
	/**
	 * Sends a request to get an income report.
	 * 
	 * @param report the income report to be retrieved
	 */
	public void getIncomeReport(IncomeReport report) {
		sendMessageToServer(new Object[] { EnumServerOperations.INCOME_REPORT, report });
	}
	/**
	 * Sends a request to get a performance report.
	 * 
	 * @param report the performance report to be retrieved
	 */
	public void getPerformanceReport(PerformanceReport report) {
		sendMessageToServer(new Object[] { EnumServerOperations.PERFORMANCE_REPORT, report });
	}
	/**
	 * Sends a request to get an orders report.
	 * 
	 * @param report the orders report to be retrieved
	 */
	public void getOrdersReport(OrdersReport report) {
		sendMessageToServer(new Object[] { EnumServerOperations.ORDERS_REPORT, report });
	}
	/**
	 * Sends a request to get a quarterly report.
	 * 
	 * @param qreport the quarterly report to be retrieved
	 */
	public void getQuarterlyReport(QuarterlyReport qreport) {
		sendMessageToServer(new Object[] { EnumServerOperations.QUARTERLY_REPORT, qreport });
	}
	/**
	 * Sends a request to get the discount amount for a user.
	 * 
	 * @param username the username of the user
	 */
	public void getDiscountAmount(String username) {
		sendMessageToServer(new Object[] { EnumServerOperations.GET_DISCOUNT_AMOUNT, username });
	}
	/**
	 * Sends a request to set the discount amount for a user.
	 * 
	 * @param username the username of the user
	 * @param amount the discount amount to be set
	 */
	public void setDiscountAmount(String username, double amount) {
		sendMessageToServer(new Object[] { EnumServerOperations.SET_DISCOUNT_AMOUNT, username, amount });
	}
	/**
	 * Sends a request to update the status of an order.
	 * 
	 * @param orderId the ID of the order to be updated
	 * @param status the new status of the order
	 * @param msg a message related to the status update
	 * @param isDelivery flag indicating whether the order is for delivery
	 */
	public void updateOrderStatus(int orderId, EnumOrderStatus status, String msg, boolean isDelivery) {
		sendMessageToServer(new Object[] { EnumServerOperations.UPDATE_ORDER_STATUS, orderId, status, msg, isDelivery});
	}
	/**
	 * Sends a request to retrieve the menu.
	 * 
	 * @param op the operation to be performed
	 * @param menuId the ID of the menu to be retrieved
	 */
	public void getViewMenu(EnumServerOperations op, int menuId) {
		sendMessageToServer(new Object[] {op, menuId });
	}
	/**
	 * Sends a request to delete a dish.
	 * 
	 * @param dish the dish to be deleted
	 */
	public void deleteDish(Dish dish) {
		sendMessageToServer(new Object[] { EnumServerOperations.DELETE_DISH, dish });
	}
	/**
	 * Sends a request to add a dish.
	 * 
	 * @param dish the dish to be added
	 */
	public void addDish(Dish dish) {
		sendMessageToServer(new Object[] { EnumServerOperations.ADD_DISH, dish });
	}
	/**
	 * Sends a request to change the home branch of a user.
	 * 
	 * @param user the user whose home branch is to be changed
	 */
	public void changeHomeBranch(User user) {
		sendMessageToServer(new Object[] { EnumServerOperations.CHANGE_HOME_BRANCH, user });
	}
	/**
     * Sends a request to update a dish.
     * 
     * @param dish the dish to update
     */
	public void updateDish(Dish dish) {
		sendMessageToServer(new Object[] { EnumServerOperations.UPDATE_DISH, dish });
	}
	/**
	 * Sends a request to retrieve the orders of a user.
	 * 
	 * @param Username the username of the user
	 */
	public void getUsersOrders(String Username) {
		sendMessageToServer(new Object[] { EnumServerOperations.USERS_ORDERS, Username});
	}
	/**
	 * Sends a request to add a client to an order.
	 * indicator for a designated pop up message
	 * for a customer that creates order while a menu change happens
	 */
	public void addClientInOrder() {
		sendMessageToServer(new Object[] { EnumServerOperations.IN_ORDER_CREATION});
	}
	/**
	 * Sends a request to remove a client from an order.
	 * indicator for a designated pop up message
	 * for a customer that creates order while a menu change happens
	 */
	public void removeClientInOrder() {
		sendMessageToServer(new Object[] { EnumServerOperations.OUT_ORDER_CREATION});
	}
	/**
	 * Sends a request to add a worker to pending orders.
	 * indicator for a designated pop up message
	 *  for a worker that views pending orders while a new order for his branch is created
	 * @param user the worker to be added
	 */
	public void addWorkerInPendingOrders(User user) {
		sendMessageToServer(new Object[] { EnumServerOperations.IN_PENDING_ORDERS, user});
	}
	/**
	 * Sends a request to remove a worker from pending orders.
	 * indicator for a designated pop up message
	 * for a worker that views pending orders while a new order for his branch is created
	 * @param user the worker to be removed
	 */
	public void removeWorkerInPendingOrders(User user) {
		sendMessageToServer(new Object[] { EnumServerOperations.OUT_PENDING_ORDERS, user});
	}
}
