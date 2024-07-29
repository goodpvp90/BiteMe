package ClientGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ReportsPageUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ReportsPage.fxml"));
        Parent root = loader.load();
        
        // Get the controller from the loader
        ReportsPageController controller = loader.getController();
        //Handler for "X" button in the top screen
        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // Prevent the window from closing immediately
			controller.closeApplication();
        });
        
        primaryStage.setTitle("Reports Page");
        primaryStage.setScene(new Scene(root, 700, 600));
        primaryStage.show();
    }
    


    public static void main(String[] args) {
        launch(args);
    }
}