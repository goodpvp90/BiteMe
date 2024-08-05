package ClientGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import common.OrdersReport;

public class OrdersReportUI extends Application {
    private OrdersReport report;
    private ReportsPageController reportsPageController;

    public void setReportData(OrdersReport report, ReportsPageController controller) {
        this.report = report;
        this.reportsPageController = controller;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("OrdersReportWindow.fxml"));
        Parent root = loader.load();
        
        OrdersReportController controller = loader.getController();
        controller.setReportData(report);
        controller.setReportsPageController(reportsPageController);

        primaryStage.setTitle("Orders Report");
        primaryStage.setScene(new Scene(root));
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            reportsPageController.enableOrdersReportButton();
            primaryStage.close();
        });
        primaryStage.show();
    }
}