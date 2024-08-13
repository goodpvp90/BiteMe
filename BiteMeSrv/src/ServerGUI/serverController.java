package ServerGUI;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.File;
import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import server.DBController;
import server.Server;
import server.UsersUtility;

public class serverController {
	
    private Server server;
    private UsersUtility usersutility;

    @FXML
    private GridPane gridPane;
    @FXML
    private Label statusLabel;
    @FXML
    private Button connectButton;
    @FXML
    private Button disconnectButton;
    @FXML
    private Button quitButton;
    @FXML
    private ListView<String> clientsListView;
    @FXML 
    private TextField portText;
    @FXML 
    private TextField DBNameText;
    @FXML 
    private TextField DBuser;
    @FXML 
    private PasswordField DBPassword;
    @FXML
    private Text ErrorServerInput;
    @FXML
    private Text IPText;

    @FXML
    private TextField usersCSVPath; // Add this
    @FXML
    private TextField customersCSVPath; // Add this

    // Initialize the serverGUI default values
    @FXML
    public void initialize() {
        IPText.setText(getIP());
    }

    // Starts up the server
    @FXML
    private void handleConnectButton(ActionEvent event) {
        try {
            CheckLegalInput(portText.getText(), DBNameText.getText(), DBuser.getText(), DBPassword.getText());
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            return;
        }

        try {
            server = new Server(Integer.parseInt(portText.getText()), DBNameText.getText(), DBuser.getText(), DBPassword.getText());
        } catch (NullPointerException e) {
            Platform.runLater(() -> showError("Error! Failed connecting to the Database!"));
            return;
        }
    
        server.setController(this);

        // handle connection to server with legal input from text fields
        // Making a thread to run in the background and listen for clients
        try {
            server.listen();
            // run later is used to sync thread updates basically
            
            Platform.runLater(() -> {
                // Turns off and on the connect and disconnect buttons respectively
                connectButton.setDisable(true);
                disconnectButton.setDisable(false);
                quitButton.setDisable(false);
                hideErrorOfWrongInput();
                importUsers(); // Import users
            });
        } catch (Exception e) {
            showError("Port already taken");
        }
    }

    // Receive text from UI screen about Port number, DB name, DB user, DB password and
    // check their correctness to required format 
    private boolean CheckLegalInput(String PortStr, String DBNameStr, String DBUserStr, String DBPassString) {
        // Port String check
        if (PortStr.equals("")) 
            throw new IllegalArgumentException("Error! Port number is empty");
        if (PortStr.length() != 4)
            throw new IllegalArgumentException("Error! Port length needs to be 4");
        if (!PortStr.matches("\\d{4}"))
            throw new IllegalArgumentException("Error! Insert Port between 0000 to 9999");

        // DB Name String check
        if (DBNameStr.equals(""))
            throw new IllegalArgumentException("Error! DB Name is empty");

        // DB User String check
        if (DBUserStr.equals(""))
            throw new IllegalArgumentException("Error! DB UserName is empty");

        // DB Password String check
        if (DBPassString.equals(""))
            throw new IllegalArgumentException("Error! DB Password is empty");

        // Everything fine
        return true;
    }

    // When an error occurs on input of wrong data in the text field, this error
    // will appear
    public void showError(String message) {
        ErrorServerInput.setText(message);
        ErrorServerInput.setVisible(true);
    }

    // Hide error message of input because of wrong data in the text field
    public void hideErrorOfWrongInput() {
        ErrorServerInput.setVisible(false);
    }

    // Turns off the server and restarts the LOGIN STATUS OF EVERY USER TO 0.
    @FXML
    private void handleDisconnectButton(ActionEvent event) {
        if (server != null) {
            try {
                resetAllUserLoggedStatus(); 
                server.stopServer(); 
                // Close the server
                server = null;
                updateStatus("Server disconnected and all user logged statuses reset");
                connectButton.setDisable(false); // Enable the connect button
                disconnectButton.setDisable(true); // Disable the disconnect button    
            } catch (Exception e) {
                e.printStackTrace();
                updateStatus("Failed to stop server: " + e.getMessage());
            }
        }
    }

    // Quits the server and closes the application
    @FXML
    private void handleQuitButton(ActionEvent event) {
        safeClose();
    }

    private void safeClose() {
        if (server != null) {
            try {
                // So it won't close before resetting login status
                resetAllUserLoggedStatus();
                server.stopServer();
            } catch (Exception e) {
                e.printStackTrace();
                updateStatus("Failed to quit server: " + e.getMessage());
            }
        }
        Platform.exit();
        Platform.runLater(() -> System.exit(0));
    }

    private String getIP() {
        try {
            // Get the local host address
            InetAddress localHost = InetAddress.getLocalHost();
            String ipAddress = localHost.getHostAddress();
            return ipAddress;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("Unable to retrieve IP address.");
        }
        return "Error IP";
    }

    // This one is for updating the label's text when needed
    public void updateStatus(String status) {
        Platform.runLater(() -> statusLabel.setText(status));
    }

    // Display the clients' details upon connection/disconnection on the ListView screen
    public void displayClientDetails(String[] msg) {
        Platform.runLater(() -> {
            StringBuilder str = new StringBuilder();
            for (String s : msg) {
                str.append(s).append(" ");
            }
            clientsListView.getItems().add(str.toString());
        });
    }

    private void resetAllUserLoggedStatus() {
        if (server != null) {
            try {
                DBController dbController = server.getDBController();
                dbController.resetAllUserLoggedStatus();
                System.out.println("All user logged statuses reset.");
            } catch (Exception e) {
                e.printStackTrace();
                updateStatus("Failed to reset user logged statuses: " + e.getMessage());
            }
        }
    }

    // If pressed on window 'X'
    public void closeApplication() {
        safeClose();
    }

    // Receive instance of server and send this instance of controller
    public void setProtoServer(Server protoServer) {
        protoServer.setController(this);
    }

    // Method to import users from external schema
    public void importUsers() {
    	UsersUtility usersutlity = server.getUsersUtility();
        usersutlity.importUsers();
    }
}

