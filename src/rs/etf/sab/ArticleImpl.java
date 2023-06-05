/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.ArticleOperations;

/**
 *
 * @author novak
 */
public class ArticleImpl implements ArticleOperations{

    @Override
    public int createArticle(int i, String string, int i1) {
        Connection conn = DB.getInstance().getConnection();
        String query = "insert into Article(Name, Price, IdS, Count) values (?, ?, ?, 0)";
        try (PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);) {
            ps.setString(1, string);
            ps.setInt(2, i1);
            ps.setInt(3, i);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys();) {
                if(rs.next() && rs.getInt(1) != 0) {
                    //System.out.println("Uspesno ste dodali novi artikal");
                    return rs.getInt(1);
                }
            }  catch (SQLException ex) {
                Logger.getLogger(ArticleImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ArticleImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }
    
}
