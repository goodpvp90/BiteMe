package ClientGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane; // Changed from GridPane
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The ClientLoginUI class is responsible for launching and displaying the Client Login window
 * in the BiteMe application. This window appears after successful connection to the server.
 */
public class ClientLoginUI extends Application {

	/**
     * The main entry point for the Client Login UI.
     * This method is called by the JavaFX runtime to initialize and display the Client Login window.
     *
     * @param primaryStage The primary stage for this application, onto which
     * the application scene can be set.
     * @throws Exception If there's an error during the initialization or loading of the FXML file.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene loginScene = loadLoginScene();
        primaryStage.setTitle("Client Login");
        primaryStage.setScene(loginScene);
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            ClientLoginController controller = (ClientLoginController) loginScene.getUserData();
            controller.closeApplication();
        });        
        primaryStage.show();
    }

    /**
     * Loads the login scene.
     * This method can be called from other parts of the application to return to the login screen.
     *
     * @return A Scene object representing the login screen.
     * @throws IOException If there's an error loading the FXML file.
     */
    public static Scene loadLoginScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(ClientLoginUI.class.getResource("ClientLogin.fxml"));
        AnchorPane root = loader.load(); 
        Scene scene = new Scene(root); 
        ClientLoginController loginController = loader.getController();
        loginController.resetClient();
        scene.setUserData(loginController);        
        return scene;
    }

    /**
     * The main method is the entry point of the Java application.
     * It launches the JavaFX application.
     *
     * @param args Command line arguments passed to the application.
     * These are not used in this application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}