package ClientGUI;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import client.Client;
import client.Client.EnumPageForDishInOrder;
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

public class MyOrders {

	private Client client;
	private User user = null;
	List<Order> Orders = new ArrayList<>();
	List<DishInOrder> PendingDishInOrders = new ArrayList<>();
	List<Order> readyOrders = new ArrayList<>();

	@FXML
    private Text errorText;
    @FXML
    private TableView<Order> orderTableView;
    @FXML
    private TableColumn<Order, Integer> orderIdColumn;
    @FXML
    private TableColumn<Order, Timestamp> orderDateColumn;
    @FXML
    private TableColumn<Order, Double> totalPriceColumn; 
    @FXML
    private Button approveOrderButton;
    @FXML
    private Button backButton;
    
    
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
    	
    	
    
    
    
    public void setUser(User user) 
    {
        this.user = user;
        OrdersLoader();
    }
    
    public void setOrders(List<Order> DBOrderList) {
    	Orders.clear();                            	 
    	Orders = DBOrderList;
    	Platform.runLater(() -> {FilterReadyOrder();});    
    }
    
    public void SetDishInOrdersFromDB(List<DishInOrder> DBDishInOrdersList)
	{
    	PendingDishInOrders.clear();                            	 
    	PendingDishInOrders = DBDishInOrdersList;   	
	}
    
    public void OrdersLoader()
    {
    	client.getUsersOrders(user.getUsername());
	}
    
    @FXML
    public void handleRefreshButton(ActionEvent event)
    {
    	orderTableView.getItems().clear();
    	OrdersLoader();
    }
    	 
    public void FilterReadyOrder() {
    	readyOrders.clear();
    	for(Order CurrentOrder:Orders) {
    		if (CurrentOrder.getStatus()==(EnumOrderStatus.READY)) {
    			readyOrders.add(CurrentOrder);
    		}
    	}
    	orderTableView.getItems().addAll(readyOrders);
    	
    }
    
    
    @FXML
    private void handleApproveOrderAction() {
    	Order selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
    	client.updateOrderStatus(selectedOrder.getOrderId(),EnumOrderStatus.COMPLETED,"");        	
    	client.sendOrderArriveOnTime(selectedOrder.getOrderId());
    }
    
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

    
    @FXML
    void handleBackButtonAction(ActionEvent event) {
        try {
            // Retrieve the existing stage for UserHomePageUI
            Stage userHomePageStage = UserHomePageUI.getStage();

            if (userHomePageStage != null) {
                userHomePageStage.show();  // Show the hidden stage again
            } else {
                // If the stage is somehow null, recreate and show it
                UserHomePageUI Userapp = new UserHomePageUI(user, true);
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
    
    
    private void showOrderDetails(int orderID) {
    	client.sendShowDishesInOrder(orderID,EnumPageForDishInOrder.CUSTOMER); 
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
        
        // Set preferred width for the columns
        nameColumn.setPrefWidth(100);
        optionalPickColumn.setPrefWidth(100);
        commentColumn.setPrefWidth(200);
        
        dishTableView.getColumns().addAll(nameColumn, optionalPickColumn, commentColumn);
        
        // Set up the scene before fetching data
        vbox.getChildren().add(dishTableView);
        detailStage.setScene(new Scene(vbox));
        detailStage.setTitle("Order Details");
        detailStage.show();
        
        // Fetch the dish data from the server
                   
        
            dishTableView.setItems(FXCollections.observableArrayList(PendingDishInOrders));
        });

    }
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
    
    private void showError(String errmsg) {
		errorText.setText(errmsg);
		errorText.setVisible(true);
	}
    
	// Making Quit Button to kill thread and send message to server
	public void closeApplication() {
		if (client != null)
			client.userLogout(user, true);
	}  
    
}
