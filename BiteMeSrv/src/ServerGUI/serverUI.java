package ServerGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The serverUI class is responsible for launching the server-side graphical user interface (GUI).
 * This class extends the Application class and is the entry point for the server application.
 */
public class serverUI extends Application {
	
	/**
	 * Initializes the primary stage for the server UI, loading the FXML file, setting the scene,
     * applying the style sheet, and configuring the close operation.
     *
     * @param primaryStage The primary stage for this application.
     * @throws Exception if the FXML file cannot be loaded.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("server.fxml"));
        Parent root = loader.load();
        serverController controller = loader.getController();
        Scene scene = new Scene(root,800,600);
        scene.getStylesheets().add(getClass().getResource("server.css").toExternalForm());
        primaryStage.setTitle("Server");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // Prevent the window from closing immediately
            controller.closeApplication();
        });
        primaryStage.show();
    }

    /**
     * The main method that launches the JavaFX application.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}