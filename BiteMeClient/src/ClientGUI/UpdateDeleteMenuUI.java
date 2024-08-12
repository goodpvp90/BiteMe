package ClientGUI;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import userEntities.User;

public class UpdateDeleteMenuUI extends Application {
	private User user;
	
	public UpdateDeleteMenuUI(User user) {
		this.user=user;
	}

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateDeleteMenu.fxml"));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);
            
            UpdateDeleteMenu controller = loader.getController();
            if(user!=null) {controller.setUser(user); }            
            
            primaryStage.setScene(scene);
            primaryStage.setTitle("Update Delete Menu");
            //Handler for "X" button in the top screen
            primaryStage.setOnCloseRequest(event -> {
                event.consume(); // Prevent the window from closing immediately
                controller.closeApplication();
            });
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}