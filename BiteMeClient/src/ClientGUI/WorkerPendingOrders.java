package ClientGUI;
import java.sql.Timestamp;
import java.util.Comparator;
import client.Client;
import common.DishInOrder;
import common.EnumOrderStatus;
import common.Order;
import common.User;
import common.Restaurant.Location;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;

public class WorkerPendingOrders {
	private Client client;
	private User user = null;
	@FXML
    private Text errorText;
    @FXML
    private TableView<Order> orderTableView;

    @FXML
    private TableColumn<Order, Integer> orderIdColumn;

    @FXML
    private TableColumn<Order, String> ordererColumn;

    @FXML
    private TableColumn<Order, Timestamp> orderDateColumn;

    @FXML
    private TableColumn<Order, Double> totalPriceColumn;

    @FXML
    private TableColumn<Order, Boolean> deliveryColumn;

    @FXML
    private TableColumn<Order, EnumOrderStatus> statusColumn;
    
    @FXML
    private Button approveOrderButton;

    @FXML
    private Button orderReadyButton;
    
    @FXML
    private TextField etaTextField;;

    @FXML
    private Button backButton;

    @FXML
    private void initialize() {
        // Set up the columns in the table
    	client = Client.getInstance();
    	client.setWorkerPendingOrders(this);
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        ordererColumn.setCellValueFactory(cellData -> {
            String[] nameParts = cellData.getValue().getUsername().split(" ");
            String firstName = nameParts.length > 0 ? nameParts[0] : "";
            String lastName = nameParts.length > 1 ? nameParts[1] : "";
            return new SimpleStringProperty(firstName + " " + lastName);
        });
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        deliveryColumn.setCellValueFactory(new PropertyValueFactory<>("delivery"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load data here
        //orderTableView.setItems(getOrders());
        orderTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateButtonStates(newValue);
            }
        });
        etaTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Enable or disable the ORDER READY button based on ETA input
            orderReadyButton.setDisable(newValue.trim().isEmpty());
        });
        // Add expandable column
        TableColumn<Order, Void> expandColumn = new TableColumn<>("Expand");
        expandColumn.setCellFactory(param -> new TableCell<>() {
            private final Button expandButton = new Button("Expand");
            {
                expandButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    showOrderDetails(order);
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
    //-----------------------------------------END OF INIT------------------------------------------
    public void setUser(User user) 
    {
        this.user = user;          
    }
    
    //FOR TESTING HOW THIS LOOKS*************************************************************
    /* private ObservableList<Order> getOrders() {
        ObservableList<Order> orders = FXCollections.observableArrayList();
        // Sample data
        Order stub1 = new Order("Bitch face", 1, Timestamp.valueOf("2023-08-07 10:00:00"), Timestamp.valueOf("2023-08-07 09:00:00"), 29.99, true);
        stub1.setOrderId(1);
        Order stub2 = new Order("Fuck nugget", 2, Timestamp.valueOf("2023-08-07 11:00:00"), Timestamp.valueOf("2023-08-07 10:00:00"), 69.99, false);
        stub2.setOrderId(2);
        stub1.setStatus(EnumOrderStatus.PENDING); // Set status
        stub2.setStatus(EnumOrderStatus.PENDING); // Set status
        orders.add(stub1);
        orders.add(stub2);

        // Sort orders by orderId
        FXCollections.sort(orders, Comparator.comparingInt(Order::getOrderId));
        return orders;
    }*/
  //********************************************************************************************
    
    private void showOrderDetails(Order order) {
        // Create a new stage to show order details
        Stage detailStage = new Stage();
        VBox vbox = new VBox();

        TableView<DishInOrder> dishTableView = new TableView<>();
        TableColumn<DishInOrder, String> nameColumn = new TableColumn<>("Name");
        TableColumn<DishInOrder, String> optionalPickColumn = new TableColumn<>("Optional Pick");
        TableColumn<DishInOrder, String> commentColumn = new TableColumn<>("Comment");

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("dishName"));
        optionalPickColumn.setCellValueFactory(new PropertyValueFactory<>("optionalPick"));
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));

        dishTableView.getColumns().addAll(nameColumn, optionalPickColumn, commentColumn);

        // Load dishes (Replace this with real data from your database)
        dishTableView.setItems(getDishesForOrder(order));

        vbox.getChildren().add(dishTableView);
        detailStage.setScene(new Scene(vbox));
        detailStage.setTitle("Order Details");
        detailStage.show();
    }

    private ObservableList<DishInOrder> getDishesForOrder(Order order) {
        ObservableList<DishInOrder> dishes = FXCollections.observableArrayList();
        // Sample data (Replace this with real data from your database)
        dishes.add(new DishInOrder("Salad", 1, "No onions", "Extra Dressing"));
        dishes.add(new DishInOrder("Steak", 2, "Add mushrooms", "Medium Rare"));
        return dishes;
    }
    
    @FXML
    private void handleApproveOrderAction() {
        Order selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            selectedOrder.setStatus(EnumOrderStatus.IN_PROGRESS);
            updateButtonStates(selectedOrder);
            orderTableView.refresh(); // Refresh the table view to show updated status
        }
    }

    @FXML
    private void handleOrderReadyAction()//CLOSE THE ROW WHEN ORDER IS READY DONT FORGET 
    {   	
        Order selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            if (selectedOrder.isDelivery() && etaTextField.isVisible() && !etaTextField.getText().isEmpty()) {
                // Here you can handle the ETA input
                // For example, set it as a property of the order or process it as needed
            }
            selectedOrder.setStatus(EnumOrderStatus.READY);
            updateButtonStates(selectedOrder);
            orderTableView.refresh(); // Refresh the table view to show updated status
        }
    }
    
    private void updateButtonStates(Order selectedOrder) {
        if (selectedOrder != null) {
            EnumOrderStatus status = selectedOrder.getStatus();
            boolean isDelivery = selectedOrder.isDelivery();

            approveOrderButton.setDisable(status != EnumOrderStatus.PENDING);
            orderReadyButton.setDisable(status != EnumOrderStatus.IN_PROGRESS);
            etaTextField.setVisible(isDelivery && status == EnumOrderStatus.IN_PROGRESS);
        } else {
            approveOrderButton.setDisable(true);
            orderReadyButton.setDisable(true);
            etaTextField.setVisible(false);
        }
    }

  //Change Error text and make it visible, appear under continue button
    //******************************NEED TO ADD IN FXML********************************************
  	private void showError(String str) {
  		errorText.setText(str);
  		errorText.setVisible(true);
  	} 
    
 // Goes back to the user's home page
 	@FXML
     private void handleBackButtonAction() {	
 		//client.sendShowPending(1);
 		client.sendShowDishesInOrder(22);
/* 		try {
         	UserHomePageUI Userapp = new UserHomePageUI(user,true);
         	Userapp.start(new Stage());
             Stage currentStage = (Stage) backButton.getScene().getWindow();
             currentStage.close();
         } catch (Exception e) {
             e.printStackTrace();
             showError("An error occurred while loading the User Home Page.");
         }*/
     }
    
  //Making Quit Button to kill thread and send message to server
    public void closeApplication() {
        if (client != null) {
            client.quit();
        }
        Platform.exit();
        System.exit(0);
    } 
}
