package ClientGUI;

import java.util.HashMap;
import common.Dish;//OFEK-------------------------
//import ClientGUI.CustomerOrderCreation.Dish;
import common.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CustomerOrderCreationUI extends Application {
	
	private User user;
	private HashMap<Dish, Integer> selectedDishes;
	
	//CONSTRUCTOR	
	public CustomerOrderCreationUI(User user,HashMap<Dish, Integer> selectedDishes) {
		this.user = user;
		this.selectedDishes=selectedDishes;
	}
	
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader
        		(getClass().getResource("CustomerOrderCreation.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Customer Order Creation");
        
        /////set user transferred from home page////////
        CustomerOrderCreation controller = loader.getController();
        if (user != null) {
            controller.setUser(user);
        }
        if (selectedDishes != null) {
            controller.setDishesCount(selectedDishes);
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