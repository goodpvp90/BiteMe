package ClientGUI;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import common.Dish;//OFEK-------------------------
//import ClientGUI.CustomerOrderCreation.Dish;
import common.User;

public class CustomerOrderGatherSelection {
	private User user = null;
	private HashMap<Dish, Integer> selectedDishes = null;
	
	@FXML
	private Button backButton;

	 //Set the user instance from the UI 
    public void setUser(User user) {
        this.user = user;
    }
    
    //Set the dishes hash map
    public void setDishesCount(HashMap<Dish, Integer> selectedDishesCount) {
        this.selectedDishes = selectedDishesCount;
    }
	
    @FXML
    private void handleBackButtonAction(ActionEvent event) throws IOException {
    	
    	CustomerOrderCreationUI COrderCreateApp = 
				new CustomerOrderCreationUI(user,selectedDishes);
    	try {
			COrderCreateApp.start(new Stage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		//close current window
		((Stage) backButton.getScene().getWindow()).close();
    	
    	//OLD LOGIC
//        Parent root = FXMLLoader.load(getClass().getResource("CustomerOrderCreation.fxml"));
//        Scene scene = new Scene(root);
//        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//        stage.setScene(scene);
//        stage.show();
    }

    @FXML
    private void handleCheckoutButtonAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("CustomerCheckout.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    // Add methods for handling other actions in this window
}