package ClientGUI;
import java.util.List;
import common.Dish;
import common.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CustomerOrderGatherSelectionUI extends Application {
	private User user = null;
    private List<Dish> orders = null;
	
    //added constructor
    public CustomerOrderGatherSelectionUI(User user, List<Dish> orders) {
    	this.user = user;
    	this.orders = orders;
	}
    
  //added constructor
    public CustomerOrderGatherSelectionUI() {
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
	public static void main(String[] args) {
        launch(args);
    }
}