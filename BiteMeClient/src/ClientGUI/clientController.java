package ClientGUI;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.io.IOException;
import client.ProtoClient;

public class clientController {
    private ProtoClient client;
    private boolean updating = false;
    
    // UI components injected from FXML
    @FXML
    private VBox ordersContainer;
    @FXML
    private ListView<String> ordersListView;
    @FXML
    private Text welcomeText;
    @FXML
    private TextField orderNumberField;
    @FXML
    private ComboBox<String> fieldComboBox;
    @FXML
    private TextField newValueField;
    @FXML
    private Button connectButton;
    @FXML
    private Button viewButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button quitButton;

    // Constructor
    public clientController() {
        client = null;
    }

    // Initialize method called after FXML loading
    @FXML
    private void initialize() {
        connectButton.setDisable(false);
        viewButton.setDisable(true);
        updateButton.setDisable(true);
        welcomeText.setText("Please press the CONNECT button to begin.");
        fieldComboBox.setItems(FXCollections.observableArrayList("Total_price", "Order_address"));
    }

    // Handles the CONNECT button logic
    @FXML
    private void handleConnectButton() {
        try {
            client = new ProtoClient("localhost", ProtoClient.DEFAULT_PORT);
            client.setGuiController(this);
            welcomeText.setText("Hello " + client.getHost() + "! Please choose an action:");
            connectButton.setDisable(true);
            viewButton.setDisable(false);
            updateButton.setDisable(false);
        } catch (IOException e) {
            e.printStackTrace();
            welcomeText.setText("Failed to connect: " + e.getMessage());
        }
    }

    // Handles the VIEW button logic
    @FXML
    private void handleViewButton() {
        if (!updating) {
            try {
                client.viewOrdersFromDB();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error while handling view button: " + e.getMessage());
            }
        }
    }

    // Handles the UPDATE button logic
    @FXML
    private void handleUpdateButton() {
        updating = true;
        updateButton.setDisable(true);
        ordersContainer.setVisible(true);
        viewButton.setDisable(true); // Disable view button during update
    }

    // Handles the CONFIRM button logic in the UPDATE section
    @FXML
    private void handleConfirmButton() {
        try {
            int orderNum = Integer.parseInt(orderNumberField.getText());
            String field = fieldComboBox.getValue();
            String newValue = newValueField.getText();

            // Checks for empty values
            if (field == null || newValue.isEmpty()) {
                welcomeText.setText("Please fill in all fields.");
                return;
            }

            // Determines the type of newValue based on selected field
            Object newVal;
            if ("Total_price".equals(field)) {
                newVal = Double.parseDouble(newValue);
            } else if ("Order_address".equals(field)) {
                newVal = newValue;
            } else {
                welcomeText.setText("Invalid field selection.");
                return;
            }

            // Sends update request to the server
            client.sendUpdateOrderRequest(orderNum, field, newVal);

            // Clears fields and resets UI after successful update
            orderNumberField.clear();
            fieldComboBox.setValue(null);
            newValueField.clear();
            ordersContainer.setVisible(false);
            updateButton.setDisable(false);
            updating = false;
            viewButton.setDisable(false); // Enable view button after update completes

        } catch (NumberFormatException e) {
            welcomeText.setText("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error during update: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Handles the QUIT button logic
    @FXML
    private void handleQuitButton() {
        client.quit();
    }

    // Displays retrieved orders in the ListView
    public void displayOrders(Object[] orders) {
        Platform.runLater(() -> {
            ordersListView.getItems().clear();
            for (Object order : orders) {
                if (order instanceof Object[]) {
                    StringBuilder orderDetails = new StringBuilder();
                    for (Object obj : (Object[]) order) {
                        orderDetails.append(obj).append("\t");
                    }
                    ordersListView.getItems().add(orderDetails.toString());
                }
            }
        });
    }

    // Updates the welcome text message
    public void updateWelcomeText(String message) {
        Platform.runLater(() -> {
            welcomeText.setText(message);
        });
    }
}
