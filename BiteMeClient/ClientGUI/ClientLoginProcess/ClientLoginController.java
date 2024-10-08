package ClientLoginProcess;

import java.io.IOException;

import CustomerOrderCreationProcess.CustomerOrderCreationUI;
import UserHomePageProcess.UserHomePageController;
import UserHomePageProcess.UserHomePageUI;
import client.Client;
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
import userEntities.User;

/**
 * Controller class for the Client Login screen in the BiteMe application.
 * This screen handles user authentication after successful connection to the server.
 */
public class ClientLoginController {

	/**
     * Text field for entering the username.
     */
	@FXML
	private TextField usernameField;
	
	/**
     * Password field for entering the user's password.
     */
	@FXML
	private PasswordField passwordField;
	
	/**
     * Text area for displaying error messages during the login process.
     */
	@FXML
	private Text errorText;
	
	/**
     * Button to initiate the login process.
     */
	@FXML
	private Button loginButton;
	
	/**
     * Button to quit the application.
     */
	@FXML
	private Button quitButton;
	
	/**
     * The client instance for communication with the server.
     */
	private Client client;
	
	/**
     * The user object representing the currently logged-in user.
     */
	private User user = null;

    /**
     * Initializes the controller. This method is automatically called after the FXML file has been loaded.
     * Sets up button actions and initializes the client instance.
     */
	@FXML
	private void initialize() {
		loginButton.setOnAction(event -> handleLogin());
		quitButton.setOnAction(event -> handleQuit());
		client = Client.getInstance();
		client.getInstanceOfClientLoginController(this);
	}

    /**
     * Handles the login process when the login button is clicked.
     * Validates user input and sends login request to the server.
     */
	private void handleLogin() {
		String username = usernameField.getText();
		String password = passwordField.getText();
		if (username.isEmpty() || password.isEmpty()) {
			showError("Username or password cannot be empty");
			return;
		}
		client.loginValidation(new User(username,password));	
	}

    /**
     * Updates the UI based on the server's response to the login attempt.
     * 
     * @param userData The response object from the server, either a User object or an error message
     */
	public void updateUser(Object userData) {
        Platform.runLater(() -> {
            if (userData instanceof User) {
                user = (User) userData;
                client.setUser(user);
                launchUserHomePageUI(user);
            } else if (userData instanceof String) {
                showError((String) userData);
            } else {
                showError("An unexpected error occurred. Please try again.");
            }
        });
    }

    /**
     * Handles the action when the quit button is clicked.
     * Closes the connection and exits the application.
     */
	private void handleQuit() {
		client.quit();
		System.exit(0);
	}
	
    /**
     * Closes the application, ensuring proper disconnection from the server.
     * This method is called when the user clicks the window's close button.
     */
    public void closeApplication() {
        if (client != null) {
            client.quit();
        }
        Platform.exit();
        System.exit(0);
    }
    
    /**
     * Displays an error message to the user.
     *
     * @param message The error message to display
     */	
	private void showError(String message) {
	        errorText.setText(message);
	        errorText.setVisible(true);
	}
	
	/**
     * Launches the User Home Page UI after successful login.
     * 
     * @param user The authenticated User object
     */
    private void launchUserHomePageUI(User user) {
    	try {
    		UserHomePageUI CustCreatApp = new UserHomePageUI(user);
    		CustCreatApp.start(new Stage());
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Resets the client state and UI elements.
     * This method is called when returning to the login screen from other parts of the application.
     */   
    public void resetClient() {
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
        usernameField.clear();
        passwordField.clear();
        errorText.setVisible(false);
    }
}
