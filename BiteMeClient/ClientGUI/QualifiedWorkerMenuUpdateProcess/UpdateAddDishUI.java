package QualifiedWorkerMenuUpdateProcess;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import userEntities.User;

/**
 * The main entry point for the UpdateAddDish UI application.
 * This class sets up the UpdateAddDish screen where users can add or update dish information.
 */
public class UpdateAddDishUI extends Application {
    
    /**
     * The user associated with this UI instance.
     */
    private User user;

    /**
     * Constructor to initialize UpdateAddDishUI with a specific user.
     * 
     * @param user The user associated with this UI instance.
     */
    public UpdateAddDishUI(User user) {
        this.user = user;
    }

    /**
     * Starts the UpdateAddDish UI application.
     * Loads the FXML file for the UpdateAddDish screen, sets up the stage,
     * and initializes the controller with the provided user.
     * 
     * @param primaryStage The primary stage for this application.
     * @throws Exception If an error occurs during FXML loading or stage setup.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateAddDish.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Add Dish");
        primaryStage.setScene(new Scene(root));

        UpdateAddDish controller = loader.getController();
        if (user != null) {
            controller.setUser(user);
        }

        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // Prevent the window from closing immediately
            controller.closeApplication();
        });

        primaryStage.show();
    }

    /**
     * The main method to launch the UpdateAddDish UI application.
     * 
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        launch(args);
    }
}
