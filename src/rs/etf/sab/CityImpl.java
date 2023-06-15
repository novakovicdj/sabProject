/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CityOperations;

/**
 *
 * @author novak
 */
public class CityImpl implements CityOperations {

    @Override
    public int createCity(String string) {
        Connection conn = DB.getInstance().getConnection();
        String q = "Insert into City(Name) values (?)";
        try (PreparedStatement ps = conn.prepareStatement(q, PreparedStatement.RETURN_GENERATED_KEYS);){
            ps.setString(1, string);
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()) {
                if(rs.next()) {
                    //System.out.println("Uspesno ste dodali grad");
                    return rs.getInt(1);
                }
            }  catch (SQLException ex) {
                Logger.getLogger(CityImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CityImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }

    @Override
    public List<Integer> getCities() {
        Connection conn = DB.getInstance().getConnection();
        String q = "select IdC from City";
        try (PreparedStatement ps = conn.prepareStatement(q); ResultSet rs = ps.executeQuery()) {
            List<Integer> l = new ArrayList<>();
            while(rs.next()) {
                l.add(rs.getInt(1));
            }
            return l.size() > 0 ? l : null;
        } catch (SQLException ex) {
            Logger.getLogger(CityImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public int connectCities(int i, int i1, int i2) {
        if(i == i1) {
            //System.out.println("Veza izmedju jednog grada nije moguca");
            return -1;
        }
        Connection conn = DB.getInstance().getConnection();
        
        String q1 = "Select IdL from Connections where IdC1 = ? and IdC2 = ? or IdC2 = ? and IdC1 = ?";
        try (PreparedStatement ps1 = conn.prepareStatement(q1);){
            ps1.setInt(1, i);
            ps1.setInt(2, i1);
            ps1.setInt(3, i);
            ps1.setInt(4, i1);
            try(ResultSet rs = ps1.executeQuery()) {
                if(rs.next()) {
                    //System.out.println("Vec postoji jedna veza izmedju ova dva grada");
                    return -1;
                }
            }  catch (SQLException ex) {
                Logger.getLogger(CityImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CityImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String q = "Insert into Connections(IdC1, IdC2, Distance) values(?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(q, PreparedStatement.RETURN_GENERATED_KEYS);){
            ps.setInt(1, i);
            ps.setInt(2, i1);
            ps.setInt(3, i2);
            if(ps.executeUpdate() > 0) {
                try(ResultSet rs = ps.getGeneratedKeys()) {
                    if(rs.next()) {
                        return rs.getInt(1);
                    }
                }  catch (SQLException ex) {
                    Logger.getLogger(CityImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(CityImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }

    @Override
    public List<Integer> getConnectedCities(int i) {
        Connection conn = DB.getInstance().getConnection();
        String q = "Select * from Connections where IdC1 = ? or IdC2 = ?";
        try (PreparedStatement ps = conn.prepareStatement(q);){
            ps.setInt(1, i);
            ps.setInt(2, i);
            try(ResultSet rs = ps.executeQuery()) {
                List<Integer> l = new ArrayList<Integer>();
                while(rs.next()) {
                    if(rs.getInt("IdC1") == i) {
                        l.add(rs.getInt("IdC2"));
                    } else {
                        l.add(rs.getInt("IdC1"));
                    }
                }
                return l.size() > 0 ? l : null;
            }  catch (SQLException ex) {
                Logger.getLogger(CityImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CityImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Integer> getShops(int i) {
        Connection conn = DB.getInstance().getConnection();
        String q = "Select IdS from Shop where IdC = ?";
        try (PreparedStatement ps = conn.prepareStatement(q);){
            ps.setInt(1, i);
            try(ResultSet rs = ps.executeQuery()) {
                List<Integer> l = new ArrayList<>();
                while(rs.next()) {
                    l.add(rs.getInt(1));
                }
                return l.size() > 0 ? l : null;
            }  catch (SQLException ex) {
                Logger.getLogger(CityImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CityImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
