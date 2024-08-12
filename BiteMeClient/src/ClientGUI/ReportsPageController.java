package ClientGUI;

import java.io.IOException;
import java.util.List;

import client.Client;
import enums.EnumBranch;
import enums.EnumType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import reports.IncomeReport;
import reports.OrdersReport;
import reports.PerformanceReport;
import reports.QuarterlyReport;
import userEntities.User;

/**
 * Controller class for the Reports Page in the BiteMe application.
 * This page is accessible from the User Home Page and provides functionality
 * to generate and view various types of reports based on user permissions.
 */
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
    //TODO ADDED FOR QUARTER REPORT
    private int openQuarterlyReportWindows = 0;

    /**
     * Initializes the controller. This method is automatically called
     * after the FXML file has been loaded.
     */
    public void initialize() {
        client = Client.getInstance();
        client.setReportsPageController(this);
        setupDropdowns();
        clearErrorMessage();
    }
    
    /**
     * Sets the user for this controller and updates the UI accordingly.
     * This method is called when navigating from the User Home Page.
     *
     * @param user The User object representing the current user.
     * @param isRegistered A boolean indicating whether the user is registered (Only Customer can be NOT registered).
     * 
     */
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
    
    /**
     * Sets the user type and adjusts the UI layout based on the user's permissions.
     *
     * @param userType The EnumType representing the user's role.
     */
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

    /**
     * Sets up the dropdown menus with appropriate values.
     */
    private void setupDropdowns() {
        monthDropdown.getItems().addAll("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        yearDropdown.getItems().addAll("2023", "2024", "2025");
        quarterDropdown.getItems().addAll("1", "2", "3", "4");
        quarterYearDropdown.getItems().addAll("2023", "2024", "2025");
    }

    /**
     * Handles the back button action, returning to the User Home Page.
     *
     * @param event The ActionEvent triggered by clicking the back button.
     */
    @FXML
    private void handleBackButton(ActionEvent event) {
    	 try {
             // Retrieve the existing stage for UserHomePageUI
             Stage userHomePageStage = UserHomePageUI.getStage();

             if (userHomePageStage != null) {
                 userHomePageStage.show();  // Show the hidden stage again
             } else {
                 // If the stage is somehow null, recreate and show it
                 UserHomePageUI Userapp = new UserHomePageUI(user, true);
                 Userapp.start(new Stage());
             }

             // Close the current stage
             Stage currentStage = (Stage) backButton.getScene().getWindow();
             currentStage.close();
         } catch (Exception e) {
             e.printStackTrace();
         }
    }

    /**
     * Handles the generation of a revenue report.
     *
     * @param event The ActionEvent triggered by clicking the revenue report button.
     */
    @FXML
    private void handleRevenueReport(ActionEvent event) {
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
            IncomeReport report = new IncomeReport(EnumBranch.valueOf(branch.toUpperCase()), month, year);
            revenueReportButton.setDisable(true);
            //Get Report from the client
            client.getIncomeReport(report);
            //revenueReportButton.setDisable(true);
        } catch (NumberFormatException e) {
            showErrorMessage("Invalid year format. Please enter a valid year.");
        }
    }
    
    /**
     * Processes the response from the server for a revenue report request.
     *
     * @param response The Object containing the server's response.
     */
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
    
    /**
     * Opens a new window to display the Revenue report.
     *
     * @param report The IncomeReport to be displayed.
     */
    private void openRevenueReportWindow(IncomeReport report) {
        try {
            RevenueReportUI revenueReportUI = new RevenueReportUI();
            revenueReportUI.setReportData(report, this);
            revenueReportUI.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("An error occurred while opening the Revenue Report window.");
            enableRevenueReportButton();
        }
    }
    
    /**
     * Enables the revenue report button after a report window is closed.
     */
    public void enableRevenueReportButton() {
        revenueReportButton.setDisable(false);
    }

    /**
     * Handles the generation of a performance report.
     *
     * @param event The ActionEvent triggered by clicking the performance report button.
     */
    @FXML
    private void handlePerformanceReport(ActionEvent event) {
        String branch = branchDropdown.getValue();
        String monthStr = monthDropdown.getValue();
        String yearStr = yearDropdown.getValue();
        
        clearErrorMessage();
        if (monthStr == null || yearStr == null || monthStr.isEmpty() || yearStr.isEmpty()) {
            showErrorMessage("Please select both a month and a year before generating the report.");
            return;
        }

        if (branch == null && user.getType() == EnumType.BRANCH_MANAGER) {
            branch = user.getHomeBranch().toString();
        }
        if (branch == null) {
            showErrorMessage("Branch information is missing.");
            return;
        }
        try {
            int month = monthDropdown.getSelectionModel().getSelectedIndex() + 1;
            int year = Integer.parseInt(yearStr);
            PerformanceReport report = new PerformanceReport(EnumBranch.valueOf(branch.toUpperCase()), month, year);
            // Don't disable the button here
            client.getPerformanceReport(report);
        } catch (NumberFormatException e) {
            showErrorMessage("Invalid year format. Please enter a valid year.");
        }
    }
    
    /**
     * Processes the response from the server for a Performance report request.
     *
     * @param response The Object containing the server's response.
     */
    public void handlePerformanceReportResponse(Object response) {
        Platform.runLater(() -> {
            if (response instanceof PerformanceReport) {
                PerformanceReport report = (PerformanceReport) response;
                if (!report.getDailyReports().isEmpty()) {
                    openPerformanceReportWindow(report);
                    // The button will be disabled in openPerformanceReportWindow if successful
                } else {
                    showErrorMessage("No performance data available for the selected month and year.");
                }
            } else if (response instanceof String) {
                showErrorMessage((String) response);
            } else {
                showErrorMessage("An unexpected error occurred while fetching the report.");
            }
        });
    }
    
    /**
     * Opens a new window to display the Performance report.
     *
     * @param report The PerformanceReport that is displayed.
     */ 
    private void openPerformanceReportWindow(PerformanceReport report) {
        try {
            PerformanceReportUI performanceReportUI = new PerformanceReportUI();
            performanceReportUI.setReportData(report, this);
            performanceReportUI.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("An error occurred while opening the Performance Report window.");
            performanceReportButton.setDisable(false);
        }
    }
    
    /**
     * Enables the performance report button after a report window is closed.
     */
    public void enablePerformanceReportButton() {
        performanceReportButton.setDisable(false);
    }

    /**
     * Handles the generation of an orders report.
     *
     * @param event The ActionEvent triggered by clicking the Orders Report button.
     */
    @FXML
    private void handleOrdersReport(ActionEvent event) {
        String branch = branchDropdown.getValue();
        String monthStr = monthDropdown.getValue();
        String yearStr = yearDropdown.getValue();
        
        clearErrorMessage();
        if (monthStr == null || yearStr == null || monthStr.isEmpty() || yearStr.isEmpty()) {
            showErrorMessage("Please select both a month and a year before generating the report.");
            return;
        }

        if (branch == null && user.getType() == EnumType.BRANCH_MANAGER) {
            branch = user.getHomeBranch().toString();
        }
        if (branch == null) {
            showErrorMessage("Branch information is missing.");
            return;
        }
        try {
            int month = monthDropdown.getSelectionModel().getSelectedIndex() + 1;
            int year = Integer.parseInt(yearStr);
            OrdersReport report = new OrdersReport(EnumBranch.valueOf(branch.toUpperCase()), month, year);
            ordersReportButton.setDisable(true);
            client.getOrdersReport(report);
        } catch (NumberFormatException e) {
            showErrorMessage("Invalid year format. Please enter a valid year.");
        }
    }
    /**
     * Processes the response from the server for an orders report request.
     *
     * @param response The Object containing the server's response.
     */
    public void handleOrdersReportResponse(Object response) {
        Platform.runLater(() -> {
            if (response instanceof OrdersReport) {
                OrdersReport report = (OrdersReport) response;
                if (!report.getDishTypeAmountMap().isEmpty()) {
                    openOrdersReportWindow(report);
                } else {
                    showErrorMessage("No orders data available for the selected month and year.");
                    enableOrdersReportButton();
                }
            } else if (response instanceof String) {
                showErrorMessage((String) response);
                enableOrdersReportButton();
            } else {
                showErrorMessage("An unexpected error occurred while fetching the report.");
                enableOrdersReportButton();
            }
        });
    }
    /**
     * Opens a new window to display the orders report.
     *
     * @param report The OrdersReport to be displayed.
     */
    private void openOrdersReportWindow(OrdersReport report) {
        try {
            OrdersReportUI ordersReportUI = new OrdersReportUI();
            ordersReportUI.setReportData(report, this);
            ordersReportUI.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("An error occurred while opening the Orders Report window.");
            enableOrdersReportButton();
        }
    }
    
    /**
     * Enables the orders report button after a report window is closed.
     */
    public void enableOrdersReportButton() {
        ordersReportButton.setDisable(false);
    }
    /**
     * Handles the generation of a quarterly report. This is only available for CEO users.
     *
     * @param event The ActionEvent triggered by clicking the quarterly report button.
     */
    @FXML
    private void handleQuarterlyReport(ActionEvent event) {
        System.out.println("Quarterly Report button clicked");
        String branch = branchDropdown.getValue();
        String quarterStr = quarterDropdown.getValue();
        String yearStr = quarterYearDropdown.getValue();
        
        clearErrorMessage();
        if (quarterStr == null || yearStr == null || quarterStr.isEmpty() || yearStr.isEmpty()) {
            showErrorMessage("Please select both a quarter and a year before generating the report.");
            return;
        }

        if (branch == null) {
            showErrorMessage("Branch information is missing.");
            return;
        }
        
        try {
            int quarter = Integer.parseInt(quarterStr);
            int year = Integer.parseInt(yearStr);
            QuarterlyReport report = new QuarterlyReport(EnumBranch.valueOf(branch.toUpperCase()), quarter, year);
            client.getQuarterlyReport(report);
            System.out.println("Quarterly Report request sent to client");
        } catch (NumberFormatException e) {
            showErrorMessage("Invalid quarter or year format. Please enter valid numbers.");
        }
    }

    
    public void handleQuarterlyReportResponse(QuarterlyReport qreport, List<Double> monthlyIncomes) {
        System.out.println("Received quarterly report response");
        Platform.runLater(() -> {
            if (qreport != null && monthlyIncomes != null && monthlyIncomes.size() == 3) {
                openQuarterlyReportWindow(qreport, monthlyIncomes);
            } else {
                showErrorMessage("Invalid data received for quarterly report.");
            }
        });
    }

    
    private void openQuarterlyReportWindow(QuarterlyReport report, List<Double> monthlyIncomes) {
        System.out.println("Attempting to open Quarterly Report window");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("QuarterlyReport.fxml"));
            Parent root = loader.load();
            QuarterlyReportController controller = loader.getController();
            controller.setReportData(report, this, monthlyIncomes);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            System.out.println("Quarterly Report window opened successfully");
            
            openQuarterlyReportWindows++;
            if (openQuarterlyReportWindows >= 2) {
                quarterlyReportButton.setDisable(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showErrorMessage("An error occurred while opening the Quarterly Report window.");
        }
    }
    
    /**
     * Called when a quarterly report window is closed. Updates the count of open windows
     * and enables/disables the quarterly report button accordingly.
     */
    public void quarterlyReportWindowClosed() {
        openQuarterlyReportWindows--;
        if (openQuarterlyReportWindows < 2) {
            quarterlyReportButton.setDisable(false);
        }
    }
    /**
     * Displays an error message to the user.
     *
     * @param message The error message to be displayed.
     */
    private void showErrorMessage(String message) {
        errorMessageLabel.setText(message);
        errorMessageLabel.setVisible(true);
        errorMessageLabel.setManaged(true);
    }
    /**
     * Clears any displayed error message.
     */
    private void clearErrorMessage() {
        errorMessageLabel.setText("");
        errorMessageLabel.setVisible(false);
        errorMessageLabel.setManaged(false);
    }
    
    /**
     * Closes the application, performing necessary cleanup.
     */
    public void closeApplication() {
    	if (client != null) {
			client.userLogout(user, true);
		}
    }
}