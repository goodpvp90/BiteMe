package ClientGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientLoginUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene loginScene = loadLoginScene();
        primaryStage.setTitle("Client Login");
        primaryStage.setScene(loginScene);

        // Made the "X" button to Close the thread and send msg to client.
        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // Prevent the window from closing immediately
            ClientLoginController controller = (ClientLoginController) loginScene.getUserData();
            controller.closeApplication();
        });
        
        primaryStage.show();
    }

    public static Scene loadLoginScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(ClientLoginUI.class.getResource("ClientLogin.fxml"));
        GridPane root = loader.load();
        Scene scene = new Scene(root);
        
        // Get the controller and reset the client
        ClientLoginController loginController = loader.getController();
        loginController.resetClient();
        
        // Store the controller in the scene's user data for easy access
        scene.setUserData(loginController);
        
        return scene;
    }

    public static void main(String[] args) {
        launch(args);
    }
}