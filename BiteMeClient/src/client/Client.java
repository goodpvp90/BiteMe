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

public class Client extends AbstractClient {
	// Default port to connect to the server
	final public static int DEFAULT_PORT = 8080;
	private static Client instance;
	private EnumPageForDishInOrder pageForDishInOrder;
	private ClientLoginController clientLoginController;
	private CustomerOrderCreation  CustomerOrderCreation;
	private ReportsPageController reportsPageController;
	private RegisterUserPageController registerUserPageController;
	private CustomerInformationUpdateController customerInformationUpdateController;
	private CustomerCheckout customerCheckout;
	private UpdateDeleteMenu updateDeleteMenu;
	private WorkerPendingOrders workerPendingOrders;
	private UpdateAddDish updateAddDish;
	private HomeBranchChange homeBranchChange;
	private UserHomePageController userHomePageController;
	//Locks for synchronization on some of the pop ups
	private final Lock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();
	private MyOrders myOrders;
	private User user;
	
	// Constructor to initialize the client with host and port, and establish
	// connection
	private Client(String host, int port) throws IOException {
		super(host, port);
		openConnection();

		// Get client's IP address and host name
		String clientIP = InetAddress.getLocalHost().getHostAddress();
		String clientHostName = InetAddress.getLocalHost().getHostName();

		// Send initial connection message to the server
		sendToServer(new Object[] { EnumServerOperations.CLIENT_CONDITION,
				new String[] { clientIP, clientHostName, "start" } });

	}
	
	// Public method to get the Server instance
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
	
	// Public method to initialize the Server and get the instance
	public static void initialize(String host, int port) throws IOException {
		if (instance == null) {
			instance = new Client(host, port);
		}		
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	// sets the WorkerPendingOrders instance
	public void setWorkerPendingOrders(WorkerPendingOrders WorkerPendingOrders) {
		this.workerPendingOrders = WorkerPendingOrders;
	}
	// sets the CustomerOrderCreation instance
	public void setCustomerOrderCreation(CustomerOrderCreation CustomerOrderCreation) {
		this.CustomerOrderCreation = CustomerOrderCreation;
	}
	
	//Sets the registered user if needed
	public void setRegisterUserPageController(RegisterUserPageController controller) {
	    this.registerUserPageController = controller;
	}
	//Sets the InformationUpdate
	public void setCustomerInformationUpdateController(CustomerInformationUpdateController controller) {
	    this.customerInformationUpdateController = controller;
	}
	//Sets the ReportsPageController.
    public void setReportsPageController(ReportsPageController controller) {
        this.reportsPageController = controller;
    }
    
	public void getInstanceOfClientLoginController(ClientLoginController client) {
		this.clientLoginController = client;
	}
	
	public void getInstanceOfCustomerCheckout(CustomerCheckout customerCheckout) {
		this.customerCheckout = customerCheckout;
	}
	
	public void getInstanceOfUpdateDeleteMenu(UpdateDeleteMenu updateDeleteMenu) {
		this.updateDeleteMenu = updateDeleteMenu;
	}
	
	public void getInstanceOfUpdateAddDish(UpdateAddDish updateAddDish) {
		this.updateAddDish = updateAddDish;
	}
	
	public void getInstanceOfHomeBranchChange(HomeBranchChange homeBranchChange) {
		this.homeBranchChange=homeBranchChange;
	}
	
	public void getUserHomePageController(UserHomePageController userHomePageController) {
		 lock.lock();
	        try {
	            this.userHomePageController = userHomePageController;
	            // Signal the condition that the controller is ready
	            condition.signalAll();
	        } finally {
	            lock.unlock();
	        }
	}
	
	public void getInstanceOfMyOrders(MyOrders myOrders) {
		this.myOrders=myOrders;
	}
	
	// Handle messages received from the server
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
					case EnumPageForDishInOrder.WORKER://for pending orders page
						workerPendingOrders.SetDishInOrdersFromDB(dishes);
						break;
					case EnumPageForDishInOrder.CUSTOMER://for my orders page
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
	
	//Locking method for sync use	
	public void waitForController() {
        lock.lock();
        try {
            while (userHomePageController == null) {
                // Wait until the controller is set
                condition.await();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
        } finally {
            lock.unlock();
        }
    }
	
	private void handleLogin(Object[] message) {
        Object messagePart = message[1];
    	clientLoginController.updateUser(messagePart);
	}

	// Quit the client and close the connection
	public void quit() {
		try {
			// Get client's IP address and host name
			String clientIP = InetAddress.getLocalHost().getHostAddress();
			String clientHostName = InetAddress.getLocalHost().getHostName();
			sendToServer(new Object[] { EnumServerOperations.CLIENT_CONDITION,
			new String[] { clientIP, clientHostName, "end" } });
			closeConnection();
		} catch (IOException e) {}
		System.exit(0);
	}

	// Send a message to the server
	public void sendMessageToServer(Object msg) {
		try {
			sendToServer(msg);
		} catch (Exception e) {}		
	}
	
	public void sendShowDishesInOrder(int orderid, EnumPageForDishInOrder page) {
		pageForDishInOrder = page;
		sendMessageToServer(new Object[] { EnumServerOperations.DISHES_IN_ORDER, orderid });
	}
	
	public void sendOrderArriveOnTime(int orderId) {
		sendMessageToServer(new Object[] { EnumServerOperations.ORDER_ON_TIME, orderId });
	}
	
	public void sendShowPending(int branchId) {
		sendMessageToServer(new Object[] { EnumServerOperations.PENDING_ORDER, branchId });
	}
	
	public void sendCreateAccout(User user) {
		sendMessageToServer(new Object[] { EnumServerOperations.CREATE_ACCOUNT, user });
	}
	
	public void sendSearchUser(String username) {
		sendMessageToServer(new Object[] { EnumServerOperations.CHECK_USER, username });
	}
	public void sendAddDishRequest(Dish dish) {
		sendMessageToServer(new Object[] { EnumServerOperations.ADD_DISH, dish });
	}

	public void sendDeleteDishRequest(Dish dish) {
		sendMessageToServer(new Object[] { EnumServerOperations.DELETE_DISH, dish });
	}
	
	public void sendCreateOrderRequest(Order order, List<Dish> dishesInOrder) {
	    sendMessageToServer(new Object[] { EnumServerOperations.INSERT_ORDER, order, dishesInOrder});		       
	}

	public void loginValidation(User user) {
		sendMessageToServer(new Object[] { EnumServerOperations.LOGIN, user });
	}
	
	public void userLogout(User user, boolean kill) {
		sendMessageToServer(new Object[] { EnumServerOperations.LOG_OUT, user });
		if (kill)
			quit();
	}
	
	public void getIncomeReport(IncomeReport report) {
		sendMessageToServer(new Object[] { EnumServerOperations.INCOME_REPORT, report });
	}
	
	public void getPerformanceReport(PerformanceReport report) {
		sendMessageToServer(new Object[] { EnumServerOperations.PERFORMANCE_REPORT, report });
	}
	
	public void getOrdersReport(OrdersReport report) {
		sendMessageToServer(new Object[] { EnumServerOperations.ORDERS_REPORT, report });
	}
	
	public void getQuarterlyReport(QuarterlyReport qreport) {
		sendMessageToServer(new Object[] { EnumServerOperations.QUARTERLY_REPORT, qreport });
	}
	
	public void getDiscountAmount(String username) {
		sendMessageToServer(new Object[] { EnumServerOperations.GET_DISCOUNT_AMOUNT, username });
	}
	
	public void setDiscountAmount(String username, double amount) {
		sendMessageToServer(new Object[] { EnumServerOperations.SET_DISCOUNT_AMOUNT, username, amount });
	}
	
	public void updateOrderStatus(int orderId, EnumOrderStatus status, String msg, boolean isDelivery) {
		sendMessageToServer(new Object[] { EnumServerOperations.UPDATE_ORDER_STATUS, orderId, status, msg, isDelivery});
	}
	
	public void getViewMenu(EnumServerOperations op, int menuId) {
		sendMessageToServer(new Object[] {op, menuId });
	}
		
	public void deleteDish(Dish dish) {
		sendMessageToServer(new Object[] { EnumServerOperations.DELETE_DISH, dish });
	}
	
	public void addDish(Dish dish) {
		sendMessageToServer(new Object[] { EnumServerOperations.ADD_DISH, dish });
	}
	
	public void changeHomeBranch(User user) {
		sendMessageToServer(new Object[] { EnumServerOperations.CHANGE_HOME_BRANCH, user });
	}
	
	public void updateDish(Dish dish) {
		sendMessageToServer(new Object[] { EnumServerOperations.UPDATE_DISH, dish });
	}
	
	public void getUsersOrders(String Username) {
		sendMessageToServer(new Object[] { EnumServerOperations.USERS_ORDERS, Username});
	}
	
	public void addClientInOrder() {
		sendMessageToServer(new Object[] { EnumServerOperations.IN_ORDER_CREATION});
	}
	
	public void removeClientInOrder() {
		sendMessageToServer(new Object[] { EnumServerOperations.OUT_ORDER_CREATION});
	}
	
	public void addWorkerInPendingOrders(User user) {
		sendMessageToServer(new Object[] { EnumServerOperations.IN_PENDING_ORDERS, user});
	}
	
	public void removeWorkerInPendingOrders(User user) {
		sendMessageToServer(new Object[] { EnumServerOperations.OUT_PENDING_ORDERS, user});
	}
}