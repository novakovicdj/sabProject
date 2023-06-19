/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.BuyerOperations;
import rs.etf.sab.operations.CityOperations;

/**
 *
 * @author novak
 */
public class BuyerImpl implements BuyerOperations {
    
    @Override
    public int createBuyer(String string, int i) {
        Connection conn = DB.getInstance().getConnection();
        String query = "insert into Buyer(Name, IdC, Credit) values(?, ?, 0)";
        try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            ps.setString(1, string);
            ps.setInt(2, i);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys();) {
                if(rs.next() && rs.getInt(1) != 0) {
                    //System.out.println("Uspesno ste dodali kupca u sistem");
                    return rs.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(BuyerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BuyerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int setCity(int i, int i1) {
        Connection conn = DB.getInstance().getConnection();
        String query = "update Buyer set IdC = ? where IdB = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, i1);
            ps.setInt(2, i);
            if( ps.executeUpdate() > 0) {
                //System.out.println("Uspesno ste azurirali grad kupca");
                return 1;
            } else {
                //System.out.println("Doslo je do greske prilikom azuriranja grada kupca");
            }
        } catch (SQLException ex) {
            Logger.getLogger(BuyerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int getCity(int i) {
        Connection conn = DB.getInstance().getConnection();
        String query = "select IdC from Buyer where IdB = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery();) {
               if(rs.next()) {
                   return rs.getInt(1);
               } 
            } catch (SQLException ex) {
                Logger.getLogger(BuyerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BuyerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public BigDecimal increaseCredit(int i, BigDecimal bd) {
        Connection conn = DB.getInstance().getConnection();
        String query = "update Buyer set Credit = Credit + ? where idB = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(query);){
            ps.setBigDecimal(1, bd);
            ps.setInt(2, i);
            if(ps.executeUpdate() > 0) {
                //System.out.println("Uspesno ste dodali kredit");
                String q1 = "select Credit from Buyer where IdB = ?";
                try(PreparedStatement ps1 = conn.prepareStatement(q1)) {
                    ps1.setInt(1, i);
                    try (ResultSet rs = ps1.executeQuery();) {
                        if(rs.next()) {
                            return rs.getBigDecimal(1).setScale(3);
                        }
                    }  catch (SQLException ex) {
                        Logger.getLogger(BuyerImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(BuyerImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            } 
        } catch (SQLException ex) {
            Logger.getLogger(BuyerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BigDecimal.valueOf(0).setScale(3);
    }

    @Override
    public int createOrder(int i) {
        int buyerCity = -1;
        int city = -1;
        int daysB = 0;
        CityOperations c = new CityImpl();
        Connection conn = DB.getInstance().getConnection();
        String poc = "select IdC from Buyer where IdB = ?";
        try(PreparedStatement psP = conn.prepareStatement(poc);) {
            psP.setInt(1, i);
            try(ResultSet rs1 = psP.executeQuery()) {
                if(rs1.next()) {
                    buyerCity = rs1.getInt(1);
                }
            }  catch (SQLException ex) {
                Logger.getLogger(BuyerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BuyerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        String s1 = "select IdS from Shop where IdC = " + buyerCity;
        
        try (PreparedStatement prep = conn.prepareStatement(s1);
                ResultSet rs2 = prep.executeQuery()){
            if(!rs2.next()) {
                String str = "select distinct IdC from Shop";
                List<Integer> cities = new ArrayList<>();
                try (PreparedStatement prepst = conn.prepareStatement(str); ResultSet rs = prepst.executeQuery()) {
                    
                    while(rs.next()) {
                        cities.add(rs.getInt(1));
                    }
                }

                String s = "select * from Connections";
        
                try (PreparedStatement ps1 = conn.prepareStatement(s);){
                    try(ResultSet rs = ps1.executeQuery();) {
                        int sz = c.getCities().size();
                        int k = 0;
                        int p = 0;
                        int graph[][] = new int[sz][sz];
                        for(k = 0; k < sz; k++) {
                            for(int j = 0; j < sz; j++) {
                                graph[k][j] = 0;
                            }
                        }
                        while(rs.next()) {
                           graph[rs.getInt("IdC1") - 1][rs.getInt("IdC2") - 1] = rs.getInt("Distance");
                           graph[rs.getInt("IdC2") - 1][rs.getInt("IdC1") - 1] = rs.getInt("Distance");
                        }
                        
                        int dist[] = new int[sz];
                        Boolean bools[] = new Boolean[sz];
                        
                        for(k = 0; k < sz; k++) {
                            dist[k] = Integer.MAX_VALUE;
                            bools[k] = false;
                        }

                        dist[buyerCity - 1] = 0;
                        // dijkstra
                        int min = Integer.MAX_VALUE;
                        int min_idx = -1;
                        for(k = 0; k < sz - 1; k++) {
                            for(p = 0; p < sz; p++) {
                                if(dist[p] < min && !bools[p]) {
                                    min = dist[p];
                                    min_idx = p;
                                }
                            } 
                            bools[min_idx] = true;

                            for(p = 0; p < sz; p++) {
                                if(!bools[p] && graph[min_idx][p] != 0
                                        && dist[min_idx] + graph[min_idx][p] < dist[p]) {
//                                    System.out.println(dist[min_idx] + Integer.parseInt(m.get(p).toString()));
                                    dist[p] = dist[min_idx] + graph[min_idx][p];
                                }
//                                System.out.print(dist[p] + " ");
                            }
//                            System.out.println();
                            min_idx = -1;
                            min = Integer.MAX_VALUE;
                        }

                        if(min_idx == -1) {
                            min_idx = 0;
                        }
                        
                        for(k = 0; k < sz; k++) {
                            if(dist[k] < min && k != buyerCity - 1 && cities.contains(k+1)) {
                                min = dist[k];
                                min_idx = k;
                            }
                        }
                        city = min_idx  + 1;
                        daysB = dist[min_idx];
//                        System.out.println("HELLO");
                    } catch (SQLException ex) {
                        Logger.getLogger(BuyerImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(BuyerImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                city = buyerCity;
                daysB = 0;
            }
        } catch (SQLException ex) {
            Logger.getLogger(BuyerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        String q = "insert into Orders(Price, State, sentTime, receivedTime, IdB, DaysA, DaysB, IdC) values (0, 'created', NULL, NULL, ?, 0, " + daysB + ", " + city + ")";
        try (PreparedStatement ps = conn.prepareStatement(q, Statement.RETURN_GENERATED_KEYS);){
            ps.setInt(1, i);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if(rs.next()) {
                    //System.out.println("Uspesno ste kreirali praznu porudzbinu");
                    return rs.getInt(1);
                }
            }  catch (SQLException ex) {
                Logger.getLogger(BuyerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BuyerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
//        System.out.println("Grad je: " + city);
        return -1;
    }

    @Override
    public List<Integer> getOrders(int i) {
        Connection conn = DB.getInstance().getConnection();
        String q = "select IdO from Orders where IdB = ?";
        try (PreparedStatement ps = conn.prepareStatement(q);){
            ps.setInt(1, i);
            try(ResultSet rs = ps.executeQuery()) {
                List<Integer> l = new ArrayList<Integer>();
                while(rs.next()) {
                    l.add(rs.getInt(1));
                }
                return l.size() > 0 ? l : null;
            }  catch (SQLException ex) {
                Logger.getLogger(BuyerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BuyerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public BigDecimal getCredit(int i) {
        Connection conn = DB.getInstance().getConnection();
        String q = "select Credit from Buyer where IdB = ?";
        try (PreparedStatement ps = conn.prepareStatement(q);){
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return rs.getBigDecimal(1).setScale(3);
                }
            }  catch (SQLException ex) {
                Logger.getLogger(BuyerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BuyerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BigDecimal.valueOf(0).setScale(3);
    }
    
}
