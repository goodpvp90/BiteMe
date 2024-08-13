package ClientGUI;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import restaurantEntities.Dish;
import userEntities.User;


/**
 * The CustomerOrderGatherSelectionUI class represents the user interface for gathering and selecting orders in the customer order process.
 * It extends Application to set up and display the JavaFX application window.
 */
public class CustomerOrderGatherSelectionUI extends Application {
	
	/** The user associated with this UI. */
    private User user = null;    
    
    /** The list of dishes associate with user order to be transfer to checkout. */
    private List<Dish> orders = null;
    
    
    /** An array of boolean parameters of user preferences selection if returned from checkout. */
    private boolean[] param = null;
    
    
    /** An array of contact information strings if filled by the user. */
    private String[] contactInfo = null;    
    
    
    /** An object representing the date information. */
    private Object date = null;
	
    
    /**
     * Constructs a CustomerOrderGatherSelectionUI instance with the specified user and list of orders.
     * called by order creation UI
     * 
     * @param user The user associated with this UI.
     * @param orders The list of dishes (orders) to be displayed.
     */
    public CustomerOrderGatherSelectionUI(User user, List<Dish> orders) {
    	this.user = user;
    	this.orders = orders;
	}
    
    /**
     * Constructor initializing the UI with user, orders, additional parameters, contact information, and date.
     * called by Checkout UI
     * 
     * @param user The user associated with this UI.
     * @param orders list of dishes associate with user order to be transfer to checkout.
     * @param param An array of boolean parameters of user preferences selection if returned from checkout.
     * @param contactInfo An array of contact information strings if filled by the user.
     * @param date An object representing the date information.
     */
    public CustomerOrderGatherSelectionUI(User user, List<Dish> orders,boolean[] param, String[] contactInfo,Object date) {
    	this.user = user;
    	this.orders = orders;
    	this.param = param;
    	this.contactInfo = contactInfo;
    	this.date = date;
	}

    /**
     * Starts the UI application by loading the FXML file and setting up the stage.
     * 
     * @param primaryStage The primary stage for this application.
     */
	@Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerOrderGatherSelection.fxml")); // Adjust the path as needed
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Order Gathering Selection");
            
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
	
	 /**
     * Launches the UI application.
     * 
     * @param args Command line arguments.
     */
	public static void main(String[] args) {
        launch(args);
    }
}