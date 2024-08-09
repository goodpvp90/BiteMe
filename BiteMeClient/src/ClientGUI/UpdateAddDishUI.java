package ClientGUI;

import common.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UpdateAddDishUI extends Application {
	private User user;
	
	public UpdateAddDishUI(User user) {
		this.user=user;
	}

    @Override
    public void start(Stage primaryStage) throws Exception {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateAddDish.fxml"));
        Parent root = loader.load();        
        primaryStage.setTitle("Add Dish");
        primaryStage.setScene(new Scene(root, 450, 400));
        
        UpdateAddDish controller = loader.getController();
        if (user != null) {
            controller.setUser(user);
        }
        
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
