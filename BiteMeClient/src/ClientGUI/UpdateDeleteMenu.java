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
    private ComboBox<?> optionalsComboBox;

    @FXML
    private ComboBox<?> grillComboBox;

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
        
        
    }
    
   
    
    public void setMenuDishes(List<Dish> chosenItemsFromMenu) {
    	this.chosenItemsFromMenu = chosenItemsFromMenu; 

    }

    public void setUser(User user) {
    	this.user=user;
    	
    	client.getViewMenu(EnumServerOperations.MENU_FOR_UPDATE,UserHomeBranchConvertToInt(user.getHomeBranch()));
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
        // Handle back button action
    }

    @FXML
    private void handleSaveButtonAction() {
    	showError("hi "+ chosenItemsFromMenu.size());
    	menuTableView.getItems().addAll(chosenItemsFromMenu);
    }

    @FXML
    private void handleDeleteButtonAction() {
        // Handle delete button action
    }
    
    private void showError(String errText) {
    	errorText.setText(errText);
    	errorText.setVisible(true);
    }

}