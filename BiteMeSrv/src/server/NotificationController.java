package server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import enums.EnumBranch;
import enums.EnumClientOperations;
import ocsf.server.ConnectionToClient;
import restaurantEntities.Order;
import userEntities.User;

public class NotificationController {
    private Server server;
	private DBController dbController;
	private Map<String, ConnectionToClient> clients = new HashMap<>();
	private List<ConnectionToClient> clientsInOrderCreation = new ArrayList<>();
	private Map<User,ConnectionToClient> workersInPendingOrders = new HashMap<>();
	
    public NotificationController(Server server, DBController dbController) {
		this.server = server;
		this.dbController = dbController;
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
    		server.sendMessageToClient(EnumClientOperations.INTERRUPT_ORDER_CREATION, c, "STOP CREATING ORDER");
    }
    
    public void addToWorkersInPendingOrders(User key, ConnectionToClient client) {
    	workersInPendingOrders.put(key, client);
    }

    public void removeFromWorkersInPendingOrders(User key) {
    	System.out.println(workersInPendingOrders.size());
    	System.out.println(getWorkerInPendingOrders(key));
    	workersInPendingOrders.remove(key);
    	System.out.println(workersInPendingOrders.size());
    }
    
    public ConnectionToClient getWorkerInPendingOrders(User key) {
        return workersInPendingOrders.get(key);
    }
    
    public void notifyWorker(EnumBranch branchLoc) {
    	System.out.println();
        for (User user : workersInPendingOrders.keySet()) {
            if(user.getHomeBranch() == branchLoc) {
            	server.sendMessageToClient(EnumClientOperations.INTERRUPT_PENDING_ORDERS, workersInPendingOrders.get(user), "RELOAD PENDING PAGE");
            }
        }
    }
    
    public void notifyUser(int orderId, String message) {
        try {
            Order order = dbController.getOrderById(orderId);
            String username = order.getUsername();
            ConnectionToClient client = getClient(username);
            if (client != null) {
            	List<String> not = new ArrayList<String>();
            	not.add(message);
            	server.sendMessageToClient(EnumClientOperations.NOTIFICATION, client, not);
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
