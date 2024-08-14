package QualifiedWorkerMenuUpdateProcess;


import UserHomePageProcess.UserHomePageUI;
import client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import userEntities.User;

/**
 * The UpdateMenuNavigation class manages the navigation options for updating, deleting,
 * and adding new dishes. It handles user interactions with the navigation buttons and
 * provides error handling for navigation-related actions.
 */
public class UpdateMenuNavigation {


    /**
     * Text element displaying the action prompt to the user.
     */
    @FXML
    private Text actionPromptText;

    /**
     * Button for navigating back to the User Home Page.
     */
    @FXML
    private Button backButton;

    /**
     * Button for navigating to the Update/Delete Dish page.
     */
    @FXML
    private Button updateDeleteButton;

    /**
     * Button for navigating to the Add New Dish page.
     */
    @FXML
    private Button addNewDishButton;

    /**
     * Text element displaying the title of the current page.
     */
    @FXML
    private Text titleText;
    
    /**
     * Text element for displaying error messages to the user.
     */
    @FXML
    private Text errorText;

    /**
     * The user currently logged into the system.
     */
    private User user;
    
    /**
     * The client instance for managing server communication.
     */
    private Client client;
    
    
    /**
     * Initializes the controller. Sets up the client instance
     */
    @FXML
    private void initialize() {
    	client = Client.getInstance();
    }
    
    /**
     * Sets the user for this navigation controller.
     * 
     * @param user The user currently logged in.
     */
    public void setUser(User user) {
    	this.user=user;

    }
    
    /**
     * Handles the action event triggered by the Update/Delete button.
     * Navigates the user to the Update/Delete Dish page.
     * 
     * @param event The action event triggered by the Update/Delete button.
     */
    @FXML
    private void handleUpdateDeleteButtonAction(ActionEvent event) {
    	try {
    		UpdateDeleteMenuUI Userapp = new UpdateDeleteMenuUI(user);
        	Userapp.start(new Stage());
            Stage currentStage = (Stage) updateDeleteButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred while loading the Update Delete Dish Page.");
        }
    }
    
    /**
     * Handles the action event triggered by the Add New Dish button.
     * Navigates the user to the Add Dish page.
     * 
     * @param event The action event triggered by the Add New Dish button.
     */
    @FXML
    private void handleAddNewDishButtonAction(ActionEvent event) {
    	try {
    		UpdateAddDishUI Userapp = new UpdateAddDishUI(user);
        	Userapp.start(new Stage());
            Stage currentStage = (Stage) addNewDishButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred while loading the Add Dish Page.");
        }
    }
    
    /**
     * Displays an error message in the errorText field.
     * 
     * @param message The error message to display.
     */
    private void showError(String errText) {
    	errorText.setText(errText);
    	errorText.setVisible(true);
    }
    
    /**
     * Handles the action event triggered by the back button.
     * Navigates the user back to the User Home Page.
     * 
     * @param event The action event triggered by the back button.
     */
    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        try {
            Stage userHomePageStage = UserHomePageUI.getStage();
            if (userHomePageStage != null) {
                userHomePageStage.show();
            } else {
                UserHomePageUI Userapp = new UserHomePageUI(user);
                Userapp.start(new Stage());
            }
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred while loading the User Home Page.");
        }
    }
    
    /**
	 * Closes the application and logs out the user.
	 */
    public void closeApplication() {
    	if (client != null) {
			client.userLogout(user, true);
		}
    }   
   
}