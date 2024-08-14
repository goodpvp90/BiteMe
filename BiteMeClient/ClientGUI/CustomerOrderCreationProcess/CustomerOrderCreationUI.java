
package CustomerOrderCreationProcess;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import restaurantEntities.Dish;
import userEntities.User;

/**
 * The UI class for customer order creation. This class initializes the order 
 * creation window and passes user and dish information to the controller.
 */
public class CustomerOrderCreationUI extends Application {
	
	/**
     * The user associated with this UI.
     */
	private User user;
	/**
     * The list of dishes (orders) selected by the user.
     */
	private List<Dish> selectedDishes;
	
	/**
     * Constructor for CustomerOrderCreationUI.
     * 
     * @param user           The user creating the order.
     * @param selectedDishes The list of dishes selected by the user.
     */
	public CustomerOrderCreationUI(User user,List<Dish> selectedDishes) {
		this.user = user;
		this.selectedDishes=selectedDishes;
	}
	
	 /**
     * Starts the order creation UI by loading the FXML file and setting up the 
     * primary stage.
     * 
     * @param primaryStage The main stage for this UI.
     * @throws Exception If an error occurs during loading of the FXML file.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader
        		(getClass().getResource("CustomerOrderCreation.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Order Creation");
        CustomerOrderCreation controller = loader.getController();
        if (selectedDishes != null) {
            controller.setDishesCount(selectedDishes);
        }
        if (user != null) {
            controller.setUser(user);
        }
        //Handler for "X" button in the top screen
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            controller.closeApplication();
        });
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * The main entry point for launching the application.
     * 
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
