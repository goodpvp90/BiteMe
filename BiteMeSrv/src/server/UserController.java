package server;

import java.io.IOException;
import java.util.List;
import ocsf.server.ConnectionToClient;


public class UserController {
    //view orders
    public static String login(ConnectionToClient client, Object[] message) {
        String username = message[0]
        try {
            client.sendToClient(server.DBController.validateLogin());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
