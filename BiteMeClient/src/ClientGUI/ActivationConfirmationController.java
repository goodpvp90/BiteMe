package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.scene.control.Button;

import common.EnumType;
import common.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Controller class for the Activation Confirmation Page in the BiteMe application.
 * This controller manages the display of user activation confirmation and provides
 * a way to return to the User Home Page.
 */
public class ActivationConfirmationController {

	@FXML
	private Text headlineText;

	@FXML
	private Text infoText;

	@FXML
	private Button returnButton;

    /**
     * Initializes the controller. This method is automatically called after the FXML file has been loaded.
     */
	public void initialize() {
	    headlineText.setText("Customer Activation Confirmation");
	}

    private User loggedInUser;
    private boolean isRegistered;

    /**
     * Sets the user information to be displayed in the confirmation message.
     * 
     * @param user The activated user object
     */
    public void setUserInfo(User user) {
        String info = String.format(
            "Customer activated successfully!\n\n" +
            "ID: %s\n" +
            "Name: %s %s\n" +
            "Email: %s\n" +
            "Phone: %s\n" +
            "Status: %s",
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getPhone(),
            user.getCustomerType() == EnumType.PRIVATE ? "Private" : "Regular"
        );
        infoText.setText(info);
    }

    /**
     * Sets the logged-in user information and registration status.
     * 
     * @param user The currently logged-in user
     * @param isRegistered Boolean indicating if the user is now registered
     */
    public void setLoggedInUser(User user, boolean isRegistered) {
        this.loggedInUser = user;
        this.isRegistered = isRegistered;
    }

    /**
     * Handles the action when the return button is clicked.
     * Returns to the User Home Page.
     */
    @FXML
    private void returnToHomePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserHomePage.fxml"));
            Parent root = loader.load();
            UserHomePageController controller = loader.getController();
            controller.setUser(loggedInUser, isRegistered);

            Stage currentStage = (Stage) infoText.getScene().getWindow();
            Scene scene = new Scene(root, 700, 600);
            currentStage.setScene(scene);
            currentStage.setTitle("User Home Page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}