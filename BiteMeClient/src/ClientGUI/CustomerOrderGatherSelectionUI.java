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

public class CustomerOrderGatherSelectionUI extends Application {
	private User user = null;
    private HashMap<Dish, Integer> orders = null;
	
    //added constructor
    public CustomerOrderGatherSelectionUI(User user, HashMap<Dish, Integer> selectedDishesCount) {
    	this.user=user;
    	orders=selectedDishesCount;
	}

	@Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerOrderGatherSelection.fxml")); // Adjust the path as needed
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Order Gathering Method Selection");
            
            CustomerOrderGatherSelection controller = loader.getController();
            if(user!=null) controller.setUser(user);
            if(orders!= null) controller.setDishesCount(orders);
            
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}