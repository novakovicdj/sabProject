/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author novak
 */
public class DB {
    private static final String username = "sa";
    private static final String password = "123";
    private static final String database = "Projekat";
    private static final int port = 1433;
    private static final String server = "localhost";
    
    private static final String connectionString = "jdbc:sqlserver://" +
            server + ":" + port + ";databaseName=" + database + ";encrypt=true"
            + ";trustServerCertificate=true";
    
    
    private static Connection conn;
    
    private DB() {
        try {
            conn = DriverManager.getConnection(connectionString, username, password);
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Connection getConnection() {
        return conn;
    }
    
    private static DB db= null;
    
    public static final DB getInstance() {
        if(db == null)
            db = new DB();
        
        return db;
    }
    
}
