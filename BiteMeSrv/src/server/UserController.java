	package server;

import java.sql.SQLException;
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

	public boolean login(ConnectionToClient client, Object[] message) {
    	User user = (User)message[1];
        Object result = server.dbController.validateLogin(user);
		if (result instanceof String) {
			server.sendMessageToClient(EnumClientOperations.USER,client, result);
			return false;
		}
		else {
			@SuppressWarnings("unchecked")
			ArrayList<Object> details = (ArrayList<Object>) result;
      
			user.setFirstName((String)details.get(0));
			user.setLastName((String)details.get(1));
			user.setEmail((String)details.get(2));
			user.setPhone((String) details.get(3));
			user.setHomeBranch((EnumBranch)details.get(4));
			user.setLogged((boolean)details.get(7));
			user.setType((EnumType)details.get(8));
			
			if (user.getType() == EnumType.CUSTOMER) {
				user.setCustomerType(server.dbController.getCustomerType(user.getUsername()));
			}
			//client.setInfo("user", user); //Store information into client object
        	server.addToClients(user.getUsername(),client);

			server.sendMessageToClient(EnumClientOperations.USER,client,(Object)user);
			return true;
		}
    }
    
    public void logout(ConnectionToClient client, Object[] message) {
        User user = (User) message[1];
        boolean logoutSuccess = server.dbController.logout(user.getUsername());

        if (logoutSuccess) {
            user.setLogged(false);
            server.sendMessageToClient(EnumClientOperations.LOG_OUT, client, (Object) user);
            server.removeFromClients(user.getUsername());
        } else {
            server.sendMessageToClient(EnumClientOperations.LOG_OUT, client, "Failed to log out user.");
        }
    }
    
    public void checkUserForCreation(ConnectionToClient client, String username) {
        try {
            User result = server.dbController.searchUsername(username);
            if (result != null) {
                server.sendMessageToClient(EnumClientOperations.CHECK_USER, client, result);
            } else {
                server.sendMessageToClient(EnumClientOperations.CHECK_USER, client, false); // No username found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            server.sendMessageToClient(EnumClientOperations.CHECK_USER, client, false); // Send false in case of error
        }
    }
    
    public void createAccount(ConnectionToClient client, Object[] message) {
        User user = (User)message[1];
        System.out.println("Im after USER after CREATEACCOUNT");
        boolean result = false;
        try {
            result = server.dbController.createUser(user.getId(), user.getUsername(), user.getPassword(), user.getEmail(), user.getPhone(),
                    user.getFirstName(), user.getLastName(), user.getHomeBranch(), user.getType(), user.getCustomerType(), user.getCreditCard());
        } catch (Exception e) {
            System.out.println("Exception occurred while calling createUser:");
            e.printStackTrace();
        }
        System.out.println("Im after RES after CREATEACCOUNT " + " RESULT IS :" + result);
    	if (!result) {
    		System.out.println("Im in ERROR CREATEACCOUNT");
    		server.sendMessageToClient(EnumClientOperations.EROR,client, result);    		
    	}
    	else {
    		System.out.println("IM IN CREATED ACCOUNT ENUM");
    		server.sendMessageToClient(EnumClientOperations.CREATED_ACCOUNT,client, (Object)user);
    	}
    }
    
    public boolean changeHomeBranch(User user) {
    	return server.dbController.changeHomeBranch(user);
    }
}
