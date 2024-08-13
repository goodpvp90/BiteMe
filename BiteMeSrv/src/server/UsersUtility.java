package server;
import java.sql.SQLException;

public class UsersUtility {
	private DBController dbController;	
	
    public UsersUtility(DBController dbController) {
		this.dbController = dbController;
	}
   
    public void importUsers() {
    	try {
			dbController.importUsers();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return;
    }
  }

