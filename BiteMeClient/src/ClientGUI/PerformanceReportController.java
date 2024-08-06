package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import common.PerformanceReport;
import common.DailyPerformanceReport;
import javafx.application.Platform;
import javafx.scene.control.Tooltip;
import javafx.scene.Node;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class PerformanceReportController {

    @FXML private Label branchLabel;
    @FXML private Label monthYearLabel;
    @FXML private Label performancePercentageLabel;
    @FXML private StackedBarChart<String, Number> performanceChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    private ReportsPageController reportsPageController;

    public void setReportsPageController(ReportsPageController controller) {
        this.reportsPageController = controller;
    }

    public void setReportData(PerformanceReport report) {
        branchLabel.setText("Branch: " + report.getRestaurant().toString());
        monthYearLabel.setText("Report for: " + report.getMonth() + "/" + report.getYear());
        
        xAxis.setLabel("Day of Month");
        yAxis.setLabel("Number of Orders");

        XYChart.Series<String, Number> completedSeries = new XYChart.Series<>();
        completedSeries.setName("Completed in Time");

        XYChart.Series<String, Number> notCompletedSeries = new XYChart.Series<>();
        notCompletedSeries.setName("Not Completed in Time");

        List<DailyPerformanceReport> dailyReports = report.getDailyReports();
        int totalOrdersMonth = 0;
        int completedOrdersMonth = 0;

        for (DailyPerformanceReport dailyReport : dailyReports) {
            String day = String.valueOf(dailyReport.getDate().getDate());
            int totalOrders = dailyReport.getTotalOrders();
            int completedOrders = dailyReport.getOrdersCompletedInTime();
            int notCompletedOrders = totalOrders - completedOrders;

            completedSeries.getData().add(new XYChart.Data<>(day, completedOrders));
            notCompletedSeries.getData().add(new XYChart.Data<>(day, notCompletedOrders));

            totalOrdersMonth += totalOrders;
            completedOrdersMonth += completedOrders;
        }

        performanceChart.getData().addAll(completedSeries, notCompletedSeries);

        // Calculate overall performance percentage
        double performancePercentage = (double) completedOrdersMonth / totalOrdersMonth * 100;
        performancePercentageLabel.setText(String.format("Overall Performance: %.2f%%", performancePercentage));

        // Apply tooltips after chart has been laid out
        Platform.runLater(this::applyTooltips);
    }

    private void applyTooltips() {
        for (XYChart.Series<String, Number> series : performanceChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    String day = data.getXValue();
                    int value = data.getYValue().intValue();
                    String seriesName = series.getName();
                    
                    Tooltip tooltip = new Tooltip(String.format("%s on Day %s: %d orders", seriesName, day, value));
                    tooltip.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
                    Tooltip.install(node, tooltip);

                    node.setOnMouseEntered(event -> node.setOpacity(0.8));
                    node.setOnMouseExited(event -> node.setOpacity(1));
                }
            }
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) branchLabel.getScene().getWindow();
        reportsPageController.enablePerformanceReportButton();
        stage.close();
    }
}