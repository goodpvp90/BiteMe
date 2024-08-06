package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;
import common.PerformanceReport;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PerformanceReportController {

    @FXML private Label branchLabel;
    @FXML private Label monthYearLabel;
    @FXML private Label performancePercentageLabel;
    @FXML private PieChart performanceChart;
    @FXML private Label completedLabel;
    @FXML private Label notCompletedLabel;

    private ReportsPageController reportsPageController;

    public void setReportsPageController(ReportsPageController controller) {
        this.reportsPageController = controller;
    }

    public void setReportData(PerformanceReport report) {
        branchLabel.setText("Branch: " + report.getRestaurant().toString());
        monthYearLabel.setText("Produced at: " + report.getMonth() + "/" + report.getYear());
        
        int totalOrders = report.getTotalOrders();
        int ordersCompletedInTime = report.getOrdersCompletedInTime();
        int ordersNotCompletedInTime = totalOrders - ordersCompletedInTime;
        
        double performancePercentage = (double) ordersCompletedInTime / totalOrders * 100;
        performancePercentageLabel.setText(String.format("Performance: %.2f%%", performancePercentage));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("", ordersCompletedInTime),
            new PieChart.Data("", ordersNotCompletedInTime)
        );
        
        performanceChart.setData(pieChartData);
        performanceChart.setStartAngle(90);

        pieChartData.get(0).getNode().setStyle("-fx-pie-color: #2ecc71;");
        pieChartData.get(1).getNode().setStyle("-fx-pie-color: #e74c3c;");
        
        performanceChart.setLegendVisible(false);
        
        completedLabel.setText(String.format("Completed in Time: %.1f (%.1f%%)", (double)ordersCompletedInTime, performancePercentage));
        notCompletedLabel.setText(String.format("Not Completed in Time: %.1f (%.1f%%)", (double)ordersNotCompletedInTime, 100 - performancePercentage));
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) branchLabel.getScene().getWindow();
        reportsPageController.enablePerformanceReportButton();
        stage.close();
    }
}