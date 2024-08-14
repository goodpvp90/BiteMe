package CustomerOrderCreationProcess;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import UserHomePageProcess.UserHomePageUI;
import client.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import restaurantEntities.Dish;
import restaurantEntities.Order;
import userEntities.User;

/**
 * Controller for the customer checkout process. Manages the checkout UI,
 * calculates total prices, applies discounts and compensation, and confirms orders.
 */
public class CustomerCheckout {
	
	/**
	 * Button to navigate back to the previous screen or menu selection.
	 */
	@FXML
	private Button backButton;

	/**
	 * Button to confirm the order and proceed with checkout.
	 */
	@FXML
	private Button confirmOrderButton;

	/**
	 * Displays detailed information about the customer.
	 */
	@FXML
	private Text customerDetailsText;

	/**
	 * Label for displaying the name of the receiver.
	 */
	@FXML
	private Text receiverNameLabel;

	/**
	 * Label for displaying the phone number of the receiver.
	 */
	@FXML
	private Text phoneNumberLabel;

	/**
	 * Label for displaying the delivery address of the receiver.
	 */
	@FXML
	private Text deliveryAddressLabel;

	/**
	 * TableView displaying the list of selected dishes for the order.
	 */
	@FXML
	private TableView<Dish> menuTableView;

	/**
	 * TableColumn showing the name of each dish.
	 */
	@FXML
	private TableColumn<Dish, String> nameColumn;

	/**
	 * TableColumn showing the price of each dish.
	 */
	@FXML
	private TableColumn<Dish, Double> priceColumn;

	/**
	 * TableColumn showing optional extras for each dish.
	 */
	@FXML
	private TableColumn<Dish, String> optionalsColumn;

	/**
	 * TableColumn showing any comments associated with each dish.
	 */
	@FXML
	private TableColumn<Dish, String> commentsColumn;

	/**
	 * Text displaying information about the selected items in the order.
	 */
	@FXML
	private Text selectedItemsText;

	/**
	 * Text displaying the receipt or order summary.
	 */
	@FXML
	private Text receiptText;

	/**
	 * Text displaying the phone number of the customer or receiver.
	 */
	@FXML
	private Text phoneNumInfoText;

	/**
	 * Text displaying the name of the customer or receiver.
	 */
	@FXML
	private Text receiverNameInfoText;

	/**
	 * Text displaying the delivery address for the order.
	 */
	@FXML
	private Text deliveryAddInfoText;

	/**
	 * Label for displaying the total price of the order.
	 */
	@FXML
	private Text totalPriceLabel;

	/**
	 * Text displaying detailed price information including any discounts or additional charges.
	 */
	@FXML
	private Text priceTextInfo;

	/**
	 * Text displaying information about discounts applied to the order.
	 */
	@FXML
	private Text discountsText;

	/**
	 * Label for indicating early discount information.
	 */
	@FXML
	private Text earlyLabel;

	/**
	 * Label for indicating shared delivery information.
	 */
	@FXML
	private Text sharedDeliveryLabel;

	/**
	 * Label for indicating late order compensation information.
	 */
	@FXML
	private Text lateOrderCompensationLabel;

	/**
	 * Text displaying information about early discount eligibility and amount.
	 */
	@FXML
	private Text earlyInfoText;

	/**
	 * Text displaying information about shared delivery eligibility and amount.
	 */
	@FXML
	private Text sharedDeliveryInfoText;

	/**
	 * Text displaying information about late order delivery compensation.
	 */
	@FXML
	private Text lateOrderDeliveryInfoText;

	/**
	 * Text displaying compensation information if applicable.
	 */
	@FXML
	private Text compensationText;

	/**
	 * Button to confirm and apply compensation if the user chooses to do so.
	 */
	@FXML
	private Button yesButton;

	/**
	 * Button to reject compensation if the user chooses not to apply it.
	 */
	@FXML
	private Button noButton;

	/**
	 * Text displaying a message related to compensation, if applicable.
	 */
	@FXML
	private Text compensationMessageText;

	/**
	 * Text displaying the amount of compensation offered.
	 */
	@FXML
	private Text compensationValueText;

	/**
	 * Text displaying error messages if any issues occur during the checkout process.
	 */
	@FXML
	private Text errorText;

	/**
	 * List of dishes selected by the customer for the order.
	 */
	private List<Dish> chosenItemsFromMenu = new ArrayList<>();

	/**
	 * The client instance used to communicate with the server and handle order processing.
	 */
	private Client client;

	/**
	 * The user who is placing the order.
	 */
	private User user;

	/**
	 * Array of booleans representing the user's preferences and options for the order.
	 */
	private boolean[] returnBooleanPrefGather;

	/**
	 * Array of strings containing contact information for the delivery.
	 */
	private String[] contactInfo;

	/**
	 * The date of the order, used for early discounts and scheduling.
	 */
	private LocalDate date;

	/**
	 * The total price of the selected dishes before applying discounts and compensation.
	 */
	private double totalPrice = 0;

	/**
	 * The temporary price after applying compensation but before final adjustments.
	 */
	private double priceTempAfterComp;

	/**
	 * The amount of compensation, only if user deserves from previous orders.
	 */
	private double compensation;

	/**
	 * Indicates whether the user has chosen to use compensation.
	 */
	private boolean choosedUseCompensation = false;

	/**
	 * Indicates whether the user deserves compensation based on order conditions.
	 */
	private boolean isdeservingCompensation = false;

	/**
	 * The cost associated with delivering the order.
	 */
	private int deliveryPrice;

	
    /**
     * Initializes the CustomerCheckout controller. Sets up table columns and client instance.
     */
    @FXML
    private void initialize() {
    	client = Client.getInstance();
    	client.getInstanceOfCustomerCheckout(this);
    	nameColumn.setCellValueFactory(new PropertyValueFactory<>("dishName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        optionalsColumn.setCellValueFactory(new PropertyValueFactory<>("optionalPick")); 
        commentsColumn.setCellValueFactory(new PropertyValueFactory<>("comments"));    	
    }

    
    /**
     * Sets the chosen items from the menu Picked by the user in the Gather UI
     * 
     * @param chosenItemsFromMenu the list of dishes selected by the customer
     */
    public void setChosenItemsFromMenu(List<Dish> chosenItemsFromMenu) {
    	this.chosenItemsFromMenu = chosenItemsFromMenu;
    }
    
    /**
     * Sets the return boolean preferences of the user in gather UI.
     * the array contains choices of option: {early or regular ,if one of early or regular chosen
     * if deliver chosen ,if one of pickup or delivery chosen, if shared delivery option chosen}
     * 
     * @param param an array of booleans representing the user's choices
     */
    public void setReturnBooleanPrefGather(boolean[] param) {
    	returnBooleanPrefGather = param;
    }
    
    /**
     * Sets the user for this checkout process and updates the UI with user details.
     * fill the dish table with dishes from chosenItemsFromMenu and calling method
     * for calculate the user total price 
     * 
     * @param user the user who is checking out
     */
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
    
    /**
     * Sets the contact information provided by the gather UI, receiving
     * {City address, Street address,Receiver name, phone number,order hour
     * minutes, participants number in shared delivery }
     * 
     * @param contactInfo an array of strings containing contact details
     */
    public void setContacts (String[] contactInfo) {
    	this.contactInfo = contactInfo;    	
    }
    
    /**
     * Sets the compensation amount for the user if he deserves one.
     * 
     * @param compensation the amount of compensation the user deserves
     */
	public void setCompensation(double compensation) {
		this.compensation = compensation;
	}
    
	
	 /**
     * Set client details on the screen
     * 
     * @param name of the client
     * @param phone of the client
     * @param address of the client
     */
    private void setDetailsOnClient(String name, String phone, String address) {
    	
    	receiverNameInfoText.setText(name);
		phoneNumInfoText.setText(phone);
		deliveryAddInfoText.setText(address);

    }
    
    /**
     * Sets the date the user asked for the order if early order chosen.
     * 
     * @param date the date of the order
     */
    public void setDate (Object date) {
    	this.date=(LocalDate)date;
    }

    /**
     * Handles the action when the back button is pressed and return to user home page
     * 
     * @param event the event triggered by pressing the back button
     * @throws IOException if an I/O error occurs while loading the page
     */
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
        }
    }

   
    /**
     * Calculates and updates the total price of selected dishes, including any discounts and delivery costs.
     */
	private void checkoutTotalPrice() {
		for (Dish dish : chosenItemsFromMenu) {
			totalPrice += dish.getPrice();
		}
		
		setEarlyDiscount();
		calculateDelivery();	
		//if delivery chosen
		if(returnBooleanPrefGather[2])
			priceTextInfo.setText(String.format("%.2f", totalPrice) +
					" (Delivery price included: " + deliveryPrice+")");
		else
			priceTextInfo.setText(String.format("%.2f", totalPrice));
	}

	/**
     * Update user compensation discount if eligible
     */
	private void setEarlyDiscount() {
    	if (returnBooleanPrefGather[0]) {
    		totalPrice*=0.9;
    		earlyInfoText.setText("Discount Confirmed: 10%");
    	}
    	else
    		earlyInfoText.setText("Option not choosed");

    }
	
	
	/**
     * Update user compensation discount if eligible
     */
	private void deserveCompensation() {
		client.getDiscountAmount(user.getUsername());
		Platform.runLater(() -> {
			if (compensation != 0) {
				isdeservingCompensation = true;
				showCompensationText(true);
				compensationValueText.setText("You deserve: " + compensation);
				lateOrderDeliveryInfoText.setText("Please Choose yes/no.");
			}
		});

	}

	/**
     * Display user compensation discount dialog if eligible for user to chose 
     * if he want to use it or not
     * 
     * @param show A boolean indicating whether to show or hide the compensation discount options.
     */
	private void showCompensationText(boolean show) {
		compensationMessageText.setVisible(show);
		compensationText.setVisible(show);
		yesButton.setVisible(show);
		noButton.setVisible(show);
		compensationValueText.setVisible(show);
	}
	
	/**
     * Handle user "Yes" selection and reduce from the total price the compensation
     * value the user had 
     */
	@FXML
	private void handleYesCompensationButton(ActionEvent event) {
	    changedYesNofunctions(false);
	    BigDecimal compensationDecimal = new BigDecimal(compensation).setScale(2, RoundingMode.HALF_UP);
	    BigDecimal totalPriceDecimal = new BigDecimal(totalPrice).setScale(2, RoundingMode.HALF_UP);
	    if (compensationDecimal.compareTo(totalPriceDecimal) <= 0) {
	        lateOrderDeliveryInfoText.setText("Discount confirmed: " + compensationDecimal);
	        priceTempAfterComp = totalPriceDecimal.doubleValue();
	        totalPriceDecimal = totalPriceDecimal.subtract(compensationDecimal);
	        compensationDecimal = BigDecimal.ZERO;
	    } else {
	        lateOrderDeliveryInfoText.setText("Discount confirmed: " + totalPriceDecimal);
	        compensationDecimal = compensationDecimal.subtract(totalPriceDecimal);
	        priceTempAfterComp = totalPriceDecimal.doubleValue();
	        totalPriceDecimal = BigDecimal.ZERO;
	    }
	    compensation = compensationDecimal.doubleValue();
	    totalPrice = totalPriceDecimal.doubleValue();
	    if (returnBooleanPrefGather[2]) { // if delivery chosen
	        priceTextInfo.setText(String.format("%.2f", totalPrice) + 
	                " (Delivery price included: " + deliveryPrice + ")");
	    } else {
	        priceTextInfo.setText(String.format("%.2f", totalPrice));
	    }
	}
	
	/**
     * Handles the action when the user selects "No" for compensation. 
     * It processes the compensation value and updates the total price based on 
     * the current order price before compensation adjustments (if any were made). 
     * If there were previous compensation changes, they are taken into account.
     * The method also updates the UI to reflect the selected action and the total price.
     * 
     * @param event The action event triggered by the "No" button click, 
     * which indicates that the user has declined the compensation offer.
     */
	@FXML
	private void handleNoCompensationButton(ActionEvent event) {
		changedYesNofunctions(true);
		BigDecimal compensationDecimal = new BigDecimal(compensation).setScale(2, RoundingMode.HALF_UP);
		BigDecimal totalPriceDecimal = new BigDecimal(totalPrice).setScale(2, RoundingMode.HALF_UP);
		BigDecimal priceTempAfterCompDecimal = new BigDecimal(priceTempAfterComp).setScale(2, RoundingMode.HALF_UP);
		if (priceTempAfterCompDecimal.compareTo(BigDecimal.ZERO) != 0) {
			if (compensationDecimal.compareTo(totalPriceDecimal) <= 0) {
				compensationDecimal = priceTempAfterCompDecimal.subtract(totalPriceDecimal);
				totalPriceDecimal = priceTempAfterCompDecimal;
			} else {
				compensationDecimal = compensationDecimal.add(priceTempAfterCompDecimal.add(totalPriceDecimal));
				totalPriceDecimal = priceTempAfterCompDecimal;
			}
			priceTempAfterCompDecimal = BigDecimal.ZERO;
		}
		compensation = compensationDecimal.doubleValue();
		totalPrice = totalPriceDecimal.doubleValue();
		priceTempAfterComp = priceTempAfterCompDecimal.doubleValue();
		lateOrderDeliveryInfoText.setText("Discount was not chosen");
		if (returnBooleanPrefGather[2]) { // if delivery chosen
			priceTextInfo
					.setText(String.format("%.2f", totalPrice) + " (Delivery price included: " + deliveryPrice + ")");
		} else {
			priceTextInfo.setText(String.format("%.2f", totalPrice));
		}
	}
	
	/**
     * Handle visibility of "Yes" and "No" button and hide and show them according the user decision
     * 
     * @param hide A boolean indicating whether to hide the "Yes" button and show the "No" button.
     */
	private void changedYesNofunctions(boolean hide) {
		errorText.setVisible(false);
		yesButton.setDisable(!hide);
		noButton.setDisable(hide);
		choosedUseCompensation= true;
	}
	
	/**
	 * Calculates and updates the delivery price based on the chosen delivery options.
	 */
	private void calculateDelivery() {
		deliveryPrice=0;
		//if normal delivery chosen
		if(returnBooleanPrefGather[2]) {
			deliveryPrice=25;
			//if shared delivery chosen	
			if(returnBooleanPrefGather[4]&& Integer.parseInt(contactInfo[6])!=0) {
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
	
	/**
	 * Displays an error message in the UI.
	 *
	 * @param errmsg the error message to display
	 */
	private void showError(String errmsg) {
		errorText.setText(errmsg);
		errorText.setVisible(true);
	}

	
	/**
	 * Handles the action when the confirm order button is pressed.
	 * Creates an order and sends it to the server.
	 *
	 * @param event the event triggered by pressing the confirm order button
	 */
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
			orderNew = new Order(user.getUsername(), chosenItemsFromMenu.get(0).getMenuId(), orderDateTimestamp,
					orderRequestTimestamp, totalPrice, returnBooleanPrefGather[2], contactInfo[0], contactInfo[1],
					contactInfo[3], contactInfo[2]);
		client.sendCreateOrderRequest(orderNew, chosenItemsFromMenu);
		client.setDiscountAmount(user.getUsername(), compensation);
		showConfirmationDialog();
	}
	
	/**
	 * Displays a confirmation dialog for a successful order creation.
	 * If the OK button is pressed or the dialog is closed, the home page is launched.
	 */
	private void showConfirmationDialog() {
		Platform.runLater(() -> {
	    Alert alert = new Alert(Alert.AlertType.INFORMATION);
	    alert.initStyle(StageStyle.UTILITY);
	    alert.setTitle("Order Confirmation");
	    alert.setHeaderText(null);
	    alert.setContentText("Order created successfully! You will receive an update soon.");
	    ButtonType okButton = new ButtonType("OK", ButtonData.OK_DONE);
	    alert.getButtonTypes().setAll(okButton);
	    alert.showAndWait().ifPresentOrElse(response -> {
	        if (response == okButton) {
	            launchHomePage();
	        }
	    }, () -> {
	        launchHomePage();  // Handle the X button press
	    });
		});
	}
	
	/**
	 * Creates the home page stage and closes the current stage.
	 */
	private void launchHomePage() {
		client.removeClientInOrder();
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
	 * Closes the application and logs out the user.
	 */
	public void closeApplication() {
		client.removeClientInOrder();
		if (client != null) {
			client.userLogout(user, true);
		}
	}  

	 
	 
}