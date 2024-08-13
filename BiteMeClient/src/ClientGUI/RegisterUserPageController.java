package ClientGUI;

import java.io.IOException;

import client.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import userEntities.User;

/**
 * Controller class for the Register User Page in the BiteMe application.
 * This controller manages the initial step of user registration, where a username is checked
 * for availability or existing partial registration.
 */
public class RegisterUserPageController {

	/**
	 * Button to return to the previous page.
	 */
	@FXML private Button backButton;

	/**
	 * Text field for entering the username.
	 */
	@FXML private TextField usernameField;

	/**
	 * Button to proceed with the registration process.
	 */
	@FXML private Button continueButton;

	/**
	 * Text area for displaying error messages.
	 */
	@FXML private Text errorText;

	/**
	 * The currently logged-in user.
	 */
	private User loggedInUser;

	/**
	 * The scene of the previous page, used for navigation.
	 */
	private Scene previousScene;

	/**
	 * The client instance for communication with the server.
	 */
	private Client client;

    /**
     * Initializes the controller. This method is automatically called after the FXML file has been loaded.
     */
    public void initialize() {
        client = Client.getInstance();
        client.setRegisterUserPageController(this);
    }

    /**
     * Sets the previous scene to allow returning to the User Home Page.
     * 
     * @param scene The scene of the User Home Page
     */
    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }
    
    /**
     * Sets the logged-in user information.
     * 
     * @param user The currently logged-in user
     */
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    /**
     * Handles the action when the back button is clicked.
     * Returns to the User Home Page.
     */
    @FXML
    private void handleBack() {
        try {
            // Retrieve the existing stage for UserHomePageUI
            Stage userHomePageStage = UserHomePageUI.getStage();
            if (userHomePageStage != null) {
                userHomePageStage.show();  // Show the hidden stage again
            } else {
                UserHomePageUI Userapp = new UserHomePageUI(loggedInUser);
                Userapp.start(new Stage());
            }
            // Close the current stage
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred while loading the User Home Page.");
        }
    }

    /**
     * Handles the action when the continue button is clicked.
     * Validates the entered username and sends a request to the server to check its status.
     */
    @FXML
    private void handleContinue() {
        errorText.setVisible(false);
        errorText.setText("");
        String username = usernameField.getText().trim();
        
        if (username.isEmpty()) {
            showError("Please Enter username");
        } else {
            // Send the username to the server
            client.sendSearchUser(username);
        }
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
     * Handles the server's response to the username check.
     * This method is called by the Client class after receiving the server response.
     * 
     * @param Customer The response object from the server
     */
    public void handleServerResponse(Object Customer) {
        Platform.runLater(() -> {
        	if (Customer instanceof Boolean) {
        		showError("Username Not Found");	
        	}
        	else {           	
        		User user = (User)Customer;
        		if (user.getEmail()==null && user.getCreditCard()==null)  
        		{
        			CustomerInformationUpdateWindow(user);            		
        		}
        		else {
        			showError("Username Exists and is Registered");            		
        		}	
        	}
        });
        }

    /**
     * Opens the Customer Information Update window for a user that needs to complete registration.
     * 
     * @param user The user object with partial information (Unregistered Customer)
     */
    private void CustomerInformationUpdateWindow(User user) {
        // Hide any previous error
        errorText.setVisible(false);        
        System.out.println("Proceeding to next window for user: " + user.getUsername());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerInformationUpdate.fxml"));
            Parent root = loader.load();           
            CustomerInformationUpdateController controller = loader.getController();           
            if (controller != null) {
                controller.setPreviousScene(backButton.getScene());
                controller.setUser(user);
                controller.setLoggedInUser(this.loggedInUser); // Pass the logged-in user
            } else {
                System.err.println("Failed to get controller from FXMLLoader");
            }
            Scene scene = new Scene(root);
            Stage stage = (Stage) continueButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading FXML: " + e.getMessage());
            showError("Error loading the Customer Information Update window: " + e.getMessage());
        }
    }
    
    /**
     * Closes the application and performs necessary cleanup operations.
     * This method is typically called when the user exits the application.
     * It logs out the current user and ensures proper termination of the client connection.
     */
    public void closeApplication() {
    	if (client != null) {
			client.userLogout(loggedInUser, true);
		};
    } 
     
   }