package ClientGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import common.IncomeReport;

public class RevenueReportUI extends Application {
    private IncomeReport report;
    private ReportsPageController reportsPageController;

    public void setReportData(IncomeReport report, ReportsPageController controller) {
        this.report = report;
        this.reportsPageController = controller;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RevenueReportWindow.fxml"));
        Parent root = loader.load();
        
        RevenueReportController controller = loader.getController();
        controller.setReportData(report);
        controller.setReportsPageController(reportsPageController);

        primaryStage.setTitle("Revenue Report");
        primaryStage.setScene(new Scene(root));
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            reportsPageController.enableRevenueReportButton();
            primaryStage.close();
        });
        primaryStage.show();
    }
}