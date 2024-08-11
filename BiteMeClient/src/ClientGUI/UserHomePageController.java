package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;

import client.Client;
import common.EnumType;
import common.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Controller class for the User Home Page in the BiteMe application.
 * This controller manages the display and functionality of the User Home Page,
 * which varies based on the user's type and registration status.
 */
public class UserHomePageController {

	
	private User user;
	private Client client;
	
    @FXML
    private Button logoutButton;

    @FXML
    private Button createOrderButton;

    @FXML
    private Button updateMenuButton;

    @FXML
    private Button viewReportsButton;

    @FXML
    private Button changeHomeBranchButton;

    @FXML
    private Button pendingOrdersButton;

    @FXML
    private Button registerUserButton;

    @FXML
    private Button myOrdersButton;
    
    @FXML
    private Text welcomeText;
    
    @FXML
    private Text headlineText;

    private boolean isRegistered;

    /**
     * Sets the user for this controller and updates the UI accordingly.
     *
     * @param user The User object representing the current user.
     * @param isRegistered A boolean indicating whether the user is registered.
     */
    public void setUser(User user, boolean isRegistered) {
        this.user = user;
        this.isRegistered = isRegistered;
        updateUI();
    }
    
    /**
     * Updates the UI based on the user's type and registration status.
     */  
    private void updateUI() {
    	if (user.getType() == null) {
            // Handle unregistered user
            viewReportsButton.setVisible(false);
            registerUserButton.setVisible(false);
            updateMenuButton.setVisible(false);
            pendingOrdersButton.setVisible(false);
            createOrderButton.setVisible(false);
            changeHomeBranchButton.setVisible(false);
            myOrdersButton.setVisible(false);
            
        } 
    	else {switch(user.getType()) {
    	//CEO AND BM Same buttons but other functionalities and roles.
    	case WORKER:
    		viewReportsButton.setVisible(false);
    		registerUserButton.setVisible(false);
	        changeHomeBranchButton.setVisible(false);
	        myOrdersButton.setVisible(false);
    		break;
    	case CUSTOMER:
    		viewReportsButton.setVisible(false);
    		registerUserButton.setVisible(false);
    		updateMenuButton.setVisible(false);
    		pendingOrdersButton.setVisible(false);
    		break;
    	default://if CEO or Branch Manager
            changeHomeBranchButton.setVisible(false);
            myOrdersButton.setVisible(false);
    	}
    	
        }
    	changeHelloTextAndHeadline();
	}
    
    /**
     * Initializes the controller. This method is automatically called after the FXML file has been loaded.
     */
	@FXML
    private void initialize() {
        client = Client.getInstance();
        
        
    }
		
    /**
     * Handles the logout action, And returns to Clien Login Screen.
     *
     * @param event The ActionEvent triggered by clicking the logout button.
     */
	@FXML
	private void handleLogout(ActionEvent event) {
	    // Send logout request to the server
	    client.userLogout(user);
	    try {
	        Stage stage = (Stage) logoutButton.getScene().getWindow();
	        Scene loginScene = ClientLoginUI.loadLoginScene();
	        stage.setScene(loginScene);
	        stage.setTitle("Client Login");
	        stage.show();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

    /**
     * Handles the create order action.
     *
     * @param event The ActionEvent triggered by clicking the create order button.
     */
    @FXML
    private void handleCreateOrder(ActionEvent event) {
    	try {
            // Launch CustomerOrderCreationUI future logic
    		//OFEK changed this one to work on this current version	
    		CustomerOrderCreationUI CustCreatApp = new CustomerOrderCreationUI(user,null);
    		//use this when we don't want to test user
    		//CustomerOrderCreationUI CustCreatApp = new CustomerOrderCreationUI();
    		CustCreatApp.start(new Stage());

            // Close the current stage
            Stage currentStage = (Stage) createOrderButton.getScene().getWindow();
            currentStage.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMyOrders(ActionEvent event) {
    	try {
    		MyOrdersUI UIMyOrderApp = new MyOrdersUI(user);
    		UIMyOrderApp.start(new Stage());
            Stage currentStage = (Stage) myOrdersButton.getScene().getWindow();
            currentStage.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Handles the update menu action.
     *
     * @param event The ActionEvent triggered by clicking the update menu button.
     */
    @FXML
    private void handleUpdateMenu(ActionEvent event) {
    	try {
            
    		UpdateMenuNavigationUI UIApp = new UpdateMenuNavigationUI(user);
    		
    		UIApp.start(new Stage());

            // Close the current stage
            Stage currentStage = (Stage) updateMenuButton.getScene().getWindow();
            currentStage.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Handles the view reports action.
     *
     * @param event The ActionEvent triggered by clicking the view reports button.
     */   
    @FXML
    private void handleViewReports(ActionEvent event) {
    	try {
    		ReportsPageUI UIReeportApp = new ReportsPageUI(user);
    		UIReeportApp.start(new Stage());
            Stage currentStage = (Stage) changeHomeBranchButton.getScene().getWindow();
            currentStage.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the change home branch action.
     *
     * @param event The ActionEvent triggered by clicking the change home branch button.
     */
    @FXML
    private void handleChangeHomeBranch(ActionEvent event) {
    	try {
    		HomeBranchChangeUI UIApp = new HomeBranchChangeUI(user);
    		UIApp.start(new Stage());

            // Close the current stage
            Stage currentStage = (Stage) changeHomeBranchButton.getScene().getWindow();
            currentStage.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the pending orders action.
     *
     * @param event The ActionEvent triggered by clicking the pending orders button.
     */
    @FXML
    private void handlePendingOrders(ActionEvent event) {
    	try {
            // Launch CustomerOrderCreationUI future logic
    		//OFEK changed this one to work on this current version	
    		WorkerPendingOrdersUI CustCreatApp = new WorkerPendingOrdersUI(user);
    		//use this when we don't want to test user
    		//CustomerOrderCreationUI CustCreatApp = new CustomerOrderCreationUI();
    		CustCreatApp.start(new Stage());

            // Close the current stage
            Stage currentStage = (Stage) createOrderButton.getScene().getWindow();
            currentStage.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the welcome text and headline based on the user's type and registration status.
     */
    private void changeHelloTextAndHeadline() {
        String userType = "";
        
        if (user.getType() == null) {
            userType = "Unregistered Customer";
        } else {
            switch(user.getType()) {
                case CEO:
                    userType = "CEO";
                    break;
                case BRANCH_MANAGER:
                    userType = "Manager";
                    break;
                case WORKER:
                    userType = "Worker";
                    break;
                case CUSTOMER:
                	userType = "Customer";
            }
        } 
        headlineText.setText(user.getUsername() + ", " + userType);
        if (user.getType() == null) {
            // For unregistered customer
            welcomeText.setText("Hello, looks like\n"
                    + "you have not registered yet.\n"
                    + "Please make contact with a\n"
                    + "manager of your preferred branch.");
        } else {
            // For all registered users
            welcomeText.setText("Hello " + user.getFirstName() + ", what would you like to do?");
        }
    }
    
    /**
     * Handles the register user action.
     *
     * @param event The ActionEvent triggered by clicking the register user button.
     */   
    @FXML
    private void handleRegisterUser(ActionEvent event) {
    	try {
    		RegisterUserPageUI UIAppReg = new RegisterUserPageUI(user);
    		UIAppReg.start(new Stage());

            // Close the current stage
            Stage currentStage = (Stage) changeHomeBranchButton.getScene().getWindow();
            currentStage.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void showNotificationDialog(List<String> text) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initStyle(StageStyle.UTILITY);
		alert.setTitle("INCOMING NOTIFICATION");
		alert.setHeaderText("You got a message!");
		String content = String.join("\n", text);
	    alert.setContentText(content);
	    ButtonType okButton = new ButtonType("CLOSE", ButtonData.OK_DONE);
		alert.getButtonTypes().setAll(okButton);
		alert.showAndWait().ifPresent(response -> {
			if (response == okButton) {
				alert.close(); // Close the dialog window
			}
		});
	}
    
    /**
     * Logs out the user from the database.
     */
    public void logoutUser() {
        if (user != null && client != null) {
            client.userLogout(user);
        }
    }
    
    /**
     * Closes the application, ensuring proper logout and client shutdown.
     */
    public void closeApplication() 
    {
    	Platform.runLater(() ->
    	{
        if (client != null) 
        {
            System.out.println("Closing application from UserHomePage");
            client.userLogout(user);
            client.quit();
        }
        Platform.exit();
        System.exit(0);
    });
}}