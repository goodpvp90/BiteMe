
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
 * Manages the customer checkout interface.
 * It loads the corresponding FXML file, sets up the scene, and handles the primary stage.
 */
public class CustomerCheckoutUI extends Application {
	/** 
     * The user for checkout information.
     */
    private User user = null;

    /** 
     * The list of dishes the user picked.
     */
    private List<Dish> orders = null;

    /** 
     * The user's preference choices selected in the gather window.
     */
    private boolean[] param = null;

    /** 
     * The contact information filled by the user for delivery and time.
     */
    private String[] contactInfo = null;

    /** 
     * The date of the order.
     */
    private Object date = null;


    /**
     * Creates an instance with user, orders, parameters, contact info, and date.
     *
     * @param user The user for checkout information.
     * @param orders The list of dishes the user picked.
     * @param param of user preferences choices of the user in the gather window.
     * @param contactInfo filled by the user of delivery and time.
     * @param date The date of the order.
     */
    public CustomerCheckoutUI(User user, List<Dish> orders,boolean[] param, String[] contactInfo,Object date) {
    	this.user = user;
    	this.orders = orders;
    	this.param = param;
    	this.contactInfo = contactInfo;
    	this.date = date;
	}
    
    /**
     * Sets up the checkout UI and handles the primary stage.
     *
     * @param primaryStage The primary stage for the checkout scene.
     * @throws Exception If loading the FXML or setting up the stage fails.
     */
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

    /**
     * Launches the JavaFX application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
