package ClientGUI;


import client.Client;
import common.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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
    void initialize() {
        // Setup table columns
    	client = client.getInstance();
    }
    
    @FXML
    void handleBackButtonAction(ActionEvent event) {
    	try {
        	UserHomePageUI Userapp = new UserHomePageUI(user,true);
        	Userapp.start(new Stage());
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
    void handleUpdateDeleteButtonAction(ActionEvent event) {
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

    //Making Quit Button to kill thread and send message to server
    public void closeApplication() {
        if (client != null) {
        	client.userLogout(user);
            }
        client.userLogout(user);
        Platform.exit();
        System.exit(0);
    }   
    @FXML
    void handleAddNewDishButtonAction(ActionEvent event) {
        System.out.println("Add New Dish button pressed");
        // Add your logic here for the add new dish button
    }
}