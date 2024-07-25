package ClientGUI;

import client.Client;
import common.Dish;
import common.EnumDish;
import common.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class ClientLoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Text errorText;

    @FXML
    private Button loginButton;

    @FXML
    private Button quitButton;

    private Client client;
    
    private User user;
    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> handleLogin());
        quitButton.setOnAction(event -> handleQuit());
        client = Client.getInstance();
    	client.getInstanceOfClientLoginController(this);
    }

    private void handleLogin() {
    	
        String username = usernameField.getText();
        String password = passwordField.getText();
        
      //Check if the data input from user is legal
        if (username.isEmpty() || password.isEmpty()) {
            showError("Username or password cannot be empty");
            
        } else {
            // success storing the message from server if connect succeed.
            client.loginValidation(new User(username,password));
        }
    }
    
    public void updateUser(Object user) {
    	if (user instanceof String) {
    		//USER DIDNT PUT CORRECT DETAILS
    	}
    	this.user = (User)user;	
    }

    private void handleQuit() {
        System.exit(0);
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisible(true);
    }

    private boolean authenticate(String username, String password) {
        // Dummy authentication logic; replace with actual logic
        return "admin".equals(username) && "password".equals(password);
    }
}
