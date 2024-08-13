package server;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return;

    }
  }

