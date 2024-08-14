package QualifiedWorkerMenuUpdateProcess;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import userEntities.User;

/**
 * The UpdateDeleteMenuUI class is responsible for launching the Update/Delete Menu user interface.
 * It loads the corresponding FXML file, sets up the scene, and handles the primary stage.
 */
public class UpdateDeleteMenuUI extends Application {
	/**
     * The user currently logged into the system.
     */
    private User user;

    /**
     * Constructs a new UpdateDeleteMenuUI with the specified user.
     * 
     * @param user The user currently logged in.
     */
    public UpdateDeleteMenuUI(User user) {
        this.user = user;
    }

    /**
     * Starts the JavaFX application by setting up the primary stage, loading the FXML layout,
     * and displaying the scene.
     * 
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateDeleteMenu.fxml"));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);
            UpdateDeleteMenu controller = loader.getController();
            if (user != null) {
                controller.setUser(user);
            }            
            primaryStage.setScene(scene);
            primaryStage.setTitle("Update Delete Menu");
            // Handler for the "X" button in the top right corner of the screen
            primaryStage.setOnCloseRequest(event -> {
                event.consume(); // Prevent the window from closing immediately
                controller.closeApplication();
            });
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The main method which launches the JavaFX application.
     * 
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}