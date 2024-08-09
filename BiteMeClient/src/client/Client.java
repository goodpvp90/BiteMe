package client;

import ocsf.client.*;


import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import common.Dish;
import common.DishInOrder;
import common.EnumClientOperations;
import common.EnumDish;
import common.EnumOrderStatus;
import common.EnumServerOperations;
import common.IncomeReport;
import common.MonthlyReport;
import common.Order;
import common.OrdersReport;
import common.PerformanceReport;
import common.QuarterlyReport;
import common.User;
import javafx.application.Platform;
import ClientGUI.ClientLoginController;
import ClientGUI.CustomerCheckout;
import ClientGUI.CustomerInformationUpdateController;
import ClientGUI.CustomerOrderCreation;
import ClientGUI.RegisterUserPageController;
import ClientGUI.ReportsPageController;
import ClientGUI.UpdateDeleteMenu;
import ClientGUI.WorkerPendingOrders;
import common.Dish;

public class Client extends AbstractClient {
	// Default port to connect to the server
	final public static int DEFAULT_PORT = 8080;
	private static Client instance;
	// Controller for Client GUI functionality
	private ClientLoginController clientLoginController;
	private CustomerOrderCreation  CustomerOrderCreation;
	private Consumer<IncomeReport> pendingRevenueReportCallback;
	private ReportsPageController reportsPageController;
	private RegisterUserPageController registerUserPageController;
	private CustomerInformationUpdateController customerInformationUpdateController;
	private CustomerCheckout customerCheckout;
	private UpdateDeleteMenu updateDeleteMenu;
	private WorkerPendingOrders workerPendingOrders;
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
		//Added by Eldar
	    if (!instance.isConnected()) {
	        try {
	            instance.openConnection();
	        } catch (IOException e) {
	            e.printStackTrace();
	            // Handle reconnection failure
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
	            break;
			case PENDING_ORDER:
				List<Order> pendingOrders = (List<Order>)message[1];
				workerPendingOrders.SetPendingOrdersFromDB(pendingOrders);
				for (Order order :pendingOrders) {
					System.out.println(order);
				}
				break;
			case DISHES_IN_ORDER:
				//****************
	        	List<DishInOrder> dishes = (List<DishInOrder>)message[1];
				for (DishInOrder dishin :dishes) {
					System.out.println(
							"name: " + dishin.getDishName()+", Option: "+dishin.getOptionalPick()
							+ ", comment: "+dishin.getComment());
				}
				workerPendingOrders.SetDishInOrdersFromDB(dishes);
	        	break;
			case USER:
				//String bulbul = (String)message[1];
				//System.out.println(bulbul);
				handleLogin(message);
	        	break;
			case LOG_OUT:
				// UPDATE HERE WHAT NEEDED, its here for example
				// you receive object user if loggedout succesfully or string if not logged.
	        	Object loggedoutuser = (Object)message[1];
	        	//clientLoginController.updateUser(loggedoutuser);
	        	break;
			case INSERT_ORDER:
				//HERE YOU RECEIVE BACK Order, and list of dishes in order.
				Object order = (Object)message[1];
				Object dishesinorder = (Object)message[2];
				break;
			case UPDATE_WELOCME:
				// Handle non-array messages for updating the top label in clientController
	            break;
            case NOTIFICATION://לעשות פופ אפ שנועל את החלון עד שנבחר בכפתור שלו
                List<String> notifications = (List<String>) message[1];
                for (String notification : notifications) {
                	System.out.println("HI"+notification);
                    //DISPLAY NOTIFICATIONS FROM HERE
                }
                break;
            case CHECK_USER:
            	//If its Boolean = Not in DB
            	//If Name(And other) is Null = Need to Update
            	//If Nothing is Null = User Registered.
                Object res = message[1];
                
                if (registerUserPageController != null) {
                    registerUserPageController.handleServerResponse(res);
                }
                break;
            case CREATED_ACCOUNT:
            	Object dataUser = (Object)message[1]; //You receive here user object if created
                    if (customerInformationUpdateController != null) {
                        customerInformationUpdateController.handleServerResponse(dataUser);
                    }
                break;
            case VIEW_MENU:
            	System.out.println("ENTERED VIEW");
            	List<Dish> menu = (List<Dish>) message[1];
            	for (Dish dish:menu) {
            		System.out.println(dish);
            	}
            	System.out.println("HIGHER" + menu.size());
           	 	CustomerOrderCreation.SettempMenuFromDB(menu);    
           	 	for (Dish dish :  CustomerOrderCreation.tempMenuFromDB) {
        		    String dishInfo = "Dish{" +
        		                      "dishId=" + dish.getDishId()  +
        		                      '}';
        		    System.out.println(dishInfo);
        		}
                 break;
            case MENU_FOR_UPDATE:
            	@SuppressWarnings("unchecked")
            	List<Dish> menuupdate = (List<Dish>) message[1];
            	updateDeleteMenu.setMenuDishes(menuupdate);
            	break;
            case ADD_DISH:
                boolean addDishResult = (boolean) message[1];
                break;
            case DELETE_DISH:
                boolean deleteDishResult = (boolean) message[1];
                break;
            case REPORT_ERROR:
            	String errorMsg = (String)message[1];
            	//TODO somthing more            	
                if (reportsPageController != null) {
                    reportsPageController.handleIncomeReportResponse(errorMsg);
                }
            	break;
            case INCOME_REPORT:
                IncomeReport receivedReport = (IncomeReport) message[1];
                if (reportsPageController != null) {
                    reportsPageController.handleIncomeReportResponse(receivedReport);
                }
                break;
            case ORDERS_REPORT:
            	OrdersReport ordersReport = (OrdersReport)message[1];
            	if (reportsPageController != null) {
                    reportsPageController.handleOrdersReportResponse(ordersReport);
                }
                break;
            case PERFORMANCE_REPORT:
            	PerformanceReport performanceReport = (PerformanceReport)message[1];
                if (reportsPageController != null) {
                    reportsPageController.handlePerformanceReportResponse(performanceReport);
                }
            case QUARTERLY_REPORT:
            	QuarterlyReport qreport = (QuarterlyReport)message[1];
            	//TODO do smth

            	break;
            case QUARTERLY_REPORT_ERROR:
            	//u receive a (Object)string
            	//TODO do smth
            	break;
            case UPDATE_DISH:
            	//UPDATE EXISSTING DISH RESPONSE.
            	//TODO do smth
            	break;
            case UPDATE_ORDER_STATUS:
            	//HERE U GET RESPONSE IF UPDATE STATUS IS SUCCESFULL
            	break;
            case SET_DISCOUNT_AMOUNT: //NEED TO DECIDE IF TO UPDATE RESPONSE, NOW NOT USED.
            	break;
            case GET_DISCOUNT_AMOUNT: 
            	customerCheckout.setCompensation((double)message[1]);	
            	break;           
            case CHANGE_HOME_BRANCH:
            	//u receive a boolean if succcseded or not
            	//TODO do smth
            	break;
			case NONE:
				System.out.println("no operation was received");
				break;
			}
		}
	}
	
	private void handleLogin(Object[] message) {
		//SEND TO CLIENT CONTROLLER
        Object messagePart = message[1];
    	clientLoginController.updateUser(messagePart);
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
		}
	}
	
	public void sendShowDishesInOrder(int orderid) {
	    //GET PENDING ORDERS
		sendMessageToServer(new Object[] { EnumServerOperations.DISHES_IN_ORDER, orderid });
	}
	
	
	public void sendShowPending(int branchId) {
	    //GET PENDING ORDERS
		sendMessageToServer(new Object[] { EnumServerOperations.PENDING_ORDER, branchId });
	}
	
	public void sendCreateAccout(User user) {
	    // Send a request to create accout
		sendMessageToServer(new Object[] { EnumServerOperations.CREATE_ACCOUNT, user });
	}
	
	public void sendSearchUser(String username) {
	    // Send a request to create accout
		sendMessageToServer(new Object[] { EnumServerOperations.CHECK_USER, username });
	}
	public void sendAddDishRequest(Dish dish) {
	    // Send a request to add a new dish
		sendMessageToServer(new Object[] { EnumServerOperations.ADD_DISH, dish });
	}

	public void sendDeleteDishRequest(Dish dish) {
	    // Send a request to delete a new dish
		sendMessageToServer(new Object[] { EnumServerOperations.DELETE_DISH, dish });
	}
	
	public void sendCreateOrderRequest(Order order, List<Dish> dishesInOrder) {
	    sendMessageToServer(new Object[] {
		        EnumServerOperations.INSERT_ORDER, order, dishesInOrder});
	}

	public void loginValidation(User user) {
		sendMessageToServer(new Object[] { EnumServerOperations.LOGIN, user });
	}
	
	public void userLogout(User user) {
		sendMessageToServer(new Object[] { EnumServerOperations.LOG_OUT, user });
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
	
	
	//THIS FUNCTION TO GET DISCOUNT AMOUNT FOR SPECIFIC USER.
	public void getDiscountAmount(String username) {
		sendMessageToServer(new Object[] { EnumServerOperations.GET_DISCOUNT_AMOUNT, username });
	}
	
	
	//USE THIS TO SEND TO US NEW AMOUNT, THE MATHEMATICAL LOGIC YOU DO.
	public void setDiscountAmount(String username, double amount) {
		sendMessageToServer(new Object[] { EnumServerOperations.SET_DISCOUNT_AMOUNT, username, amount });
	}
	
	//USE IT TO UPDATE ORDER STATUS, IN PROGESS, READY , COMPLETED .....
	// IT HAS A LOT OF LOGIC IN BACKEND, by status we update time and etc..
	public void updateOrderStatus(int orderId, EnumOrderStatus status) {
		sendMessageToServer(new Object[] { EnumServerOperations.UPDATE_ORDER_STATUS, orderId, status});
	}

	// OFEK
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
}
