package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import common.OrdersReport;
import common.EnumDish;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class OrdersReportWindowController {

    @FXML private TableView<OrdersReportEntry> ordersTable;
    @FXML private TableColumn<OrdersReportEntry, String> dishTypeColumn;
    @FXML private TableColumn<OrdersReportEntry, Integer> amountColumn;

    private ReportsPageController reportsPageController;

    public void initialize() {
        dishTypeColumn.setCellValueFactory(new PropertyValueFactory<>("dishType"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
    }

    public void setReportData(OrdersReport report) {
        ObservableList<OrdersReportEntry> data = FXCollections.observableArrayList();
        for (EnumDish dishType : report.getDishTypeAmountMap().keySet()) {
            data.add(new OrdersReportEntry(dishType.toString(), report.getDishTypeAmountMap().get(dishType)));
        }
        ordersTable.setItems(data);
    }

    public void setReportsPageController(ReportsPageController controller) {
        this.reportsPageController = controller;
    }

    @FXML
    private void handleClose() {
        ordersTable.getScene().getWindow().hide();
        reportsPageController.enableOrdersReportButton();
    }

    public static class OrdersReportEntry {
        private String dishType;
        private int amount;

        public OrdersReportEntry(String dishType, int amount) {
            this.dishType = dishType;
            this.amount = amount;
        }

        public String getDishType() { return dishType; }
        public int getAmount() { return amount; }
    }
}