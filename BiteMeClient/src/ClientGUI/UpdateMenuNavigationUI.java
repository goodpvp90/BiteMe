package ClientGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import userEntities.User;

public class UpdateMenuNavigationUI extends Application {

	private User user;
	
	public UpdateMenuNavigationUI(User user) {
		this.user=user;
	}
	
    @Override
    public void start(Stage primaryStage) throws Exception {
    	FXMLLoader loader =new FXMLLoader(getClass().getResource("UpdateMenuNavigation.fxml"));
        Parent root = loader.load();
        
        primaryStage.setTitle("Customer Order Gather Selection");
        primaryStage.setScene(new Scene(root));
        
        UpdateMenuNavigation controller = loader.getController();
        if(user!=null) {controller.setUser(user); }
        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // Prevent the window from closing immediately
            controller.closeApplication();
        });
        
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}