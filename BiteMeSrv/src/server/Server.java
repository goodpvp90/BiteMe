package server;

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import ServerGUI.serverController;
import common.Order;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;


public class Server extends AbstractServer {
    static DBController dbController;
    private serverController controller;
    
    public Server(int port, String url, String username, String pass) {
        super(port);
        dbController = new DBController();
        try {
            dbController.connect(url, username, pass);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Failed to connect to the database.");
            e.printStackTrace();
        }
    }
    
    private String createResponseForClient(String message) {
    	String[] words = message.split("\\s+");
    	if ("inserted".equals(words[0]))
    		return "The order sent successfully";
    	else if ("Duplicate".equals(words[0]))
    		return "The order already exists";
    	else if("updated".equals(words[0]))
    		return "The order updated successfully";
    	else if("nothing".equals(words[0]))
    		return "Nothing has changed";
    	else
    		return "Something went wrong";
    }
    
    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        String result;
        if (msg instanceof String[]) {
            controller.displayClientDetails((String[]) msg);
        } else if (msg instanceof Object[]) {
            Object[] message = (Object[]) msg;
            switch (message[0].toString()) {
                case "insertOrder":
                    insertOrder(message, client);
                    break;
                case "updateOrder":
                    result = updateOrder(message);
                    break;
                case "login":
                	result = UserController.login(client, message);
                default:
                    System.out.println("Received unknown message type from client: " + msg);
            }
        } else if (msg instanceof String) {
            switch ((String) msg) {
                case "view":
                    viewOrders(client);
                    break;
                default:
                    System.out.println("Received unknown message from client: " + msg);
            }
        } else {
            System.out.println("Received unknown message from client: " + msg);
        }
    }

    
    //CHECK IF WORKS
    private void insertOrder(Object[] message, ConnectionToClient client) {
    	String result;
    	Order order = (Order) message[1];
        result = dbController.insertOrder(order.getOrderNumber(), order.getNameOfRestaurant(), order.getTotalPrice(), 
                                  order.getOrderListNumber(), order.getOrderAddress());
    	try {
            client.sendToClient(createResponseForClient(result));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //Update order
    private String updateOrder(Object[] message) {
    	int orderNum = (int) message[1];
        String toChange = message[2].toString();
        if ("Order_address".equals(toChange)){
        	return(dbController.updateOrder(orderNum, toChange, (String)message[3]));
        }
        else
        	return(dbController.updateOrder(orderNum, toChange, (double)message[3]));
    }
    
    //view orders
    private void viewOrders(ConnectionToClient client) {
        List<Object[]> orders = dbController.showOrders();
        try {
            client.sendToClient(orders.toArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void clientDisconnected(ConnectionToClient client) {
        controller.displayClientDetails((new String[]{"Client disconnected: "+client}));
    }

    @Override
    protected void serverStarted() {
    	controller.updateStatus("Server listening for connections on port "+ getPort());
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void setController(serverController controller) {
        this.controller = controller;
     }

}
