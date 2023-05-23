package Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class App {

public Connection getConnection() {
		
		Connection conn = null;
		try {
			String url = "jdbc:sqlite:C:\\Users\\Asus\\Downloads\\Third Year\\Second Term\\Software Tools 2\\AlAkeelDB.db";
			conn = DriverManager.getConnection(url);
			System.out.println("Connection to DB has been established ");	
		}catch(SQLException e) {
			System.out.println("message "+e.getMessage());
		}
		return conn;
		
	} 
	
	
	public static void main(String[] args) {
		
		App app = new App();
        try (Connection connection = app.getConnection()) {
            // Use the connection for database operations
            System.out.println("Connection successful");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
		
		
	}
}
