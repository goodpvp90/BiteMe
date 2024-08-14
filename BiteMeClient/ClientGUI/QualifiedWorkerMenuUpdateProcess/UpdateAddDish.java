package QualifiedWorkerMenuUpdateProcess;

import client.Client;
import enums.EnumBranch;
import enums.EnumDish;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import restaurantEntities.Dish;
import restaurantEntities.DishAppetizer;
import restaurantEntities.DishBeverage;
import restaurantEntities.DishDessert;
import restaurantEntities.DishMainCourse;
import restaurantEntities.DishSalad;
import userEntities.User;

/**
 * The UpdateAddDish class is a JavaFX controller for the UI used to update or add a dish to the menu.
 * It handles user inputs for dish details, validates them, and communicates with the server to 
 * add the new dish. It also manages the visibility of UI components based on user selections.
 */
public class UpdateAddDish {

    /**
     * Button to navigate back to the previous screen.
     */
    @FXML
    private Button backButton;

    /**
     * Text element for displaying the "Add Dish" title.
     */
    @FXML
    private Text addDishText;

    /**
     * Text element for displaying the "Dish Type" label.
     */
    @FXML
    private Text dishTypeText;

    /**
     * Text element for displaying the "Dish Name" label.
     */
    @FXML
    private Text dishNameText;

    /**
     * Text element for displaying the "Price" label.
     */
    @FXML
    private Text priceText;

    /**
     * Text element for displaying the "Is Grill" label.
     */
    @FXML
    private Text isGrillText;

    /**
     * TextField for inputting the name of the dish.
     */
    @FXML
    private TextField dishNameField;

    /**
     * TextField for inputting the price of the dish.
     */
    @FXML
    private TextField priceField;

    /**
     * ComboBox for selecting whether the dish is grilled or not.
     */
    @FXML
    private ComboBox<String> isGrillComboBox;

    /**
     * ComboBox for selecting the type of dish.
     */
    @FXML
    private ComboBox<EnumDish> dishTypeComboBox;

    /**
     * Button to save the dish details.
     */
    @FXML
    private Button saveDishButton;

    /**
     * Text element for displaying error messages.
     */
    @FXML
    private Text errorText;

    /**
     * The user currently logged in and interacting with the UI.
     */
    private User user;

    /**
     * Client instance used to communicate with the server.
     */
    private Client client;

    /**
     * Indicates whether the addition of the dish was successful.
     */
    private boolean success;

    /**
     * Initializes the UI components for the UpdateAddDish screen.
     * Sets default values for dishTypeComboBox and isGrillComboBox.
     * Adds a listener to dishTypeComboBox to show or hide isGrillText and isGrillComboBox
     * based on the selected dish type.
     */
    @FXML
    private void initialize() {
        client = client.getInstance();
        client.getInstanceOfUpdateAddDish(this);
        dishTypeComboBox.getItems().setAll(EnumDish.values());
        dishTypeComboBox.setValue(EnumDish.APPETIZER); //Default selection
        isGrillComboBox.getItems().addAll("Yes", "No");
        isGrillComboBox.setValue("No");
        dishTypeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == EnumDish.MAIN_COURSE) {
                isGrillText.setVisible(true);
                isGrillComboBox.setVisible(true);                
            } else {
                isGrillText.setVisible(false);
                isGrillComboBox.setVisible(false);
            }
        });
        //Initially hide isGrillText and isGrillComboBox if not MAIN_COURSE
        if (dishTypeComboBox.getValue() != EnumDish.MAIN_COURSE) {
            isGrillText.setVisible(false);
            isGrillComboBox.setVisible(false);
        }
    }

    /**
     * Sets the user for this UI.
     *
     * @param user the user currently logged in
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Sets the result of the dish addition operation and updates the UI accordingly.
     *
     * @param success true if the dish was added successfully; false otherwise
     */
    public void setSucceededAdd(boolean success) {
        this.success = success;
        Platform.runLater(() -> checkSuccessAndProceed());
    }

    /**
     * Handles the action of the back button. Navigates to the update menu navigation page.
     */
    @FXML
    private void handleBackButtonAction() {   
        try {
            UpdateMenuNavigationUI userApp = new UpdateMenuNavigationUI(user);
            userApp.start(new Stage());
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred while loading the Update Menu Navigation Page.");
        }
    }

    /**
     * Handles the action of the save dish button. Validates input and saves the dish.
     */
    @FXML
    private void handleSaveDishAction() {
        String dishName = dishNameField.getText();
        String price = priceField.getText();
        boolean isGrill = "Yes".equals(isGrillComboBox.getValue());
        if (!CheckIfLegalNameAndPrice(dishName, price)) {
            return;
        }
        errorText.setVisible(false);
        int dishMenuID = UserHomeBranchConvertToInt(user.getHomeBranch());
        addDishToDB(dishName, Double.parseDouble(price), dishMenuID, isGrill);
    }

    /**
     * Checks if the dish addition was successful and updates the UI accordingly.
     */
    private void checkSuccessAndProceed() {
        if (!success) {
            showError("Adding new dish failed: Dish already exists.");
        } else {
            showSuccessDialog();
            resetFields();
        }
    }

    /**
     * Shows a dialog indicating that the dish was successfully added.
     */
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
                alert.close();
            }
        });
    }

    /**
     * Resets the input fields and ComboBoxes to their default values.
     */
    private void resetFields() {
        dishNameField.clear();
        priceField.clear();
        dishTypeComboBox.setValue(EnumDish.APPETIZER);
        isGrillComboBox.setValue("No"); 
        if (dishTypeComboBox.getValue() != EnumDish.MAIN_COURSE) {
            isGrillText.setVisible(false);
            isGrillComboBox.setVisible(false);
        }
    }

    /**
     * Adds a dish to the database based on user input.
     *
     * @param dishName the name of the dish
     * @param dishPrice the price of the dish
     * @param dishMenuID the ID of the menu to which the dish belongs
     * @param isGrill whether the dish is grilled
     */
    private void addDishToDB(String dishName, double dishPrice, int dishMenuID, boolean isGrill) {
        EnumDish dishTypePick = dishTypeComboBox.getValue();
        Dish createdDish = null;
        switch (dishTypePick) {
            case SALAD:
                DishSalad dishSalad = new DishSalad(dishName, false, dishPrice, dishMenuID);
                dishSalad.setDishType(dishTypePick);
                createdDish = dishSalad;
                break;
            case BEVERAGE:
                DishBeverage dishBev = new DishBeverage(dishName, false, dishPrice, dishMenuID);
                dishBev.setDishType(dishTypePick);
                createdDish = dishBev;
                break;
            case DESSERT:
                DishDessert dishDess = new DishDessert(dishName, false, dishPrice, dishMenuID);
                dishDess.setDishType(dishTypePick);
                createdDish = dishDess;
                break;
            case MAIN_COURSE:
                DishMainCourse dishMainC = new DishMainCourse(dishName, isGrill, dishPrice, dishMenuID);
                dishMainC.setDishType(dishTypePick);
                createdDish = dishMainC;
                break;
            case APPETIZER:
                DishAppetizer dishApptzr = new DishAppetizer(dishName, false, dishPrice, dishMenuID);
                dishApptzr.setDishType(dishTypePick);
                createdDish = dishApptzr;
                break;
        }
        if (createdDish != null) {
            client.addDish(createdDish);
        } else {
            showError("This dish type does not exist");
        }
    }

    /**
     * Converts the user's home branch to an integer representation.
     *
     * @param enumGet the user's home branch
     * @return an integer representing the branch: 1 for NORTH, 2 for CENTER, 3 for SOUTH, 0 for unknown
     */
    private int UserHomeBranchConvertToInt(EnumBranch enumGet) {
        switch (enumGet) {
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
     * Checks if the dish name and price are valid.
     *
     * @param name the name of the dish
     * @param price the price of the dish
     * @return true if the name and price are valid; false otherwise
     */
    private boolean CheckIfLegalNameAndPrice(String name, String price) {
        if (name == null || name.trim().isEmpty()) {
            showError("Dish name cannot be empty.");
            return false;
        }
        if (!name.matches("[a-zA-Z0-9 ]+")) {
            showError("Dish name can only contain letters, numbers, and spaces.");
            return false;
        }
        if (price == null || price.trim().isEmpty()) {
            showError("Price cannot be empty.");
            return false;
        }
        if (!price.matches("\\d+(\\.\\d{1,2})?")) {
            showError("Price must be a number with up to 2 decimal places.");
            return false;
        }
        return true;
    }

    /**
     * Displays an error message in the UI.
     *
     * @param errText the error message to display
     */
    private void showError(String errText) {
        errorText.setText(errText);
        errorText.setVisible(true);
    }

    /**
     * Closes the application and logs out the user.
     */
    public void closeApplication() {
        if (client != null) {
            client.userLogout(user, true);
        }
    }
}
