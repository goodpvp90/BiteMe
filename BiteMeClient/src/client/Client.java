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
import common.EnumServerOperations;
import common.IncomeReport;
import common.MonthlyReport;
import common.Order;
import common.OrdersReport;
import common.PerformanceReport;
import common.User;
import javafx.application.Platform;
import ClientGUI.ClientLoginController;
import ClientGUI.ZProtoClientController;
import ClientGUI.CustomerOrderCreation;
import ClientGUI.ReportsPageController;
import common.Dish;

public class Client extends AbstractClient {
	// Default port to connect to the server
	final public static int DEFAULT_PORT = 8080;
	private static Client instance;
	// Controller for Client GUI functionality
	private ZProtoClientController clientController;
	private ClientLoginController clientLoginController;
	private CustomerOrderCreation  CustomerOrderCreation;
	private Consumer<IncomeReport> pendingRevenueReportCallback;
	private ReportsPageController reportsPageController;
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
	
	// sets the CustomerOrderCreation instance
	public void setCustomerOrderCreation(CustomerOrderCreation CustomerOrderCreation) {
		this.CustomerOrderCreation = CustomerOrderCreation;
	}

	// Sets the GUI controller for this client
	public void setGuiController(ZProtoClientController clientController) {
		this.clientController = clientController;
	}
	
	//Sets the ReportsPageController.
    public void setReportsPageController(ReportsPageController controller) {
        this.reportsPageController = controller;
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
	            if (clientController != null) {
	                clientController.updateWelcomeText("Message from server: " + message[1]);
	            }
	            break;
            case NOTIFICATION:
                List<String> notifications = (List<String>) message[1];
                for (String notification : notifications) {
                    //DISPLAY NOTIFICATIONS FROM HERE
                }
                break;
            case CREATED_ACCOUNT:
            	Object dataUser = (Object)message[1]; //You receive here user object if created
                if (clientController != null) {
    	        	//clientLoginController.updateUser(data);
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
            case ADD_DISH:
                boolean addDishResult = (boolean) message[1];
                if (clientController != null) {
                    //clientController.displayAddDishResult(addDishResult);
                }
                break;
            case DELETE_DISH:
                boolean deleteDishResult = (boolean) message[1];
                if (clientController != null) {
                    //clientController.displayDeleteDishResult(deleteDishResult);
                }
                break;
            case REPORT_ERROR:
            	String errorMsg = (String)message[1];
            	//TODO somthing more            	
                if (reportsPageController != null) {
                    reportsPageController.handleIncomeReportResponse(message[1]);
                }
            	break;
            case INCOME_REPORT:
                IncomeReport receivedReport = (IncomeReport) message[1];
                if (reportsPageController != null) {
                    reportsPageController.handleIncomeReportResponse(message[1]);
                }
                break;
            case ORDERS_REPORT:
            	OrdersReport ordersReport = (OrdersReport)message[1];
            	//TODO do smth
            	break;
            case PERFORMANCE_REPORT:
            	PerformanceReport performanceReport = (PerformanceReport)message[1];
            	//TODO do smth
            	break;
            case UPDATE_DISH:
            	//UPDATE EXISSTING DISH RESPONSE.
            	//TODO do smth
            	break;
			case NONE:
				System.out.println("no operation was recived");
				break;
			}
		}
	}
	
	

	
	private void handleLogin(Object[] message) {
		//SEND TO CLIENT CONTROLLER
        Object messagePart = message[1];
        Object[] data;
        if (messagePart instanceof String) {
            // Convert to Object[] containing the single string
            data = new Object[]{messagePart};
        } else {
            // Return as is if it's already an Object[]
            data = (Object[]) messagePart;
        }
		System.out.println("4");
    	clientLoginController.updateUser(data);
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
		
	public void sendCreateAccout(User user) {
	    // Send a request to create accout
		sendMessageToServer(new Object[] { EnumServerOperations.CREATE_ACCOUNT, user });
	}
	
	public void sendAddDishRequest(Dish dish) {
	    // Send a request to add a new dish
		sendMessageToServer(new Object[] { EnumServerOperations.ADD_DISH, dish });
	}

	
	public void sendDeleteDishRequest(Dish dish) {
	    // Send a request to delete a new dish
		sendMessageToServer(new Object[] { EnumServerOperations.DELETE_DISH, dish });
	}
	
	public void sendCreateOrderRequest(Order order, List<DishInOrder> dishesInOrder) {
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

	// OFEK
	public void getViewMenu(int menuId) {
		sendMessageToServer(new Object[] { EnumServerOperations.VIEW_MENU, menuId });
	}
}
