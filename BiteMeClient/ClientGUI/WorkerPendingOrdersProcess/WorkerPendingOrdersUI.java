package WorkerPendingOrdersProcess;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import userEntities.User;
/**
 * The WorkerPendingOrdersUI class is responsible for launching the Pending Orders user interface. 
 * It loads the corresponding FXML layout and sets up the primary stage for
 * the Update Menu Navigation window.
 */
public class WorkerPendingOrdersUI extends Application {
	/**
     * The user currently logged into the system.
     */
	private User user;
	/**
     * Constructor for UpdateMenuNavigationUI.
     * 
     * @param user The user currently logged in.
     */
	public WorkerPendingOrdersUI(User user) {
		this.user = user;
	}
	/**
     * Starts the JavaFX application by setting up the primary stage with the Pending Orders
     * Navigation layout.
     * 
     * @param primaryStage The primary stage for this application.
     * @throws Exception If an error occurs during loading the FXML file.
     */
	@Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader
        		(getClass().getResource("WorkerPendingOrders.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Pending Branch Orders");
        WorkerPendingOrders controller = loader.getController();
        if (user != null) {
            controller.setUser(user);
        }
        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // Prevent the window from closing immediately
            controller.closeApplication();
        });
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }
	/**
     * The main method that launches the JavaFX application.
     * 
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}

