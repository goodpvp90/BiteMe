package ManagerUserRegistrationProcess;

import java.io.IOException;

import client.Client;
import enums.EnumBranch;
import enums.EnumType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import userEntities.User;

/**
 * Controller class for the Customer Information Update Page in the BiteMe application.
 * This controller manages the process of completing or updating user registration information.
 */
public class CustomerInformationUpdateController {

	/**
     * Button to return to the previous page.
     */
    @FXML private Button backButton;

    /**
     * Text field for entering the user's ID.
     */
    @FXML private TextField idField;

    /**
     * Text field for entering the user's first name.
     */
    @FXML private TextField firstNameField;

    /**
     * Text field for entering the user's last name.
     */
    @FXML private TextField lastNameField;

    /**
     * Text field for entering the user's email address.
     */
    @FXML private TextField emailField;

    /**
     * Text field for entering the user's phone number.
     */
    @FXML private TextField phoneNumberField;

    /**
     * Text field for entering the user's credit card information.
     */
    @FXML private TextField creditCardField;

    /**
     * Combo box for selecting the user's branch.
     */
    @FXML private ComboBox<EnumBranch> branchComboBox;

    /**
     * Button to submit the updated user information.
     */
    @FXML private Button updateButton;

    /**
     * Text area for displaying error messages during the update process.
     */
    @FXML private Text errorText;

    /**
     * The scene of the previous page, used for navigation.
     */
    private Scene previousScene;

    /**
     * The client instance for communication with the server.
     */
    private Client client;

    /**
     * The user object representing the user being updated or registered.
     */
    private User user;

    /**
     * The user object representing the currently logged-in user (typically a BM or CEO).
     */
    private User loggedInUser;
   
    /**
     * Initializes the controller. This method is automatically called after the FXML file has been loaded.
     */
    public void initialize() {
        client = Client.getInstance();
        branchComboBox.getItems().addAll(EnumBranch.values());
        client.setCustomerInformationUpdateController(this);
    }

    /**
     * Sets the previous scene to allow returning to the Register User Page.
     * 
     * @param scene The scene of the Register User Page
     */
    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }
    
    /**
     * Sets the logged-in user information.
     * 
     * @param loggedInUser The currently logged-in user (typically a BM or CEO)
     */
    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    /**
     * Sets the user information to be updated or completed.
     * 
     * @param user The user object to be updated
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Handles the action when the back button is clicked.
     * Returns to the Register User Page.
     */
    @FXML
    private void handleBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(previousScene);
    }

    /**
     * Handles the action when the update button is clicked.
     * Validates the entered information and sends an update request to the server .
     * And opens an Activation Confirtmation screen if succeed
     */
    @FXML
    private void handleUpdate() {
        try {
            String id = getTextSafely(idField);
            String firstName = getTextSafely(firstNameField);
            String lastName = getTextSafely(lastNameField);
            String email = getTextSafely(emailField);
            String phone = getTextSafely(phoneNumberField);
            String creditCard = getTextSafely(creditCardField);
            if (id.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                showError("Please fill in all required fields");
                return;
            }
            // ID REGEX
            if (!id.matches("[a-zA-Z0-9]+"))
            { 
            	showError("Please enter a valid ID ");
            	return;
            }
            // Check if phone number contains only digits
            if (!phone.matches("\\d+")) {
                showError("Phone number must contain only numbers");
                return;                
            }
            // Check if first name contains only letters
            if (!firstName.matches("[a-zA-Z]+")) {
                showError("First name must contain only letters");
                return;
            }
            // Check if last name contains only letters
            if (!lastName.matches("[a-zA-Z]+")) {
                showError("Last name must contain only letters");
                return;
            }
            //Email REGEX
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                showError("Please enter a valid email address");
                return;
            }else {
                user.setId(id);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email);
                user.setPhone(phone);
                user.setCreditCard(creditCard);
                EnumBranch selectedBranch = branchComboBox.getValue();
                if (selectedBranch == null) {
                    showError("Please select a branch");
                    return;
                }
                user.setHomeBranch(selectedBranch);             
                user.setType(EnumType.CUSTOMER);
                user.setCustomerType(EnumType.REGULAR);
                if (!creditCard.isEmpty()) {
                	if (!creditCard.matches("\\d+"))
                	{
                		showError("Credit card must contain only numbers");
                		return;
                	}                	
                    user.setCreditCard(creditCard);
                    user.setCustomerType(EnumType.PRIVATE);
                }
                client.sendCreateAccout(user);
                System.out.println("Update request sent to server");
                openActivationConfirmationWindow();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred: " + e.getMessage());
        }
    }
    
    /**
     * Retrieves text from a TextField safely, handling null values.
     * 
     * @param field The TextField to get text from
     * @return The trimmed text from the field, or an empty string if null
     */
    private String getTextSafely(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }
    
    /**
     * Opens the Activation Confirmation window after successful user information update.
     */
    private void openActivationConfirmationWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ActivationConfirmation.fxml"));
            Parent root = loader.load();
            ActivationConfirmationController controller = loader.getController();
            controller.setUserInfo(user);
            controller.setLoggedInUser(loggedInUser); // Assuming the user is now registered
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Customer Activation Confirmation");
            stage.show();
            Stage currentStage = (Stage) updateButton.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error opening confirmation window: " + e.getMessage());
        }
    }

    /**
     * Displays an error message to the user.
     * 
     * @param message The error message to display
     */
    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisible(true);
    }

    /**
     * Handles the server's response to the user information update request.
     * 
     * @param response The response object from the server
     */
    public void handleServerResponse(Object response) {
        if (response instanceof User) {
            User updatedUser = (User) response;
            Platform.runLater(() -> {
                showError("User information updated successfully");
                // Optionally update the UI with the new user information
                setUser(updatedUser);
            });
        } else if (response instanceof Boolean) {
            Boolean success = (Boolean) response;
            Platform.runLater(() -> {
                if (success) {
                    showError("User information updated successfully");
                } else {
                    showError("Failed to update user information. Please try again.");
                }
            });
        } else {
            Platform.runLater(() -> {
                showError("Unexpected response from server. Please try again.");
            });
        }
    }
}