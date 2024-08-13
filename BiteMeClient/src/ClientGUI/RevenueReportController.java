package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import reports.IncomeReport;

/**
 * Controller class for the Revenue Report window in the BiteMe application.
 * This controller manages the display of Revenue report data and handles user interactions
 * within the Revenue Report window.
 */
public class RevenueReportController {

	/**
	 * Label for displaying the branch information.
	 */
	@FXML private Label branchLabel;

	/**
	 * Label for displaying the month and year of the report.
	 */
	@FXML private Label monthYearLabel;

	/**
	 * Label for displaying the total revenue.
	 */
	@FXML private Label revenueLabel;

	/**
	 * Label for displaying error messages.
	 */
	@FXML private Label errorLabel;

	/**
	 * Reference to the ReportsPageController for communication with the main Reports Page.
	 */
	private ReportsPageController reportsPageController;
    
    /**
     * Sets the reference to the ReportsPageController.
     * This allows communication back to the main reports page.
     *
     * @param controller The ReportsPageController instance
     */
    public void setReportsPageController(ReportsPageController controller) {
        this.reportsPageController = controller;
    }
    
    /**
     * Populates the Revenue Report window with data from the IncomeReport.
     *
     * @param report The IncomeReport containing the revenue data to be displayed
     */
    public void setReportData(IncomeReport report) {
        branchLabel.setText("Branch: " + report.getRestaurant().toString());
        monthYearLabel.setText("Month/Year: " + report.getMonth() + "/" + report.getYear());
        revenueLabel.setText(String.format("$%.2f", report.getIncome()));
    }

    /**
     * Handles the closing of the Revenue Report window.
     * This method is called when the user clicks the close button.
     * It notifies the ReportsPageController to re-enable the revenue report button.
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) branchLabel.getScene().getWindow();
        reportsPageController.enableRevenueReportButton();
        stage.close();
    }
}