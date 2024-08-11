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
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import common.EnumBranch;

public class WorkerPendingOrders {
	private Client client;
	private User user = null;
	List<Order> pendingOrders = new ArrayList<>();
	List<DishInOrder> PendingDishInOrders = new ArrayList<>();
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
    private ComboBox<String> etaComboBox;
    @FXML
    private Button backButton;   
    
    ////////////////////////////////////////////////////////////////////////////////////////////
    @FXML
    private void initialize() {
        // Set up the columns in the table
    	client = Client.getInstance();
    	client.setWorkerPendingOrders(this);
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        ordererColumn.setCellValueFactory(cellData -> {
        	if(cellData.getValue().isDelivery()==true)
        		return new SimpleStringProperty(cellData.getValue().getReceiverName());
            String[] nameParts = cellData.getValue().getUsername().split(" ");
            String firstName = nameParts.length > 0 ? nameParts[0] : "";
            String lastName = nameParts.length > 1 ? nameParts[1] : "";
            return new SimpleStringProperty(firstName + " " + lastName);
        });
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        deliveryColumn.setCellValueFactory(new PropertyValueFactory<>("delivery"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        orderTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateButtonStates(newValue);
                etaComboBox.setValue(null);
            }
        });
        //***********    
        etaComboBox.setItems(FXCollections.observableArrayList(
                "In about 20 minutes",
                "In about 1 hour",
                "In Above 1 hour"
        ));
        etaComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            orderReadyButton.setDisable(newValue == null || newValue.isEmpty());
        });
        etaComboBox.setVisible(false);
        //***********
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
    //-----------------------------------------END OF INIT------------------------------------------
    public void setUser(User user) 
    {
        this.user = user;     
        pendingOrdersLoader();        
    }
    /////********************//////////////******
    public void pendingOrdersLoader()
    {    	
    	EnumBranch homeBranch = user.getHomeBranch();   	    	
    	
        int homeBranchID;      
    	switch(homeBranch)
    	{
    	case EnumBranch.NORTH:
    		homeBranchID = 1;
    		break;
    	case EnumBranch.CENTER:
    		homeBranchID = 2;
    		break;
    	case EnumBranch.SOUTH:
    		homeBranchID = 3;	
    		break;
    	default:
    		homeBranchID = 0;
    		break;
    	}    	
    	if(homeBranchID==0)
    		showError("Branch Initialization Error!"); 	
    	client.sendShowPending(homeBranchID);  
    	Platform.runLater(() -> {
		orderTableView.getItems().addAll(pendingOrders);   		 
    	});  	
    }
    
    public void SetPendingOrdersFromDB(List<Order> DBOrderList)
	{
    	pendingOrders.clear();                            	 
    	pendingOrders = DBOrderList;   	
	}
    
    public void SetDishInOrdersFromDB(List<DishInOrder> DBDishInOrdersList)
	{
    	PendingDishInOrders.clear();                            	 
    	PendingDishInOrders = DBDishInOrdersList;   	
	}
    
    // Create a new stage to show order details
    private void showOrderDetails(int orderID) {
    	client.sendShowDishesInOrder(orderID,EnumPageForDishInOrder.WORKER); 
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

    
    @FXML
    private void handleApproveOrderAction() {
 		Order selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {       	
        	client.updateOrderStatus(selectedOrder.getOrderId(),EnumOrderStatus.IN_PROGRESS,"Order " + selectedOrder.getOrderId()+": Your order has been approved and is being prepared!");        	
            selectedOrder.setStatus(EnumOrderStatus.IN_PROGRESS);
            orderTableView.refresh();
            updateButtonStates(selectedOrder);           
        }
        else
        	showError("Order selection error!!!");        
    }

    @FXML
    private void handleOrderReadyAction() {
    	//אם זה פיקאפ אז סטאטוס משתנה לCOMPLETED
        Order selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            if (selectedOrder.isDelivery()) {
                etaComboBox.setVisible(true);
                if (etaComboBox.getValue() != null && !etaComboBox.getValue().equals("-Choose Comment-")) {
                    client.updateOrderStatus(selectedOrder.getOrderId(), EnumOrderStatus.READY, "Order " + selectedOrder.getOrderId()+": Your order is ready and the delivery is on its way!\n Estimated time of arrival will be "+etaComboBox.getValue());
                } else {
                    showError("ETA must be provided for delivery orders.");
                    return; 
                }
            } else {
                // If it's not a delivery order, allow status update without checking etaComboBox
                client.updateOrderStatus(selectedOrder.getOrderId(), EnumOrderStatus.COMPLETED,"Order " + selectedOrder.getOrderId()+": Your order is ready for pickup!");
            }
            // Remove order from the table after setting status to READY
            selectedOrder.setStatus(EnumOrderStatus.READY);
            orderTableView.getItems().remove(selectedOrder);
            orderTableView.refresh();
            etaComboBox.setValue(null);          
      		errorText.setVisible(false);
            updateButtonStates(selectedOrder);
        } else {
            showError("Order selection error!!!");
        }
    }
    
    private void updateButtonStates(Order selectedOrder) {
        if (selectedOrder != null) {
            EnumOrderStatus status = selectedOrder.getStatus();
            boolean isDelivery = selectedOrder.isDelivery();
            approveOrderButton.setDisable(status != EnumOrderStatus.PENDING);
            orderReadyButton.setDisable(status != EnumOrderStatus.IN_PROGRESS);
            etaComboBox.setVisible(isDelivery && status == EnumOrderStatus.IN_PROGRESS);
            etaComboBox.setValue(null);
        } else {
            approveOrderButton.setDisable(true);
            orderReadyButton.setDisable(true);
            etaComboBox.setVisible(false);
            etaComboBox.setValue(null);
        }
    }

    //Change Error text and make it visible, appear under continue button
  	private void showError(String str) {
  		errorText.setText(str);
  		errorText.setVisible(true);
  	} 
  	//TEMP
  	 public void showNotificationDialog(List<String> text) {
  		Platform.runLater(() -> {
            if (text == null || text.isEmpty()) {
                System.out.println("No notifications to show.");
                return;
            }            
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("You got a message!");
            alert.setHeaderText(null);
            String content = String.join("\n", text);
            alert.setContentText(content);
            ButtonType okButton = new ButtonType("CLOSE", ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(okButton);
            alert.showAndWait().ifPresent(response -> {
                if (response == okButton) {
                    alert.close(); // Close the dialog window
                }
            });
        });
     }
    
  	// Goes back to the user's home page
  	@FXML
  	private void handleBackButtonAction() {    
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
    
 	//Making Quit Button to kill thread and send message to server
    public void closeApplication() {
    	if (client != null) 
			  client.userLogout(user, true);
  }
}