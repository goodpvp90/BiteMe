	package UpperManagementReportProcess;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import reports.PerformanceReport;

/**
 * The PerformanceReportUI class is responsible for launching and displaying the Performance Report window
 * in the BiteMe application. It loads the FXML file, sets up the primary stage, and establishes
 * the connection between the UI, its controller, and the main Reports Page.
 */
public class PerformanceReportUI extends Application {

    /**
     * The PerformanceReport object containing the data to be displayed in the report.
     */
    private PerformanceReport report;

    /**
     * Reference to the ReportsPageController for callback communication.
     */
    private ReportsPageController reportsPageController;

    /**
     * Sets the report data and the reference to the ReportsPageController.
     * This method is called before the start method to provide necessary data and context.
     *
     * @param report The PerformanceReport containing the performance data to be displayed
     * @param controller The ReportsPageController instance for callback communication
     */
    public void setReportData(PerformanceReport report, ReportsPageController controller) {
        this.report = report;
        this.reportsPageController = controller;
    }
    /**
     * The main entry point for the Performance Report UI.
     * This method is called by the JavaFX runtime to initialize and display the Performance Report window.
     *
     * @param primaryStage The primary stage for this application, onto which
     * the application scene can be set.
     * @throws Exception If there's an error during the initialization or loading of the FXML file.
     */
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