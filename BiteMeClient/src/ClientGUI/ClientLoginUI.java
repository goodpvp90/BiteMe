package ClientGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ClientLoginUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientLogin.fxml"));
        GridPane root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Client Login");
        primaryStage.setScene(scene);
        // Add this close request handler
        // Made the "X" button to Close the thread and send msg to client.
        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // Prevent the window from closing immediately
            ClientLoginController controller = loader.getController();
            controller.closeApplication();
        });
        primaryStage.show();//
    }

    public static void main(String[] args) {
        launch(args);
    }
}
