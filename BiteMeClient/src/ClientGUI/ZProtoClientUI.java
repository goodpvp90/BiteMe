package ClientGUI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;





//Changed to ZProto so itll be last in the list because we have it as reference, Maybe later we will delete.

public class ZProtoClientUI extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client.fxml"));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("Client GUI");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error during application startup: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
