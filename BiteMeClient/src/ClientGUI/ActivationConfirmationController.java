package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.scene.control.Button;

import common.EnumType;
import common.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class ActivationConfirmationController {

	@FXML
	private Text headlineText;

	@FXML
	private Text infoText;

	@FXML
	private Button returnButton;

	public void initialize() {
	    headlineText.setText("Customer Activation Confirmation");
	}

    private User loggedInUser;
    private boolean isRegistered;

    public void setUserInfo(User user) {
        String info = String.format(
            "Customer activated successfully!\n\n" +
            "ID: %s\n" +
            "Name: %s %s\n" +
            "Email: %s\n" +
            "Phone: %s\n" +
            "Status: %s",
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getPhone(),
            user.getCustomerType() == EnumType.PRIVATE ? "Private" : "Regular"
        );
        infoText.setText(info);
    }

    public void setLoggedInUser(User user, boolean isRegistered) {
        this.loggedInUser = user;
        this.isRegistered = isRegistered;
    }

    @FXML
    private void returnToHomePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserHomePage.fxml"));
            Parent root = loader.load();
            UserHomePageController controller = loader.getController();
            controller.setUser(loggedInUser, isRegistered);

            Stage currentStage = (Stage) infoText.getScene().getWindow();
            Scene scene = new Scene(root, 700, 600);
            currentStage.setScene(scene);
            currentStage.setTitle("User Home Page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}