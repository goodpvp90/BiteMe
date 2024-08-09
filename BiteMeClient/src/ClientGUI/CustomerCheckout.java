package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import client.Client;
import common.Dish;
import common.Order;
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
    private Text earlyLabel;

    @FXML
    private Text sharedDeliveryLabel;

    @FXML
    private Text lateOrderCompensationLabel;

    @FXML
    private Text earlyInfoText;

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
    @FXML
    private Text compensationValueText;
    
    @FXML
    private Text errorText;
    
    
    private List<Dish> chosenItemsFromMenu = new ArrayList<>(); 
    private boolean isEligible;
    private Client client;
    private User user;
    private boolean[] returnBooleanPrefGather;
    private String[] contactInfo;
    private LocalDate date;
    private double totalPrice = 0;
    private double priceTempAfterComp;
    private double compensation;
    private boolean choosedUseCompensation = false;
    private boolean isdeservingCompensation = false;
    private int deliveryPrice;
	
    
    @FXML
    private void initialize() {
    	client = Client.getInstance();
    	client.getInstanceOfCustomerCheckout(this);
    	
    	//Set table columns to get the right getters value
    	nameColumn.setCellValueFactory(new PropertyValueFactory<>("dishName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        optionalsColumn.setCellValueFactory(new PropertyValueFactory<>("optionalPick")); 
        commentsColumn.setCellValueFactory(new PropertyValueFactory<>("comments"));
        
    	
    }

    
    public void setChosenItemsFromMenu(List<Dish> chosenItemsFromMenu) {
    	this.chosenItemsFromMenu = chosenItemsFromMenu;
    }
    
    public void setReturnBooleanPrefGather(boolean[] param) {
    	returnBooleanPrefGather = param;
    }
    public void setUser (User user) {
    	this.user = user;
    	

    	menuTableView.getItems().clear();   
        menuTableView.getItems().addAll(chosenItemsFromMenu);
        
        
        if(returnBooleanPrefGather[2]) {
			setDetailsOnClient(contactInfo[2], contactInfo[3], contactInfo[1] + ", " + contactInfo[0]);		
    	}
        else {
        	setDetailsOnClient(user.getFirstName()+" "+ user.getLastName(),
        			user.getPhone(),"N/A, not delivery");
        }
        
		deserveCompensation();
        checkoutTotalPrice();

    }
    public void setContacts (String[] contactInfo) {
    	this.contactInfo = contactInfo;
    	//city
    	//street
    	//receiver
    	//phone
    	//if delivery chosen
    	
    }
    
    //receives from Client if the user deserves compensation(amount)
	public void setCompensation(double compensation) {
		this.compensation = compensation;
	}
    
    private void setDetailsOnClient(String name, String phone, String address) {
    	
    	receiverNameInfoText.setText(name);
		phoneNumInfoText.setText(phone);
		deliveryAddInfoText.setText(address);

    }
    
    public void setDate (Object date) {
    	this.date=(LocalDate)date;
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

   

	private void checkoutTotalPrice() {
		for (Dish dish : chosenItemsFromMenu) {
			totalPrice += dish.getPrice();
		}
		
		setEarlyDiscount();
		calculateDelivery();	
		
		if(returnBooleanPrefGather[2])//if delivery chosen
			priceTextInfo.setText(String.format("%.2f", totalPrice) +
					" (Delivery price included: " + deliveryPrice+")");
		else
			priceTextInfo.setText(String.format("%.2f", totalPrice));

	}

	private void setEarlyDiscount() {
    	if (returnBooleanPrefGather[0]) {
    		totalPrice*=0.9;
    		earlyInfoText.setText("Discount Confirmed: 10%");
    	}
    	else
    		earlyInfoText.setText("Option not choosed");

    }
	
	//check if user deserves compensation
	private void deserveCompensation(){
//		showCompensationText(true);
//		compensationValueText.setText("You deserves: "+ compensation);
        client.getDiscountAmount(user.getUsername());

		Platform.runLater(() -> {
		if (compensation!=0) {
			isdeservingCompensation = true;
			showCompensationText(true);
			compensationValueText.setText("You deserve: "+ compensation);
			lateOrderDeliveryInfoText.setText("Please Choose yes/no.");
		}	
    	});

	}

	private void showCompensationText(boolean show) {
		compensationMessageText.setVisible(show);
		compensationText.setVisible(show);
		yesButton.setVisible(show);
		noButton.setVisible(show);
		compensationValueText.setVisible(show);
	}
	
	@FXML
	private void handleYesCompensationButton(ActionEvent event) {
		changedYesNofunctions(false);
		if(compensation<=totalPrice) {
			lateOrderDeliveryInfoText.setText("Discount confirmed: " + compensation);
			priceTempAfterComp=totalPrice;
			totalPrice-=compensation;
			compensation=0;
		}
		else {
			lateOrderDeliveryInfoText.setText("Discount confirmed: " + totalPrice);
			compensation-=totalPrice;
			priceTempAfterComp= totalPrice;
			totalPrice=0;
		}

		if(returnBooleanPrefGather[2])//if delivery chosen
			priceTextInfo.setText(String.format("%.2f", totalPrice) +
					" (Delivery price included: " + deliveryPrice+")");
		else
			priceTextInfo.setText(String.format("%.2f", totalPrice));
			
	}
	
	@FXML
	private void handleNoCompensationButton(ActionEvent event) {
		changedYesNofunctions(true);
		if (priceTempAfterComp != 0) {
			if (compensation <= totalPrice) {
				compensation=priceTempAfterComp-totalPrice;
				totalPrice=priceTempAfterComp;
				}
			else {
				compensation += priceTempAfterComp+totalPrice;
				totalPrice = priceTempAfterComp;
			}
			priceTempAfterComp=0;
		}
		lateOrderDeliveryInfoText.setText("Discount was not chosen");
		
		if(returnBooleanPrefGather[2])//if delivery chosen
			priceTextInfo.setText(String.format("%.2f", totalPrice) +
					" (Delivery price included: " + deliveryPrice+")");
		else
			priceTextInfo.setText(String.format("%.2f", totalPrice));
	}
	@FXML
	private void changedYesNofunctions(boolean hide) {
		errorText.setVisible(false);
		yesButton.setDisable(!hide);
		noButton.setDisable(hide);
		choosedUseCompensation= true;
	}
	
	private void calculateDelivery() {
		deliveryPrice=0;
		if(returnBooleanPrefGather[2]) {
			//if normal delivery chosen
			deliveryPrice=25;
			if(returnBooleanPrefGather[4]&& Integer.parseInt(contactInfo[6])!=0) {
				//if shared delivery chosen
				
				//delivery price decrease based on number of participants
				switch(Integer.parseInt(contactInfo[6])) {
				case 1: //delivery not changed
					deliveryPrice=20;
					break;
				default: //number is participants is 2 or higher max delivery discount achieved
					deliveryPrice=15;
					break;
					}
				sharedDeliveryInfoText.setText("Approved, Delivery cost "+ deliveryPrice);
			}
		}
		//Update total price with delivery price
		totalPrice+=deliveryPrice;
	}
	
	private void showError(String errmsg) {
		errorText.setText(errmsg);
		errorText.setVisible(true);
	}

	
	
	@FXML
	private void handleConfirmOrderAction(ActionEvent event) {
		if (isdeservingCompensation) {
			if (!(choosedUseCompensation)) {
				showError("Error! Please choose if you want to\n use your compensation discount");
				return;
			}
		}

		// Create Order Request Time
		LocalDateTime orderRequestDateTime = null;
		Timestamp orderRequestTimestamp = null;
		if (returnBooleanPrefGather[0]) { // if early method chosen set time, if not null
			LocalTime orderTime = LocalTime.of(Integer.valueOf(contactInfo[4]), Integer.valueOf(contactInfo[5]));
			orderRequestDateTime = LocalDateTime.of(date, orderTime);
			orderRequestTimestamp = Timestamp.valueOf(orderRequestDateTime);
		}

		// Create Order Date (current date and time)
		Timestamp orderDateTimestamp = Timestamp.valueOf(LocalDateTime.now());

		Order orderNew;
		if (returnBooleanPrefGather[2])// if delivery send delivery info
		{// Create new order
			orderNew = new Order(user.getUsername(), chosenItemsFromMenu.get(0).getMenuId(), orderDateTimestamp,
					orderRequestTimestamp, totalPrice, returnBooleanPrefGather[2], contactInfo[0], contactInfo[1],
					Integer.valueOf(contactInfo[3]), contactInfo[2]);
		} else
			orderNew = new Order(user.getUsername(), chosenItemsFromMenu.get(0).getMenuId(), orderDateTimestamp,
					orderRequestTimestamp, totalPrice, returnBooleanPrefGather[2], contactInfo[0], contactInfo[1], 0,
					contactInfo[2]);

		client.sendCreateOrderRequest(orderNew, chosenItemsFromMenu);
		client.setDiscountAmount(user.getUsername(), compensation);
		showConfirmationDialog();
		//launchHomePage();
	}
	
	private void showConfirmationDialog() {
	    Alert alert = new Alert(AlertType.INFORMATION);
	    alert.initStyle(StageStyle.UTILITY);
	    alert.setTitle("Order Confirmation");
	    alert.setHeaderText(null);
	    alert.setContentText("Order Created successfully! You will receive an update soon.");

	    ButtonType okButton = new ButtonType("OK", ButtonData.OK_DONE);
	    alert.getButtonTypes().setAll(okButton);

	    alert.showAndWait().ifPresent(response -> {
	        if (response == okButton) {
	            launchHomePage();
	        }
	    });
	}
	
	private void launchHomePage() {
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
	 
	 
	 
}