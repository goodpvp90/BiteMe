package server;

import java.sql.SQLException;
import java.util.ArrayList;

import enums.EnumBranch;
import enums.EnumClientOperations;
import enums.EnumType;
import ocsf.server.ConnectionToClient;
import userEntities.User;


public class UserController {
	private Server server;
    private NotificationController notificationController;
	private DBController dbController;

    public UserController(Server server, NotificationController notificationController, DBController dbController) {
		this.server = server;
		this.notificationController = notificationController;
		this.dbController = dbController;
	}

	public boolean login(ConnectionToClient client, Object[] message) {
    	User user = (User)message[1];
        Object result = dbController.validateLogin(user);
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
			
			if (user.getType() == EnumType.CUSTOMER)
				user.setCustomerType(dbController.getCustomerType(user.getUsername()));
			//client.setInfo("user", user); //Store information into client object
			notificationController.addToClients(user.getUsername(), client);
			server.sendMessageToClient(EnumClientOperations.USER,client,(Object)user);
			return true;
		}
    }
    
    public void logout(ConnectionToClient client, Object[] message) {
        User user = (User) message[1];
        boolean logoutSuccess = dbController.logout(user.getUsername());

        if (logoutSuccess) {
            user.setLogged(false);
            notificationController.removeFromClients(user.getUsername());
        }

    }
    
    public void checkUserForCreation(ConnectionToClient client, String username) {
        try {
            User result = dbController.searchUsername(username);
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
            result = dbController.createUser(user.getId(), user.getUsername(), user.getPassword(), user.getEmail(), user.getPhone(),
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
    	return dbController.changeHomeBranch(user);
    }
}