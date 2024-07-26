package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UserHomePageController {

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
    private void initialize() {
        // Initialize any necessary components or data
        // For example, you might want to set the welcome message:
        // welcomeText.setText("Hello '" + currentUser.getName() + "', Choose an Option");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        // Implement logout logic
        System.out.println("Logout button clicked");
        // Navigate to login page or close the application
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

    @FXML
    private void handleRegisterUser(ActionEvent event) {
        System.out.println("Register User button clicked");
        // Implement navigation to Register User page
    }
}