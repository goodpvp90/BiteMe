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
		//Using dummy to enter:
		//Username : a
		//Password: a
        //if (authenticate(username, password))
	
		
		//Send to client user name username and password which we received earlier
		//if user doesn't exist login failed and error is printed
		//else we launch appropriate Home page
		client.loginValidation(new User(username,password));

	}

	public void updateUser(Object user) {
		if (user instanceof String) {
			showError("Incorrect Information, Try Again.");
			user=null;
			return;
		}
		this.user = (User) user;
		System.out.println(this.user.getFirstName());
	}

	private void handleQuit() {
		System.exit(0);
	}

	private void showError(String message) {
		errorText.setText(message);
		errorText.setVisible(true);
	}

	private boolean authenticate(String username, String password) {
		 //Dummy authentication logic; replace with actual logic
		return "a".equals(username) && "a".equals(password);
	}
	
    private void launchUserHomePageUI() {
        try {
        	UserHomePageUI Userapp = new UserHomePageUI(user);
        	Userapp.start(new Stage());
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred while loading the User Home Page.");
        }
    }



}
