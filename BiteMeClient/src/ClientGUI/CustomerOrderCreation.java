package ClientGUI;
import common.Restaurant.Location;
import common.User;
import common.EnumDish;
import common.Dish;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
	private List<Dish> TemporaryMenuList = new ArrayList<>();
	private HashMap<Dish, Integer> selectedDishesCount = new HashMap<>();
    @FXML
    private ComboBox<Location> branchComboBox;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private TableView<Dish> menuTableView;
    @FXML
    private ListView<String> selectedItemsListView; 
    @FXML
    private TableColumn<Dish, String> nameColumn;
    @FXML
    private TableColumn<Dish, Double> priceColumn;
    @FXML
    private TableColumn<Dish, String> optionalsColumn;
    @FXML
    private TableColumn<Dish, String> commentsColumn;
    @FXML
    private Text errorText;
    @FXML
    private Button continueButton;
    
    //METHODS--------------------------------------------------------
    
    @FXML
    private void initialize() {
    	// Initialize the client
        client = Client.getInstance();
        client.setCustomerOrderCreation(this);
        // Populate the branchComboBox with locations
        branchComboBox.getItems().addAll(Location.values());
        branchComboBox.setPromptText("Select a branch");
        // Set up the listener for the ComboBox
        //branchComboBox.setOnAction(event -> handleBranchSelection());***************
        
        // Populate the categoryComboBox with categories
        categoryComboBox.setItems(FXCollections.observableArrayList(
        	    Arrays.stream(EnumDish.values())
        	          .map(EnumDish::toString) // Convert each enum to a string
        	          .collect(Collectors.toList())
        	));
        
        categoryComboBox.setPromptText("Select a category");
        // Setup table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        optionalsColumn.setCellValueFactory(new PropertyValueFactory<>("optionals"));
        // Enable multiple selection on the TableView
        menuTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);   
        // Populate the table with items based on the selected category
        menuTableView.getItems().clear();
        // Set up click listener for the ListView
        selectedItemsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                handleSelectedItemClick();
            }
        });  
  
        //if we returned from CustomerOrderGatherSelection show the view list
        if(selectedDishesCount.size()>0) {updateSelectedItemsListView(); }   
         
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
                    // Set the default value to the first element if available
                    if (!options.isEmpty()) {
                        comboBox.setValue(options.get(0));
                        //dish.setOptionalPick(options.get(0)); WILL FIX*****************
                    }
                    //comboBox.valueProperty().addListener((obs, oldVal, newVal) -> dish.setOptionalPick(newVal)); WILL FIX*****************
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
        optionalsColumn.setOnEditCommit(event -> {
            Dish dish = event.getRowValue();
            String selectedOption = event.getNewValue();
            // Handle the selected option (e.g., update the Dish object, perform any necessary actions)
        });  
        
        
     // Setup comments column
        commentsColumn.setCellValueFactory(new PropertyValueFactory<>("comments"));
        commentsColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        commentsColumn.setOnEditCommit(event -> {
            Dish dish = event.getRowValue();
            dish.setComments(event.getNewValue());
        });
        
        // Enable editing for the table
        menuTableView.setEditable(true);
    }

    //Set the user instance from the UI 
    public void setUser(User user) {
        this.user = user;
        //NEW
        if (user != null) {
            branchComboBox.setValue(UserHomeBranchToRestaurantBranch(user));
        }
        else
        {   branchComboBox.setValue(Location.SOUTH); // or any default location
    		showError("NO HOME BRANCH FOUND FOR USER");        
        }
    }
    
    // This one will update the menu based on the chosen branch
    public void handleBranchSelection(List<Dish> menu) {
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
            // Call the backend function to get the menu for the selected branch
        	client.getViewMenu(MenuID);
            List<Dish> menuItems = menu;              
            menuTableView.getItems().setAll(menuItems);
            // Clear the selected dishes count
            selectedDishesCount.clear();
            updateSelectedItemsListView();                  
            // Clear the category selection
            categoryComboBox.setValue(null);
        }
    }
  
    
    //Set the dishes hash map
    public void setDishesCount(HashMap<Dish, Integer> selectedDishesCount) {
        this.selectedDishesCount = selectedDishesCount;
        handleConfirmSelectionAction();
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

    @FXML
    private void handleCategorySelection() {
        String selectedCategory = categoryComboBox.getValue();
        menuTableView.getItems().clear();                    
        // Load menu items based on the selected category
        switch (selectedCategory) {
            case "APPETIZER":
            	menuTableView.getItems().addAll();
                break;
            case "SALAD":
            	menuTableView.getItems().addAll();
                break;
            case "MAIN_COURSE":
            	menuTableView.getItems().addAll();
                break;
            case "BEVERAGE":
            	menuTableView.getItems().addAll();
                break;
            case "DESSERT":
            	menuTableView.getItems().addAll();
                break;
        }
    }
	
    @FXML
    private void handleConfirmSelectionAction() {
        // Get all selected dishes
        ObservableList<Dish> selectedItems = menuTableView.getSelectionModel().getSelectedItems(); 

        // Update the count for selected dishes and display them in the ListView
        for (Dish dish : selectedItems) {
            selectedDishesCount.put(dish, selectedDishesCount.getOrDefault(dish, 0) + 1);
        }
    	errorText.setVisible(false);  	
        updateSelectedItemsListView();
    }
    
    //shows the selected items from the menu on the list at the bottom of the screen
    private void updateSelectedItemsListView() {
        // Prepare the updated list of selected item names to display
        List<String> selectedNames = new ArrayList<>();
        for (Map.Entry<Dish, Integer> entry : selectedDishesCount.entrySet()) {
            Dish dish = entry.getKey();
            int count = entry.getValue();           
            selectedNames.add(dish.getDishName() + " - $" + dish.getPrice() + " (x" + count + ")");
            
            //if (!dish.getOptionalPick().equals(""))
            //added getOptionalPick
           // selectedNames.add(dish.getDishName() +" "/+ dish.getOptionalPick()/ + " - $" + dish.getPrice() + " (x" + count + ")");
            //else
            //    selectedNames.add(dish.getDishName() + " - $" + dish.getPrice() + " (x" + count + ")");*/
        }
        // Update the ListView with selected dish names and counts
        selectedItemsListView.setItems(FXCollections.observableArrayList(selectedNames)); // Display selected items in the ListView
        System.out.println("Selected dishes: " + selectedDishesCount); // Print the selected dishes to the console
    }

    //This one lets a user delete an item from the order list by clicking on it
    private void handleSelectedItemClick() {  	
        String selectedItem = selectedItemsListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            // Extract the dish name and count from the selected item string
            String[] parts = selectedItem.split(" \\- \\$"); // Split by " - $"
            String dishName = parts[0];
            String[] countParts = parts[1].split(" \\(x");
            double price = Double.parseDouble(countParts[0]);
            int currentCount = Integer.parseInt(countParts[1].replace(")", ""));       
            // Find the corresponding Dish object
            Dish selectedDish = null;
            for (Dish dish : selectedDishesCount.keySet()) {
                if (dish.getDishName().equals(dishName) && dish.getPrice() == price) {
                    selectedDish = dish;
                    break;
                }
            }            
            // Decrease the count or remove the dish if count is 1
            if (selectedDish != null) {
                if (currentCount > 1) {
                    selectedDishesCount.put(selectedDish, currentCount - 1);
                } else {
                    selectedDishesCount.remove(selectedDish); // Remove the dish from the map
                }
            }
            // Update the ListView to reflect the changes
            updateSelectedItemsListView();           
        }
    }

    //	UNUSEABLE UNTIL DISH ISSUE IS DEALT WITH
	@FXML
	private void handleContinueAction(javafx.event.ActionEvent event) throws IOException {		
		if (selectedDishesCount.size() > 0) {
			CustomerOrderGatherSelectionUI COrderGatherApp = 
					new CustomerOrderGatherSelectionUI(user,selectedDishesCount);
			COrderGatherApp.start(new Stage());
			//close current window
			((Stage) continueButton.getScene().getWindow()).close();
		}
		else {
			showError("Error, please add at least ONE item to your order.");
		}
	}
	
	
	
	//Change Error text and make it visible, appear under continue button
	private void showError(String str) {
		errorText.setText(str);
		errorText.setVisible(true);
	}

	
	
	
	
    @FXML
    private void handleBackButtonAction() {
        System.out.println("Back button clicked");
        client.getViewMenu(1);
        if(user==null)
        	showError("Yep, user is fucking NULL here");
        else
        	showError("I guess user is NOT NULL");
    }
    
    
    
    
    
  //Making Quit Button to kill thread and send message to server
    public void closeApplication() {
        if (client != null) {
            System.out.println("Closing application from UserHomePage");
            client.quit();
        }
        Platform.exit();
        System.exit(0);
    }
    
  
    
    // Inner class to represent a dish
    /*public static class Dish {
        private String name;
        private double price;
        private String[] optionals;//changed to array
        private String optionalPick = "";//added
        private String comments; // New field for comments
        
        public Dish(String name, double price, String[] optionals) {
            this.name = name;
            this.price = price;
            this.optionals = optionals;
            this.comments = "Add Comment Here";
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public String[] getOptionals() {
            return optionals;
        }
     
        public String getOptionalPick() {
        	return optionalPick;
        }
        
        public String getComments() 
        {
        	return comments; 
    	}

        public void setOptionalPick(String optionalPick) {
        	this.optionalPick=optionalPick;
        }
        
        public void setComments(String comments) { 
        	this.comments = comments;
    	} 
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Dish)) return false;
            Dish dish = (Dish) obj;
            return Double.compare(dish.price, price) == 0 && name.equals(dish.name) && optionalPick.equals(dish.optionalPick);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(name, price, optionalPick);
        }
    }*/
    
	


}