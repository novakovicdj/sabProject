/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.TransactionOperations;

/**
 *
 * @author novak
 */
public class TransactionImpl implements TransactionOperations {
    
    /**
     * 
     * Napisati trigger u bazi
     * Kad se menja vreme azurirati u bazi sta treba
     * Ako ne racunam dobro koliko para svakoj radnji ide, dodati kolonu u tabeli inOrder gde ce to da se racuna kad se dodaju artikli
     */
    
    @Override
    public BigDecimal getBuyerTransactionsAmmount(int i) {
        Connection conn = DB.getInstance().getConnection();
        String s = "select coalesce(Sum(Amount), 0) from Transactions where IdB = ? and IdS IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(s);){
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return BigDecimal.valueOf(rs.getDouble(1)).setScale(3);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TransactionImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BigDecimal.valueOf(-1).setScale(3);
    }

    @Override
    public BigDecimal getShopTransactionsAmmount(int i) {
        Connection conn = DB.getInstance().getConnection();
        String s = "select coalesce(Sum(Amount), 0) from Transactions where IdS = ? and IdB IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(s);){
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return BigDecimal.valueOf(rs.getDouble(1)).setScale(3);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TransactionImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BigDecimal.valueOf(-1).setScale(3);
    }

    @Override
    public List<Integer> getTransationsForBuyer(int i) {
        Connection conn = DB.getInstance().getConnection();
        String s = "select IdT from Transactions where IdB = ? and IdS IS NULL";
        List<Integer> l = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(s);){
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    l.add(rs.getInt(1));
                }
                return l.size() > 0 ? l : null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(TransactionImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public int getTransactionForBuyersOrder(int i) {
        Connection conn = DB.getInstance().getConnection();
        String s = "select IdT from Transactions where IdO = ? and IdS IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(s);){
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TransactionImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int getTransactionForShopAndOrder(int i, int i1) {
        Connection conn = DB.getInstance().getConnection();
        String s = "Select IdT from Transactions where IdS = ? and IdO = ? and IdB IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(s);){
            ps.setInt(1, i1);
            ps.setInt(2, i);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TransactionImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public List<Integer> getTransationsForShop(int i) {
        Connection conn = DB.getInstance().getConnection();
        String s = "select IdT from Transactions where IdS = ? and IdB IS NULL";
        List<Integer> l = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(s);){
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    l.add(rs.getInt(1));
                }
                return l.size() > 0 ? l : null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(TransactionImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Calendar getTimeOfExecution(int i) {
        Connection conn = DB.getInstance().getConnection();
        String s = "Select Time from Transactions where IdT = ?";
        Calendar cal = Calendar.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(s);){
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                   cal.setTime(rs.getDate(1));
                   return cal;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TransactionImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public BigDecimal getAmmountThatBuyerPayedForOrder(int i) {
        Connection conn = DB.getInstance().getConnection();
        String s = "select Amount from Transactions where IdO = ? and IdS IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(s);){
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return BigDecimal.valueOf(rs.getDouble(1)).setScale(3);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TransactionImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BigDecimal.valueOf(-1).setScale(3);
    }

    @Override
    public BigDecimal getAmmountThatShopRecievedForOrder(int i, int i1) {
         Connection conn = DB.getInstance().getConnection();
        String s = "Select Amount from Transactions where IdS = ? and IdO = ? and IdB IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(s);){
            ps.setInt(1, i1);
            ps.setInt(2, i);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return BigDecimal.valueOf(rs.getDouble(1)).setScale(3);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TransactionImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BigDecimal.valueOf(-1);
    }

    @Override
    public BigDecimal getTransactionAmount(int i) {
        Connection conn = DB.getInstance().getConnection();
        String s = "select Amount from Transactions where IdT = ?";
        try (PreparedStatement ps = conn.prepareStatement(s);){
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return BigDecimal.valueOf(rs.getDouble(1)).setScale(3);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TransactionImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BigDecimal.valueOf(-1).setScale(3);
    }

    @Override
    public BigDecimal getSystemProfit() {
        Connection conn = DB.getInstance().getConnection();
        
        String s = "Select sum(Amount) from Profit";
        try (PreparedStatement ps = conn.prepareStatement(s); ResultSet rs = ps.executeQuery()){ 
            if(rs.next()) {
                return BigDecimal.valueOf(rs.getDouble(1)).setScale(3);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TransactionImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BigDecimal.valueOf(0).setScale(3);

    }
    
}
