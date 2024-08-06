package ClientGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import common.PerformanceReport;

public class PerformanceReportUI extends Application {
    private PerformanceReport report;
    private ReportsPageController reportsPageController;

    public void setReportData(PerformanceReport report, ReportsPageController controller) {
        this.report = report;
        this.reportsPageController = controller;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PerformanceReportWindow.fxml"));
        Parent root = loader.load();
        
        PerformanceReportController controller = loader.getController();
        controller.setReportData(report);
        controller.setReportsPageController(reportsPageController);

        primaryStage.setTitle("Performance Report");
        primaryStage.setScene(new Scene(root));
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            reportsPageController.enablePerformanceReportButton();
            primaryStage.close();
        });
        primaryStage.show();
    }
}