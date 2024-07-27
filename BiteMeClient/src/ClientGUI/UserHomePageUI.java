package ClientGUI;

import common.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UserHomePageUI extends Application {
	private User user; 
	
	//Receives User information to set the right screen buttons for user 
	//by his permissions
	public UserHomePageUI (User user) {
		this.user = user;
	}
	
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("UserHomePage.fxml"));
        Parent root = loader.load();
        
        UserHomePageController controller = loader.getController();
        controller.setUser(user);
        
        
        primaryStage.setTitle("User Home Page");
        primaryStage.setScene(new Scene(root, 700, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
