/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kursov.shashki;
import java.sql.*;
/**
 *
 * @author Александра
 */
public class DataBase {
    private static final String DB_URL = "jdbc:sqlite:db/shashki.db";
    
    public static Connection getConnection(){
        Connection connection = null;
        try{
        connection = DriverManager.getConnection(DB_URL);
        System.out.println("Connected to database");
        }catch(SQLException e){
            System.out.println("Database connection error" + e.getMessage());
        }
        return connection;
    }
    
    public static boolean addUser(String login, String password){
        String str = "INSERT INTO Users(Name, Password) VALUES (?, ?)";
        try(Connection connection = getConnection();){
            PreparedStatement statement = connection.prepareStatement(str);
            statement.setString(1, login);
            statement.setString(2, password);
            statement.executeUpdate();
            return true;
        }catch(SQLException e){
            System.out.println("Error adding user" + e.getMessage());
        }
        return false;
    }
    
    public static boolean checkBeforeAdd(String login){
        String str = "SELECT Name FROM Users WHERE Name=?";
        String _login = null;
        try(Connection connection = getConnection();){
            PreparedStatement statement = connection.prepareStatement(str);
            statement.setString(1, login);
            ResultSet result = statement.executeQuery();
            while(result.next()){
                _login = result.getString(1);
            }
        }catch(SQLException e){
            System.out.println("Error get user" + e.getMessage());
        }
        return _login==null;
    }
    public static boolean checkUser(String login, String password){
        String str = "SELECT Name, Password FROM Users WHERE Name=? AND Password=?";
        String _login = null;
        String _password = null;
        try(Connection connection = getConnection();){
            PreparedStatement statement = connection.prepareStatement(str);
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet result = statement.executeQuery();
            while(result.next()){
                _login = result.getString(1);
                _password = result.getString(2);
            }
        }catch(SQLException e){
            System.out.println("Error get user" + e.getMessage());
        }
        return _login!=null && _password!=null;
    }
    
}
