package QualifiedWorkerMenuUpdateProcess;

import java.util.ArrayList;
import java.util.List;

import client.Client;
import enums.EnumBranch;
import enums.EnumDish;
import enums.EnumServerOperations;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import restaurantEntities.Dish;
import userEntities.User;


/**
 * The UpdateDeleteMenu class handles the UI and logic for updating and deleting dishes from the menu.
 */
public class UpdateDeleteMenu {

	/** Button to navigate back to the previous menu. */
    @FXML
    private Button backButton;

    /** Button to save changes made to a selected dish. */
    @FXML
    private Button saveButton;

    /** Button to delete a selected dish. */
    @FXML
    private Button deleteButton;

    /** TableView to display the list of dishes. */
    @FXML
    private TableView<Dish> menuTableView;

    /** TableColumn to display the type of the dish. */
    @FXML
    private TableColumn<Dish, String> typeColumn;
    
    /** TableColumn to display the name of the dish. */
    @FXML
    private TableColumn<Dish, String> nameColumn;

    /** TableColumn to display the price of the dish. */
    @FXML
    private TableColumn<Dish, Double> priceColumn;

    /** TableColumn to display the optional items for the dish. */
    @FXML
    private TableColumn<Dish, String> optionalsColumn;

    /** TableColumn to indicate if the dish is grilled. */
    @FXML
    private TableColumn<Dish, Boolean> grillColumn;

    /** TextField to input or display the name of the dish. */
    @FXML
    private TextField nameField;

    /** TextField to input or display the price of the dish. */
    @FXML
    private TextField priceField;

    /** ComboBox to select whether the dish is grilled or not. */
    @FXML
    private ComboBox<String> grillComboBox;

    /** Text label for the dish name field. */
    @FXML
    private Text nameText;

    /** Text label for the dish price field. */
    @FXML
    private Text priceText;

    /** Text label for the grill option field. */
    @FXML
    private Text grillText;
    
    /** Text label to display error messages. */
    @FXML
    private Text errorText;
    
    /** Client instance for interacting with the server. */
    private Client client;
    
    /** User instance representing the current logged-in user. */
    private User user;
    
    /** Flag to indicate if the delete operation was successful. */
    private boolean successDelete;
    
    /** Flag to indicate if the edit operation was successful. */
    private boolean successEdit;
    
    /** List of dishes chosen from the menu. */
	public List<Dish> chosenItemsFromMenu = new ArrayList<>();
	
    /** Flag to indicate which operation was chosen (false for delete, true for edit). */
	private boolean whichOptionChoosed = false;
	
    /** Previous dish name for backup in case an update fails. */
	private String prevDishName;

    /** Previous dish price for backup in case an update fails. */
	private double prevDishPrice;

    /** Previous grill status for backup in case an update fails. */
	private boolean prevIsGrill;


	/** 
     * Initializes the UpdateDeleteMenu controller. 
     * Sets up the table columns, initializes the Client instance, and adds listeners for UI interactions.
     */
    @FXML
    private void initialize() {
    	client = Client.getInstance();
    	client.getInstanceOfUpdateDeleteMenu(this);
    	typeColumn.setCellValueFactory(new PropertyValueFactory<>("dishType"));
    	nameColumn.setCellValueFactory(new PropertyValueFactory<>("dishName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        optionalsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(formatOptionals(cellData.getValue().getOptionals())));
        grillColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isGrill()));
        grillColumn.setCellFactory(column -> new TableCell<Dish, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Yes" : "No");
                }
            }
        });        
        menuTableView.getItems().clear();
        grillComboBox.getItems().addAll("No", "Yes");
        resetFields();
        menuTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        	errorText.setVisible(false);
            if (newValue != null) {
                Dish selectedDish = (Dish) newValue;
                nameField.setText(selectedDish.getDishName());
                priceField.setText(String.valueOf(selectedDish.getPrice()));
            	String griilOrNot = (selectedDish.isGrill()?"Yes":"No");
            	grillComboBox.setValue(griilOrNot);
                if(selectedDish.getDishType()==EnumDish.MAIN_COURSE) {
                	grillText.setVisible(true);
                	grillComboBox.setVisible(true);
                }
                else {
                	grillText.setVisible(false);
                	grillComboBox.setVisible(false);
                }    	
            }  
        });
    }
    
    /**
     * Resets the input fields and ComboBox to their default values.
     */
    private void resetFields() {
        nameField.setText("");
        priceField.setText("");
        grillComboBox.setValue("No");
        grillText.setVisible(false);
        grillComboBox.setVisible(false);
    }
    
    /**
     * Formats the list of optionals into a comma-separated string.
     * 
     * @param optionals List of optionals.
     * @return Comma-separated string of optionals.
     */
    private String formatOptionals(ArrayList<String> optionals) {
        if (optionals == null || optionals.isEmpty()) {
            return "";
        }
        return String.join(", ", optionals);
    }
    
    /**
     * Updates the menu table with the provided list of dishes and update the
     * menu dishes list on table view.
     * 
     * @param dishes List of dishes to display in the table.
     */
    public void updateMenuTable(List<Dish> dishes) {
        Platform.runLater(() -> {
            menuTableView.getItems().clear();
            menuTableView.getItems().addAll(dishes);
            resetFields();
            menuTableView.getSortOrder().add(typeColumn);
            typeColumn.setSortType(TableColumn.SortType.ASCENDING);
        });
    }
    
    /**
     * Sets the list of dishes chosen from the menu.
     * 
     * @param chosenItemsFromMenu List of chosen dishes.
     */
    public void setMenuDishes(List<Dish> chosenItemsFromMenu) {
    	this.chosenItemsFromMenu = chosenItemsFromMenu;
    }
    
    /**
     * Sets the current user and fetches the menu for updating.
     * 
     * @param user Current user.
     */
    public void setUser(User user) {
    	this.user=user;
    	client.getViewMenu(EnumServerOperations.MENU_FOR_UPDATE,UserHomeBranchConvertToInt(user.getHomeBranch()));
        Platform.runLater(() -> {
    	menuTableView.getItems().addAll(chosenItemsFromMenu);
    	menuTableView.getSortOrder().add(typeColumn);
        typeColumn.setSortType(TableColumn.SortType.ASCENDING);
        });
    }
    
    /**
     * Converts the user's home branch to an integer representation.
     * 
     * @param enumGet The EnumBranch value to convert.
     * @return Integer representation of the branch.
     */
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
    
    
    /**
     * Handles the action of the back button, navigating to the Update Menu Navigation Page.
     */
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

    /**
     * Updates the selected dish with the details entered, after validating the input.
     * If no dish is selected, an error message is displayed.
     */
    @FXML
    private void handleSaveButtonAction() {
    	whichOptionChoosed=true;
        Dish selectedDish = menuTableView.getSelectionModel().getSelectedItem();
        String name = nameField.getText();
        String price = priceField.getText();
        boolean grill = (grillComboBox.getValue().equals("Yes")?true:false);
        if (selectedDish != null ) {
        	if(checkIfLegalFieldsEdit(name,price)) {
				if (checkIfNotTheSameDetails(selectedDish, name, Double.valueOf(price), grill)) {
					// backup in case it didn't succeed
					prevDishName = selectedDish.getDishName();
					prevDishPrice = selectedDish.getPrice();
					prevIsGrill = selectedDish.isGrill();
					selectedDish.setDishName(name);
					selectedDish.setPrice(Double.valueOf(price));
					selectedDish.setGrill(grill);
					client.updateDish(selectedDish);
				} else
					return;
        	}
        	else
        		return;
        }
        else
        	showError("Please select a dish to edit");

    }
    
    /**
     * Checks if the details of the dish have been changed.
     * 
     * @param selectedDish The selected dish.
     * @param name The new dish name.
     * @param price The new dish price.
     * @param grill The new grill status.
     * @return true if details have changed, false otherwise.
     */
    private boolean checkIfNotTheSameDetails(Dish dish, String name, double price,boolean isGrill) {
		return !(dish.getDishName().equals(name) && dish.getPrice() == price && dish.isGrill() == isGrill);
    }

    /**
     * Validates the input fields for editing a dish.
     * 
     * @param name The entered dish name.
     * @param price The entered dish price.
     * @return true if fields are valid, false otherwise.
     */
    private boolean checkIfLegalFieldsEdit(String name, String price) {
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
        return true;
    }

    /**
     * Handles the action of the delete button, deleting the selected dish from the menu.
     */
    @FXML
    private void handleDeleteButtonAction() {
    	whichOptionChoosed=false;
        Dish selectedDish = menuTableView.getSelectionModel().getSelectedItem();
        if (selectedDish != null) {
            client.deleteDish(selectedDish);
        } else {
            showError("Please select a dish to delete.");
        }
    }
    
    
    /**
     * Sets the result received from the server of the delete operation and proceeds accordingly.
     * 
     * @param successDelete true if the delete operation was successful, false otherwise.
     */
    public void SetSuccessDelete(boolean successDelete) {
    	this.successDelete=successDelete;
    	Platform.runLater(() -> checkSuccessAndProceed());
    }
    
    /**
     * Sets the result received from the server of the edit operation and proceeds accordingly.
     * 
     * @param successEdit true if the edit operation was successful, false otherwise.
     */
    public void SetSuccessEdit(boolean successEdit) {
    	this.successEdit=successEdit;
    	Platform.runLater(() ->client.getViewMenu
    			(EnumServerOperations.MENU_FOR_UPDATE,UserHomeBranchConvertToInt(user.getHomeBranch())));
    	Platform.runLater(() -> checkSuccessAndProceed());

    }
    
    /**
     * Verifies the success of the last edit or delete operation, updates the UI accordingly,
     * and reverts the dish details to their previous state if the operation fails.
     */
	private void checkSuccessAndProceed() {
        Dish selectedDish = menuTableView.getSelectionModel().getSelectedItem();
		if (successDelete || successEdit) {
			if(successDelete) {
				showSuccessDialog("Delete Dish Complete","deleted");
				chosenItemsFromMenu.remove(selectedDish);
			}
			if(successEdit)
				showSuccessDialog("Edit Dish Complete","edited");
			updateMenuTable(chosenItemsFromMenu);
			successDelete=false;
			successEdit=false;
		} else {
			if(!successDelete && !whichOptionChoosed)
				showError("Failed to delete dish.");
			if(!successEdit && whichOptionChoosed) {
				showError("Failure! This name already exist under this selected category!");
				//return previous values before edit
		        selectedDish.setDishName(prevDishName);
	        	selectedDish.setPrice(prevDishPrice);
	        	selectedDish.setGrill(prevIsGrill);
			}
		}
	}

	/**
	 * Displays a success dialog with a given title and action content.
	 * 
	 * @param title The title of the dialog.
	 * @param contentAction The content describing the action performed (e.g., "deleted", "edited").
	 */
	private void showSuccessDialog(String title, String contentAction) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initStyle(StageStyle.UTILITY);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText("Dish " + contentAction + " successfully!");
		ButtonType okButton = new ButtonType("OK", ButtonData.OK_DONE);
		alert.getButtonTypes().setAll(okButton);
		alert.showAndWait().ifPresent(response -> {
			if (response == okButton) {
				alert.close();
			}
		});
	}
	
	/**
	 * Closes the application and logs out the user.
	 */
	public void closeApplication() {
		if (client != null) {
			client.userLogout(user, true);
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

}
