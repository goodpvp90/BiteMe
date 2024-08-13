package ClientGUI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import userEntities.User;

/**
 * The RegisterUserPageUI class is responsible for creating and displaying the UI
 * for the User Registration Page in the BiteMe application.
 * It extends the JavaFX Application class to create a standalone window for user registration.
 */
public class RegisterUserPageUI extends Application {

	/**
	 * The User object representing the currently logged-in user.
	 */
	private User user;
	
    /**
     * Constructor for RegisterUserPageUI.
     * 
     * @param user The User object representing the currently logged-in user.
     */
	public RegisterUserPageUI(User user) {
		this.user=user;
	}

    /**
     * Starts the RegisterUserPageUI application.
     * This method is called automatically when the application is launched.
     * It loads the FXML file, sets up the controller, and displays the window.
     * 
     * @param primaryStage The primary stage for this application.
     * @throws Exception If there's an error during the start process.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RegisterUserPage.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Register User");
        RegisterUserPageController controller = loader.getController();
        if(user!=null) controller.setLoggedInUser(user);
        
        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // Prevent the window from closing immediately
            controller.closeApplication();
        });       
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * The main entry point for the application.
     * This method is called when the application is run directly from this class.
     * 
     * @param args Command line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}