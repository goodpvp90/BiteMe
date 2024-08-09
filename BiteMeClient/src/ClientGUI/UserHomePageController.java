package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.io.IOException;

import client.Client;
import common.EnumType;
import common.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        } 
    	else {switch(user.getType()) {
    	//CEO AND BM Same buttons but other functionalities and roles.
    	case CEO:
    		break;
    	case BRANCH_MANAGER:
    		//viewReportsButton.setOnAction(this::handleViewReportsForBranch);
    		break;
    	case WORKER:
    		viewReportsButton.setVisible(false);
    		registerUserButton.setVisible(false);
    		break;
    	case CUSTOMER:
    		viewReportsButton.setVisible(false);
    		registerUserButton.setVisible(false);
    		updateMenuButton.setVisible(false);
    		pendingOrdersButton.setVisible(false);
    		break;
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
     * Handles the logout action.
     *
     * @param event The ActionEvent triggered by clicking the logout button.
     */
	@FXML
	private void handleLogout(ActionEvent event) {
	    // Send logout request to the server
	    client.userLogout(user);

	    // Navigate back to the ClientLoginUI
	    try {
	        // Get the current stage
	        Stage stage = (Stage) logoutButton.getScene().getWindow();
	        // Load the login scene
	        Scene loginScene = ClientLoginUI.loadLoginScene();
	        stage.setScene(loginScene);
	        stage.setTitle("Client Login");
	        stage.show();
	    } catch (IOException e) {
	        e.printStackTrace();
	        // Handle the exception (show an error message to the user)
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
            currentStage.close();
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
            currentStage.close();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ReportsPage.fxml"));
            Parent root = loader.load();
            ReportsPageController reportsController = loader.getController();
            //Sending the data for next page so if i would want to go back then i wont lose DATA
            reportsController.setUser(this.user, this.isRegistered);
            reportsController.setUserType(user.getType());
            Stage stage = (Stage) viewReportsButton.getScene().getWindow();
            stage.setScene(new Scene(root, 700, 600));
            stage.setTitle("Reports Page");
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
        System.out.println("Change Home Branch button clicked");
        // Implement navigation to Change Home Branch page or functionality
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
            currentStage.close();
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
                    break;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RegisterUserPage.fxml"));
            Parent root = loader.load();
            RegisterUserPageController registerController = loader.getController();            
            // Store the current scene before switching
            Scene currentScene = registerUserButton.getScene();
            registerController.setPreviousScene(currentScene);            
            // Pass the logged-in user information
            registerController.setLoggedInUser(this.user);            
            Stage stage = (Stage) registerUserButton.getScene().getWindow();
            Scene newScene = new Scene(root);
            stage.setScene(newScene);
            stage.setTitle("Register User");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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