package CustomerMyOrdersProcess;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import userEntities.User;

/**
* The MyOrdersUI class represents the user interface for managing and viewing orders.
* It initializes the UI for the MyOrders screen, sets up the controller, and handles
* the window close event.
*/
public class MyOrdersUI extends Application {
	
	/**
     * The user currently logged into the system.
     */
    private User user;

    /**
     * Constructs a MyOrdersUI instance with the specified user.
     *
     * @param user The user to be associated with this UI.
     */
	public MyOrdersUI(User user) {
		this.user = user;
	}

	 /**
     * Initializes and displays the MyOrders user interface.
     *
     * @param primaryStage The primary stage for this application.
     * @throws Exception If there is an error loading the FXML file or setting up the stage.
     */
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("MyOrders.fxml"));
		Parent root = loader.load();
		primaryStage.setTitle("My Orders");
		MyOrders controller = loader.getController();
		if (user != null) {
			controller.setUser(user);
		}
		// Handler for "X" button in the top screen
		primaryStage.setOnCloseRequest(event -> {
			event.consume(); // Prevent the window from closing immediately
			controller.closeApplication();
		});
		primaryStage.setScene(new Scene(root, 800, 600));
		primaryStage.show();
	}

	/**
     * Launches the application.
     *
     * @param args Command-line arguments passed to the application.
     */
	public static void main(String[] args) {
		launch(args);
	}

}

