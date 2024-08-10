package ClientGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The ClientConnectUI class is responsible for launching and displaying the Client Connect window
 * in the BiteMe application. This is the initial window where users enter server connection details.
 */
public class ClientConnectUI extends Application {

	/**
     * The main entry point for the Client Connect UI.
     * This method is called by the JavaFX runtime to initialize and display the Client Connect window.
     *
     * @param primaryStage The primary stage for this application, onto which
     * the application scene can be set.
     * @throws IOException If there's an error during the initialization or loading of the FXML file.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientConnect.fxml"));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Client Connect");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * The main method is the entry point of the Java application.
     * It launches the JavaFX application.
     *
     * @param args Command line arguments passed to the application.
     *             These are not used in this application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}