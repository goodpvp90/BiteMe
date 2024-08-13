package ClientGUI;

import java.util.Map;

import enums.EnumDish;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import reports.OrdersReport;

/**
 * Controller class for the Orders Report window in the BiteMe application.
 * This controller manages the display of order statistics for a specific branch and time period.
 * It is accessed from the Reports Page.
 */
public class OrdersReportController {

    @FXML private TableView<Map.Entry<EnumDish, Integer>> ordersTable;
    @FXML private TableColumn<Map.Entry<EnumDish, Integer>, String> dishTypeColumn;
    @FXML private TableColumn<Map.Entry<EnumDish, Integer>, Integer> amountColumn;
    @FXML private Label branchLabel;
    @FXML private Label monthYearLabel;

    private ReportsPageController reportsPageController;

    /**
     * Initializes the controller. This method is automatically called after the FXML file has been loaded.
     * It sets up the cell value factories for the table columns.
     */
    public void initialize() {
        dishTypeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.ReadOnlyStringWrapper(cellData.getValue().getKey().toString()));
        amountColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.ReadOnlyObjectWrapper<>(cellData.getValue().getValue()));
    }

    /**
     * Sets the report data to be displayed in the Orders Report window.
     * 
     * @param report The OrdersReport object containing the data to be displayed.
     */
    public void setReportData(OrdersReport report) {
        branchLabel.setText("Branch: " + report.getRestaurant().toString());
        monthYearLabel.setText("Month/Year: " + report.getMonth() + "/" + report.getYear());       
        ObservableList<Map.Entry<EnumDish, Integer>> data = 
            FXCollections.observableArrayList(report.getDishTypeAmountMap().entrySet());
        ordersTable.setItems(data);
    }
      
    /**
     * Sets the reference to the ReportsPageController.
     * This allows for communication back to the main Reports Page.
     * 
     * @param controller The ReportsPageController instance.
     */
    public void setReportsPageController(ReportsPageController controller) {
        this.reportsPageController = controller;
    }

    /**
     * Handles the close action for the Orders Report window.
     * This method is called when the user attempts to close the window.
     * It notifies the ReportsPageController to re-enable the Orders Report button.
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) ordersTable.getScene().getWindow();
        reportsPageController.enableOrdersReportButton();
        stage.close();
    }
}