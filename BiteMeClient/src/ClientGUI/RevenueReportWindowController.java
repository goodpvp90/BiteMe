package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import common.IncomeReport;
import common.Restaurant.Location;
import client.Client;
import java.util.function.Consumer;

public class RevenueReportWindowController {

    @FXML private Label branchLabel;
    @FXML private Label monthYearLabel;
    @FXML private Label revenueLabel;
    @FXML private Label errorLabel;

    private Client client;
    private ReportsPageController reportsPageController;
    public void initialize() {
        client = Client.getInstance();
    }
    
    public void setReportsPageController(ReportsPageController controller) {
        this.reportsPageController = controller;
    }

    public void setReportData(IncomeReport report) {
        branchLabel.setText("Branch: " + report.getRestaurant().toString());
        monthYearLabel.setText("Month/Year: " + report.getMonth() + "/" + report.getYear());
        revenueLabel.setText(String.format("$%.2f", report.getIncome()));
    }
   

    @FXML
    private void handleClose() {
        Stage stage = (Stage) branchLabel.getScene().getWindow();
        stage.close();
        
        // Re-enable the Revenue Report button in the ReportsPageController
        if (reportsPageController != null) {
            reportsPageController.enableRevenueReportButton();
        }
    }
}