package controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import containers.ListContainer;
import enums.EnumBranch;
import enums.EnumClientOperations;
import ocsf.server.ConnectionToClient;
import restaurantEntities.Order;
import server.Server;
import userEntities.User;
/**
 * Manages client and worker notifications for the server.
 **/
public class NotificationController {
    /**
     * The server instance.
     */
    private Server server;
    /**
     * The database controller instance.
     */
	private DBController dbController;
    /**
     * A map of clients identified by a username.
     */
	private Map<String, ConnectionToClient> clients = new HashMap<>();
    /**
     * A list of clients currently in the process of order creation.
     */
	private List<ConnectionToClient> clientsInOrderCreation = new ArrayList<>();
    /**
     * A map of workers in pending orders identified by a User object.
     */
	private Map<User,ConnectionToClient> workersInPendingOrders = new HashMap<>();
	
    /**
     * Constructor for NotificationController.
     *
     * @param server the server instance
     * @param dbController the database controller instance
     */
    public NotificationController(Server server, DBController dbController) {
		this.server = server;
		this.dbController = dbController;
	}
    
    /**
     * Adds a client to the clients map.
     *
     * @param key the key to identify the client
     * @param client the client connection
     */
	public void addToClients(String key, ConnectionToClient client) {
        clients.put(key, client);
    }
	
    /**
     * Removes a client from the clients map.
     *
     * @param key the username to identify the client
     */
    public void removeFromClients(String key) {
        clients.remove(key);
    }
    
    /**
     * Retrieves a client from the map by key.
     *
     * @param key the username to identify the client
     * @return the client connection
     */    public ConnectionToClient getClient(String key) {
        return clients.get(key);
    }
     
     /**
      * Adds a client to the list of clients in order creation.
      *
      * @param client the client connection
      */
    public void addClientToClientsInOrderCreation(ConnectionToClient client) {
    	clientsInOrderCreation.add(client);
    }
    
    /**
     * Removes a client from the list of clients in order creation.
     *
     * @param client the client connection
     */
    public void removeClientsInOrderCreation(ConnectionToClient client) {
    	clientsInOrderCreation.remove(client);
    }
    
    /**
     * Notifies all clients in order creation to stop creating the order.
     */
    public void notifyUpdatedMenu() {
    	for (ConnectionToClient c : clientsInOrderCreation)
    		server.sendMessageToClient(EnumClientOperations.INTERRUPT_ORDER_CREATION, c, "STOP CREATING ORDER");
    }
    
    /**
     * Adds a worker to the workers in pending orders map.
     *
     * @param key    the user key
     * @param client the client connection
     */
    public void addToWorkersInPendingOrders(User key, ConnectionToClient client) {
    	workersInPendingOrders.put(key, client);
    }

    /**
     * Removes a worker from the workers in pending orders map.
     *
     * @param key the user key
     */
    public void removeFromWorkersInPendingOrders(User key) {
    	workersInPendingOrders.remove(key);
    }
    
    /**
     * Retrieves a worker's client connection from the workers in pending orders map.
     *
     * @param key the user key
     * @return the client connection
     */
    public ConnectionToClient getWorkerInPendingOrders(User key) {
        return workersInPendingOrders.get(key);
    }
    
    /**
     * Notifies workers in the specified branch to reload the pending page.
     *
     * @param branchLoc the branch location
     */
    public void notifyWorker(EnumBranch branchLoc) {
    	System.out.println();
        for (User user : workersInPendingOrders.keySet()) {
            if(user.getHomeBranch() == branchLoc) {
            	server.sendMessageToClient(EnumClientOperations.INTERRUPT_PENDING_ORDERS, workersInPendingOrders.get(user), "RELOAD PENDING PAGE");
            }
        }
    }
    
    /**
     * Notifies a user about the status of their order.
     * if the user is not connected saves the message if the DB
     *
     * @param orderId the order ID
     * @param message the notification message
     */
    public void notifyUser(int orderId, String message) {
        try {
            Order order = dbController.getOrderById(orderId);
            String username = order.getUsername();
            ConnectionToClient client = getClient(username);
            if (client != null) {
            	List<String> not = new ArrayList<String>();
            	not.add(message);
            	ListContainer notContainter = new ListContainer();
            	notContainter.setListString(not);
            	server.sendMessageToClient(EnumClientOperations.NOTIFICATION, client, notContainter);
            }else 
                dbController.savePendingNotification(username, orderId, message);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
	//=======================================================
    public boolean areConnectionsEqual(ConnectionToClient client1, ConnectionToClient client2) {
        return clients.entrySet().stream()
            .anyMatch(entry -> entry.getValue().equals(client1) && entry.getValue().equals(client2));
    }
    //==========================================================
}
