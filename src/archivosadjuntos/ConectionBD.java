/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archivosadjuntos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Esau
 */
public class ConectionBD {

    static String driver = "org.postgresql.Driver";
    static String connectString = "jdbc:postgresql://localhost:5432/";
    static String user = "ERPUser";
    static String password;
    static String bbdd;

    public ConectionBD(String base, String pwd) {
        connectString = connectString.concat(base);
        password = pwd;
    }
    
    public Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(connectString, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Ocurrio un error al conectar a la base: " + e.getMessage() 
                    + "--> " + e.getCause()+ "-->" +e.getLocalizedMessage());
        }
        return conn;
    }
    
    public static void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}

