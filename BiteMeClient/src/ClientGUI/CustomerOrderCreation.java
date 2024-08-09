package ClientGUI;
import common.Restaurant.Location;
import common.User;
import common.EnumDish;
import common.EnumServerOperations;
import common.Dish;
import common.DishAppetizer;
import common.DishBeverage;
import common.DishDessert;
import common.DishMainCourse;
import common.DishSalad;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import client.Client;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.Callback;

public class CustomerOrderCreation {	
	private Client client;
	private User user = null;
	public List<Dish> tempMenuFromDB = new ArrayList<>(); 
	public List<Dish> tempChosenItemsFromMenu = new ArrayList<>(); 
	public List<Dish> ChosenItemsFromMenu = new ArrayList<>(); 
    @FXML
    private ComboBox<Location> branchComboBox;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private TableView<Dish> menuTableView;
    @FXML
    private TableColumn<Dish, String> nameColumn;
    @FXML
    private TableColumn<Dish, Double> priceColumn;
    @FXML
    private TableColumn<Dish, String> optionalsColumn;
    @FXML
    private TableColumn<Dish, String> commentsColumn;     
    @FXML
    private TableView<Dish> ChosenItemsTableView;
    @FXML
    private TableColumn<Dish, String> ChosenItemsnameColumn;
    @FXML
    private TableColumn<Dish, Double> ChosenItemspriceColumn;
    @FXML
    private TableColumn<Dish, String> ChosenItemsoptionalsColumn;
    @FXML
    private TableColumn<Dish, String> ChosenItemscommentsColumn; 
    @FXML
    private Text errorText;
    @FXML
    private Button continueButton;
    @FXML
	private Button backButton;
    
  
    @FXML
    private void initialize() {
    	// Initialize the client
        client = Client.getInstance();
        client.setCustomerOrderCreation(this);
        branchComboBox.getItems().addAll(Location.values());
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
    
    
    //Set the user instance from the UI 
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
        	if(ChosenItemsFromMenu.size()==0) {
            branchComboBox.setValue(UserHomeBranchToRestaurantBranch(user));
            Location homeBranch = UserHomeBranchToRestaurantBranch(user);
            branchComboBox.setValue(homeBranch);
            handleBranchSelection(); // Call to load the menu for the home branch
        	}
        	else {
        		Location returnLocation = convertNumToLocation(ChosenItemsFromMenu.get(0).getMenuId());
                branchComboBox.setValue(returnLocation);
                ChosenItemsTableView.getItems().addAll(ChosenItemsFromMenu);
            	client.getViewMenu(EnumServerOperations.VIEW_MENU, ChosenItemsFromMenu.get(0).getMenuId());
        	}
        }
        else
        {   branchComboBox.setValue(Location.SOUTH); // or any default location
    		showError("NO HOME BRANCH FOUND FOR USER");        
        }            
    }
    
    
    private Location convertNumToLocation(int menuID) {
    	switch(menuID)
    	{
    	case 1:
    		return Location.NORTH;
    	case 2:
    		return Location.CENTER;
    	case 3:
    		return Location.SOUTH;	
    	default:
    		return Location.NORTH;
    	}
    }
    	
    //Set the default home branch of the viewer as a default selected branch when ordering
    private Location UserHomeBranchToRestaurantBranch(User user)
    {
    	switch(user.getHomeBranch())
    	{
    	case NORTH:
    		return Location.NORTH;
    	case CENTER:
    		return Location.CENTER;
    	case SOUTH:
    		return Location.SOUTH;	
    	default:
    		return Location.NORTH;
    	}
    }
    
    // update the menu based on the chosen branch
    public void handleBranchSelection() {

    	int MenuID=0;
        Location selectedBranch = branchComboBox.getValue();
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
    
    // Handles the category selection part 
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

    // Save Selections button. shows a table of all the picked items from the menu
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

   //shows the selected items from the menu on the list at the bottom of the screen
    private void updateSelectedItemsListView() { 	          
    	ChosenItemsTableView.getItems().addAll(tempChosenItemsFromMenu); 
    	ChosenItemsFromMenu.addAll(tempChosenItemsFromMenu); //The list of dishes that will continue with us until the Checkout   
	}
        
    //This one lets a user delete an item from the order list by clicking on it
    private void handleDeleteSelectedItemWithClick() {
    	Dish dishToDelete = ChosenItemsTableView.getSelectionModel().getSelectedItem();
    	ChosenItemsFromMenu.remove(dishToDelete);
    	ChosenItemsTableView.getItems().remove(dishToDelete);              
    }
    
    //Set the list of items chosen from the menu, to be saved until checkout
    public void setDishesCount(List<Dish> ChosenItemsFromMenu) {
        this.ChosenItemsFromMenu = ChosenItemsFromMenu;
    }
    
    //Continues to the choosing supply method part, with a list of the items we chose
	@FXML
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
	
	// for the client's backend. sets up the tempMenuFromDB's relevant branch information 
	public void SettempMenuFromDB(List<Dish> DBlist)
	{
		tempMenuFromDB.clear();                            	 
		tempMenuFromDB = DBlist; 
	}
	// Goes back to the user's home page
	@FXML
    private void handleBackButtonAction() {			
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
    
  //Change Error text and make it visible, appear under continue button
  	private void showError(String str) {
  		errorText.setText(str);
  		errorText.setVisible(true);
  	}   
    
  //Making Quit Button to kill thread and send message to server
    public void closeApplication() {
        if (client != null) {
            client.quit();
        }
        Platform.exit();
        System.exit(0);
    }      
}