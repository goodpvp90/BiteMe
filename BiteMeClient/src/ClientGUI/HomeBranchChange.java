package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import client.Client;
import common.EnumBranch;
import common.User;
import common.EnumBranch;
import javafx.application.Platform;
import javafx.event.ActionEvent;

public class HomeBranchChange {

    @FXML
    private Button backButton;

    @FXML
    private ComboBox<String> homeBranchComboBox;

    @FXML
    private Text titleText;

    @FXML
    private Text errorText;

    @FXML
    private Text successText;
 
    private User user;
    
    private Client client;
    
    @FXML
    public void initialize() {
        // Initialize ComboBox items here
        homeBranchComboBox.getItems().addAll("North", "Center", "South");
        homeBranchComboBox.setValue("North");
    }
    
    public void setUser(User user) {
    	this.user=user;
    	homeBranchComboBox.setValue(convertLocEnumToStr(user.getHomeBranch()));
    }
    
    
    
    private String convertLocEnumToStr(EnumBranch enumValue) {
    	switch(enumValue) {
    	case EnumBranch.NORTH:
    		return "North";
    	case EnumBranch.CENTER:
    		return "Center";
    	case EnumBranch.SOUTH:	
    		return "South";
    	}
    	return "No Home Branch";
    } 
    
    private EnumBranch convertStrToEnumBranch(String branchName) {
        switch (branchName.toUpperCase()) {
            case "North":
                return EnumBranch.NORTH;
            case "Center":
                return EnumBranch.CENTER;
            case "South":
                return EnumBranch.SOUTH;
            default:
            	showError("Error Choosing Home Branch");
            
            return user.getHomeBranch();
        }
    }
    
	@FXML
	void handleComboBoxAction(ActionEvent event) {
		// Handle ComboBox selection action

		EnumBranch choice = convertStrToEnumBranch(homeBranchComboBox.getValue());

		if (!choice.equals(user.getHomeBranch())) {
			
			//add client method
			showSuccess(convertLocEnumToStr(choice));
		} else {
			errorText.setVisible(false);
			successText.setVisible(false);
		}
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
    	successText.setVisible(false);
    	errorText.setText(errText);
    	errorText.setVisible(true);
    }
    private void showSuccess(String choice) {
    	
    	errorText.setVisible(false);
    	
    	successText.setText("Changed home branch to " + choice + " successfully!" );
    	successText.setVisible(true);
    }
    
    //Making Quit Button to kill thread and send message to server
    public void closeApplication() {
        if (client != null) {
        	client.userLogout(user);
            client.quit();
            }
        Platform.exit();
        System.exit(0);
    }   
 
}
