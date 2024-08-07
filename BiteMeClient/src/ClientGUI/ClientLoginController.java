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
		client.loginValidation(new User(username,password));	
	}
	
	public void updateUser(Object userData) {
        Platform.runLater(() -> {
            if (userData instanceof User) {
                User user = (User) userData;
                //TODO Check with guys whats ratherr (Using the function below or no)
                boolean isRegistered = (user.getType() != null);
                launchUserHomePageUI(user, isRegistered);
            } else if (userData instanceof String) {
                showError((String) userData);
            } else {
                showError("An unexpected error occurred. Please try again.");
            }
        });
    }

//	private boolean isUnregisteredCustomer(User user) {
//	    return user.getType().equals(EnumType.CUSTOMER) 
//	           && (user.getEmail() == null || user.getEmail().isEmpty())
//	           && (user.getFirstName() == null || user.getFirstName().isEmpty())
//	           && (user.getLastName() == null || user.getLastName().isEmpty())
//	           && (user.getPhone() == null || user.getPhone().isEmpty());
//	}

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserHomePage.fxml"));
            Parent root = loader.load();
            UserHomePageController controller = loader.getController();
            controller.setUser(user, isRegistered);

            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root, 700, 600);
            currentStage.setScene(scene);
            currentStage.setTitle("User Home Page");

            // Set up the close request handler
            currentStage.setOnCloseRequest(event -> {
                event.consume(); // Prevent the window from closing immediately
                controller.closeApplication();
            });

            currentStage.show();
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
