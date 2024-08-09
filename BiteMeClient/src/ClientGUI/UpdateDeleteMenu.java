package ClientGUI;

import java.util.ArrayList;
import java.util.List;

import client.Client;
import common.Dish;
import common.EnumBranch;
import common.EnumDish;
import common.EnumServerOperations;
import common.User;
import common.Restaurant.Location;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

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
    private TextField optionalsField;

    @FXML
    private ComboBox<String> grillComboBox;

    @FXML
    private Text nameText;

    @FXML
    private Text priceText;

    @FXML
    private Text optionalsText;

    @FXML
    private Text grillText;
    
    @FXML
    private Text errorText;
    
    private Client client;
    
    private User user;
    
    private boolean successDelete;
    
	public List<Dish> chosenItemsFromMenu = new ArrayList<>(); 


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
        menuTableView.getSortOrder().add(typeColumn);
        typeColumn.setSortType(TableColumn.SortType.ASCENDING); // or DESCENDING for reverse order
        
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
                
                // Handle optionals
                if (selectedDish.getOptionals() != null && !selectedDish.getOptionals().isEmpty()) {
                    optionalsField.setText(formatOptionals(selectedDish.getOptionals()));
                } else {
                    optionalsField.setText(""); // Clear field if no optionals
                }
                
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
                	
            }  // Sort the table by the Type column upon opening

        });
        
    }
    
    //Reset the fill field and combo box to default values
    private void resetFields() {
    	// Set the TextFields to be empty initially
        nameField.setText("");
        priceField.setText("");
        optionalsField.setText("");
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
    	showError("hi "+ chosenItemsFromMenu.get(0).getOptionals());
    }

    @FXML
    private void handleDeleteButtonAction() {
        Dish selectedDish = menuTableView.getSelectionModel().getSelectedItem();
        if (selectedDish != null) {
			chosenItemsFromMenu.remove(selectedDish);
            client.deleteDish(selectedDish);
        } else {
            showError("Please select a dish to delete.");
        }
    }
    
    
    
    // Add this method or similar to handle server response
    public void SetSuccessDelete(boolean successDelete) {
    	this.successDelete=successDelete;
    	Platform.runLater(() -> checkDeleteSuccessAndProceed());
    }

	private void checkDeleteSuccessAndProceed() {
		if (successDelete) {
			showSuccessDeleteDialog();
			// Refresh the table view
			updateMenuTable(chosenItemsFromMenu);
			
		} else {
			showError("Failed to delete the dish.");
		}
	}

	private void showSuccessDeleteDialog() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initStyle(StageStyle.UTILITY);
		alert.setTitle("Added Dish Complete");
		alert.setHeaderText(null);
		alert.setContentText("Dish deleted successfully!");

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
        	client.userLogout(user);
            client.quit();
            }
        Platform.exit();
        System.exit(0);
    }   
    
    private void showError(String errText) {
    	errorText.setText(errText);
    	errorText.setVisible(true);
    }

}