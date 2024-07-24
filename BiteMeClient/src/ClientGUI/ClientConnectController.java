package ClientGUI;

import java.io.IOException;

import client.Client;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ClientConnectController {

    @FXML
    private TextField serverIpTextField;

    @FXML
    private TextField serverPortTextField;

    @FXML
    private Button connectButton;

    @FXML
    private Button quitButton;
    @FXML
    private Text ErrorTextConnect;
    
    @FXML
    private void initialize() {
        // Initialization code, if needed
    }

    @FXML
    private void handleConnectButtonAction() throws IOException {
        String serverIp = serverIpTextField.getText();
        int serverPort = Integer.parseInt(serverPortTextField.getText());
        
        //////////////added switch to login page
        boolean connect = true;
        Client.initialize(serverIp, serverPort);
        if(connect)
        	launchClientLoginUI();
        
        // Handle the connect logic here
        System.out.println("Connecting to server " + serverIp + " on port " + serverPort);
        
    }

    @FXML
    private void handleQuitButtonAction() {
        // Close the application
        Stage stage = (Stage) quitButton.getScene().getWindow();
        stage.close();
    }
    
    //launching Client Login UI, close the current window and set up new UI.
    private void launchClientLoginUI() {
        try {
            // Launch ClientLoginUI
            ClientLoginUI loginApp = new ClientLoginUI();
            loginApp.start(new Stage());

            // Close the current stage
            Stage currentStage = (Stage) connectButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
