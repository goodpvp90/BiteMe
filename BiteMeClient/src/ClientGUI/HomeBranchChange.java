package ClientGUI;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.text.Font;
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
    
    private EnumBranch beckupIfDidntSuccessBranch;
    
    
    @FXML
    public void initialize() {
    	client = client.getInstance();
    	client.getInstanceOfHomeBranchChange(this);
    	
        // Initialize ComboBox items here
        homeBranchComboBox.getItems().addAll("North", "Center", "South");
        homeBranchComboBox.setValue("North");
        
        
     // Center the text and set the font size in the ComboBox
        homeBranchComboBox.setButtonCell(createCenteredCell());
        homeBranchComboBox.setCellFactory(param -> createCenteredCell());
    }
    
	// Method to create a ListCell with centered text and larger font size
	private ListCell<String> createCenteredCell() {
		return new ListCell<>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					setText(item);
					setFont(Font.font(16)); // Adjust font size as needed
					setAlignment(Pos.CENTER); // Center the text
				}
			}
		};
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
        switch (branchName) {
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
			beckupIfDidntSuccessBranch= user.getHomeBranch();
			user.setHomeBranch(choice);
			client.changeHomeBranch(user);
		}

	}
	
	//receive information from the client if change user branch succeed
    public void checkSuccessChangeHomeBranch(boolean result) {
    	if(result) {
			showSuccess(homeBranchComboBox.getValue());
			}
    	else {
    		//if the set didn't succeed we want to set previous home branch
    		user.setHomeBranch(beckupIfDidntSuccessBranch);
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
            }
        Platform.exit();
        System.exit(0);
    }   
 
}
