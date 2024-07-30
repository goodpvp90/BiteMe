package ClientGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CustomerOrderGatherSelectionUI extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerOrderGatherSelection.fxml")); // Adjust the path as needed
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Order Gathering Method Selection");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
