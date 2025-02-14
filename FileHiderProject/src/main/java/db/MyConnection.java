package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    public static Connection connection = null;
    public static Connection getConnection() {
        if (connection == null) {  // Ensure connection is only created if not already established
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/FileHiderProject?useSSL=false", "root", "shrihari245");
                System.out.println("Connection Done");
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace(); // Change this to proper logging
                throw new RuntimeException("Database connection failed!");
            }
        }
        return connection;
    }


    public static void closeConnection(){
        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
