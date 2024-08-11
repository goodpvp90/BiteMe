package ClientGUI;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import client.Client;
import client.Client.EnumPageForDishInOrder;
import common.DishInOrder;
import common.EnumOrderStatus;
import common.Order;
import common.User;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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
    private Button backButton;
    
    
    @FXML
    private void initialize() {
    	 // Set up the columns in the table
    	client = Client.getInstance();
    	client.getInstanceOfMyOrders(this);
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
    	 
    public void FilterReadyOrder() {
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
        if (selectedOrder != null) {       	
        	client.updateOrderStatus(selectedOrder.getOrderId(),EnumOrderStatus.COMPLETED,"");        	
            readyOrders.remove(selectedOrder);
            orderTableView.getItems().clear();
        	orderTableView.getItems().addAll(readyOrders);
            
        }
        else
        	showError("Order selection error!!!");
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
    
    private void showError(String errmsg) {
		errorText.setText(errmsg);
		errorText.setVisible(true);
	}
    
	// Making Quit Button to kill thread and send message to server
	public void closeApplication() {
		if (client != null) {
			client.userLogout(user);
		}
		Platform.exit();
		System.exit(0);
	}  
    
}
