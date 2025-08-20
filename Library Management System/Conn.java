package library.management.system;

import java.sql.*;

public class Conn {

    public Connection c;
    public Statement s;
    
    public Conn() {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            c=DriverManager.getConnection("jdbc:mysql:///librarydb","root","abhinav706077");
            s=c.createStatement();
        }catch(ClassNotFoundException c){
            System.out.println("MySQL JDBC Driver not found.");
            c.printStackTrace();
        }catch(SQLException e){
            System.out.println("Connection failed..."+e.getMessage());
            e.printStackTrace();
        }
    }
}