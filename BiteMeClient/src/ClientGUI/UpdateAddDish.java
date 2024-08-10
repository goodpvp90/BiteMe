package ClientGUI;

import client.Client;
import common.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import common.Dish;
import common.DishAppetizer;
import common.DishBeverage;
import common.DishDessert;
import common.DishMainCourse;
import common.DishSalad;
import common.EnumBranch;
import common.EnumDish;

public class UpdateAddDish {

    @FXML
    private Button backButton;

    @FXML
    private Text addDishText;

    @FXML
    private Text dishTypeText;

    @FXML
    private Text dishNameText;

    @FXML
    private Text priceText;

    @FXML
    private Text isGrillText;

    @FXML
    private TextField dishNameField;

    @FXML
    private TextField priceField;

    @FXML
    private ComboBox<String> isGrillComboBox;

    @FXML
    private ComboBox<EnumDish> dishTypeComboBox;

    @FXML
    private Button saveDishButton;

    @FXML
    private Text errorText;
    
    private User user;
    
    private Client client;
    
    private boolean success;
    

    @FXML
    private void initialize() {
    	client = client.getInstance();
    	client.getInstanceOfUpdateAddDish(this);
    	
    	// Populate dishTypeComboBox with EnumDish values
        dishTypeComboBox.getItems().setAll(EnumDish.values());
        dishTypeComboBox.setValue(EnumDish.APPETIZER); // Default selection

        // Populate isGrillComboBox with "Yes" and "No"
        isGrillComboBox.getItems().addAll("Yes", "No");
        isGrillComboBox.setValue("No"); // Default selection

        // Add listener to dishTypeComboBox
        dishTypeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == EnumDish.MAIN_COURSE) {
                isGrillText.setVisible(true);
                isGrillComboBox.setVisible(true);                
            } else {
                isGrillText.setVisible(false);
                isGrillComboBox.setVisible(false);
            }
        });

        // Initially hide isGrillText and isGrillComboBox if not MAIN_COURSE
        if (dishTypeComboBox.getValue() != EnumDish.MAIN_COURSE) {
            isGrillText.setVisible(false);
            isGrillComboBox.setVisible(false);
        }
    	
    }
    
	public void setUser(User user) {
		this.user = user;
	}
	
	public void setSucceededAdd(boolean success) {
		this.success = success;
		Platform.runLater(() -> checkSuccessAndProceed());
	}
    
    @FXML
    private void handleBackButtonAction() {   
    	try {
    		UpdateMenuNavigationUI Userapp = new UpdateMenuNavigationUI(user);
        	Userapp.start(new Stage());
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred while loading the Update Menu Nevigation Page.");
        }
    }
    
    @FXML
    private void handleSaveDishAction() {
    	String dishName = dishNameField.getText();
    	String price = priceField.getText();
		boolean isGrill = (isGrillComboBox.getValue().equals("Yes") ? true : false);
    	
    	if(!CheckIfLegalNameAndPrice(dishName,price)){
    		return;
    	}
    	errorText.setVisible(false);
    	
    	  	
    	int dishMenuID = UserHomeBranchConvertToInt(user.getHomeBranch());
    	
    	addDishToDB(dishName, Double.valueOf(price),dishMenuID,isGrill);
    }
    
    private void checkSuccessAndProceed() {
        if (!success) {
            showError("Adding new dish failed: Dish already exists.");
        } else {
            showSuccessDialog();
            resetFields();
        }
    }
    
	private void showSuccessDialog() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initStyle(StageStyle.UTILITY);
		alert.setTitle("Added Dish Complete");
		alert.setHeaderText(null);
		alert.setContentText(dishNameField.getText() + " Created successfully!");

		ButtonType okButton = new ButtonType("OK", ButtonData.OK_DONE);
		alert.getButtonTypes().setAll(okButton);

		alert.showAndWait().ifPresent(response -> {
			if (response == okButton) {
				alert.close(); // Close the dialog window
			}
		});
	}
    
	private void resetFields() {
	    // Reset all TextFields to empty
	    dishNameField.clear();
	    priceField.clear();

	    // Reset ComboBoxes to their default values
	    dishTypeComboBox.setValue(EnumDish.APPETIZER);  // Default value for dishTypeComboBox
	    isGrillComboBox.setValue("No");  // Default value for isGrillComboBox
	    
	    // Hide isGrill fields if the default dish type is not MAIN_COURSE
	    if (dishTypeComboBox.getValue() != EnumDish.MAIN_COURSE) {
	        isGrillText.setVisible(false);
	        isGrillComboBox.setVisible(false);
	    }
	}
	
	private void addDishToDB(String dishName, double dishPrice, int dishMenuID, boolean isGrill) {

		EnumDish dishTypePick = dishTypeComboBox.getValue();
		Dish createdDish=null;
		
		switch (dishTypePick) {
		case EnumDish.SALAD:
			DishSalad dishSalad = new DishSalad(dishName, false, dishPrice, dishMenuID);
			dishSalad.setDishType(dishTypePick);
			createdDish = dishSalad;
			break;
		case EnumDish.BEVERAGE:
			DishBeverage dishBev = new DishBeverage(dishName, false, dishPrice, dishMenuID);
			dishBev.setDishType(dishTypePick);
			createdDish = dishBev;
			break;
		case EnumDish.DESSERT:
			DishDessert dishDess = new DishDessert(dishName, false, dishPrice, dishMenuID);
			dishDess.setDishType(dishTypePick);
			createdDish = dishDess;
			break;
		case EnumDish.MAIN_COURSE:
			DishMainCourse dishMainC = new DishMainCourse(dishName, isGrill, dishPrice, dishMenuID);
			dishMainC.setDishType(dishTypePick);
			createdDish = dishMainC;
			break;
		case EnumDish.APPETIZER:
			DishAppetizer dishApptzr = new DishAppetizer(dishName, false, dishPrice, dishMenuID);
			dishApptzr.setDishType(dishTypePick);
			createdDish = dishApptzr;
			break;
		}
		if(createdDish!=null)
			client.addDish(createdDish);
		else
			showError("This dish type is not exist");
		
	}
    //Making Quit Button to kill thread and send message to server
    public void closeApplication() {
        if (client != null) {
        	client.userLogout(user);
            }
        Platform.exit();
        System.exit(0);
    }   
    
    private int UserHomeBranchConvertToInt(EnumBranch enumGet)
    {
    	switch(user.getHomeBranch())
    	{
    	case NORTH:
    		return 1;
    	case CENTER:
    		return 2;
    	case SOUTH:
    		return 3;	
    	default:
    		return 0;
    	}
    }
    
    private boolean CheckIfLegalNameAndPrice(String name, String price) {
        // Case 1: Name is empty
        if (name == null || name.trim().isEmpty()) {
            showError("Dish name cannot be empty.");
            return false;
        }

        // Case 2: Name contains invalid characters (only allows letters, numbers, and spaces)
        if (!name.matches("[a-zA-Z0-9 ]+")) {
            showError("Dish name can only contain letters, numbers, -, and spaces.");
            return false;
        }

        // Case 3: Price is empty
        if (price == null || price.trim().isEmpty()) {
            showError("Price cannot be empty.");
            return false;
        }

        // Case 4: Price contains non-numeric characters (only allows numbers)
        if (!price.matches("\\d+(\\.\\d{1,2})?")) {
            showError("Price must be a number with up to 2 decimal places.");
            return false;
        }

        // All checks passed, input is valid
        return true;
    }

    
    private void showError(String errText) {
    	errorText.setText(errText);
    	errorText.setVisible(true);
    }
	
  
    
}

