package ClientGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import reports.IncomeReport;

/**
 * The RevenueReportUI class is responsible for launching and displaying the Revenue Report window
 * in the BiteMe application. It loads the FXML file, sets up the primary stage, and establishes
 * the connection between the UI, its controller, and the main Reports Page.
 */
public class RevenueReportUI extends Application {
	
	/**
	 * The IncomeReport containing the revenue data to be displayed in the Revenue Report UI.
	 */
    private IncomeReport report;
    
    /**
     * The ReportsPageController instance that manages the main Reports Page.
     * This reference is used for callback communication, such as re-enabling the Revenue Report button
     * when the Revenue Report window is closed.
     */
    private ReportsPageController reportsPageController;

    /**
     * Sets the report data and the reference to the ReportsPageController.
     * This method is called before the start method to provide necessary data and context.
     *
     * @param report The IncomeReport containing the revenue data to be displayed
     * @param controller The ReportsPageController instance for callback communication
     */
    public void setReportData(IncomeReport report, ReportsPageController controller) {
        this.report = report;
        this.reportsPageController = controller;
    }

    /**
     * The main entry point for the Revenue Report UI.
     * This method is called by the JavaFX runtime to initialize and display the Revenue Report window.
     *
     * @param primaryStage The primary stage for this application, onto which
     * the application scene can be set.
     * @throws Exception If there's an error during the initialization or loading of the FXML file.
     */
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