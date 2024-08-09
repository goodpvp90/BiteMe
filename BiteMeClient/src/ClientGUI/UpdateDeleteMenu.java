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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import common.EnumDish;

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
    
	public List<Dish> chosenItemsFromMenu = new ArrayList<>(); 


    @FXML
    void initialize() {
        // Setup table columns
    	client = client.getInstance();
    	client.getInstanceOfUpdateDeleteMenu(this);
    	
    	typeColumn.setCellValueFactory(new PropertyValueFactory<>("dishType"));
    	nameColumn.setCellValueFactory(new PropertyValueFactory<>("dishName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        optionalsColumn.setCellValueFactory(new PropertyValueFactory<>("optionals"));
        //grillColumn.setCellValueFactory(new PropertyValueFactory<>("isGrill")); 
        
        grillComboBox.getItems().addAll("No", "Yes");
        grillComboBox.setValue("No");
        
        // Add a listener to handle row selection
        menuTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
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
        // Handle delete button action
    }
    
  //Making Quit Button to kill thread and send message to server
    public void closeApplication() {
        if (client != null) {
        	client.userLogout(user);
            }
        Platform.exit();
        System.exit(0);
    }   
    
    private void showError(String errText) {
    	errorText.setText(errText);
    	errorText.setVisible(true);
    }

}