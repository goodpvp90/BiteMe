package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import client.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ReportsPageController {

    @FXML
    private Button backButton;

    @FXML
    private Button revenueReportButton;

    @FXML
    private Button performanceReportButton;

    @FXML
    private Button ordersReportButton;
    
    
    private Client client;
    @FXML
    private void initialize() {
    	client = Client.getInstance();
    }
    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            // Navigate back to the previous page (UserHomePage)
            Parent root = FXMLLoader.load(getClass().getResource("UserHomePage.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root, 700, 600));
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
    
    public void closeApplication() {
        if (client != null) {
            System.out.println("Closing application from UserHomePage");
            client.quit();
        }
        Platform.exit();
        System.exit(0);
    }
    
}