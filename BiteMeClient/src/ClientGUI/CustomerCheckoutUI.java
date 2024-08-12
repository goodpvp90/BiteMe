package ClientGUI;

import java.util.List;

import common.Dish;
import common.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CustomerCheckoutUI extends Application {
	private User user = null;
    private List<Dish> orders = null;
    private boolean[] param = null;
    private String[] contactInfo=null;
    private Object date = null;


    public CustomerCheckoutUI(User user, List<Dish> orders,boolean[] param, String[] contactInfo,Object date) {
    	this.user = user;
    	this.orders = orders;
    	this.param = param;
    	this.contactInfo = contactInfo;
    	this.date = date;
	}
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerCheckout.fxml")); // Adjust the path as needed
        Parent root = loader.load();
        primaryStage.setTitle("Checkout");
        primaryStage.setScene(new Scene(root));

        CustomerCheckout controller = loader.getController();
        if(orders!= null) controller.setChosenItemsFromMenu(orders);
        if(param!=null) controller.setReturnBooleanPrefGather(param);
        if(contactInfo!=null) controller.setContacts(contactInfo);
        if(user!=null) controller.setUser(user);
        if(date!=null) controller.setDate(date);

        
      //Handler for "X" button in the top screen
        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // Prevent the window from closing immediately
            controller.closeApplication();
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}