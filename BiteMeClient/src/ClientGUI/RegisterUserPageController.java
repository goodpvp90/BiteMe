package ClientGUI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

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
        stage.setTitle("User Home Page");
    }

    @FXML
    private void handleContinue() {
        errorText.setVisible(false);
        errorText.setText("");
        String username = usernameField.getText().trim();
        
        if (username.isEmpty()) {
            showError("Please Enter username");
        } else {
            // Send the username to the server
            client.sendSearchUser(username);
        }
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisible(true);
    }

    // This method should be called Client class after receiving the server response
    public void handleServerResponse(Object Customer) {
        Platform.runLater(() -> {
        	if (Customer instanceof Boolean) {
        		showError("Username Not Found");	
        	}
        	else {           	
        		User user = (User)Customer;
        		if (user.getEmail()==null && user.getCreditCard()==null)  
        		{
        			CustomerInformationUpdateWindow(user);            		
        		}
        		else {
        			showError("Username Exists and is Registered");            		
        		}	
        	}
        });
        }

    private void CustomerInformationUpdateWindow(User user) {
        // Hide any previous error
        errorText.setVisible(false);
        
        // TODO:
        System.out.println("Proceeding to next window for user: " + user.getUsername());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerInformationUpdate.fxml"));
            Parent root = loader.load();           
            CustomerInformationUpdateController controller = loader.getController();           
            if (controller != null) {
                controller.setPreviousScene(backButton.getScene());
                controller.setUser(user);
            } else {
                System.err.println("Failed to get controller from FXMLLoader");
            }
            Scene scene = new Scene(root);
            Stage stage = (Stage) continueButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading FXML: " + e.getMessage());
            showError("Error loading the Customer Information Update window: " + e.getMessage());
        }
    }
   }