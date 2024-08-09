package ClientGUI;

import client.Client;
import common.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The UserHomePageUI class is responsible for launching and displaying the User Home Page window
 * in the BiteMe application. This window appears after successful login and displays options based on user permissions.
 */
public class UserHomePageUI extends Application {
	private User user;
	private boolean isRegistered; 
	private Client client;
	
	/**
     * Constructor for UserHomePageUI.
     * 
     * @param user The User object representing the logged-in user.
     * @param isRegistered A boolean indicating whether the user is fully registered.
     */
	public UserHomePageUI (User user, boolean isRegistered) {
		this.user = user;
		this.isRegistered=isRegistered;
	}

	/**
     * The main entry point for the User Home Page UI.
     * This method is called by the JavaFX runtime to initialize and display the User Home Page window.
     *
     * @param primaryStage The primary stage for this application, onto which
     *                     the application scene can be set.
     * @throws Exception If there's an error during the initialization or loading of the FXML file.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("UserHomePage.fxml"));
        Parent root = loader.load();
        UserHomePageController controller = loader.getController();
        controller.setUser(user, isRegistered); 
        //Handler for "X" button in the top screen
        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // Prevent the window from closing immediately
            controller.closeApplication();
        });
        primaryStage.setTitle("User Home Page");
        primaryStage.setScene(new Scene(root, 700, 600));
        primaryStage.show();
    }

    /**
     * The main method is the entry point of the Java application.
     * It launches the JavaFX application.
     *
     * @param args Command line arguments passed to the application.
     *             These are not used in this application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}