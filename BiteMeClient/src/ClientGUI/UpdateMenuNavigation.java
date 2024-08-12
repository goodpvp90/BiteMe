package ClientGUI;


import client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import userEntities.User;

public class UpdateMenuNavigation {

    @FXML
    private Text actionPromptText;

    @FXML
    private Button backButton;

    @FXML
    private Button updateDeleteButton;

    @FXML
    private Button addNewDishButton;

    @FXML
    private Text titleText;
    
    @FXML
    private Text errorText;

    private User user;
    
    private Client client;
    
    public void setUser(User user) {
    	this.user=user;

    }
    
    @FXML
    private void initialize() {
        // Setup table columns
    	client = client.getInstance();
    }
    
    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        try {
            // Retrieve the existing stage for UserHomePageUI
            Stage userHomePageStage = UserHomePageUI.getStage();

            if (userHomePageStage != null) {
                userHomePageStage.show();  // Show the hidden stage again
            } else {
                // If the stage is somehow null, recreate and show it
                UserHomePageUI Userapp = new UserHomePageUI(user, true);
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

    private void showError(String errText) {
    	errorText.setText(errText);
    	errorText.setVisible(true);
    }
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
    
    //Making Quit Button to kill thread and send message to server
    public void closeApplication() {
    	if (client != null) {
			client.userLogout(user, true);
		}
    }   
   
}