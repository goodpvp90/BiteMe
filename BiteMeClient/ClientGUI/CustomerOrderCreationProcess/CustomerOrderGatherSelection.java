package CustomerOrderCreationProcess;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.List;

import client.Client;
import enums.EnumType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import restaurantEntities.Dish;
import userEntities.User;

/**
 * Handles the customer order gathering and selection process.
 * Provides functionality for selecting delivery options, date and time, and managing customer inputs.
 */
public class CustomerOrderGatherSelection {
	/**
     * The user currently logged into the system.
     */
    private User user = null;

    /**
     * The list of dishes selected by the user in the creation menu.
     */
    private List<Dish> selectedDishes = null;

    /**
     * Button to navigate back to the previous page.
     */
    @FXML
    private Button backButton;

    /**
     * Button to proceed with checkout.
     */
    @FXML
    private Button checkoutButton;

    /**
     * Text indicating the supply method selection.
     */
    @FXML
    private Text selectSupplyMethodText;

    /**
     * Text indicating the date and time selection.
     */
    @FXML
    private Text chooseDateTimeText;

    /**
     * Button for selecting regular delivery.
     */
    @FXML
    private Button regularButton;

    /**
     * Button for selecting early delivery.
     */
    @FXML
    private Button earlyButton;

    /**
     * DatePicker for selecting the delivery date.
     */
    @FXML
    private DatePicker datePicker;

    /**
     * Text label for the date selection.
     */
    @FXML
    private Text dateLabelText;

    /**
     * Text label for the time selection.
     */
    @FXML
    private Text timeLabelText;

    /**
     * Text label for the minutes selection.
     */
    @FXML
    private Text minutesLabelText;

    /**
     * Text label for the hours selection.
     */
    @FXML
    private Text hoursLabelText;

    /**
     * ComboBox for selecting hours.
     */
    @FXML
    private ComboBox<String> hoursComboBox;

    /**
     * ComboBox for selecting minutes.
     */
    @FXML
    private ComboBox<String> minutesComboBox;

    /**
     * Text indicating the supply method selection.
     */
    @FXML
    private Text chooseSupplyMethodText;

    /**
     * Button for selecting pickup.
     */
    @FXML
    private Button pickupButton;

    /**
     * Button for selecting delivery.
     */
    @FXML
    private Button deliveryButton;

    /**
     * Text providing information about delivery.
     */
    @FXML
    private Text deliveryInfoText;

    /**
     * Text label for the delivery address input.
     */
    @FXML
    private Text deliveryAddressText;

    /**
     * Text label for the receiver's name input.
     */
    @FXML
    private Text receiverText;

    /**
     * Text label for the phone number input.
     */
    @FXML
    private Text phoneNumberText;

    /**
     * TextField for entering the city address.
     */
    @FXML
    private TextField cityAddressTextField;

    /**
     * TextField for entering the street address.
     */
    @FXML
    private TextField streetAddressTextField;

    /**
     * TextField for entering the receiver's name.
     */
    @FXML
    private TextField receiverTextField;

    /**
     * TextField for entering the phone number.
     */
    @FXML
    private TextField phoneNumberTextField;

    /**
     * Text label for the delivery type selection.
     */
    @FXML
    private Text deliveryTypeText;

    /**
     * ComboBox for selecting the delivery type.
     */
    @FXML
    private ComboBox<String> deliveryTypeComboBox;

    /**
     * TextField for entering the number of participants.
     */
    @FXML
    private TextField participantsTextField;

    /**
     * Text label for the number of participants input.
     */
    @FXML
    private Text participantsText;

    /**
     * Text warning for early order.
     */
    @FXML
    private Text earlyOrderWarningText;

    /**
     * Text label for the city input.
     */
    @FXML
    private Text cityText;

    /**
     * Text label for the street or work place input.
     */
    @FXML
    private Text streetText;

    /**
     * Text providing additional receiver information.
     */
    @FXML
    private Text receiverInfoText;

    /**
     * Text for displaying error messages.
     */
    @FXML
    private Text errorText;

    /**
     * Text for displaying robot-dlivery option chosen error messages.
     */
    @FXML
    private Text errorRobotText;

    /**
     * Client instance for handling server communications.
     */
    private Client client;

    /**
     * Flag indicating if early order option is chosen.
     */
    private boolean earlyChoosed = false;

    /**
     * Flag indicating if delivery option is chosen.
     */
    private boolean deliveryChoosed = false;

    /**
     * Flag indicating if one of the date and time options selected.
     */
    private boolean chooseTime = false;

    /**
     * Flag indicating if one of pickup and delivery selected.
     */
    private boolean chooseSupply = false;

    /**
     * Flag indicating if an shared delivery option is selected.
     */
    private boolean chooseOptionShared = false;

    /**
     * Flag indicating if a robot option is chosen.
     */
    private boolean chooseOptionRobot = false;


    /**
     * Initializes the controller. Sets up combo boxes and default values.
     */
	@FXML
	private void initialize() {
		client = Client.getInstance();
		//Set the hours and minute combo box with elements
		ObservableList<String> hoursList = FXCollections.observableArrayList();
        ObservableList<String> minutesList = FXCollections.observableArrayList();

        // Populate hours (00 to 23)
        for (int i = 0; i < 24; i++) {
            String hour = String.format("%02d", i); // Format as two digits
            hoursList.add(hour);
        }

        // Populate minutes (00 to 59)
        for (int i = 0; i < 60; i += 1) {
            String minute = String.format("%02d", i); // Format as two digits
            minutesList.add(minute);
        }

        hoursComboBox.setItems(hoursList);
        minutesComboBox.setItems(minutesList);
        
        //set current time
        LocalTime now = LocalTime.now();
        
		if (now.getHour() < 10)
			hoursComboBox.setValue("0" + String.valueOf(now.getHour()));
		else
			hoursComboBox.setValue(String.valueOf(now.getHour()));

		if (now.getMinute() < 10)
			minutesComboBox.setValue("0" + String.valueOf(now.getMinute()));
		else
			minutesComboBox.setValue(String.valueOf(now.getMinute()));
        
        //set type of delivery combo box with elements
        ObservableList<String> typeOfDelivery = FXCollections.observableArrayList(
        		"Normal", "Shared","Robot");
        deliveryTypeComboBox.setItems(typeOfDelivery);

        
        //set current date and time
        datePicker.setValue(LocalDate.now());
        
        //set delivery type to default
        deliveryTypeComboBox.setValue("Normal");
        
	}

	
	/**
     * Sets the user instance from the UI.
     * 
     * @param user The user currently logged into the system.
     */
	public void setUser(User user) {
		this.user = user;
	}

	/**
     * Sets the list of selected dishes from the server.
     * 
     * @param selectedDishesCount The list of dishes selected by the user.
     */
	public void setDishesCount(List<Dish> selectedDishesCount) {
		this.selectedDishes = selectedDishesCount;
	}
	
	/**
     * Sets various boolean parameters and updates the UI accordingly.
     * 
     * @param param Array of boolean parameters to set.
     */
	public void setBooleanParam(boolean[] param) {
		earlyChoosed = param[0];
		setVisibleForChoiceOfDateAndTime(earlyChoosed);
		chooseTime = param[1];

		if(chooseTime) {
			earlyButton.setDisable(earlyChoosed);
			regularButton.setDisable(!earlyChoosed);
		}
			
		
		deliveryChoosed = param[2];
		setVisibleForDeliveryandPickup(deliveryChoosed);
		
		chooseSupply = param[3];
		if(chooseSupply)
		{
			deliveryButton.setDisable(deliveryChoosed);
			pickupButton.setDisable(!deliveryChoosed);

		}	
		
		chooseOptionShared = param[4];
		setVisibleForDeliveryType(chooseOptionShared);
		if(param[4]) {
			deliveryTypeComboBox.setValue("Shared");
		}
		
	}
	
	/**
     * Updates the contact information fields.
     * 
     * @param contactInfo Array of contact information.
     */
	public void setContactInfo(String[] contactInfo) {
		cityAddressTextField.setText(contactInfo[0]);
		streetAddressTextField.setText(contactInfo[1]);
		receiverTextField.setText(contactInfo[2]);
		phoneNumberTextField.setText(contactInfo[3]);
		hoursComboBox.setValue(contactInfo[4]);
		minutesComboBox.setValue(contactInfo[5]);
		participantsTextField.setText(contactInfo[6]);
	}
	
	
	/**
     * Updates the date picker with the given date.
     * 
     * @param dateOld The date to set in the date picker.
     */
	public void setDateInfo(Object dateOld) {
		LocalDate date = (LocalDate) dateOld;
		datePicker.setValue(date);
	}
	
	
	/**
     * Handles the action for the early order button. Shows date and time selection.
     * 
     * @param event The action event triggered by the button click.
     */
	@FXML
	private void handleEarlyButtonAction(ActionEvent event) throws IOException {
		setVisibleForChoiceOfDateAndTime(true);
		
	}
	
	/**
     * Handles the action for the regular button. Shows delivery options.
     * 
     * @param event The action event triggered by the button click.
     */
	@FXML
	private void handleRegularButtonAction(ActionEvent event) throws IOException {
		setVisibleForChoiceOfDateAndTime(false);	
		earlyOrderWarningText.setVisible(false);
	}
	
    /**
     * Sets visibility for date and time selection according early or pick up selection.
     *
     * @param hide If true, hides the date and time selection components.
     */
	private void setVisibleForChoiceOfDateAndTime(boolean hide) {
		chooseTime = true;
		earlyChoosed=hide;
		regularButton.setDisable(!hide);
		earlyButton.setDisable(hide);
		
        datePicker.setVisible(hide);
        dateLabelText.setVisible(hide);
        timeLabelText.setVisible(hide);
        minutesLabelText.setVisible(hide);
        hoursLabelText.setVisible(hide);
        hoursComboBox.setVisible(hide);
        minutesComboBox.setVisible(hide);

	}
	
	 /**
     * Handles the action for the delivery button.
     *
     * @param event The action event.
     * @throws IOException If an I/O error occurs.
     */
	@FXML
	private void handleDeliveryButtonAction(ActionEvent event) throws IOException {
		setVisibleForDeliveryandPickup(true);	
			
	}
	
	/**
     * Handles the action for the pickup button.
     *
     * @param event The action event.
     * @throws IOException If an I/O error occurs.
     */
	@FXML
	private void handlePickupButtonAction(ActionEvent event) throws IOException {
		setVisibleForDeliveryandPickup(false);	
	}
	
	/**
     * Sets visibility for delivery and pickup options.
     *
     * @param hide If true, hides the delivery and pickup components.
     */
	private void setVisibleForDeliveryandPickup(boolean hide) {
		chooseSupply = true;
		deliveryButton.setDisable(hide);
		pickupButton.setDisable(!hide);
		deliveryChoosed=hide;
		
		deliveryInfoText.setVisible(hide);
		deliveryAddressText.setVisible(hide);
		receiverText.setVisible(hide);
		phoneNumberText.setVisible(hide);
		cityAddressTextField.setVisible(hide);
		streetAddressTextField.setVisible(hide);
		receiverTextField.setVisible(hide);
		phoneNumberTextField.setVisible(hide);
		deliveryTypeText.setVisible(hide);
		deliveryTypeComboBox.setVisible(hide);
		cityText.setVisible(hide);
		streetText.setVisible(hide);
		receiverInfoText.setVisible(hide);
		
		participantsTextField.setVisible(false);
		participantsText.setVisible(false);
		errorRobotText.setVisible(false);
		chooseOptionShared = false;
		chooseOptionRobot = false;
		deliveryTypeComboBox.setValue("Normal");
		
	}
	
	/**
     * Handles the action for the minute and hour ComboBoxes, checking if the selected time is at least 2 hours from the current time for early delivery.
     * 
     * @param event The action event triggered by the ComboBox selection.
     * @throws IOException If an I/O error occurs during the operation.
     */
	@FXML
	private void handleMinuteandHourComboBoxAction(ActionEvent event) throws IOException {
		
		if (calculateTime2hours(hoursComboBox.getValue(),minutesComboBox.getValue())) {
				earlyOrderWarningText.setVisible(false);
				earlyOrderWarningText.setText("For early order you need to choose\n +2hr from now atleast");
		}
		
		else
			earlyOrderWarningText.setVisible(true);
			
	}

	
	/**
     * Calculates whether the selected time is at least 2 hours from the current time for early delivery, considering day differences.
     * 
     * @param hour The selected hour.
     * @param minute The selected minute.
     * @return true if the selected time is at least 2 hours from now, false otherwise.
     */
	private boolean calculateTime2hours(String hour, String minute ) {
		LocalTime now = LocalTime.now();
		Period period = Period.between(LocalDate.now(),datePicker.getValue());
		return ((Integer.valueOf(hour)-now.getHour()>=3)&&(period.getDays()==0))||
				(((Integer.valueOf(hour)-now.getHour()==2) && (Integer.valueOf(minute)-now.getMinute()>=0))&&(period.getDays()==0))||
				(period.getDays()>1)||
				((period.getDays()==1 && (Integer.valueOf(hour)-(now.getHour()-22)>0)))||
				((period.getDays()==1 && (Integer.valueOf(hour)-(now.getHour()-22)==0)) &&
				(Integer.valueOf(minute)-(now.getMinute())>=0));
	}
	
	/**
     * Handles the action for the delivery type ComboBox, updating visibility and options based on the selected delivery type.
     */
	@FXML
	private void handleDeliveryTypeComboBoxAction() {
		String choice = deliveryTypeComboBox.getValue();
		switch(choice) {
		case "Normal":
			setVisibleForDeliveryType(false);
			chooseOptionShared = false;
			chooseOptionRobot = false;
			errorRobotText.setVisible(false);
			break;
		case "Shared":
			setVisibleForDeliveryType(true);
			chooseOptionShared = true;
			chooseOptionRobot = false;
			errorRobotText.setVisible(false);
			break;
		case "Robot":
			setVisibleForDeliveryType(false);
			chooseOptionShared = false;
			chooseOptionRobot = true;
			errorRobotText.setVisible(true);
		}
	}
	
	/**
     * Sets the visibility of the delivery type fields based on the hide parameter.
     * will shot text and text field if shared option selected.
     * 
     * @param hide true to hide the fields, false to show them.
     */
	private void setVisibleForDeliveryType(boolean hide) {
		participantsTextField.setVisible(hide);
		participantsText.setVisible(hide);
	}


	/**
     * Validates the delivery information input fields.
     * 
     * @return true if all delivery information fields are valid, false otherwise.
     */
	private boolean checkInputForDeliveryInfo() {
        if (cityAddressTextField.getText().isEmpty()) {
            showError("City address cannot be empty.");
            return false;
        } else if (!cityAddressTextField.getText().matches("[a-zA-Z -]+")) {
            showError("City address must contain only letters.");
            return false;
        }

        if (streetAddressTextField.getText().isEmpty()) {
            showError("Street address cannot be empty.");
            return false;
        } else if (!streetAddressTextField.getText().matches("[a-zA-Z0-9 ,.-]+")) {
            showError("Street address must contain only letters and numbers.");
            return false;
        }


        if (receiverTextField.getText().isEmpty()) {
            showError("Receiver cannot be empty.");
            return false;
        } else if (!receiverTextField.getText().matches("[a-zA-Z ]+")) {
            showError("Receiver must contain only letters.");
            return false;
        }
        
        if (phoneNumberTextField.getText().isEmpty()) {
            showError("Phone number cannot be empty.");
            return false;
        } else if (!phoneNumberTextField.getText().matches("\\d+")) {
            showError("Phone number must contain only numbers.");
            return false;
        }

        return true;
	}
	
	/**
     * Validates the number of participants input field.
     * 
     * @return true if the participants input field is valid, false otherwise.
     */
	private boolean checkInputForParticipantsNum() {
		if (participantsTextField.getText().isEmpty()) {
            showError("Participants box cannot be empty.");
            return false;
        } else if (!participantsTextField.getText().matches("\\d+")) {
            showError("Participants box must contain only numbers.");
            return false;
        }
		return true;
	}

	
	 /**
     * Handles the action for the checkout button, validating inputs and launching the checkout process.
     * 
     * @param event The action event triggered by the button click.
     */
	@FXML
	private void handleCheckoutButtonAction(ActionEvent event) {
		if(!chooseTime) {
			showError("Please choose one of date&time option.");
			return;
		}
		if(!chooseSupply) {
			showError("Please choose one of supply option.");
			return;
		}
		if(!checkInputForDeliveryInfo() && deliveryChoosed) 
			return;
		if(!calculateTime2hours(hoursComboBox.getValue(),minutesComboBox.getValue())&& earlyChoosed) {
			showError("For early order you need to choose +2hr from now atleast.");
			return;
		}
		if(!checkInputForParticipantsNum() &&chooseOptionShared) {
			return;
		}
		if (chooseOptionRobot) {
			showError("Please choose different delivery type.");
			return;
		} 
		if(user.getCustomerType()!=EnumType.BUSINESS && chooseOptionShared) {
			showError("Only business users can choose shared delivery.\n Please choose different delivery type.");
			return;
		}

			
		launchCheckout();
	}

	 /**
     * Handles the action for the back button, returning to the previous screen.
     * 
     * @param event The action event triggered by the button click.
     * @throws IOException If an I/O error occurs during the operation.
     */
	@FXML
	private void handleBackButtonAction(ActionEvent event) throws IOException {

		CustomerOrderCreationUI COrderCreateApp = new CustomerOrderCreationUI(user, selectedDishes);
		try {
			COrderCreateApp.start(new Stage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// close current window
		((Stage) backButton.getScene().getWindow()).close();

	}
	
	/**
     * Displays an error message in the errorText field.
     * 
     * @param message The error message to display.
     */
	private void showError(String message) {
		errorText.setText(message);
		errorText.setVisible(true);
	}
	
	private void launchCheckout() {
		boolean[] param = {earlyChoosed,chooseTime,deliveryChoosed,chooseSupply,chooseOptionShared};
		String[] paramContacts = { cityAddressTextField.getText(), streetAddressTextField.getText(),
				receiverTextField.getText(), phoneNumberTextField.getText(), hoursComboBox.getValue(),
				minutesComboBox.getValue(),participantsTextField.getText() };
		try {
        	CustomerCheckoutUI Userapp = 
        			new CustomerCheckoutUI(user,selectedDishes,param,paramContacts,datePicker.getValue());
        	Userapp.start(new Stage());
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred while loading the User Checkout Page.");
        }
	}
	
	 /**
     * Closes the application, removing the client from the order and logging out the user.
     */
	public void closeApplication() {
		client.removeClientInOrder();
		if (client != null) {
			client.userLogout(user, true);
		}
	}  
}