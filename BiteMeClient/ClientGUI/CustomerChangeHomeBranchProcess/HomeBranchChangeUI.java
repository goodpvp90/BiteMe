package CustomerChangeHomeBranchProcess;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import userEntities.User;

/**
 * The HomeBranchChangeUI class is a JavaFX application that provides a user interface
 * for changing the home branch of a user. It loads the FXML file for the branch change
 * screen and sets up the user interface for this functionality.
 */
public class HomeBranchChangeUI extends Application {

	/**
     * The user whose home branch is to be changed.
     */
    private User user;

    /**
     * Constructs a HomeBranchChangeUI instance with the specified user.
     *
     * @param user the user whose home branch is to be changed
     */
    public HomeBranchChangeUI(User user) {
        this.user = user;
    }

    /**
     * Initializes and starts the JavaFX application. Loads the FXML file, sets the scene,
     * and shows the primary stage.
     *
     * @param primaryStage the primary stage for this application
     * @throws Exception if an error occurs during loading of the FXML file
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("HomeBranchChange.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Home Branch Change");
        
        HomeBranchChange controller = loader.getController();
        if (user != null) controller.setUser(user);
        
        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // Prevent the window from closing immediately
            controller.closeApplication();
        });
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
