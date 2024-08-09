package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import common.QuarterlyReport;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.application.Platform;

/**
 * Controller class for the Quarterly Report view.
 * Handles the display and interaction of quarterly report data.
 */
public class QuarterlyReportController {

    @FXML
    private BarChart<String, Number> reportChart;

    @FXML
    private Label reportIncome;

    private ReportsPageController parentController;
    private QuarterlyReport report;

    /**
     * Initializes the controller. This method is automatically called after the FXML file has been loaded.
     * It schedules the window title update and close handler setup to run on the JavaFX Application Thread.
     */
    public void initialize() {
        Platform.runLater(() -> {
            updateWindowTitle();
            setupCloseHandler();
        });
    }

    /**
     * Sets the report data and updates the chart.
     * This method is called from the ReportsPageController to provide the report data and set up the parent controller reference.
     *
     * @param report The QuarterlyReport data to be displayed
     * @param controller The parent ReportsPageController
     */
    public void setReportData(QuarterlyReport report, ReportsPageController controller) {
        this.parentController = controller;
        this.report = report;
        updateChart();
    }

    /**
     * Updates the chart with the current report data.
     * This method clears the existing chart data, creates a new reports with the report data,
     * and adds tooltips to each data point.
     */
    private void updateChart() {
        reportChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Days per Order Count Range");

        String[] ranges = {"0-20", "21-40", "41-60", "61-80", "81-100", "101-120", "121-140", "141-160", "161-180", "181-200", "201+"};
        String[] dataKeys = {"0_20", "21_40", "41_60", "61_80", "81_100", "101_120", "121_140", "141_160", "161_180", "181_200", "201_plus"};

        for (int i = 0; i < ranges.length; i++) {
            Integer value = report.getDaysInRanges().getOrDefault(dataKeys[i], 0);
            XYChart.Data<String, Number> data = new XYChart.Data<>(ranges[i], value);
            series.getData().add(data);

            Tooltip tooltip = new Tooltip(value + " days");
            Tooltip.install(data.getNode(), tooltip);
        }

        reportChart.getData().add(series);
        reportIncome.setText("Total Income: $" + report.getIncome());
    }

    /**
     * Updates the window title with the report details.
     * This method sets the stage title to include the Branch, Quarter, and Year from the report.
     */
    private void updateWindowTitle() {
        if (report != null) {
            String title = report.getRestaurant() + " Q" + report.getQuarter() + " " + report.getYear();
            Stage stage = (Stage) reportChart.getScene().getWindow();
            if (stage != null) {
                stage.setTitle(title);
            }
        }
    }
    
    /**
     * Sets up the close handler for the window.
     * This method attaches a close request handler to the stage to ensure proper cleanup when the window is closed.
     */
    private void setupCloseHandler() {
        Stage stage = (Stage) reportChart.getScene().getWindow();
        if (stage != null) {
            stage.setOnCloseRequest(this::handleCloseRequest);
        }
    }
    
    /**
     * Handles the close request event.
     * This method is called when the user attempts to close the window. It calls the closeWindow method
     * and consumes the event to prevent the default close operation.
     *
     * @param event The WindowEvent triggered by the close request
     */
    private void handleCloseRequest(WindowEvent event) {
        closeWindow();
        event.consume();
    }

    /**
     * Closes the window and performs necessary cleanup.
     * This method notifies the parent controller that the window is closing, then closes the stage.
     * It's called both by the custom close button and when the user clicks the window's close button.
     */
    @FXML
    public void closeWindow() {
        if (parentController != null) {
            try {
                parentController.quarterlyReportWindowClosed();
            } catch (Exception e) {
                System.err.println("Error calling quarterlyReportWindowClosed: " + e.getMessage());
            }
        }
        Stage stage = (Stage) reportChart.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}