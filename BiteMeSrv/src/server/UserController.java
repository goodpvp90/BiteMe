package server;

import java.util.ArrayList;
import common.EnumBranch;
import common.EnumType;
import common.User;
import ocsf.server.ConnectionToClient;


public class UserController {
	private static Server server = Server.getInstance();
    //view orders
    public static void login(ConnectionToClient client, Object[] message) {
    	User user = (User)message[1];
        Object result = server.dbController.validateLogin(user);
		if (result instanceof String)
			server.sendMessageToClient(client, result);
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
			server.sendMessageToClient(client, (Object)user);
		}
    }
}
