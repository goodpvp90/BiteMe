package ClientGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import reports.OrdersReport;

/**
 * The OrdersReportUI class is responsible for launching and displaying the Orders Report window
 * in the BiteMe application. It loads the FXML file, sets up the primary stage, and establishes
 * the connection between the UI, its controller, and the main Reports Page.
 */
public class OrdersReportUI extends Application {
	
    /**
     * The OrdersReport object containing the data to be displayed in the report.
     */
    private OrdersReport report;
    
    /**
     * Reference to the ReportsPageController for callback communication.
     */
    private ReportsPageController reportsPageController;

    /**
     * Sets the report data and the reference to the ReportsPageController.
     * This method is called before the start method to provide necessary data and context.
     *
     * @param report The OrdersReport containing the Orders data to be displayed
     * @param controller The ReportsPageController instance for callback communication
     */
    public void setReportData(OrdersReport report, ReportsPageController controller) {
        this.report = report;
        this.reportsPageController = controller;
    }
 
    /**
     * The main entry point for the Orders Report UI.
     * This method is called by the JavaFX runtime to initialize and display the Orders Report window.
     *
     * @param primaryStage The primary stage for this application, onto which
     * the application scene can be set.
     * @throws Exception If there's an error during the initialization or loading of the FXML file.
     */
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