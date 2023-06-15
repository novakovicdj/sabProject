/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.ShopOperations;

/**
 *
 * @author novak
 */
public class ShopImpl implements ShopOperations {

    @Override
    public int createShop(String string, String string1) {
        Connection conn = DB.getInstance().getConnection();
        String s1 = "Select * from Shop where Name = ?";
        String s2 = "insert into Shop (IdC, Discount, Name) values ((select Idc from City where Name = ?), 0, ?)";
        try ( PreparedStatement ps = conn.prepareStatement(s1);
                PreparedStatement ps1 = conn.prepareStatement(s2, PreparedStatement.RETURN_GENERATED_KEYS);){
           ps.setString(1, string);
           try (ResultSet rs = ps.executeQuery()) {
               if(rs.next()) {
                   return -1;
               } else {
                   ps1.setString(1, string1);
                   ps1.setString(2, string);
                   ps1.execute();
                   try (ResultSet rs1 = ps1.getGeneratedKeys()) {
                       if(rs1.next()) {
                           //System.out.println("Uspesno ste dodali prodavnicu");
                           return rs1.getInt(1);
                       }
                   }
               }
           }
        } catch (SQLException ex) {
            Logger.getLogger(ShopImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int setCity(int i, String string) {
        Connection conn = DB.getInstance().getConnection();
        String s1 = "update Shop set IdC = (select IdC from City where Name = ?) where IdS = ?";
        try (PreparedStatement ps = conn.prepareStatement(s1);){
            ps.setString(1, string);
            ps.setInt(2, i);
            if(ps.executeUpdate() > 0) {
                return 1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ShopImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int getCity(int i) {
        Connection conn = DB.getInstance().getConnection();
        String s = "select IdC from Shop where IdS = ?";
        try (PreparedStatement ps = conn.prepareStatement(s);){
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ShopImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int setDiscount(int i, int i1) {
        Connection conn = DB.getInstance().getConnection();
        String s = "update Shop set Discount = ? where IdS = ?";
        try (PreparedStatement ps = conn.prepareStatement(s);){
            ps.setInt(1, i1);
            ps.setInt(2, i);
            if(ps.executeUpdate() > 0) {
                return 1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ShopImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int increaseArticleCount(int i, int i1) {
        Connection conn = DB.getInstance().getConnection();
        String s1 = "Update Article set Count = Count + ? where IdA = ?";
        String s2 = "Select Count from Article where IdA = ?";
        try (PreparedStatement ps = conn.prepareStatement(s1);
                PreparedStatement ps1 = conn.prepareStatement(s2);){
            ps.setInt(1, i1);
            ps.setInt(2, i);
            if(ps.executeUpdate() > 0) {
                ps1.setInt(1, i);
                try (ResultSet rs = ps1.executeQuery()) {
                    if(rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ShopImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int getArticleCount(int i) {
        Connection conn = DB.getInstance().getConnection();
        String s = "select Count from Article where IdA = ?";
        try (PreparedStatement ps = conn.prepareStatement(s);){
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ShopImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public List<Integer> getArticles(int i) {
        Connection conn = DB.getInstance().getConnection();
        List<Integer> l = new ArrayList<>();
        String s = "select IdA from Article where IdS = ?";
        try (PreparedStatement ps = conn.prepareStatement(s);){
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    l.add(rs.getInt(1));
                }
                return l.size() > 0 ? l : null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ShopImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public int getDiscount(int i) {
        Connection conn = DB.getInstance().getConnection();
        String s = "select Discount from Shop where IdS = ?";
        try (PreparedStatement ps = conn.prepareStatement(s);){
           ps.setInt(1, i);
           try (ResultSet rs = ps.executeQuery()) {
               if(rs.next()) {
                   return rs.getInt(1);
               }
           }
        } catch (SQLException ex) {
            Logger.getLogger(ShopImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
}
