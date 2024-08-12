package ClientGUI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import userEntities.User;

public class MyOrdersUI extends Application {
	private User user;
	
	//CONSTRUCTOR	
		public MyOrdersUI(User user) {
			this.user = user;
		}
		 @Override
		    public void start(Stage primaryStage) throws Exception {
		        FXMLLoader loader = new FXMLLoader
		        		(getClass().getResource("MyOrders.fxml"));
		        Parent root = loader.load();
		        primaryStage.setTitle("My Orders");
		        
		        /////set user transferred from home page////////
		        MyOrders controller = loader.getController();

		        if (user != null) {
		            controller.setUser(user);
		        }
		        
		      //Handler for "X" button in the top screen
		        primaryStage.setOnCloseRequest(event -> {
		            event.consume(); // Prevent the window from closing immediately
		            controller.closeApplication();
		        });
		        primaryStage.setScene(new Scene(root, 800, 600));
		        primaryStage.show();
		    }

		    public static void main(String[] args) {
		        launch(args);
    }

}

