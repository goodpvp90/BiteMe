package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class ClientLoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Text errorText;

    @FXML
    private Button loginButton;

    @FXML
    private Button quitButton;

    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> handleLogin());
        quitButton.setOnAction(event -> handleQuit());
    }

    private void handleLogin() {
    	
        String username = usernameField.getText();
        String password = passwordField.getText();
        
      //Check if the data input from user is legal
        if (username.isEmpty() || password.isEmpty()) {
            showError("Username or password cannot be empty");
            
        } else {
            // success storing the message from server if connect succeed
            boolean success=false;
            
            if (success) {
                // Proceed to next scene or application logic
            } else {
                showError("Invalid username or password");
            }
        }
    }

    private void handleQuit() {
        System.exit(0);
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisible(true);
    }

    private boolean authenticate(String username, String password) {
        // Dummy authentication logic; replace with actual logic
        return "admin".equals(username) && "password".equals(password);
    }
}
