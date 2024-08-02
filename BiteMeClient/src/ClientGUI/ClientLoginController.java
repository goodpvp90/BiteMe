package ClientGUI;

import java.io.IOException;

import client.Client;
import common.Dish;
import common.EnumDish;
import common.EnumType;
import common.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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

	private User user = null;

	@FXML
	private void initialize() {
		loginButton.setOnAction(event -> handleLogin());
		quitButton.setOnAction(event -> handleQuit());
		
		//Initialize the client controller in the client class the instance of this class
		client = Client.getInstance();
		client.getInstanceOfClientLoginController(this);
	}

	private void handleLogin() {

		String username = usernameField.getText();
		String password = passwordField.getText();

		// Check if the data input from user is legal
		if (username.isEmpty() || password.isEmpty()) {
			showError("Username or password cannot be empty");
			return;
		}
		//Send to client user name username and password which we received earlier
		//if user doesn't exist login failed and error is printed
		//else we launch appropriate Home page
		//updateUser is the one that takes care of incorrect information.
		client.loginValidation(new User(username,password));	
	}
	
	//New By Eldar because of thread problems
	//Added Plat.run later because of a thread problem (Because we have to do it, Otherwise will be thread problem)
	public void updateUser(Object[] user) {
	    Platform.runLater(() -> {
	        if (user[0] instanceof User) {
	        	//Check for Unregistered Customer
	        	if (((User)user[0]).getType().equals(EnumType.CUSTOMER) && !(boolean)user[1]){
	        		this.user = (User) user[0];
	        		launchUserHomePageUI((User) user[0], (boolean) false);	
	        	}
	        	else
	        	launchUserHomePageUI((User) user[0], (boolean) true);
	        } else if (user[0] instanceof String) {
	            showError((String) user[0]);
	            this.user = null;
	        } else {
	            showError("An unexpected error occurred. Please try again.");
	            this.user = null;
	        }
	    });
	}
	
	
	//Quit button sends a msg that client disconnected
	private void handleQuit() {
		client.quit();
		System.exit(0);
	}
	//TODO Talk to Ben how to implement it so itll be not copied always.
	//By clickin "X" same effect like clicking Quit Button
    public void closeApplication() {
        if (client != null) {
            client.quit();
        }
        Platform.exit();
        System.exit(0);
    }
	
	private void showError(String message) {
	        errorText.setText(message);
	        errorText.setVisible(true);
	}
	
	
    private void launchUserHomePageUI(User user, boolean isRegistered) {
        try {
        	UserHomePageUI Userapp = new UserHomePageUI(user, isRegistered);
        	Userapp.start(new Stage());
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.hide();
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred while loading the User Home Page.");
        }
    }
    public void resetClient() {
        // Reset the client state if necessary
        client = Client.getInstance();
        try {
            if (!client.isConnected()) {
                client.openConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to connect to the server.");
        }
        client.getInstanceOfClientLoginController(this);
        
        // Clear the input fields
        usernameField.clear();
        passwordField.clear();
        errorText.setVisible(false);
    }


}
