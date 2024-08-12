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

public class QuarterlyReportController {

    @FXML private BarChart<String, Number> reportChart;
    @FXML private Label reportIncome;
    @FXML private Label monthlyIncome;

    private ReportsPageController parentController;
    private QuarterlyReport report;
    private List<Double> monthlyIncomes;

    public void initialize() {
        // Set bold font for labels
        Font boldFont = Font.font(reportIncome.getFont().getFamily(), FontWeight.BOLD, reportIncome.getFont().getSize());
        reportIncome.setFont(boldFont);
        monthlyIncome.setFont(boldFont);
    }

    public void setReportData(QuarterlyReport report, ReportsPageController controller, List<Double> monthlyIncomes) {
        this.parentController = controller;
        this.report = report;
        this.monthlyIncomes = monthlyIncomes;
        updateChart();
    }

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

    private void updateIncomeLabels() {
        // Update total income label
        reportIncome.setText(String.format("Total Income: $%d", report.getIncome()));

        // Update monthly income label
        StringBuilder monthlyIncomeText = new StringBuilder();
        String[] monthNames = getMonthNames(report.getQuarter());
        for (int i = 0; i < monthlyIncomes.size(); i++) {
            if (i > 0) monthlyIncomeText.append("   ");
            monthlyIncomeText.append(String.format("%s: $%.2f", monthNames[i], monthlyIncomes.get(i)));
        }
        monthlyIncome.setText("|     "+monthlyIncomeText.toString());
    }

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
     * Updates the window title with the report details.
     * This method sets the stage title to include the Branch, Quarter, and Year from the report.
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
     * This method attaches a close request handler to the stage to ensure proper cleanup when the window is closed.
     */
    public void setupCloseHandler() {
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