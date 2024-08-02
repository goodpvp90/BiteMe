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
		System.out.println(serverPort);

//		if (IPandPortLegal(serverIp, serverPort)) {
			Client.initialize(serverIp, serverPort);
			launchClientLoginUI();
//		}
	}
    
	private boolean IPandPortLegal(String ip, int port) {
		if (port < 1000 || port > 9999) {
			showError("Port must be a 4-digit number.");
			return false;
		}

		if (ip == null || ip.isEmpty()) {
			showError("IP address cannot be empty.");
			return false;
		}

		String[] ipParts = ip.split("\\.");
		if (ipParts.length != 4) {
			showError("IP address must be 4 numbers separated by dots.");
			return false;
		}

		for (String part : ipParts) {
			if (!part.matches("\\d+")) {
				showError("IP address must contain only numbers.");
				return false;
			}

			int num = Integer.parseInt(part);
			if (num < 0 || num > 255) {
				showError("Each number in the IP address must be between 0 and 255.");
				return false;
			}
		}

		return true;
	}

    private void showError(String err) {
    	ErrorTextConnect.setVisible(true);
    	ErrorTextConnect.setText(err);
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
