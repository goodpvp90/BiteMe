package ClientGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import userEntities.User;

/**
 * The UpdateMenuNavigationUI class is responsible for launching the Update Menu Navigation
 * user interface. It loads the corresponding FXML layout and sets up the primary stage for
 * the Update Menu Navigation window.
 */
public class UpdateMenuNavigationUI extends Application {

	 /**
     * The user currently logged into the system.
     */
    private User user;
    
    /**
     * Constructor for UpdateMenuNavigationUI.
     * 
     * @param user The user currently logged in.
     */
    public UpdateMenuNavigationUI(User user) {
        this.user = user;
    }
	
    /**
     * Starts the JavaFX application by setting up the primary stage with the Update Menu
     * Navigation layout.
     * 
     * @param primaryStage The primary stage for this application.
     * @throws Exception If an error occurs during loading the FXML file.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
    	FXMLLoader loader =new FXMLLoader(getClass().getResource("UpdateMenuNavigation.fxml"));
        Parent root = loader.load();
        
        primaryStage.setTitle("Update Menu");
        primaryStage.setScene(new Scene(root));
        
        UpdateMenuNavigation controller = loader.getController();
        if(user!=null) {controller.setUser(user); }
        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // Prevent the window from closing immediately
            controller.closeApplication();
        });
        
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