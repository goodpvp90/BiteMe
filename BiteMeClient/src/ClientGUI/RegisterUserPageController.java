package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Scene;
import client.Client;
import common.User;

public class RegisterUserPageController {

    @FXML
    private Button backButton;

    @FXML
    private TextField usernameField;

    @FXML
    private Button continueButton;

    @FXML
    private Text errorText;

    private Scene previousScene;
    private Client client;

    public void initialize() {
        client = Client.getInstance();
        client.setRegisterUserPageController(this);
    }

    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(previousScene);
    }

    @FXML
    private void handleContinue() {
        String username = usernameField.getText().trim();
        
        if (username.isEmpty()) {
            showError("Please Enter username");
        } else {
            // Send the username to the server
            client.sendSearchUser(username);
            // The response will be handled in the Client's handleMessageFromServer method
            // You need to implement a way to get the response back to this controller
        }
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisible(true);
    }

    // This method should be called from your Client class after receiving the server response
    public void handleServerResponse(Object response) {
        if (response instanceof User) {
            User user = (User) response;
            // User found, proceed to next window
            proceedToNextWindow(user);
        } else {
            // Username not found
            showError("Username does not exist");
        }
    }

    private void proceedToNextWindow(User user) {
        // Hide any previous error
        errorText.setVisible(false);
        
        // TODO: Implement navigation to the next window
        System.out.println("Proceeding to next window for user: " + user.getUsername());
    }
}