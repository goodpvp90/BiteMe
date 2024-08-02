package ClientGUI;

import common.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UserHomePageUI extends Application {
	private User user;
	private boolean isRegistered; 
	
	//Receives User information to set the right screen buttons for user 
	//by his permissions
	public UserHomePageUI (User user, boolean isRegistered) {
		this.user = user;
		this.isRegistered=isRegistered;
	}
	
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("UserHomePage.fxml"));
        Parent root = loader.load();
        
        UserHomePageController controller = loader.getController();
        //OFEK, this is a comment now because of issues
        //controller.setUser(user, isRegistered);
        controller.setUser(user);
        
        //Handler for "X" button in the top screen
        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // Prevent the window from closing immediately
            controller.closeApplication();
        });
        
        primaryStage.setTitle("User Home Page");
        primaryStage.setScene(new Scene(root, 700, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}