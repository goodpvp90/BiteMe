package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import client.Client;
import common.EnumType;
import common.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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

    
    public void setUser(User user) {
        this.user = user;
        updateUI();
    }
    
    
    private void updateUI() {
    	switch(user.getType()) {
    	case BRANCH_MANAGER:
    		viewReportsButton.setOnAction(this::handleViewReportsForBranch);
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
      	//need to add one more case
    	//if user is not registered customer he need approval of manager
    	//all not buttons not visible

    	}
    	//CEO is default screen
    	
    	
    	changeHelloTextAndHeadline();
	}


	@FXML
    private void initialize() {
        // Initialize the client
        client = Client.getInstance();
        // Initialize any necessary components or data
        // For example, you might want to set the welcome message:
        // welcomeText.setText("Hello '" + currentUser.getName() + "', Choose an Option");
    }
	@FXML
    private void handleLogout(ActionEvent event) {
        // Implement logout logic
        System.out.println("Logout button clicked");
        
        // Send logout request to the server
        client.userLogout(user);
        
        // Navigate back to the login page
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientLogin.fxml"));
            Parent root = loader.load();
            
            ClientLoginController loginController = loader.getController();
            // Reset the user in the login controller
            //loginController.updateUser(new Object[]{null});
            
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Client Login");
            
            // Add close request handler
            stage.setOnCloseRequest(e -> {
                e.consume();
                loginController.closeApplication();
            });
            
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading login page: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreateOrder(ActionEvent event) {
        System.out.println("Create Order button clicked");
        // Implement navigation to Create Order page
    }

    @FXML
    private void handleUpdateMenu(ActionEvent event) {
        System.out.println("Update Menu button clicked");
        // Implement navigation to Update Menu page
    }

    @FXML
    private void handleViewReports(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ReportsPage.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) viewReportsButton.getScene().getWindow();
            stage.setScene(new Scene(root, 700, 600));
            stage.setTitle("Reports Page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Different functionality for branch manager when pressed view
    //change to the right one
    @FXML
    private void handleViewReportsForBranch(ActionEvent event) {
    	
    }
    
    @FXML
    private void handleChangeHomeBranch(ActionEvent event) {
        System.out.println("Change Home Branch button clicked");
        // Implement navigation to Change Home Branch page or functionality
    }

    @FXML
    private void handlePendingOrders(ActionEvent event) {
        System.out.println("Pending Orders button clicked");
        // Implement navigation to Pending Orders page
    }

    private void changeHelloTextAndHeadline() {
    	String userType = ""; 
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
    	//add Unregistered customer case
    	}
    	headlineText.setText(user.getUsername()+ ", "+userType);
    	if(!userType.equals("Unregistered Customer"))
    		welcomeText.setText("Hello " + user.getFirstName()+ ", what would you like to do?");
    	else
    		welcomeText.setText("Hello " + user.getFirstName()+", looks like\n"
    				+ " you have not registered yet.\n"
    				+ "Please make contact with a\n "
    				+ "manager of your prefered brunch.");
    }
    
    
    @FXML
    private void handleRegisterUser(ActionEvent event) {
        System.out.println("Register User button clicked");
        // Implement navigation to Register User page
    }
    
	//Making Quit Button to kill thread and send message to server
    public void closeApplication() {
        if (client != null) {
            System.out.println("Closing application from UserHomePage");
            client.quit();
        }
        Platform.exit();
        System.exit(0);
    }
    
}