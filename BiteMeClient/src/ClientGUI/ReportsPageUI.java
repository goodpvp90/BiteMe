package ClientGUI;

import common.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * The ReportsPageUI class is responsible for launching and displaying the Reports Page
 * of the BiteMe application. It loads the FXML file, sets up the primary stage,
 * and establishes the connection between the UI and its controller.
 */
public class ReportsPageUI extends Application {

	private User user;
    
	
	public ReportsPageUI(User user) {
		this.user=user;
	}
	
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ReportsPage.fxml"));
        Parent root = loader.load();
        ReportsPageController controller = loader.getController();
        //Handler for "X" button in the top screen
        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // Prevent the window from closing immediately
			controller.closeApplication();
        });   
        
        controller.setUser(user, true);
        controller.setUserType(user.getType());
        primaryStage.setTitle("Reports Page");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    
    /**
     * The main method is the entry point of the Java application.
     * It launches the JavaFX application.
     *
     * @param args Command line arguments passed to the application.
     * 
     */
    public static void main(String[] args) {
        launch(args);
    }
}