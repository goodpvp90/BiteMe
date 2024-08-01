package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import client.Client;
import common.EnumType;
import common.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ReportsPageController {

    @FXML private Button backButton;
    @FXML private Button revenueReportButton;
    @FXML private Button performanceReportButton;
    @FXML private Button ordersReportButton;
    @FXML private Button quarterlyReportButton;
    @FXML private ComboBox<String> branchDropdown;
    @FXML private ComboBox<String> monthDropdown;
    @FXML private ComboBox<String> yearDropdown;
    @FXML private ComboBox<String> quarterDropdown;
    @FXML private ComboBox<String> quarterYearDropdown;
    @FXML private VBox quarterlyReportSection;
    @FXML private GridPane reportsGridPane;

    private Client client;
    private EnumType userType;
    private User user;
    private boolean isRegistered;

    public void initialize() {
        client = Client.getInstance();
        setupDropdowns();
    }
    
//Setter so i can go back with same user to UserHomePage
    public void setUser(User user, boolean isRegistered) {
        this.user = user;
        this.isRegistered = isRegistered;
        setUserType(user.getType());
    }

    //For the right user is going to be right UI
    public void setUserType(EnumType userType) {
        if (userType == EnumType.CEO) {
            // Two-column layout for CEO
            reportsGridPane.getColumnConstraints().get(1).setPercentWidth(50);
            quarterlyReportSection.setVisible(true);
            quarterlyReportSection.setManaged(true);
        } else {
            // Single-column layout for Branch Manager
            reportsGridPane.getColumnConstraints().get(0).setPercentWidth(100);
            reportsGridPane.getColumnConstraints().get(1).setPercentWidth(0);
            quarterlyReportSection.setVisible(false);
            quarterlyReportSection.setManaged(false);
        }
    }

    // Drop Downs for each user (HardCodded maybe later we will switch that)
    //TODO Maybe add Calendar Later
    private void setupDropdowns() {
        // Initialize dropdowns with appropriate values
        monthDropdown.getItems().addAll("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        yearDropdown.getItems().addAll("2023", "2024", "2025");
        quarterDropdown.getItems().addAll("1", "2", "3", "4");
        quarterYearDropdown.getItems().addAll("2023", "2024", "2025");

        // TODO: Populate branchDropdown with actual branch data
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserHomePage.fxml"));
            Parent root = loader.load();
            UserHomePageController controller = loader.getController();
            // Pass the user and registration status
            controller.setUser(this.user, this.isRegistered); 
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root, 700, 600);
            stage.setScene(scene);
            stage.setTitle("User Home Page");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRevenueReport(ActionEvent event) {
        // Implement logic for generating and displaying the Revenue Report
        System.out.println("Revenue Report button clicked");
    }

    @FXML
    private void handlePerformanceReport(ActionEvent event) {
        // Implement logic for generating and displaying the Performance Report
        System.out.println("Performance Report button clicked");
    }

    @FXML
    private void handleOrdersReport(ActionEvent event) {
        // Implement logic for generating and displaying the Orders Report
        System.out.println("Orders Report button clicked");
    }

    @FXML
    private void handleQuarterlyReport(ActionEvent event) {
        // Implement logic for generating and displaying the Quarterly Report
        System.out.println("Quarterly Report button clicked");
    }

    public void closeApplication() {
        if (client != null) {
            System.out.println("Closing application from ReportsPage");
            client.quit();
        }
        Platform.exit();
        System.exit(0);
    }
}