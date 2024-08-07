package ClientGUI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Scene;
import client.Client;
import common.User;
import common.EnumBranch;
import common.EnumType;

public class CustomerInformationUpdateController {

    @FXML
    private Button backButton;
    
    @FXML
    private TextField idField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField creditCardField;

    @FXML
    private Button updateButton;

    @FXML
    private Text errorText;

    private Scene previousScene;
    private Client client;
    private User user;

    public void initialize() {
        client = Client.getInstance();
        client.setCustomerInformationUpdateController(this);
    }

    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }

    public void setUser(User user) {
        this.user = user;
        // Pre-fill the fields with existing user information
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
        phoneNumberField.setText(user.getPhone());
        // Don't pre-fill credit card for security reasons
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(previousScene);
    }

    @FXML
    private void handleUpdate() {
    	try {
            System.out.println("handleUpdate method called");
            String id = idField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneNumberField.getText().trim();
            String creditCard = creditCardField.getText().trim();

            if (id.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                showError("Please fill in all required fields");
            } else {
                // Update user information
                user.setId(id);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email);
                user.setPhone(phone);
                //TODO HOMEBRANCH AND TYPE
                user.setHomeBranch(EnumBranch.NORTH);
                user.setType(EnumType.CUSTOMER);
                user.setCustomerType(EnumType.REGULAR);
                if (!creditCard.isEmpty()) {
                    user.setCreditCard(creditCard);
                    user.setCustomerType(EnumType.PRIVATE);
                }

                // Send the updated user information to the server
                client.sendCreateAccout(user);
                System.out.println("Update request sent to server");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisible(true);
    }

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