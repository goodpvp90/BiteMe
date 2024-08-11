package ClientGUI;

import java.io.IOException;
import java.net.ConnectException;

import client.Client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;


/**
 * Controller class for the Client Connect screen in the BiteMe application.
 * This is the initial screen where users enter server connection details.
 */
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
    
    /**
     * Initializes the controller. This method is automatically called after the FXML file has been loaded.
     */
    private void initialize() {
        // Nothing is needed for now Maybe later :)
    }
    
    /**
     * Handles the action when the connect button is clicked.
     * Attempts to establish a connection to the server with the provided IP and port.
     *
     * @throws IOException If there's an error during the connection process
     */
    @FXML
    private void handleConnectButtonAction() {
        String serverIp = serverIpTextField.getText();
        int serverPort = Integer.parseInt(serverPortTextField.getText());

        if (IPandPortLegal(serverIp, serverPort)) {
            new Thread(() -> {
                try {
                	showError("Proccessing...");
                    Client.initialize(serverIp, serverPort);
                    Platform.runLater(() -> launchClientLoginUI());
                } catch (IOException e) {
                    Platform.runLater(() -> showError("Connection to server failed, please try again"));
               
                }
            }).start();
        } else {
            showError("The IP address or port number is invalid. Please check and try again.");
        }
    }
//		if (IPandPortLegal(serverIp, serverPort)) {
//			try {
//			Client.initialize(serverIp, serverPort);
//			}
//			catch(ConnectException e) {
//				showError("Connect to server port failed, please try again");
//				return;
//			}
//
//			launchClientLoginUI();
//		}


    /**
     * Validates the IP address and port number entered by the user.
     *
     * @param ip The IP address entered by the user
     * @param port The port number entered by the user
     * @return true if the IP and port are valid, false otherwise
     */
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

    /**
     * Displays an error message to the user.
     *
     * @param err The error message to display
     */
    private void showError(String err) {
    	ErrorTextConnect.setVisible(true);
    	ErrorTextConnect.setText(err);
    }

    /**
     * Handles the action when the quit button is clicked.
     * Closes the application.
     */
    @FXML
    private void handleQuitButtonAction() {
        // Close the application
        Stage stage = (Stage) quitButton.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Launches the Client Login UI after successful connection to the server.
     * Closes the current Client Connect window.
     */
    private void launchClientLoginUI() {
        try {
            ClientLoginUI loginApp = new ClientLoginUI();
            loginApp.start(new Stage());
            Stage currentStage = (Stage) connectButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
