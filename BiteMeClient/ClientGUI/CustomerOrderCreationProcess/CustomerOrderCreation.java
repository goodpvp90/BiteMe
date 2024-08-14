package CustomerOrderCreationProcess;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import UserHomePageProcess.UserHomePageUI;
import client.Client;
import enums.EnumBranch;
import enums.EnumDish;
import enums.EnumServerOperations;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import restaurantEntities.Dish;
import restaurantEntities.DishAppetizer;
import restaurantEntities.DishBeverage;
import restaurantEntities.DishDessert;
import restaurantEntities.DishMainCourse;
import restaurantEntities.DishSalad;
import userEntities.User;

/**
 * Manages the order creation process for customers, allowing them to select dishes
 * from a menu and customize their order.
 */
public class CustomerOrderCreation {
	
    /** The client instance used for communication with the server. */
    private Client client;
    
    /** The user associated with this order creation session. */
    private User user = null;
    
    /** Temporary storage for the menu items retrieved from the database. */
    public List<Dish> tempMenuFromDB = new ArrayList<>(); 
    
    /** Temporary storage for items chosen by the user from the menu. */
    public List<Dish> tempChosenItemsFromMenu = new ArrayList<>(); 
    
    /** Storage for items chosen by the user to be added to the final order. */
    public List<Dish> ChosenItemsFromMenu = new ArrayList<>(); 
    
    /** ComboBox for selecting the branch where the order will be placed. */
    @FXML
    private ComboBox<EnumBranch> branchComboBox;
    
    /** ComboBox for selecting the category of dishes to display. */
    @FXML
    private ComboBox<String> categoryComboBox;
    
    /** TableView for displaying the menu items available for selection. */
    @FXML
    private TableView<Dish> menuTableView;
    
    /** TableColumn for displaying the name of the dish in the menu. */
    @FXML
    private TableColumn<Dish, String> nameColumn;
    
    /** TableColumn for displaying the price of the dish in the menu. */
    @FXML
    private TableColumn<Dish, Double> priceColumn;
    
    /** TableColumn for displaying optional choices for a dish in the menu. */
    @FXML
    private TableColumn<Dish, String> optionalsColumn;
    
    /** TableColumn for displaying comments for a dish in the menu. */
    @FXML
    private TableColumn<Dish, String> commentsColumn;     
    
    /** TableView for displaying the items chosen by the user for the order. */
    @FXML
    private TableView<Dish> ChosenItemsTableView;
    
    /** TableColumn for displaying the name of the chosen dish. */
    @FXML
    private TableColumn<Dish, String> ChosenItemsnameColumn;
    
    /** TableColumn for displaying the price of the chosen dish. */
    @FXML
    private TableColumn<Dish, Double> ChosenItemspriceColumn;
    
    /** TableColumn for displaying the optional choices of the chosen dish. */
    @FXML
    private TableColumn<Dish, String> ChosenItemsoptionalsColumn;
    
    /** TableColumn for displaying comments for the chosen dish. */
    @FXML
    private TableColumn<Dish, String> ChosenItemscommentsColumn; 
    
    /** Text element for displaying error messages to the user. */
    @FXML
    private Text errorText;
    
    /** Button to proceed to the next step in the order process. */
    @FXML
    private Button continueButton;
    
    /** Button to return to the previous screen. */
    @FXML
    private Button backButton;
    
    /**
     * Initializes the order creation screen, setting up the ComboBoxes, TableViews,
     * and event handlers.
     */
    @FXML
    private void initialize() {
    	// Initialize the client
        client = Client.getInstance();
        client.setCustomerOrderCreation(this);
        branchComboBox.getItems().addAll(EnumBranch.values());
        branchComboBox.setPromptText("Select a branch");
        branchComboBox.setOnAction(event -> handleBranchSelection());
        categoryComboBox.setItems(FXCollections.observableArrayList(
    	    Arrays.stream(EnumDish.values())
    	          .map(EnumDish::toString) 
    	          .collect(Collectors.toList())
    	));       
        categoryComboBox.setPromptText("Select a category");
        // Setup table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("dishName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        optionalsColumn.setCellValueFactory(new PropertyValueFactory<>("optionals")); 
        commentsColumn.setCellValueFactory(new PropertyValueFactory<>("comments")); 
        ChosenItemsnameColumn.setCellValueFactory(new PropertyValueFactory<>("dishName"));
        ChosenItemspriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        ChosenItemsoptionalsColumn.setCellValueFactory(new PropertyValueFactory<>("optionalPick"));
        ChosenItemscommentsColumn.setCellValueFactory(new PropertyValueFactory<>("comments"));
        // Populate the table with items based on the selected category
        menuTableView.getItems().clear();             
        // Set up ComboBoxTableCell for optionalsColumn
        optionalsColumn.setCellFactory(column -> new ComboBoxTableCell<Dish, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Dish dish = getTableView().getItems().get(getIndex());
                    ObservableList<String> options = FXCollections.observableArrayList(dish.getOptionals());
                    ComboBox<String> comboBox = new ComboBox<>(options); 
                    comboBox.setValue(dish.getOptionalPick());
                    comboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                        dish.setOptionalPick(newVal);
                    });
                    // Set the default value to the first element if available
                    if (!options.isEmpty()) {
                        comboBox.setValue(options.get(0));
                    }
                    setGraphic(comboBox);
                    setText(null); // Ensure no text is displayed beside the ComboBox
                }
            }
        });
        optionalsColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Dish, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Dish, String> param) {
                Dish dish = param.getValue();
                ObservableList<String> options = FXCollections.observableArrayList(dish.getOptionals());
                return new SimpleStringProperty(options.isEmpty() ? "" : options.get(0)); // Default to first option if available
            }
        });                        
        commentsColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        commentsColumn.setOnEditCommit(new EventHandler<CellEditEvent<Dish, String>>() {
            @Override
            public void handle(CellEditEvent<Dish, String> event) {
                Dish dish = event.getRowValue();
                dish.setComments(event.getNewValue());
            }
        });        
        ChosenItemsTableView.setRowFactory(tv -> {
            TableRow<Dish> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    handleDeleteSelectedItemWithClick();
                }
            });
            return row;
        });
        menuTableView.setEditable(true);
    }
    
    
    /**
     * Sets the user instance associated with this order creation session.
     * Also set the combo box values according to user details
     * 
     * @param user The user to set.
     */
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
        	if(ChosenItemsFromMenu.size()==0) {
            branchComboBox.setValue(UserHomeBranchToRestaurantBranch(user));
            EnumBranch homeBranch = UserHomeBranchToRestaurantBranch(user);
            branchComboBox.setValue(homeBranch);
            handleBranchSelection(); // Call to load the menu for the home branch
        	}
        	else {
        		EnumBranch returnLocation = convertNumToLocation(ChosenItemsFromMenu.get(0).getMenuId());
                branchComboBox.setValue(returnLocation);
                ChosenItemsTableView.getItems().addAll(ChosenItemsFromMenu);
            	client.getViewMenu(EnumServerOperations.VIEW_MENU, ChosenItemsFromMenu.get(0).getMenuId());
        	}
        }
        else
        {   branchComboBox.setValue(EnumBranch.SOUTH); // or any default location
    		showError("NO HOME BRANCH FOUND FOR USER");        
        }            
    }
    
    /**
     * Converts a menu ID to the corresponding branch location.
     * 
     * @param menuID The ID of the menu.
     * @return The corresponding EnumBranch value.
     */
    private EnumBranch convertNumToLocation(int menuID) {
    	switch(menuID)
    	{
    	case 1:
    		return EnumBranch.NORTH;
    	case 2:
    		return EnumBranch.CENTER;
    	case 3:
    		return EnumBranch.SOUTH;	
    	default:
    		return EnumBranch.NORTH;
    	}
    }
    	
    
    /**
     * Maps the user's home branch to the corresponding restaurant branch.
     * 
     * @param user The user whose home branch is being mapped.
     * @return The corresponding EnumBranch value.
     */
    private EnumBranch UserHomeBranchToRestaurantBranch(User user)
    {
    	switch(user.getHomeBranch())
    	{
    	case NORTH:
    		return EnumBranch.NORTH;
    	case CENTER:
    		return EnumBranch.CENTER;
    	case SOUTH:
    		return EnumBranch.SOUTH;	
    	default:
    		return EnumBranch.NORTH;
    	}
    }
    
    /**
     * Handles the selection of a branch, updating an load the menu items based on the selected branch.
     */
    public void handleBranchSelection() {

    	int MenuID=0;
    	EnumBranch selectedBranch = branchComboBox.getValue();
        if (selectedBranch != null) {
        	switch (selectedBranch)
        	{
        	case NORTH:
        		MenuID = 1;
        		break;
        	case CENTER:
        		MenuID = 2;
        		break;
        	case SOUTH:
        		MenuID = 3;
        		break; 
        	}    

        	client.getViewMenu(EnumServerOperations.VIEW_MENU, MenuID);
        	ChosenItemsFromMenu.clear();                  
        	ChosenItemsTableView.getItems().clear();
            categoryComboBox.setValue(null);      
        }
    }
    
    /**
     * Handles the selection of a dish category, filtering the displayed menu items
     * based on the selected category in combo box selection.
     */
    @FXML
    private void handleCategorySelection() {
        String selectedCategory = categoryComboBox.getValue();
        menuTableView.getItems().clear();
        if (selectedCategory != null) {
            EnumDish categoryType = EnumDish.valueOf(selectedCategory);        
            List<Dish> filteredDishes = new ArrayList<>();
            for (Dish dish : tempMenuFromDB) {
                if (dish.getDishType() == categoryType) {
                	filteredDishes.add(dish);     
                	}
            }          
            menuTableView.getItems().addAll(filteredDishes);        
        }
    }

    /**
     * Handles the confirmation of selected items, adding them to the list of chosen items.
     */
    @FXML
    private void handleConfirmSelectionAction() {
    	errorText.setVisible(false);
    	if(menuTableView.getSelectionModel().getSelectedItem()==null)
    	{
    		showError("No item was picked from the menu!");
    		return;
    	}
    	// Get all selected dishes
    	Dish tempDish = menuTableView.getSelectionModel().getSelectedItem();
    	String updatedComment = tempDish.getComments();    	
    	if(updatedComment.equals("Add Comment Here"))
    		updatedComment = "";
    	tempDish.setComments("Add Comment Here");//Prevents the changed comment from being saved on the menu table
    	menuTableView.refresh();
    	String dishName = tempDish.getDishName();
    	double dishPrice = tempDish.getPrice();
    	int dishMenuID= tempDish.getMenuId();
    	int dishId = tempDish.getDishId();
    	boolean isGrill = tempDish.isGrill();
    	String dishOptPick = tempDish.getOptionalPick();
    	switch(tempDish.getDishType().toString())
    	{
    	case "SALAD":   
    		DishSalad dishSalad = new DishSalad(dishName,isGrill,dishPrice,dishMenuID);
    		dishSalad.setOptionalPick(dishOptPick);
    		dishSalad.setComments(updatedComment);
    		dishSalad.setDishId(dishId);
    		tempChosenItemsFromMenu.add(dishSalad);
    		break;
    	case "BEVERAGE":
    		DishBeverage dishBev = new DishBeverage(dishName,isGrill,dishPrice,dishMenuID);
    		dishBev.setOptionalPick(dishOptPick);
    		dishBev.setComments(updatedComment);
    		dishBev.setDishId(dishId);
    		tempChosenItemsFromMenu.add(dishBev);
    		break;
    	case "DESSERT":
    		DishDessert dishDess = new DishDessert(dishName,isGrill,dishPrice,dishMenuID);
    		dishDess.setOptionalPick(dishOptPick);
    		dishDess.setComments(updatedComment);
    		dishDess.setDishId(dishId);
    		tempChosenItemsFromMenu.add(dishDess);
    		break;
    	case "MAIN_COURSE":
    		DishMainCourse dishMainC = new DishMainCourse(dishName,isGrill,dishPrice,dishMenuID);
    		dishMainC.setOptionalPick(dishOptPick);
    		dishMainC.setComments(updatedComment);
    		dishMainC.setDishId(dishId);
    		tempChosenItemsFromMenu.add(dishMainC);
    		break;
    	case "APPETIZER":
    		DishAppetizer dishApptzr = new DishAppetizer(dishName,isGrill,dishPrice,dishMenuID);
    		dishApptzr.setOptionalPick(dishOptPick);
    		dishApptzr.setComments(updatedComment);
    		dishApptzr.setDishId(dishId);
    		tempChosenItemsFromMenu.add(dishApptzr);
    		break;   		
    	}
    	errorText.setVisible(false);  	
        updateSelectedItemsListView();
        tempChosenItemsFromMenu.clear();
    }

    /**
     * Updates the list of selected items displayed to the user.
     */
    private void updateSelectedItemsListView() { 	          
    	ChosenItemsTableView.getItems().addAll(tempChosenItemsFromMenu); 
    	ChosenItemsFromMenu.addAll(tempChosenItemsFromMenu); //The list of dishes that will continue with us until the Checkout   
	}
        
    /**
     * Handles the deletion of an item from the list of selected items when clicked.
     */
    private void handleDeleteSelectedItemWithClick() {
    	Dish dishToDelete = ChosenItemsTableView.getSelectionModel().getSelectedItem();
    	ChosenItemsFromMenu.remove(dishToDelete);
    	ChosenItemsTableView.getItems().remove(dishToDelete);              
    }
    
    /**
     * Sets the list of items chosen from the menu, to be saved until checkout.
     * 
     * @param ChosenItemsFromMenu The list of chosen dishes from the menu.
     */
    public void setDishesCount(List<Dish> ChosenItemsFromMenu) {
        this.ChosenItemsFromMenu = ChosenItemsFromMenu;
    }
    
    /**
     * Continues to the choosing supply method part with a list of the items we chose.
     * If no items are chosen, an error message is shown.
     * 
     * @param event The event that triggers this action.
     * @throws IOException If an I/O error occurs while loading the next scene.
     */	@FXML
	private void handleContinueAction(javafx.event.ActionEvent event) throws IOException {	
		if (ChosenItemsFromMenu.size() > 0) {
			CustomerOrderGatherSelectionUI COrderGatherApp = 
					new CustomerOrderGatherSelectionUI(user,ChosenItemsFromMenu);
			COrderGatherApp.start(new Stage());
			((Stage) continueButton.getScene().getWindow()).close();
		}
		else {
			showError("Error, please add at least ONE item to your order.");
		}
	}
	
     /**
      * Sets up the tempMenuFromDB with relevant branch information.
      * 
      * @param DBlist The list of dishes retrieved from the database.
      */
     public void SettempMenuFromDB(List<Dish> DBlist)
	{
		tempMenuFromDB.clear();                            	 
		tempMenuFromDB = DBlist; 
	}
	
     /**
      * Handles the action when the back button is clicked. 
      * Navigates back to the User Home Page UI.
      */
	@FXML
	private void handleBackButtonAction() {			
	    try {
	    	client.removeClientInOrder();
	        // Retrieve the existing stage for UserHomePageUI
	        Stage userHomePageStage = UserHomePageUI.getStage();

	        if (userHomePageStage != null) {
	            userHomePageStage.show();  // Show the hidden stage again
	        } else {
	            // If the stage is somehow null, recreate and show it
	            UserHomePageUI Userapp = new UserHomePageUI(user);
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
    
	/**
	 * Changes the error text and makes it visible.
	 * 
	 * @param str The error message to display.
	 */
  	private void showError(String str) {
  		errorText.setText(str);
  		errorText.setVisible(true);
  	}   
    
  	/**
  	 * Handles the Quit button action. 
  	 * Logs the user out, kills the thread, and sends a message to the server.
  	 */
  	public void closeApplication() {
		client.removeClientInOrder();
		if (client != null) {
			client.userLogout(user, true);
		}
	}      
}