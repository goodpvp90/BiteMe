package CustomerChangeHomeBranchProcess;

import UserHomePageProcess.UserHomePageUI;
import client.Client;
import enums.EnumBranch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import userEntities.User;

/**
 * Controller class for handling home branch changes in the user interface.
 */
public class HomeBranchChange {

    /**
     * Button to navigate back to the previous screen or menu.
     */
    @FXML
    private Button backButton;

    /**
     * ComboBox allowing the user to select their home branch.
     */
    @FXML
    private ComboBox<String> homeBranchComboBox;

    /**
     * Text displaying the title of the current screen.
     */
    @FXML
    private Text titleText;

    /**
     * Text displaying error messages related to branch selection.
     */
    @FXML
    private Text errorText;

    /**
     * Text displaying success messages related to branch change.
     */
    @FXML
    private Text successText;

    /**
     * The user whose home branch is being changed.
     */
    private User user;

    /**
     * The client instance used for communication with the server.
     */
    private Client client;

    /**
     * Backup of the user's home branch if the branch change fails.
     */
    private EnumBranch beckupIfDidntSuccessBranch;

    /**
     * Initializes the controller and sets up the ComboBox with branch options.
     * Configures ComboBox to display items with centered text and larger font size.
     */
    @FXML
    public void initialize() {
        client = Client.getInstance();
        client.getInstanceOfHomeBranchChange(this);

        // Initialize ComboBox items here
        homeBranchComboBox.getItems().addAll("North", "Center", "South");
        homeBranchComboBox.setValue("North");

        // Center the text and set the font size in the ComboBox
        homeBranchComboBox.setButtonCell(createCenteredCell());
        homeBranchComboBox.setCellFactory(param -> createCenteredCell());
    }

    /**
     * Creates a ListCell with centered text and a specified font size for the ComboBox.
     *
     * @return a ListCell with centered text and larger font size
     */
    private ListCell<String> createCenteredCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setFont(Font.font(16));
                    setAlignment(Pos.CENTER);
                }
            }
        };
    }

    /**
     * Sets the user and updates the ComboBox with the user's current home branch.
     *
     * @param user the user whose home branch is to be set
     */
    public void setUser(User user) {
        this.user = user;
        homeBranchComboBox.setValue(convertLocEnumToStr(user.getHomeBranch()));
    }

    /**
     * Converts an EnumBranch value to its corresponding string representation.
     *
     * @param enumValue the EnumBranch value
     * @return the string representation of the EnumBranch value
     */
    private String convertLocEnumToStr(EnumBranch enumValue) {
        switch (enumValue) {
            case NORTH:
                return "North";
            case CENTER:
                return "Center";
            case SOUTH:
                return "South";
            default:
                return "No Home Branch";
        }
    }

    /**
     * Converts a string representation of a branch to its corresponding EnumBranch value.
     * Displays an error message if the conversion fails.
     *
     * @param branchName the string representation of the branch
     * @return the EnumBranch value corresponding to the string representation
     */
    private EnumBranch convertStrToEnumBranch(String branchName) {
        switch (branchName) {
            case "North":
                return EnumBranch.NORTH;
            case "Center":
                return EnumBranch.CENTER;
            case "South":
                return EnumBranch.SOUTH;
            default:
                showError("Error Choosing Home Branch");
                return user.getHomeBranch();
        }
    }

    /**
     * Handles the ComboBox action event to update the user's home branch.
     *
     * @param event the action event triggered by ComboBox selection
     */
    @FXML
    void handleComboBoxAction(ActionEvent event) {
        EnumBranch choice = convertStrToEnumBranch(homeBranchComboBox.getValue());

        if (!choice.equals(user.getHomeBranch())) {
            beckupIfDidntSuccessBranch = user.getHomeBranch();
            user.setHomeBranch(choice);
            client.changeHomeBranch(user);
        }
    }

    /**
     * Receives information from the client about whether the home branch change was successful.
     *
     * @param result true if the home branch change was successful, false otherwise
     */
    public void checkSuccessChangeHomeBranch(boolean result) {
        if (result) {
            showSuccess(homeBranchComboBox.getValue());
        } else {
            // If the change didn't succeed, revert to the previous home branch
            user.setHomeBranch(beckupIfDidntSuccessBranch);
        }
    }

    /**
     * Handles the action event for the back button to navigate to the user home page.
     *
     * @param event the action event triggered by the back button
     */
    @FXML
    void handleBackButtonAction(ActionEvent event) {
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
     * Displays an error message in the errorText Text element.
     *
     * @param errText the error message to display
     */
    private void showError(String errText) {
        successText.setVisible(false);
        errorText.setText(errText);
        errorText.setVisible(true);
    }

    /**
     * Displays a success message in the successText Text element.
     *
     * @param choice the new home branch that was successfully set
     */
    private void showSuccess(String choice) {
        errorText.setVisible(false);
        successText.setText("Changed home branch to " + choice + " successfully!");
        successText.setVisible(true);
    }

    /**
     * Closes the application and logs the user out, sending a logout message to the server.
     */
    public void closeApplication() {
        if (client != null) {
            client.userLogout(user, true);
        }
    }
}
