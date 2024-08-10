package ClientGUI;
import common.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RegisterUserPageUI extends Application {
	private User user;
	
	public RegisterUserPageUI(User user) {
		this.user=user;
	}

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RegisterUserPage.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Register User");
        RegisterUserPageController controller = loader.getController();
        if(user!=null) controller.setLoggedInUser(user);
        
        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // Prevent the window from closing immediately
            controller.closeApplication();
        });       
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}