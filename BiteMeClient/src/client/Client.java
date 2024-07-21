package client;

import ocsf.client.*;
import java.io.*;
import java.net.InetAddress;
import common.Order;
import ClientGUI.clientController;

public class Client extends AbstractClient {
    // Default port to connect to the server
    final public static int DEFAULT_PORT = 8080;
    
    // Controller for Client GUI functionality
    private clientController clientController;

    // Constructor to initialize the client with host and port, and establish connection
    public Client(String host, int port) throws IOException {
        super(host, port);
        openConnection();
        
        // Get client's IP address and host name
        String clientIP = InetAddress.getLocalHost().getHostAddress();
        String clientHostName = InetAddress.getLocalHost().getHostName();
        
        // Send initial connection message to the server
        sendToServer(new String[] { clientIP, clientHostName, "start" });
    }
    
    // Sets the GUI controller for this client
    public void setGuiController(clientController clientController) {
        this.clientController = clientController;
    }
    
    // Handle messages received from the server
    @Override
    protected void handleMessageFromServer(Object msg) {
        if (msg instanceof Object[]) {
            // Handle array of orders from the server
            Object[] orders = (Object[]) msg;
            if (clientController != null) {
                clientController.displayOrders(orders);
            }
        } else {
            // Handle non-array messages for updating the top label in clientController
            if (clientController != null) {
                clientController.updateWelcomeText("Message from server: " + msg);
            }
        }
    }
    
    // Quit the client and close the connection
    public void quit() {
        try {
            // Get client's IP address and host name
            String clientIP = InetAddress.getLocalHost().getHostAddress();
            String clientHostName = InetAddress.getLocalHost().getHostName();
            
            // Send disconnection message to the server
            sendToServer(new String[] { clientIP, clientHostName, "end" });
            
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
    
    // Send a request to update an order on the server
    public void sendUpdateOrderRequest(int orderNum, String colToChange, Object newVal) {
        Object[] arr = new Object[4];
        arr[0] = "updateOrder";
        arr[1] = orderNum;
        arr[2] = colToChange;
        arr[3] = newVal;
        sendMessageToServer(arr);
    }
    
    // Send a request to insert a new order on the server
    public void sendInsertOrderRequest(Order order) {
        sendMessageToServer(new Object[] { "insertOrder", order });
    }

    // Request to view orders from the database
    public void viewOrdersFromDB() {
        sendMessageToServer("view");
    }
}
