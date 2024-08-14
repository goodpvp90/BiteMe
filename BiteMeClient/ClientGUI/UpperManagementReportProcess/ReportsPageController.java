package UpperManagementReportProcess;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import UserHomePageProcess.UserHomePageUI;
import client.Client;
import enums.EnumBranch;
import enums.EnumType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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

	/**
	 * Button to return to the previous page.
	 */
	@FXML 
	private Button backButton;

	/**
	 * Button to generate a revenue report.
	 */
	@FXML 
	private Button revenueReportButton;

	/**
	 * Button to generate a performance report.
	 */
	@FXML 
	private Button performanceReportButton;

	/**
	 * Button to generate an orders report.
	 */
	@FXML 
	private Button ordersReportButton;

	/**
	 * Button to generate a quarterly report.
	 */
	@FXML 
	private Button quarterlyReportButton;

	/**
	 * Dropdown menu for selecting the branch.
	 */
	@FXML 
	private ComboBox<String> branchDropdown;

	/**
	 * Dropdown menu for selecting the month.
	 */
	@FXML 
	private ComboBox<String> monthDropdown;

	/**
	 * Dropdown menu for selecting the year.
	 */
	@FXML 
	private ComboBox<String> yearDropdown;

	/**
	 * Dropdown menu for selecting the quarter.
	 */
	@FXML 
	private ComboBox<String> quarterDropdown;

	/**
	 * Dropdown menu for selecting the year for quarterly reports.
	 */
	@FXML 
	private ComboBox<String> quarterYearDropdown;

	/**
	 * Container for quarterly report UI elements.
	 */
	@FXML 
	private VBox quarterlyReportSection;

	/**
	 * Grid layout for organizing report UI elements.
	 */
	@FXML 
	private GridPane reportsGridPane;

	/**
	 * Label for displaying error messages.
	 */
	@FXML 
	private Label errorMessageLabel;
    
    /**
     * The client instance for communication with the server.
     */
    private Client client;

    /**
     * The User object representing the current user.
     */
    private User user;

    /**
     * Counter for the number of open quarterly report windows.
     */
    private int openQuarterlyReportWindows = 0;

    /**
     * Initializes the controller. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        client = Client.getInstance();
        client.setReportsPageController(this);
        setupDropdowns();
        clearErrorMessage();
        yearDropdown.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                updateMonthDropdown();
            }
        });
        quarterYearDropdown.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                updateQuarterDropdown();
            }
        });
    }
    
    /**
     * Sets the user for this controller and updates the UI accordingly.
     * This method is called when navigating from the User Home Page.
     *
     * @param user The User object representing the current user.
     * @param isRegistered A boolean indicating whether the user is registered (Only Customer can be NOT registered).
     * 
     */
    public void setUser(User user) {
        this.user = user;
        setUserType(user.getType());
        if (user.getType() == EnumType.BRANCH_MANAGER) {
            branchDropdown.setValue(user.getHomeBranch().toString());
            branchDropdown.setDisable(true);
        } else if (user.getType() == EnumType.CEO) {
            branchDropdown.getItems().clear();
            branchDropdown.getItems().addAll(
                Arrays.stream(EnumBranch.values())
                    .map(Enum::toString)
                    .collect(Collectors.toList())
            );
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
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();
        
        // Setup month dropdown
        monthDropdown.getItems().clear();
        for (Month month : Month.values()) {
            monthDropdown.getItems().add(month.toString());
        }
        // Setup year dropdowns
        yearDropdown.getItems().clear();
        quarterYearDropdown.getItems().clear();
        yearDropdown.getItems().addAll(String.valueOf(currentYear - 1), String.valueOf(currentYear));
        quarterYearDropdown.getItems().addAll(String.valueOf(currentYear - 1), String.valueOf(currentYear));
        // Setup quarter dropdown
        quarterDropdown.getItems().clear();
        for (int i = 1; i <= 4; i++) {
            quarterDropdown.getItems().add(String.valueOf(i));
        }
        // Disable future months and quarters
        monthDropdown.setOnShowing(event -> updateMonthDropdown());
        quarterDropdown.setOnShowing(event -> updateQuarterDropdown());        
        yearDropdown.setOnAction(event -> updateMonthDropdown());
        quarterYearDropdown.setOnAction(event -> updateQuarterDropdown());
    }
    
    /**
     * Updates the month dropdown based on the selected year.
     */
    private void updateMonthDropdown() {
        if (yearDropdown.getValue() == null) return;
        int selectedYear = Integer.parseInt(yearDropdown.getValue());
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();
        if (selectedYear < currentYear) {
            // Show all months for past years
            monthDropdown.setItems(FXCollections.observableArrayList(
                Arrays.stream(Month.values())
                    .map(Month::toString)
                    .collect(Collectors.toList())));
        } else if (selectedYear == currentYear) {
            // Show months up to the current month for the current year
            monthDropdown.setItems(FXCollections.observableArrayList(
                Arrays.stream(Month.values())
                    .filter(month -> month.getValue() < currentMonth)
                    .map(Month::toString)
                    .collect(Collectors.toList())));
        } else {
            // Clear the dropdown for future years
            monthDropdown.getItems().clear();
        }
    }

    /**
     * Updates the quarter dropdown based on the selected year.
     */
    private void updateQuarterDropdown() {
        if (quarterYearDropdown.getValue() == null) return;

        int selectedYear = Integer.parseInt(quarterYearDropdown.getValue());
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int currentQuarter = (currentDate.getMonthValue() - 1) / 3 + 1;

        if (selectedYear < currentYear) {
            // Show all quarters for past years
            quarterDropdown.setItems(FXCollections.observableArrayList("1", "2", "3", "4"));
        } else if (selectedYear == currentYear) {
            // Show quarters up to the current quarter for the current year
            quarterDropdown.setItems(FXCollections.observableArrayList(
                java.util.stream.IntStream.rangeClosed(1, currentQuarter)
                    .mapToObj(String::valueOf)
                    .toList()));
        } else {
            // Clear the dropdown for future years
            quarterDropdown.getItems().clear();
        }
    }
    

    /**
     * Handles the back button action, returning to the User Home Page.
     *
     * @param event The ActionEvent triggered by clicking the back button.
     */
    @FXML
    private void handleBackButton(ActionEvent event) {
    	 try {
             Stage userHomePageStage = UserHomePageUI.getStage();
             if (userHomePageStage != null) {
                 userHomePageStage.show();  // Show the hidden stage again
             } else {
                 // If the stage is somehow null, recreate and show it
                 UserHomePageUI Userapp = new UserHomePageUI(user);
                 Userapp.start(new Stage());
             }
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
                    showErrorMessage("no such report exists.");
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
            LocalDate currentDate = LocalDate.now();
            LocalDate quarterEndDate = YearMonth.of(year, quarter * 3).atEndOfMonth();
            if (quarterEndDate.isAfter(currentDate)) {
                showErrorMessage("The selected quarter has not ended yet. Please choose a completed quarter.");
                return;
            }
            QuarterlyReport report = new QuarterlyReport(EnumBranch.valueOf(branch.toUpperCase()), quarter, year);
            client.getQuarterlyReport(report);
        } catch (NumberFormatException e) {
            showErrorMessage("Invalid quarter or year format. Please enter valid numbers.");
        }
    }

    /**
     * Handles the generation of a quarterly report. This is only available for CEO users.
     *
     * @param event The ActionEvent triggered by clicking the quarterly report button.
     */
    public void handleQuarterlyReportResponse(QuarterlyReport qreport, List<Double> monthlyIncomes) {
        System.out.println("Received quarterly report response");
        Platform.runLater(() -> {
            if (qreport != null && monthlyIncomes != null) {
                openQuarterlyReportWindow(qreport, monthlyIncomes);
            } else {
                showErrorMessage("Invalid data received for quarterly report.");
            }
        });
    }

    /**
     * Processes the response from the server for a quarterly report request.
     *
     * @param qreport The QuarterlyReport object received from the server.
     * @param monthlyIncomes List of monthly incomes for the quarter.
     */
    private void openQuarterlyReportWindow(QuarterlyReport report, List<Double> monthlyIncomes) {
        try {
        	QuarterlyReportUI quarterlyReportUI = new QuarterlyReportUI(report, this, monthlyIncomes);
            quarterlyReportUI.start(new Stage());            
            openQuarterlyReportWindows++;
            if (openQuarterlyReportWindows >= 2) {
                quarterlyReportButton.setDisable(true);
            }
        } catch (Exception e) {
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