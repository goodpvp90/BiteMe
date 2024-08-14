package WorkerPendingOrdersProcess;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import UserHomePageProcess.UserHomePageUI;
import client.Client;
import enums.EnumPageForDishInOrder;
import enums.EnumBranch;
import enums.EnumOrderStatus;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
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
 * Controller class for handling worker's pending orders in the restaurant management system.
 * This class manages the display and interactions of the pending orders, allowing the worker
 * to approve and mark orders as ready, while providing the necessary UI updates.
 */
public class WorkerPendingOrders {
	/** 
	 * Client instance for interacting with the server.
	 */
	private Client client;
	/** 
	 * User instance representing the current logged-in user. 
	 */
	private User user = null;
	/**
	 *  List of pending orders of a specific branch.
	 */
	List<Order> pendingOrders = new ArrayList<>();
	/**
	 *  List of dishes in a pending order of a specific branch. 
	 */
	List<DishInOrder> PendingDishInOrders = new ArrayList<>();
	/**
	 *  Text label to display error messages.
	 */
	@FXML
    private Text errorText;
	/** 
	 * TableView to display the pending orders. 
	 */
    @FXML
    private TableView<Order> orderTableView;
    /**
     *  TableColumn to display the order id. 
     */
    @FXML
    private TableColumn<Order, Integer> orderIdColumn;
    /**
     *  TableColumn to display the name of the orderer. 
     */
    @FXML
    private TableColumn<Order, String> ordererColumn;
    /**
     *  TableColumn to display the time and date of arrival for the order.       
     */
    @FXML
    private TableColumn<Order, Timestamp> orderDateColumn;
    /**
     *  TableColumn to display the price of the order. 
     */
    @FXML
    private TableColumn<Order, Double> totalPriceColumn;
    /**
     * TableColumn indicating whether the order is for delivery.
     */    
    @FXML
    private TableColumn<Order, Boolean> deliveryColumn;
    /**
     * TableColumn showing the current status of the order.
     */
    @FXML
    private TableColumn<Order, EnumOrderStatus> statusColumn;  
    /**
     * Button for approving an order.
     */
    @FXML
    private Button approveOrderButton;
    /**
     * Button for marking an order as ready.
     */
    @FXML
    private Button orderReadyButton;   
    /**
     * ComboBox for selecting the estimated time of arrival for delivery orders.
     */
    @FXML
    private ComboBox<String> etaComboBox;
    /**
     * Button for navigating back to the previous UI.
     */
    @FXML
    private Button backButton;   
    /**
     * Initializes the UI components and sets up the necessary event listeners.
     */
    @FXML
    private void initialize() {
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
        etaComboBox.setItems(FXCollections.observableArrayList(
                "In about 20 minutes",
                "In about 1 hour",
                "In Above 1 hour"
        ));
        etaComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            orderReadyButton.setDisable(newValue == null || newValue.isEmpty());
        });
        etaComboBox.setVisible(false);
        TableColumn<Order, Void> expandColumn = new TableColumn<>("Expand");
        expandColumn.setCellFactory(param -> new TableCell<>() {
            private final Button expandButton = new Button("Expand");
            {
            	expandButton.setOnAction(event -> {
                    Order Selectedorder = getTableView().getItems().get(getIndex());
                    if (Selectedorder != null) {
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
     * Sets the user for this UI and loads the pending orders.
     *
     * @param user The user to set.
     */
    public void setUser(User user) 
    {
        this.user = user;     
        pendingOrdersLoader();        
    }
    /**
     * Loads the pending orders for the user's home branch.
     */
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
    /**
     * Sets the pending orders retrieved from the database.
     *
     * @param DBOrderList The list of orders from the database.
     */
    public void SetPendingOrdersFromDB(List<Order> DBOrderList)
	{
    	pendingOrders.clear();                            	 
    	pendingOrders = DBOrderList;   	
	}
    /**
     * Sets the dishes in orders retrieved from the database.
     *
     * @param DBDishInOrdersList The list of dishes in orders from the database.
     */
    public void SetDishInOrdersFromDB(List<DishInOrder> DBDishInOrdersList)
	{
    	PendingDishInOrders.clear();                            	 
    	PendingDishInOrders = DBDishInOrdersList;   	
	}
    /**
     * Displays the dishes of the selected order in a new stage.
     *
     * @param orderID The ID of the order to display dishes for.
     */
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
        nameColumn.setPrefWidth(100);
        optionalPickColumn.setPrefWidth(100);
        commentColumn.setPrefWidth(200);
        dishTableView.getColumns().addAll(nameColumn, optionalPickColumn, commentColumn);       
        vbox.getChildren().add(dishTableView);
        detailStage.setScene(new Scene(vbox));
        detailStage.setTitle("Order Details");
        detailStage.show();
        dishTableView.setItems(FXCollections.observableArrayList(PendingDishInOrders));
        });
    }
    /**
     * Handles the action of approving an order.
     */
    @FXML
    private void handleApproveOrderAction() {
 		Order selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {       	
        	client.updateOrderStatus(selectedOrder.getOrderId(),EnumOrderStatus.IN_PROGRESS,"Order " + selectedOrder.getOrderId()+": Your order has been approved and is being prepared!",selectedOrder.isDelivery());        	
            selectedOrder.setStatus(EnumOrderStatus.IN_PROGRESS);
            orderTableView.refresh();
            updateButtonStates(selectedOrder);           
        }
        else
        	showError("Order selection error!!!");        
    }
    /**
     * Handles the action of marking an order as ready.
     */
    @FXML
    private void handleOrderReadyAction() {
        Order selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            if (selectedOrder.isDelivery()) {
                etaComboBox.setVisible(true);
                if (etaComboBox.getValue() != null && !etaComboBox.getValue().equals("-Choose Comment-")) {
                    client.updateOrderStatus(selectedOrder.getOrderId(), EnumOrderStatus.READY, "Order " + selectedOrder.getOrderId()+": Your order is ready and the delivery is on its way!\n Estimated time of arrival will be "+etaComboBox.getValue(),selectedOrder.isDelivery());
                } else {
                    showError("ETA must be provided for delivery orders.");
                    return; 
                }
            } else {
                client.updateOrderStatus(selectedOrder.getOrderId(), EnumOrderStatus.COMPLETED,"Order " + selectedOrder.getOrderId()+": Your order is ready for pickup!",selectedOrder.isDelivery());
            }
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
    /**
     * Updates the states of the buttons based on the selected order.
     *
     * @param selectedOrder The selected order.
     */
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
    /**
     * Displays an error message in the UI.
     *
     * @param message The error message to display.
     */
  	private void showError(String str) {
  		errorText.setText(str);
  		errorText.setVisible(true);
  	} 
  	/**
  	 * Displays a notification dialog with a list of messages.
  	 * Used for displaying real time updates for a customer regarding his order's status (SMS like)
  	 * @param text The list of messages to display. If empty or null, no dialog is shown.
  	 */
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
  	/**
      * Handles the action of navigating back to the previous UI.
      */
  	@FXML
  	private void handleBackButtonAction() {    
  	    client.removeWorkerInPendingOrders(user);
  		try {
  	        Stage userHomePageStage = UserHomePageUI.getStage();
  	        if (userHomePageStage != null) {
  	            userHomePageStage.show();  
  	        } else {
  	            UserHomePageUI Userapp = new UserHomePageUI(user);
  	            Userapp.start(new Stage());
  	        }
  	        Stage currentStage = (Stage) backButton.getScene().getWindow();
  	        currentStage.close();
  	    } catch (Exception e) {
  	        e.printStackTrace();
  	        showError("An error occurred while loading the User Home Page.");
  	    }
  	}  	
  	/**
  	 * Logs out the user and removes the worker from pending orders.
  	 */
    public void closeApplication() {
  	    client.removeWorkerInPendingOrders(user);
    	if (client != null) 
			  client.userLogout(user, true);
    }
  }