package ClientGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import reports.QuarterlyReport;

import java.util.List;

public class QuarterlyReportUI extends Application {
    private QuarterlyReport report;
    private ReportsPageController reportsPageController;
    private List<Double> monthlyIncomes;

    public void setReportData(QuarterlyReport report, ReportsPageController controller, List<Double> monthlyIncomes) {
        this.report = report;
        this.reportsPageController = controller;
        this.monthlyIncomes = monthlyIncomes;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("QuarterlyReport.fxml"));
        Parent root = loader.load();
        QuarterlyReportController controller = loader.getController();
        controller.setReportData(report, reportsPageController, monthlyIncomes);

        primaryStage.setTitle("Quarterly Report");
        primaryStage.setScene(new Scene(root));

        // Call the updateWindowTitle method
        controller.updateWindowTitle();

        // Set up the close handler
        controller.setupCloseHandler();

        primaryStage.show();
    }
}