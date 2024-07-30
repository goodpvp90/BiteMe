package ClientGUI;

import common.Restaurant.Location; 
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CustomerOrderCreation {

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

    private HashMap<Dish, Integer> selectedDishesCount = new HashMap<>();

    @FXML
    private void initialize() {
        // Populate the branchComboBox with locations
        branchComboBox.getItems().addAll(Location.values());
        branchComboBox.setPromptText("Select a branch");
        branchComboBox.setValue(Location.NORTH); // Set default selection to NORTH

        // Populate the categoryComboBox with categories
        categoryComboBox.setItems(FXCollections.observableArrayList("Appetizer", "Main Course", "Beverage", "Dessert"));
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
    }

    @FXML
    private void handleCategorySelection() {
        String selectedCategory = categoryComboBox.getValue();
        menuTableView.getItems().clear();

        // Load menu items based on the selected category
        switch (selectedCategory) {
            case "Appetizer":
                menuTableView.getItems().addAll(
                    new Dish("Salad", 5.0, "S/L"),
                    new Dish("Spring Rolls", 6.0, "S/L"),
                    new Dish("Bruschetta", 4.5, "S/L")
                );
                break;
            case "Main Course":
                menuTableView.getItems().addAll(
                    new Dish("Steak", 15.0, "R/M/WD"),
                    new Dish("Pasta", 12.0, "N/A"),
                    new Dish("Pizza", 10.0, "N/A")
                );
                break;
            case "Beverage":
                menuTableView.getItems().addAll(
                    new Dish("Soda", 2.0, "S/L"),
                    new Dish("Water", 1.0, "N/A"),
                    new Dish("Juice", 3.0, "S/L")
                );
                break;
            case "Dessert":
                menuTableView.getItems().addAll(
                    new Dish("Cake", 4.0, "N/A"),
                    new Dish("Ice Cream", 3.5, "N/A"),
                    new Dish("Pie", 4.5, "N/A")
                );
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

        // Prepare the updated list of selected item names to display
        updateSelectedItemsListView();
    }

    private void updateSelectedItemsListView() {
        // Prepare the updated list of selected item names to display
        List<String> selectedNames = new ArrayList<>();
        for (Map.Entry<Dish, Integer> entry : selectedDishesCount.entrySet()) {
            Dish dish = entry.getKey();
            int count = entry.getValue();
            selectedNames.add(dish.getName() + " - $" + dish.getPrice() + " (x" + count + ")");
        }

        // Update the ListView with selected dish names and counts
        selectedItemsListView.setItems(FXCollections.observableArrayList(selectedNames)); // Display selected items in the ListView

        System.out.println("Selected dishes: " + selectedDishesCount); // Print the selected dishes to the console
    }

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
                if (dish.getName().equals(dishName) && dish.getPrice() == price) {
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

    @FXML
    private void handleContinueAction(javafx.event.ActionEvent event) throws IOException {
        System.out.println("Continue button clicked");
        Parent root = FXMLLoader.load(getClass().getResource("CustomerOrderGatherSelection.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleBackButtonAction() {
        System.out.println("Back button clicked");
        // Implement your back navigation logic here
    }

    // Inner class to represent a dish
    public static class Dish {
        private String name;
        private double price;
        private String optionals;

        public Dish(String name, double price, String optionals) {
            this.name = name;
            this.price = price;
            this.optionals = optionals;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public String getOptionals() {
            return optionals;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Dish)) return false;
            Dish dish = (Dish) obj;
            return Double.compare(dish.price, price) == 0 && name.equals(dish.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, price);
        }
    }
}
