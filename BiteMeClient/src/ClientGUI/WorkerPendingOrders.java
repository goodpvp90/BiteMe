package ClientGUI;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import client.Client;
import common.Dish;
import common.DishInOrder;
import common.EnumOrderStatus;
import common.Order;
import common.User;
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
    private TextField etaTextField;;
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
    /////***********************************************************//////////////*******************
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
        
        // Set up the scene before fetching data
        vbox.getChildren().add(dishTableView);
        detailStage.setScene(new Scene(vbox));
        detailStage.setTitle("Order Details");
        detailStage.show();
        
        // Fetch the dish data from the server
        client.sendShowDishesInOrder(orderID);        
        // When the data arrives, populate the table view
        PendingDishInOrders.clear(); // Clear any previous data       
        Platform.runLater(() -> {
            dishTableView.setItems(FXCollections.observableArrayList(PendingDishInOrders));
        });

    }

    
    @FXML
    private void handleApproveOrderAction() {
 		Order selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {       	
        	client.updateOrderStatus(selectedOrder.getOrderId(),EnumOrderStatus.IN_PROGRESS);
        	Platform.runLater(() -> {
        	orderTableView.refresh();
        	});
            //selectedOrder.setStatus(EnumOrderStatus.IN_PROGRESS);
            updateButtonStates(selectedOrder);           
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
    //*NEED TO ADD IN FXML*******************************************
  	private void showError(String str) {
  		errorText.setText(str);
  		errorText.setVisible(true);
  	} 
    
 // Goes back to the user's home page
 	@FXML
     private void handleBackButtonAction() {	
 		try {
         	UserHomePageUI Userapp = new UserHomePageUI(user,true);
         	Userapp.start(new Stage());
             Stage currentStage = (Stage) backButton.getScene().getWindow();
             currentStage.close();
         } catch (Exception e) {
             e.printStackTrace();
             showError("An error occurred while loading the User Home Page.");
         }
     }
    
 	//Making Quit Button to kill thread and send message to server
    public void closeApplication() {
        if (client != null) {
        	client.userLogout(user);
            }
        Platform.exit();
        System.exit(0);
    } 
}