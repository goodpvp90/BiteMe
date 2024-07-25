package ClientGUI;

import client.Client;
import common.Dish;
import common.EnumDish;
import common.User;
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
		//client = Client.getInstance();
		//client.getInstanceOfClientLoginController(this);
	}

	private void handleLogin() {

		String username = usernameField.getText();
		String password = passwordField.getText();

		// Check if the data input from user is legal
		if (username.isEmpty() || password.isEmpty()) {
			showError("Username or password cannot be empty");

		}else 
		{
            //if (authenticate(username, password)) {
             launchUserHomePageUI(username, false); // assuming non-CEO user for now
			// success storing the message from server if connect succeed.
			//client.loginValidation(new User(username, password));

			//if (client == null) {
			//	return;
			//} else {
			//launchUserHomePageUI();
			//}

		}
	}

	public void updateUser(Object user) {
		if (user instanceof String) {
			showError("Incorrect Information, Try Again.");
			return;
		}
		this.user = (User) user;
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


//    private void launchClientLoginUI() {
//        try {
//            // Launch ClientLoginUI
//            ClientLoginUI loginApp = new ClientLoginUI();
//            loginApp.start(new Stage());
//
//            // Close the current stage
//            Stage currentStage = (Stage) connectButton.getScene().getWindow();
//            currentStage.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
	
    private void launchUserHomePageUI(String username, boolean isCEO) {
        try {
        	UserHomePageUI Userapp = new UserHomePageUI();
        	Userapp.start(new Stage());
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred while loading the User Home Page.");
        }
    }

}
