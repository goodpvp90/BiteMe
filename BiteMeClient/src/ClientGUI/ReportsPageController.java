package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

import client.Client;
import common.EnumType;
import common.IncomeReport;
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
    @FXML private Label errorMessageLabel;
    
    private Client client;
    private EnumType userType;
    private User user;
    private boolean isRegistered;
    private Stage revenueReportStage;

    public void initialize() {
        client = Client.getInstance();
        client.setReportsPageController(this);
        setupDropdowns();
        clearErrorMessage();
    }
    
//Setter so i can go back with same user to UserHomePage
    public void setUser(User user, boolean isRegistered) {
        this.user = user;
        this.isRegistered = isRegistered;
        setUserType(user.getType());

        // Set up the branch dropdown based on user type
        if (user.getType() == EnumType.BRANCH_MANAGER) {
            branchDropdown.setValue(user.getHomeBranch().toString());
            branchDropdown.setDisable(true);
        } else if (user.getType() == EnumType.CEO) {
            branchDropdown.getItems().addAll("North", "Center", "South");
            branchDropdown.setDisable(false);
        }
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
        // If a window is already open, bring it to front instead of opening a new one
        if (revenueReportStage != null && revenueReportStage.isShowing()) {
            revenueReportStage.toFront();
            return;
        }
        String branch = branchDropdown.getValue(); // This should be pre-set and disabled for BM
        String monthStr = monthDropdown.getValue();
        String yearStr = yearDropdown.getValue();
     
        
        // Clear any previous error messages
        clearErrorMessage();
        if (monthStr == null || yearStr == null || monthStr.isEmpty() || yearStr.isEmpty()) {
            showErrorMessage("Please select both a month and a year before generating the report.");
            return;
        }

        // For BM, the branch should already be set
        if (branch == null && user.getType() == EnumType.BRANCH_MANAGER) {
            branch = user.getHomeBranch().toString();
        }
        //Check for NULL Branch (NEVER SHOULD GET HERE)
        if (branch == null) {
        	showErrorMessage("Branch information is missing.");
            return;
        }
        try {
            int month = monthDropdown.getSelectionModel().getSelectedIndex() + 1;
            int year = Integer.parseInt(yearStr);
         // Create an IncomeReport object
            IncomeReport report = new IncomeReport(common.Restaurant.Location.valueOf(branch.toUpperCase()), month, year);
            revenueReportButton.setDisable(true);
            //Get Report from the client
            client.getIncomeReport(report);
            //revenueReportButton.setDisable(true);
        } catch (NumberFormatException e) {
            showErrorMessage("Invalid year format. Please enter a valid year.");
        }
    }
    
    // This method should be called when you receive the response from the server
    public void handleIncomeReportResponse(Object response) {
        Platform.runLater(() -> {
            if (response instanceof IncomeReport) {
                IncomeReport report = (IncomeReport) response;
                if (report.getIncome() > 0) {
                    openRevenueReportWindow(report);
                } else {
                    showErrorMessage("No income data available for the selected month and year.");
                    enableRevenueReportButton();
                }
            } else if (response instanceof String) {
                showErrorMessage((String) response);
                enableRevenueReportButton();
            } else {
                showErrorMessage("An unexpected error occurred while fetching the report.");
                enableRevenueReportButton();
            }
        });
    }
    
    private void openRevenueReportWindow(IncomeReport report) {
        if (revenueReportStage != null && revenueReportStage.isShowing()) {
            revenueReportStage.toFront();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RevenueReportWindow.fxml"));
            Parent root = loader.load();
            
            RevenueReportWindowController controller = loader.getController();
            controller.setReportData(report);
            controller.setReportsPageController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Revenue Report");
            
            // Re-enable the button when the window is closed
            stage.setOnHidden(e -> enableRevenueReportButton());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorMessage("An error occurred while opening the Revenue Report window.");
            enableRevenueReportButton();
        }
    }
    
    //To disable Revenue Report Button while the window is open.
    public void enableRevenueReportButton() {
        revenueReportButton.setDisable(false);
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

    private void showErrorMessage(String message) {
        errorMessageLabel.setText(message);
        errorMessageLabel.setVisible(true);
        errorMessageLabel.setManaged(true);
    }
    
    private void clearErrorMessage() {
        errorMessageLabel.setText("");
        errorMessageLabel.setVisible(false);
        errorMessageLabel.setManaged(false);
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