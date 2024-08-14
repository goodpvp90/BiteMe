package ServerGUI;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import controllers.DBController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import server.Server;

/**
 * The serverController class handles the server-side operations and user interactions
 * within the server's GUI. It manages the connection to the server, database interaction, and 
 * the server's state.
 */
public class serverController {
	
	/**
	 * The server instance that this controller manages.
	 */
    private Server server;

    /**
	 * The main layout container for the server's UI.
	 */
    @FXML
    private GridPane gridPane;
    
    /**
	 * A label to display the current status of the server (e.g., connected, disconnected).
	 */
    @FXML
    private Label statusLabel;
    
    /**
	 * A button that allows the user to start the server.
	 */
    @FXML
    private Button connectButton;
    
    /**
	 * A button that allows the user to stop the server.
	 */
    @FXML
    private Button disconnectButton;
    
    /**
	 * A button that allows the user to safely quit the server application.
	 */
    @FXML
    private Button quitButton;
    
    /**
	 * A list view that displays the connected clients to the server.
	 */
    
    @FXML
    private ListView<String> clientsListView;
    /**
	 * A text field for entering the port number on which the server will listen.
	 */
    @FXML 
    private TextField portText;
    
    /**
	 * A text field for entering the name of the database the server will connect to.
	 */
    @FXML 
    private TextField DBNameText;
    
    /**
	 * A text field for entering the username to access the database.
	 */
    @FXML 
    private TextField DBuser;
    
    /**
	 * A password field for entering the password to access the database.
	 */
    @FXML 
    private PasswordField DBPassword;
    
    /**
	 * A text field for displaying error messages related to server input.
	 */
    @FXML
    private Text ErrorServerInput;
    
    /**
	 * A text field displaying the IP address of the server.
	 */
    @FXML
    private Text IPText;

    /**
	 * A text field for entering the file path to a CSV file containing user data.
	 */
    @FXML
    private TextField usersCSVPath;
    
    /**
	 * A text field for entering the file path to a CSV file containing customer data.
	 */
    @FXML
    private TextField customersCSVPath;

    /**
     * Initializes the server GUI with default values, particularly setting the IP address.
     */    @FXML
    public void initialize() {
        IPText.setText(getIP());
    }

     /**
      * Handles the event when the "Connect" button is clicked. It checks the legality of the input,
      * starts the server, and updates the UI accordingly.
      *
      * @param event The event triggered by clicking the "Connect" button.
      */
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
        try {
            server.listen();            
            Platform.runLater(() -> {
                // Turns off and on the connect and disconnect buttons respectively
                connectButton.setDisable(true);
                disconnectButton.setDisable(false);
                quitButton.setDisable(false);
                hideErrorOfWrongInput();
                importUsers();
            });
        } catch (Exception e) {
            showError("Port already taken");
        }
    }

	/**
	 * Validates the input for port number, database name, user, and password.
	 *
	 * @param PortStr      The port number.
	 * @param DBNameStr    The name of the database.
	 * @param DBUserStr    The database username.
	 * @param DBPassString The database password.
	 * @return true if all inputs are valid.
	 * @throws IllegalArgumentException If any input does not meet the required
	 *                                  format or is empty.
	 */
    private boolean CheckLegalInput(String PortStr, String DBNameStr, String DBUserStr, String DBPassString) {
        if (PortStr.equals("")) 
            throw new IllegalArgumentException("Error! Port number is empty");
        if (PortStr.length() != 4)
            throw new IllegalArgumentException("Error! Port length needs to be 4");
        if (!PortStr.matches("\\d{4}"))
            throw new IllegalArgumentException("Error! Insert Port between 0000 to 9999");
        if (DBNameStr.equals(""))
            throw new IllegalArgumentException("Error! DB Name is empty");
        if (DBUserStr.equals(""))
            throw new IllegalArgumentException("Error! DB UserName is empty");
        if (DBPassString.equals(""))
            throw new IllegalArgumentException("Error! DB Password is empty");
        return true;
    }

    /**
     * Display the error message with given text.
     */
    public void showError(String message) {
        ErrorServerInput.setText(message);
        ErrorServerInput.setVisible(true);
    }

    /**
     * Hides the error message.
     */
    public void hideErrorOfWrongInput() {
        ErrorServerInput.setVisible(false);
    }

    /**
     * Handles the event when the "Disconnect" button is clicked. It stops the server
     * and resets the login status of all users in the database.
     *
     * @param event The event triggered by clicking the "Disconnect" button.
     */
    @FXML
    private void handleDisconnectButton(ActionEvent event) {
        if (server != null) {
            try {
                resetAllUserLoggedStatus(); 
                server.stopServer(); 
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

    /**
     * Handles the event when the "Quit" button is clicked. It safely closes the server and exits the application.
     *
     * @param event The event triggered by clicking the "Quit" button.
     */
    @FXML
    private void handleQuitButton(ActionEvent event) {
        safeClose();
    }

    /**
     * Safely closes the server by resetting all users' login status and stopping the server.
     * The application then exits.
     */
    private void safeClose() {
        if (server != null) {
            try {
                resetAllUserLoggedStatus();
                server.stopServer();
            } catch (Exception e) {
                e.printStackTrace();
                updateStatus("Failed to quit server: " + e.getMessage());
            }
        }
        Platform.exit();
         System.exit(0);
    }

    /**
     * Retrieves the local IP address of the server.
     *
     * @return The IP address as a string, or "Error IP" if the IP cannot be retrieved.
     */
    private String getIP() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String ipAddress = localHost.getHostAddress();
            return ipAddress;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "Error IP";
    }

    /**
     * Updates the server status label on the UI with the provided status message.
     *
     * @param status The status message to be displayed.
     */
    public void updateStatus(String status) {
        Platform.runLater(() -> statusLabel.setText(status));
    }

    /**
     * Displays the details of connected or disconnected clients on the ListView.
     *
     * @param msg The array of strings containing client details.
     */
    public void displayClientDetails(String[] msg) {
        Platform.runLater(() -> {
            StringBuilder str = new StringBuilder();
            for (String s : msg) {
                str.append(s).append(" ");
            }
            clientsListView.getItems().add(str.toString());
        });
    }

    /**
     * Resets the login status of all users in the database to 0.
     * This method is called when the server is disconnected or closed.
     */
    private void resetAllUserLoggedStatus() {
        if (server != null) {
            try {
                DBController dbController = server.getDBController();
                dbController.resetAllUserLoggedStatus();
            } catch (Exception e) {
                e.printStackTrace();
                updateStatus("Failed to reset user logged statuses: " + e.getMessage());
            }
        }
    }

    /**
     * Closes the application safely when the window's close button is clicked.
     */
    public void closeApplication() {
        safeClose();
    }

    /**
     * Sets the controller for the server instance.
     *
     * @param protoServer The server instance to be controlled.
     */
    public void setProtoServer(Server protoServer) {
        protoServer.setController(this);
    }

    /**
     * Imports user data from Data Managements system's scheme into BiteMe scheme
     */
    public void importUsers() {
    	DBController dbcontroller = server.getDBController();
    	try {
			dbcontroller.importUsers();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}

