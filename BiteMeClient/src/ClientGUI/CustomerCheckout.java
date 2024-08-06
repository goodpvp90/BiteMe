package ClientGUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.Client;
import common.Dish;
import common.User;

public class CustomerCheckout {
	
    @FXML
    private Button backButton;

    @FXML
    private Button confirmOrderButton;

    @FXML
    private Text customerDetailsText;

    @FXML
    private Text receiverNameLabel;

    @FXML
    private Text phoneNumberLabel;

    @FXML
    private Text deliveryAddressLabel;

    @FXML
    private TableView<Dish> menuTableView;

    @FXML
    private TableColumn<Dish, String> nameColumn;

    @FXML
    private TableColumn<Dish, Double> priceColumn;

    @FXML
    private TableColumn<Dish, String> optionalsColumn;

    @FXML
    private TableColumn<Dish, String> commentsColumn;

    @FXML
    private Text selectedItemsText;

    @FXML
    private Text receiptText;

    @FXML
    private Text phoneNumInfoText;

    @FXML
    private Text receiverNameInfoText;

    @FXML
    private Text deliveryAddInfoText;

    @FXML
    private Text totalPriceLabel;

    @FXML
    private Text priceTextInfo;

    @FXML
    private Text discountsText;

    @FXML
    private Text earlyDeliveryLabel;

    @FXML
    private Text sharedDeliveryLabel;

    @FXML
    private Text lateOrderCompensationLabel;

    @FXML
    private Text earlyDeliveryInfoText;

    @FXML
    private Text sharedDeliveryInfoText;

    @FXML
    private Text lateOrderDeliveryInfoText;

    @FXML
    private Text compensationText;

    @FXML
    private Button yesButton;

    @FXML
    private Button noButton;

    @FXML
    private Text compensationMessageText;
    
    
    private List<Dish> chosenItemsFromMenu = new ArrayList<>(); 
    private boolean isEligible;
    private Client client;
    private User user;
    private boolean[] returnBooleanPrefGather;
    private String[] contactInfo;
    private Object date;
    
    @FXML
    private void initialize() {
//    	client = Client.getInstance();

    	
//    	//Set table columns to get the right getters value
//    	nameColumn.setCellValueFactory(new PropertyValueFactory<>("dishName"));
//        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
//        optionalsColumn.setCellValueFactory(new PropertyValueFactory<>("optionalsPick")); 
//        commentsColumn.setCellValueFactory(new PropertyValueFactory<>("comments"));
//        menuTableView.getItems().clear();   
//        menuTableView.getItems().addAll(chosenItemsFromMenu);        

    	

    }
    
    public void setChosenItemsFromMenu(List<Dish> chosenItemsFromMenu) {
    	this.chosenItemsFromMenu = chosenItemsFromMenu;
    }
    
    public void setReturnBooleanPrefGather(boolean[] param) {
    	returnBooleanPrefGather = param;
    }
    public void setUser (User user) {
    	this.user = user;
    }
    public void setContacts (String[] contactInfo) {
    	this.contactInfo = contactInfo;
    }
    public void setDate (Object date) {
    	this.date=date;
    }

	
    @FXML
    private void handleBackButtonAction(ActionEvent event) throws IOException { 
    	try {
        	CustomerOrderGatherSelectionUI Userapp =
        			new CustomerOrderGatherSelectionUI(user,chosenItemsFromMenu,returnBooleanPrefGather,contactInfo,date);
        	Userapp.start(new Stage());
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            //showError("An error occurred while loading the User Home Page.");
        }
    }

    @FXML
    private void handleConfirmOrderAction(ActionEvent event) {
        System.out.println("Order confirmed!");
    }
    
    private void checkoutTotalPrice() {
    	double totalPrice = 0;
    	for (Dish dish:chosenItemsFromMenu) {
    		totalPrice+=dish.getPrice();
    	}
    		
    	priceTextInfo.setText(totalPrice + " ");
    }

}