package ClientGUI;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import reports.QuarterlyReport;

/**
 * Controller class for handling the Quarterly Report view in the BiteMe application.
 * This class manages the display of quarterly report data including a bar chart of number of days accroding to order range count
 * and monthly income information of the quarter.
 */
public class QuarterlyReportController {

    /**
     * Bar chart for displaying the report data.
     */
    @FXML 
    private BarChart<String, Number> reportChart;

    /**
     * Label for displaying the total income for the quarter.
     */
    @FXML 
    private Label reportIncome;

    /**
     * Label for displaying the monthly income breakdown.
     */
    @FXML 
    private Label monthlyIncome;

    /**
     * Reference to the parent ReportsPageController.
     */
    private 
    ReportsPageController parentController;

    /**
     * The QuarterlyReport object containing the report data.
     */
    private 
    QuarterlyReport report;

    /**
     * List of monthly incomes for the quarter.
     */
    private 
    List<Double> monthlyIncomes;


    /**
     * Initializes the controller. This method is automatically called
     * after the FXML file has been loaded.
     */
    public void initialize() {
        // Set bold font for labels Not must, Only for design.
        Font boldFont = Font.font(reportIncome.getFont().getFamily(), FontWeight.BOLD, reportIncome.getFont().getSize());
        reportIncome.setFont(boldFont);
        monthlyIncome.setFont(boldFont);
    }

    /**
     * Sets the report data and updates the chart and labels.
     *
     * @param report The QuarterlyReport object containing the Quarter report data.
     * @param controller The parent ReportsPageController.
     * @param monthlyIncomes List of monthly income values.
     */
    public void setReportData(QuarterlyReport report, ReportsPageController controller, List<Double> monthlyIncomes) {
        this.parentController = controller;
        this.report = report;
        this.monthlyIncomes = monthlyIncomes;
        updateChart();
    }

    /**
     * Updates the bar chart with the report data.
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

        // Update the income labels
        updateIncomeLabels();
    }

    /**
     * Updates the income labels with total and monthly income information.
     */
    private void updateIncomeLabels() {
        reportIncome.setText(String.format("Total Income: $%d", report.getIncome()));
        StringBuilder monthlyIncomeText = new StringBuilder();
        String[] monthNames = getMonthNames(report.getQuarter());
        for (int i = 0; i < monthlyIncomes.size(); i++) {
            if (i > 0) monthlyIncomeText.append("   ");
            monthlyIncomeText.append(String.format("%s: $%.2f", monthNames[i], monthlyIncomes.get(i)));
        }
        monthlyIncome.setText("|     "+monthlyIncomeText.toString());
    }

    /**
     * Gets an array of month names for the given quarter.
     *
     * @param quarter The quarter number (1-4).
     * @return An array of month names corresponding to the given quarter.
     */
    private String[] getMonthNames(int quarter) {
        switch (quarter) {
            case 1: return new String[]{"January", "February", "March"};
            case 2: return new String[]{"April", "May", "June"};
            case 3: return new String[]{"July", "August", "September"};
            case 4: return new String[]{"October", "November", "December"};
            default: return new String[]{"Month 1", "Month 2", "Month 3"};
        }
    }

    /**
     * Updates the window title with the report information.
     */
    public void updateWindowTitle() {
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
     */
    public void setupCloseHandler() {
        Stage stage = (Stage) reportChart.getScene().getWindow();
        if (stage != null) {
            stage.setOnCloseRequest(this::handleCloseRequest);
        }
    }
 
    /**
     * Handles the window close request.
     *
     * @param event The WindowEvent triggered by the close request.
     */
    private void handleCloseRequest(WindowEvent event) {
        closeWindow();
        event.consume();
    }

    /**
     * Closes the quarterly report window and notifies the parent controller.
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