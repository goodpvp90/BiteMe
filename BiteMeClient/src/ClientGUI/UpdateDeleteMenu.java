package ClientGUI;

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

public class UpdateDeleteMenu {

    @FXML
    private Button backButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TableView<Dish> menuTableView;

    @FXML
    private TableColumn<Dish, String> typeColumn;
    
    @FXML
    private TableColumn<Dish, String> nameColumn;

    @FXML
    private TableColumn<Dish, Double> priceColumn;

    @FXML
    private TableColumn<Dish, String> optionalsColumn;

    @FXML
    private TableColumn<Dish, Boolean> grillColumn;

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;


    @FXML
    private ComboBox<String> grillComboBox;

    @FXML
    private Text nameText;

    @FXML
    private Text priceText;


    @FXML
    private Text grillText;
    
    @FXML
    private Text errorText;
    
    private Client client;
    
    private User user;
    
    private boolean successDelete;
    
    private boolean successEdit;
    
	public List<Dish> chosenItemsFromMenu = new ArrayList<>();
	
	private boolean whichOptionChoosed = false; //false to delete true to edit
	
	//if update didn't succeed use this as backup
	private String prevDishName;
	private double prevDishPrice;
	private boolean prevIsGrill;


    @FXML
    private void initialize() {
        // Setup table columns
    	client = client.getInstance();
    	client.getInstanceOfUpdateDeleteMenu(this);
    	
    	typeColumn.setCellValueFactory(new PropertyValueFactory<>("dishType"));
    	nameColumn.setCellValueFactory(new PropertyValueFactory<>("dishName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        // Set up the optionalsColumn to display formatted optionals
        optionalsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(formatOptionals(cellData.getValue().getOptionals())));
        // Set up the grillColumn to show "Yes" or "No"
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
        //Sort Type column in table
        menuTableView.getItems().clear();
        
        
        grillComboBox.getItems().addAll("No", "Yes");
        
        resetFields();
        
        
        
        // Add a listener to handle row selection
        menuTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        	errorText.setVisible(false);
            if (newValue != null) { // Ensure newValue is not null
                Dish selectedDish = (Dish) newValue;
                // Update the TextFields with the selected dish details
                nameField.setText(selectedDish.getDishName());
                priceField.setText(String.valueOf(selectedDish.getPrice()));
                
                
                //Set the is grill value
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
    
    //Reset the fill field and combo box to default values
    private void resetFields() {
    	// Set the TextFields to be empty initially
        nameField.setText("");
        priceField.setText("");
        grillComboBox.setValue("No");
        
        // Hide the grill-related UI elements initially
        grillText.setVisible(false);
        grillComboBox.setVisible(false);
    }
    
    private String formatOptionals(ArrayList<String> optionals) {
        if (optionals == null || optionals.isEmpty()) {
            return "";
        }
        return String.join(", ", optionals);
    }
    
    public void updateMenuTable(List<Dish> dishes) {
        Platform.runLater(() -> {
            // Clear existing items and add the fetched dishes
            menuTableView.getItems().clear();
            menuTableView.getItems().addAll(dishes);
            resetFields();
            
            //sort Type column
            menuTableView.getSortOrder().add(typeColumn);
            typeColumn.setSortType(TableColumn.SortType.ASCENDING);
           
        });
    }
   
    public void setMenuDishes(List<Dish> chosenItemsFromMenu) {
    	this.chosenItemsFromMenu = chosenItemsFromMenu;
    	

    }

    public void setUser(User user) {
    	this.user=user;
    	client.getViewMenu(EnumServerOperations.MENU_FOR_UPDATE,UserHomeBranchConvertToInt(user.getHomeBranch()));
        Platform.runLater(() -> {
    	menuTableView.getItems().addAll(chosenItemsFromMenu);
    	menuTableView.getSortOrder().add(typeColumn);
        typeColumn.setSortType(TableColumn.SortType.ASCENDING); // or DESCENDING for reverse order
        });

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
    private void handleSaveButtonAction() {
    	whichOptionChoosed=true;
        Dish selectedDish = menuTableView.getSelectionModel().getSelectedItem();
        
        String name = nameField.getText();
        String price = priceField.getText();
        boolean grill = (grillComboBox.getValue().equals("Yes")?true:false);
        if (selectedDish != null && checkIfLegalFieldsEdit(name,price)) {
        	if(checkIfNotTheSameDetails(selectedDish,name,Double.valueOf(price),grill)) {
        	//backup in case it didn't succeed
        	prevDishName = selectedDish.getDishName();
        	prevDishPrice = selectedDish.getPrice();
        	prevIsGrill = selectedDish.isGrill();
        	selectedDish.setDishName(name);
        	selectedDish.setPrice(Double.valueOf(price));
        	selectedDish.setGrill(grill);
        	
        	client.updateDish(selectedDish);
        	}
        	else //do nothing, same details entered
        		return;
        		
        }
        else
        	showError("Please select a dish to edit");

    }
    
    //check if the details entered are different from the current dish details
    private boolean checkIfNotTheSameDetails(Dish dish, String name, double price,boolean isGrill) {
		return !(dish.getDishName().equals(name) && dish.getPrice() == price && dish.isGrill() == isGrill);
    }

    
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

        // All checks passed, input is valid
        return true;
    }

    @FXML
    private void handleDeleteButtonAction() {
    	whichOptionChoosed=false;
        Dish selectedDish = menuTableView.getSelectionModel().getSelectedItem();
        if (selectedDish != null) {
			chosenItemsFromMenu.remove(selectedDish);
            client.deleteDish(selectedDish);
        } else {
            showError("Please select a dish to delete.");
        }
    }
    
    
    
    public void SetSuccessDelete(boolean successDelete) {
    	this.successDelete=successDelete;
    	Platform.runLater(() -> checkSuccessAndProceed());
    }
    
    public void SetSuccessEdit(boolean successEdit) {
    	this.successEdit=successEdit;
    	
    	Platform.runLater(() ->client.getViewMenu
    			(EnumServerOperations.MENU_FOR_UPDATE,UserHomeBranchConvertToInt(user.getHomeBranch())));
    	
    	Platform.runLater(() -> checkSuccessAndProceed());

    }
    
    
	private void checkSuccessAndProceed() {
		if (successDelete || successEdit) {
			if(successDelete)
				showSuccessDialog("Delete Dish Complete","deleted");
			if(successEdit)
				showSuccessDialog("Edit Dish Complete","edited");
				
			// Refresh the table view
			updateMenuTable(chosenItemsFromMenu);
			successDelete=false;
			successEdit=false;
		} else {
			if(!successDelete && !whichOptionChoosed)
				showError("Failed to delete dish.");
			if(!successEdit && whichOptionChoosed) {
				showError("Failed to edit dish.");
			}
		}
	}

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
				alert.close(); // Close the dialog window
			}
		});
	}
	
  //Making Quit Button to kill thread and send message to server
    public void closeApplication() {
		if (client != null) {
			client.userLogout(user, true);
		}
    }   
    
    private void showError(String errText) {
    	errorText.setText(errText);
    	errorText.setVisible(true);
    }

}