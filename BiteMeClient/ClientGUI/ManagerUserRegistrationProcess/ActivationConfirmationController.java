package ManagerUserRegistrationProcess;

import UserHomePageProcess.UserHomePageController;
import UserHomePageProcess.UserHomePageUI;
import enums.EnumType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import userEntities.User;

/**
 * Controller class for the Activation Confirmation Page in the BiteMe application.
 * This controller manages the display of user activation confirmation and provides
 * a way to return to the User Home Page.
 */
public class ActivationConfirmationController {

	/**
     * The text displayed as the headline of the confirmation page.
     */
	@FXML	
	private Text headlineText;
	
    /**
     * The text area displaying the detailed user information.
     */
	@FXML	
	private Text infoText;
	
    /**
     * The button to return to the previous page.
     */
	@FXML	
	private Button returnButton;

	/**
	 * The currently logged-in user.
	 */
	private User loggedInUser;
	
    /**
     * Initializes the controller. This method is automatically called after the FXML file has been loaded.
     */
	public void initialize() {
	    headlineText.setText("Customer Activation Confirmation");
	}


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
            "Status: %s\n" +
            "Branch: %s",
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getPhone(),
            user.getCustomerType() == EnumType.PRIVATE ? "Private" : "Regular",
            user.getHomeBranch()
        );
        infoText.setText(info);
    }

    /**
     * Sets the logged-in user information
     * 
     * @param user The currently logged-in user
     */
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    /**
     * Handles the action when the return button is clicked.
     * Returns to the User Home Page.
     */
    @FXML
    private void returnToHomePage() {
    	try {
    		UserHomePageUI CustCreatApp = new UserHomePageUI(loggedInUser);
    		CustCreatApp.start(new Stage());
            Stage currentStage = (Stage) returnButton.getScene().getWindow();
            currentStage.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}