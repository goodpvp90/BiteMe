package ClientGUI;

import java.io.IOException;
import java.util.List;

import client.Client;
import enums.EnumType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import userEntities.User;

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


    /**
     * Sets the user for this controller and updates the UI accordingly.
     *
     * @param user The User object representing the current user.
     */
    public void setUser(User user) {
        this.user = user;
        updateUI();      
    }
    
    /**
     * Updates the UI based on the user's type and registration status.
     */  
    private void updateUI() {
    	
    	switch(user.getType()) {    	
    	case CEO:
    		updateMenuButton.setVisible(false);
    		pendingOrdersButton.setVisible(false);
	        changeHomeBranchButton.setVisible(false);
    		break;
    	case WORKER:
    		updateMenuButton.setVisible(false);
    		viewReportsButton.setVisible(false);
    		registerUserButton.setVisible(false);
	        changeHomeBranchButton.setVisible(false);
    		break;
    	case QUALIFIED_WORKER:
    		viewReportsButton.setVisible(false);
    		registerUserButton.setVisible(false);
	        changeHomeBranchButton.setVisible(false);
	        break;
    	case CUSTOMER:
    		viewReportsButton.setVisible(false);
    		registerUserButton.setVisible(false);
    		updateMenuButton.setVisible(false);
    		pendingOrdersButton.setVisible(false);
    		break;
    	case UN_CUSTOMER:
    		viewReportsButton.setVisible(false);
            registerUserButton.setVisible(false);
            updateMenuButton.setVisible(false);
            pendingOrdersButton.setVisible(false);
            createOrderButton.setVisible(false);
            changeHomeBranchButton.setVisible(false);
            myOrdersButton.setVisible(false);
            break;
    	default://if Branch Manager
            changeHomeBranchButton.setVisible(false);
            break;
    	}	
    	changeHelloTextAndHeadline();

    }
    
    /**
     * Initializes the controller. This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        client = Client.getInstance();
        if (client != null) {
            client.getUserHomePageController(this);
            System.out.println("Client and UserHomePageController initialized successfully.");
        } else {
            System.out.println("Failed to initialize Client.");
        }
    }
		
    /**
     * Handles the logout action, And returns to Clien Login Screen.
     *
     * @param event The ActionEvent triggered by clicking the logout button.
     */
	@FXML
	private void handleLogout(ActionEvent event) {
	    // Send logout request to the server
	    client.userLogout(user,false);
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
    		client.addClientInOrder();
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
    	client.addWorkerInPendingOrders(user);
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
                case QUALIFIED_WORKER:
                	userType = "Qualified Worker";
                    break;
                case CUSTOMER:
                	switch(user.getCustomerType()) {
                	case BUSINESS:
                		userType = "Business Customer";
                        break;
                	case PRIVATE: 
                		userType = "Private Customer";
                        break;
                	default:
                    	userType = "Customer";
                        break;
                	}
            }
        } 
        headlineText.setText(user.getUsername() + ", " + userType);
        if (user.getType()==EnumType.UN_CUSTOMER) {
            // For unregistered customer
            welcomeText.setText("Hello, looks like"
                    + " you have not registered yet.\n"
                    + "Please make contact with a "
                    + "manager of your \npreferred branch.");
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
    
    // pop up window handler for an SMS like notification for a customer
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
    
    //pop up window handler for a customer when a qualified worker changes the menu
    public void showCreateOrderDuringUpdateMenuDialog() {
        Platform.runLater(() -> {
        	boolean MenuUpdateToCustomer = true;// Indicator for the helper method which window should open
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Attention!");
            alert.setHeaderText(null);
            alert.setContentText("Looks like there was a change in the menus!\n"
                    + "Please press OK or close the dialog to go back to the home page.\n"
                    + "Sorry for the inconvenience...");
            ButtonType okButton = new ButtonType("OK", ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(okButton);
            alert.showAndWait().ifPresentOrElse(response -> {
                if (response == okButton) {
                    closeCurrentWindowAndOpenNewOne(MenuUpdateToCustomer);
                }
            }, () -> {
                closeCurrentWindowAndOpenNewOne(MenuUpdateToCustomer);
            });
        });
    }
    
  //pop up window handler for a worker viewing pending list when a customer creates a new order
    public void showPendingOrderDuringOrderCreationDialog() {
        Platform.runLater(() -> {
        	boolean MenuUpdateToCustomer = false;// Indicator for the helper method which window should open
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Attention!");
            alert.setHeaderText(null);
            alert.setContentText("Looks like a customer created a new order!\n"
                    + "Please press CONFIRM or close the dialog to reload the page.\n");
            ButtonType okButton = new ButtonType("CONFIRM", ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(okButton);
            alert.showAndWait().ifPresentOrElse(response -> {
                if (response == okButton) {
                    closeCurrentWindowAndOpenNewOne(MenuUpdateToCustomer);
                }
            }, () -> {
                closeCurrentWindowAndOpenNewOne(MenuUpdateToCustomer);
            });
        });
    }

    // Helper method to close the current window and open the new one for the pop up handler 
    private void closeCurrentWindowAndOpenNewOne(boolean popup) {
        try {
            // Close the current window
            Stage currentStage = (Stage) Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);
            if (currentStage != null) {
                currentStage.close();
            }
            if(popup==true)//opens CustomerorderCreation
            {
            	CustomerOrderCreationUI custCreatApp = new CustomerOrderCreationUI(user, null);
                custCreatApp.start(new Stage());
            }
            else//opens WorkerPendingOrders
            {
            	WorkerPendingOrdersUI pendingCreatApp = new WorkerPendingOrdersUI(user);
            	pendingCreatApp.start(new Stage());
            }        	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

           
    /**
     * Closes the application, ensuring proper logout and client shutdown.
     */
	public void closeApplication() {
		if (client != null) {
			client.userLogout(user, true);
		}
	}
}