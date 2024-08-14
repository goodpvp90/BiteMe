package CustomerMyOrdersProcess;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import UserHomePageProcess.UserHomePageUI;
import client.Client;
import enums.EnumPageForDishInOrder;
import enums.EnumOrderStatus;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import restaurantEntities.DishInOrder;
import restaurantEntities.Order;
import userEntities.User;


/**
 * The MyOrders class handles the user interface for viewing and managing orders.
 * It displays user orders in a table, allows for order approval, and shows order details.
 */
public class MyOrders {

	/**
     * The client instance used for communication with the server.
     */
    private Client client;

    /**
     * The user currently logged into the system.
     */
    private User user = null;

    /**
     * The list of all orders user ordered.
     */
    private List<Order> Orders = new ArrayList<>();

    /**
     * The list of dishes associated with the order.
     */
    private List<DishInOrder> OrderDishes = new ArrayList<>();

    /**
     * The filtered list of orders with "ready" status.
     */
    private List<Order> readyOrders = new ArrayList<>();


    /**
     * Text field for displaying error messages.
     */
    @FXML
    private Text errorText;

    /**
     * Table view for displaying orders.
     */
    @FXML
    private TableView<Order> orderTableView;

    /**
     * Column for displaying order IDs in the table view.
     */
    @FXML
    private TableColumn<Order, Integer> orderIdColumn;

    /**
     * Column for displaying order dates in the table view.
     */
    @FXML
    private TableColumn<Order, Timestamp> orderDateColumn;

    /**
     * Column for displaying total prices of orders in the table view.
     */
    @FXML
    private TableColumn<Order, Double> totalPriceColumn;

    /**
     * Button for approving selected orders.
     */
    @FXML
    private Button approveOrderButton;

    /**
     * Button for navigating back to the previous screen.
     */
    @FXML
    private Button backButton;
    
    /**
     * Initializes the UI components and sets up the table columns.
     */
    @FXML
    private void initialize() {
    	 // Set up the columns in the table
    	client = Client.getInstance();
    	client.getInstanceOfMyOrders(this);
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        
    	
        TableColumn<Order, Void> expandColumn = new TableColumn<>("Expand");
        expandColumn.setCellFactory(param -> new TableCell<>() {
            private final Button expandButton = new Button("Expand");
            {
            	expandButton.setOnAction(event -> {
                    // Get the Order object associated with the row
                    Order Selectedorder = getTableView().getItems().get(getIndex());
                    if (Selectedorder != null) {
                        // Pass the orderId to the showOrderDetails method
                        showOrderDetails(Selectedorder.getOrderId()); 
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(expandButton);
                }
            }
        });
        orderTableView.getColumns().add(expandColumn);
    }
    	
    	
    
    
    /**
     * Sets the current user and loads their orders.
     * 
     * @param user The user to set.
     */
    public void setUser(User user) 
    {
        this.user = user;
        OrdersLoader();
    }
    
    /**
     * Updates the list of orders associated with the user with the given list from the database.
     * 
     * @param DBDishInOrdersList The list of dish orders to set.
     */
    public void setOrders(List<Order> DBOrderList) {
    	Orders.clear();                            	 
    	Orders = DBOrderList;
    	Platform.runLater(() -> {FilterReadyOrder();});    
    }
    
    
    /**
     *Sets the dishes in orders retrieved from the database.
     *Prepares the pop up screen for dishes of the selected order
     * @param DBDishInOrdersList The list of dishes in orders from the database.
     */
    public void SetDishInOrdersFromDB(List<DishInOrder> DBDishInOrdersList) {
    	OrderDishes.clear();
    	OrderDishes.addAll(DBDishInOrdersList);
        Platform.runLater(() -> {
            Stage detailStage = new Stage();
            VBox vbox = new VBox();
            TableView<DishInOrder> dishTableView = new TableView<>();
            TableColumn<DishInOrder, String> nameColumn = new TableColumn<>("Name");
            TableColumn<DishInOrder, String> optionalPickColumn = new TableColumn<>("Optional Pick");
            TableColumn<DishInOrder, String> commentColumn = new TableColumn<>("Comment");
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("dishName"));
            optionalPickColumn.setCellValueFactory(new PropertyValueFactory<>("optionalPick"));
            commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
            nameColumn.setPrefWidth(100);
            optionalPickColumn.setPrefWidth(100);
            commentColumn.setPrefWidth(200);
            dishTableView.getColumns().add(nameColumn); 
            dishTableView.getColumns().add(optionalPickColumn);
            dishTableView.getColumns().add(commentColumn);
            vbox.getChildren().add(dishTableView);
            detailStage.setScene(new Scene(vbox));
            detailStage.setTitle("Order Details");
            detailStage.show();
            dishTableView.setItems(FXCollections.observableArrayList(OrderDishes));
        });
    }
    
    /**
     * Requests the user's orders from the server. start proccess of loading orders to the list
     */
 
    public void OrdersLoader()
    {
    	client.getUsersOrders(user.getUsername());
	}
    
    /**
     * Refreshes the table view by clearing current items and reloading orders.
     * 
     * @param event The action event that triggered the refresh.
     */
    @FXML
    public void handleRefreshButton(ActionEvent event)
    {
    	orderTableView.getItems().clear();
    	OrdersLoader();
    }
    	 
    /**
     * Filters the orders to show only those with a "ready" status and updates the table view.
     */
    public void FilterReadyOrder() {
    	readyOrders.clear();
    	for(Order CurrentOrder:Orders) {
    		if (CurrentOrder.getStatus()==(EnumOrderStatus.READY)) {
    			readyOrders.add(CurrentOrder);
    		}
    	}
    	orderTableView.getItems().addAll(readyOrders);
    	
    }
    
    /**
     * Handles the action of approving a selected order. 
     */
    @FXML
    private void handleApproveOrderAction() {
    	Order selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
    	if(selectedOrder!=null) {
			client.updateOrderStatus(selectedOrder.getOrderId(), EnumOrderStatus.COMPLETED, "", true);
			client.sendOrderArriveOnTime(selectedOrder.getOrderId());
    	}
    }
    
    /**
     * Handles the completion of an order, including showing a compensation dialog if needed.
     * 
     * @param show True to show the compensation dialog, false otherwise.
     */
    public void OrderCompleteHandle(boolean show) {
    	Order selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
    	//if customer deserves compensation
    	if(show)
    		showSorryForDelayDialog(selectedOrder);
    	
    	else {       	
            readyOrders.remove(selectedOrder);
            orderTableView.getItems().clear();
        	orderTableView.getItems().addAll(readyOrders);
            
        }
   
    }

    /**
     * Handles the action of navigating back to the user home page.
     * 
     * @param event The action event that triggered the navigation.
     */
    @FXML
    void handleBackButtonAction(ActionEvent event) {
        try {
            // Retrieve the existing stage for UserHomePageUI
            Stage userHomePageStage = UserHomePageUI.getStage();

            if (userHomePageStage != null) {
                userHomePageStage.show();  // Show the hidden stage again
            } else {
                // If the stage is somehow null, recreate and show it
                UserHomePageUI Userapp = new UserHomePageUI(user);
                Userapp.start(new Stage());
            }

            // Close the current stage
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred while loading the User Home Page.");
        }
    }
    
    /**
     * Shows the details of an order dishes in a new pop up window.
     * 
     * @param orderID The ID of the order to display.
     */
    private void showOrderDetails(int orderID) {
    	client.sendShowDishesInOrder(orderID,EnumPageForDishInOrder.CUSTOMER); 
    }
    /**
     * Shows a dialog apologizing for a delay and providing compensation.
     * 
     * @param selectedOrder The order for which compensation is provided.
     */
    private void showSorryForDelayDialog(Order selectedOrder) {
        
        double halfPrice = (selectedOrder.getTotalPrice()) / 2;
        BigDecimal compensation = new BigDecimal(halfPrice).setScale(2, RoundingMode.HALF_UP);
        
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Order Delay");
            alert.setHeaderText(null);
            alert.setContentText("We're sorry for the delay.\n"
                    + "You'll receive " + compensation + " as compensation for your next order.");

            ButtonType okButton = new ButtonType("OK", ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(okButton);

            alert.showAndWait().ifPresentOrElse(response -> {
                if (response == okButton) {
                	readyOrders.remove(selectedOrder);
                	orderTableView.getItems().clear();
                	orderTableView.getItems().addAll(readyOrders); 
                	}
            }, () -> {
            	readyOrders.remove(selectedOrder);
            	orderTableView.getItems().clear(); 
            	orderTableView.getItems().addAll(readyOrders);
            	});
        });
    }
    
    /**
     * Displays an error message in the errorText field.
     * 
     * @param message The error message to display.
     */
    private void showError(String errmsg) {
		errorText.setText(errmsg);
		errorText.setVisible(true);
	}
    
    /**
   	 * Closes the application and logs out the user.
   	 */
    public void closeApplication() {
		if (client != null)
			client.userLogout(user, true);
	}  
    
}