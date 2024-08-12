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

public class OrdersReportController {

    @FXML private TableView<Map.Entry<EnumDish, Integer>> ordersTable;
    @FXML private TableColumn<Map.Entry<EnumDish, Integer>, String> dishTypeColumn;
    @FXML private TableColumn<Map.Entry<EnumDish, Integer>, Integer> amountColumn;
    @FXML private Label branchLabel;
    @FXML private Label monthYearLabel;

    private ReportsPageController reportsPageController;

    public void initialize() {
        dishTypeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.ReadOnlyStringWrapper(cellData.getValue().getKey().toString()));
        amountColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.ReadOnlyObjectWrapper<>(cellData.getValue().getValue()));
    }

    public void setReportData(OrdersReport report) {
        branchLabel.setText("Branch: " + report.getRestaurant().toString());
        monthYearLabel.setText("Month/Year: " + report.getMonth() + "/" + report.getYear());
        
        ObservableList<Map.Entry<EnumDish, Integer>> data = 
            FXCollections.observableArrayList(report.getDishTypeAmountMap().entrySet());
        ordersTable.setItems(data);
    }
    
    public void setReportsPageController(ReportsPageController controller) {
        this.reportsPageController = controller;
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) ordersTable.getScene().getWindow();
        reportsPageController.enableOrdersReportButton();
        stage.close();
    }
}