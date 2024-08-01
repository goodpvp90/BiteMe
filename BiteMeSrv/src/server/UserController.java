	package server;

import java.util.ArrayList;
import common.EnumBranch;
import common.EnumClientOperations;
import common.EnumType;
import common.User;
import ocsf.server.ConnectionToClient;


public class UserController {
	private Server server;
	
    public UserController(Server server) {
		this.server = server;
	}

	public void login(ConnectionToClient client, Object[] message) {
    	User user = (User)message[1];
        Object result = server.dbController.validateLogin(user);
		if (result instanceof String) {
			server.sendMessageToClient(EnumClientOperations.USER,client, result);
		}
		else {
			@SuppressWarnings("unchecked")
			ArrayList<Object> details = (ArrayList<Object>) result;

			client.setInfo("user", result); //Store information into client object
      
			user.setFirstName((String)details.get(0));
			user.setLastName((String)details.get(1));
			user.setEmail((String)details.get(2));
			user.setPhone((String) details.get(3));
			user.setHomeBranch((EnumBranch)details.get(4));
			user.setLogged((boolean)details.get(7));
			user.setType((EnumType)details.get(8));
			
			boolean isApproved = false;
			if(user.getType() == EnumType.CUSTOMER) {
				isApproved = server.dbController.checkCustomerRegistered(user);
				server.sendMessageToClient(EnumClientOperations.USER,client, new Object[] {(Object)user, isApproved});
			}
			else
				server.sendMessageToClient(EnumClientOperations.USER,client, new Object[] {(Object)user});
		}
    }
    
    public void logout(ConnectionToClient client, Object[] message) {
        User user = (User) message[1];
        boolean logoutSuccess = server.dbController.logout(user.getUsername());

        if (logoutSuccess) {
            user.setLogged(false);
            server.sendMessageToClient(EnumClientOperations.LOG_OUT, client, (Object) user);
        } else {
            server.sendMessageToClient(EnumClientOperations.LOG_OUT, client, "Failed to log out user.");
        }
    }
    
    public void createAccount(ConnectionToClient client, Object[] message) {
    	User user = (User)message[1];
    	boolean result = server.dbController.createUser(user.getUsername(), user.getPassword(), user.getEmail(), user.getPhone(), 
    			user.getFirstName(), user.getLastName(), user.getHomeBranch(), user.getType());
    	if (!result)
    		server.sendMessageToClient(EnumClientOperations.EROR,client, result);
    	else {
    		server.sendMessageToClient(EnumClientOperations.CREATED_USER,client, (Object)user);
    	}
    }
}
