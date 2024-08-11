package ClientGUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import client.Client;
import common.Dish;
import common.User;
import java.time.LocalTime;
import java.time.Period;
import common.EnumType;

public class CustomerOrderGatherSelection {
	private User user = null;
	private List<Dish> selectedDishes = null;

	@FXML
    private Button backButton;

    @FXML
    private Button checkoutButton;

    @FXML
    private Text selectSupplyMethodText;

    @FXML
    private Text chooseDateTimeText;

    @FXML
    private Button regularButton;

    @FXML
    private Button earlyButton;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Text dateLabelText;

    @FXML
    private Text timeLabelText;

    @FXML
    private Text minutesLabelText;

    @FXML
    private Text hoursLabelText;

    @FXML
    private ComboBox<String> hoursComboBox;

    @FXML
    private ComboBox<String> minutesComboBox;

    @FXML
    private Text chooseSupplyMethodText;

    @FXML
    private Button pickupButton;

    @FXML
    private Button deliveryButton;

    @FXML
    private Text deliveryInfoText;

    @FXML
    private Text deliveryAddressText;

    @FXML
    private Text receiverText;

    @FXML
    private Text phoneNumberText;

    @FXML
    private TextField cityAddressTextField;
    
    @FXML
    private TextField streetAddressTextField;

    @FXML
    private TextField receiverTextField;

    @FXML
    private TextField phoneNumberTextField;

    @FXML
    private Text deliveryTypeText;

    @FXML
    private ComboBox<String> deliveryTypeComboBox;

    @FXML
    private TextField participantsTextField;

    @FXML
    private Text participantsText;

    @FXML
    private Text earlyOrderWarningText;

    @FXML
    private Text cityText;

    @FXML
    private Text streetText;
    
    @FXML
    private Text receiverInfoText;
    
    @FXML
    private Text errorText;
    
    @FXML
    private  Text errorRobotText;
    
    private Client client;
    
    private boolean earlyChoosed = false;
    private boolean deliveryChoosed = false;
    private boolean chooseTime = false;
    private boolean chooseSupply = false;
    private boolean chooseOptionShared = false;
    private boolean chooseOptionRobot = false;



	@FXML
	private void initialize() {
		client = client.getInstance();
		client.getCustomerOrderGatherSelection(this);
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

	// Set the user instance from the UI
	public void setUser(User user) {
		this.user = user;
	}

	// Set the dishes hash map
	public void setDishesCount(List<Dish> selectedDishesCount) {
		this.selectedDishes = selectedDishesCount;
	}
	
	/////////////////////////////////////////////////////////////////////
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
	//if returned from checkout retrieve the choices
	public void setContactInfo(String[] contactInfo) {
		cityAddressTextField.setText(contactInfo[0]);
		streetAddressTextField.setText(contactInfo[1]);
		receiverTextField.setText(contactInfo[2]);
		phoneNumberTextField.setText(contactInfo[3]);
		hoursComboBox.setValue(contactInfo[4]);
		minutesComboBox.setValue(contactInfo[5]);
		participantsTextField.setText(contactInfo[6]);
	}
	//if returned from checkout retrieve the date choice
	public void setDateInfo(Object dateOld) {
		LocalDate date = (LocalDate) dateOld;
		datePicker.setValue(date);
	}
	
	@FXML
	private void handleEarlyButtonAction(ActionEvent event) throws IOException {
		setVisibleForChoiceOfDateAndTime(true);
		
	}
	@FXML
	private void handleRegularButtonAction(ActionEvent event) throws IOException {
		setVisibleForChoiceOfDateAndTime(false);	
		earlyOrderWarningText.setVisible(false);
	}
	
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
	
	@FXML
	private void handleDeliveryButtonAction(ActionEvent event) throws IOException {
		setVisibleForDeliveryandPickup(true);	
			
	}
	@FXML
	private void handlePickupButtonAction(ActionEvent event) throws IOException {
		setVisibleForDeliveryandPickup(false);	
	}
	
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
	
	//checks if we are 2 hours at least from current time in early delivery
	@FXML
	private void handleMinuteandHourComboBoxAction(ActionEvent event) throws IOException {
		
		if (calculateTime2hours(hoursComboBox.getValue(),minutesComboBox.getValue())) {
				earlyOrderWarningText.setVisible(false);
				earlyOrderWarningText.setText("For early order you need to choose\n +2hr from now atleast");
		}
		
		else
			earlyOrderWarningText.setVisible(true);
			
	}

	
	//if we are on early order calculate if its 2 hours and above from current time
	//compares also days, check if the difference is at least one day
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
	
	private void setVisibleForDeliveryType(boolean hide) {
		participantsTextField.setVisible(hide);
		participantsText.setVisible(hide);
	}


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

	private void showError(String message) {
		errorText.setText(message);
		errorText.setVisible(true);
	}
	
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
	
	public void closeApplication() {
		client.removeClientInOrder();
		if (client != null) {
			client.userLogout(user, true);
		}
	}  
}