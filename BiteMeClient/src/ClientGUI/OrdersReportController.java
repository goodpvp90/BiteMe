package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import common.OrdersReport;
import common.EnumDish;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Controller class for the Orders Report window in the BiteMe application.
 * This controller manages the display of orders report data and handles user interactions
 * within the Orders Report window.
 */
public class OrdersReportController {

    @FXML private TableView<OrdersReportEntry> ordersTable;
    @FXML private TableColumn<OrdersReportEntry, String> dishTypeColumn;
    @FXML private TableColumn<OrdersReportEntry, Integer> amountColumn;

    private ReportsPageController reportsPageController;

    /**
     * Initializes the controller. This method is automatically called after the FXML file has been loaded.
     * It sets up the cell value factories for the table columns.
     */
    public void initialize() {
        dishTypeColumn.setCellValueFactory(new PropertyValueFactory<>("dishType"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
    }

    /**
     * Populates the Orders Report table with data from the OrdersReport.
     *
     * @param report The OrdersReport containing the orders data to be displayed
     */
    public void setReportData(OrdersReport report) {
        ObservableList<OrdersReportEntry> data = FXCollections.observableArrayList();
        for (EnumDish dishType : report.getDishTypeAmountMap().keySet()) {
            data.add(new OrdersReportEntry(dishType.toString(), report.getDishTypeAmountMap().get(dishType)));
        }
        ordersTable.setItems(data);
    }
    
    /**
     * Sets the reference to the ReportsPageController.
     * This allows communication back to the main reports page.
     *
     * @param controller The ReportsPageController instance
     */
    public void setReportsPageController(ReportsPageController controller) {
        this.reportsPageController = controller;
    }

    /**
     * Handles the closing of the Orders Report window.
     * This method is called when the user clicks the close button.
     * It notifies the ReportsPageController to re-enable the orders report button.
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) ordersTable.getScene().getWindow();
        reportsPageController.enableOrdersReportButton();
        stage.close();
    }

    /**
     * Inner class representing an entry in the Orders Report table.
     */
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