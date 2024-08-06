package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import common.IncomeReport;

public class RevenueReportController {

    @FXML private Label branchLabel;
    @FXML private Label monthYearLabel;
    @FXML private Label revenueLabel;
    @FXML private Label errorLabel;

    private ReportsPageController reportsPageController;

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
        reportsPageController.enableRevenueReportButton();
        stage.close();
    }
}