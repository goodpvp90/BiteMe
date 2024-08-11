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
    private boolean[] param = null;
    private String[] contactInfo=null;
    private Object date=null;
	
    //added constructor
    public CustomerOrderGatherSelectionUI(User user, List<Dish> orders) {
    	this.user = user;
    	this.orders = orders;
	}
    ////////////////////////////////////////////////////////////////////////////////
  //added constructor
    public CustomerOrderGatherSelectionUI(User user, List<Dish> orders,boolean[] param, String[] contactInfo,Object date) {
    	this.user = user;
    	this.orders = orders;
    	this.param = param;
    	this.contactInfo = contactInfo;
    	this.date = date;
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
            if(param!=null) controller.setBooleanParam(param);
            if(contactInfo!=null) controller.setContactInfo(contactInfo);
            if(date!=null) controller.setDateInfo(date);

          //Handler for "X" button in the top screen
            primaryStage.setOnCloseRequest(event -> {
                event.consume(); // Prevent the window from closing immediately
                controller.closeApplication();
            });
            
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	public static void main(String[] args) {
        launch(args);
    }
}