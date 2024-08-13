package ClientGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import reports.QuarterlyReport;

import java.util.List;

/**
 * The QuarterlyReportUI class is responsible for creating and displaying the UI
 * for the Quarterly Report in the BiteMe application.
 * It extends the JavaFX Application class to create a standalone window for the report.
 */
public class QuarterlyReportUI extends Application {

    /**
     * The QuarterlyReport object containing the report data.
     */
    private QuarterlyReport report;

    /**
     * Reference to the ReportsPageController for callback communication.
     */
    private ReportsPageController reportsPageController;

    /**
     * List of monthly incomes for the quarter.
     */
    private List<Double> monthlyIncomes;

    /**
     * Constructor to initialize the QuarterlyReportUI with necessary data.
     *
     * @param report The QuarterlyReport object containing the report data.
     * @param controller The ReportsPageController that initiated this report.
     * @param monthlyIncomes A List of Double values representing monthly incomes.
     */
    public QuarterlyReportUI(QuarterlyReport report, ReportsPageController controller, List<Double> monthlyIncomes) {
        this.report = report;
        this.reportsPageController = controller;
        this.monthlyIncomes = monthlyIncomes;
    }

    /**
     * Starts the QuarterlyReportUI application.
     * This method is called automatically when the application is launched.
     * It loads the FXML file, sets up the controller, and displays the window.
     *
     * @param primaryStage The primary stage for this application.
     * @throws Exception If there's an error during the start process.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("QuarterlyReport.fxml"));
        Parent root = loader.load();
        QuarterlyReportController controller = loader.getController();
        controller.setReportData(report, reportsPageController, monthlyIncomes);
        primaryStage.setTitle("Quarterly Report");
        primaryStage.setScene(new Scene(root));
        controller.updateWindowTitle();
        controller.setupCloseHandler();
        primaryStage.show();
    }
}