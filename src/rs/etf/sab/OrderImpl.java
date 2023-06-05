/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.OrderOperations;

/**
 *
 * @author novak
 */
public class OrderImpl implements OrderOperations {
    
    GeneralOperations g;

    public OrderImpl(GeneralOperations go) {
        this.g = go;
    }
    
    

    @Override
    public int addArticle(int i, int i1, int i2) {
        Connection conn = DB.getInstance().getConnection();
        String q1 = "select IdC from Article, Shop where Article.Count >= ? and Article.IdA = ? and Shop.IdS = Article.IdS";
        try (PreparedStatement ps1 = conn.prepareStatement(q1);) {
            ps1.setInt(1, i2);
            ps1.setInt(2, i1);
            try (ResultSet rs = ps1.executeQuery()) {
                if (rs.next()) { // ima dovoljno kolicine
                    String u = "Update Article set Count = Count - ? where IdA = ?";
                    try (PreparedStatement st1 = conn.prepareStatement(u);) {
                        st1.setInt(1, i2);
                        st1.setInt(2, i1);
                        st1.executeUpdate();
                    }
                    String pstr = "Select * from inOrder where IdA = ? and IdO = ?";
                    try (PreparedStatement pp = conn.prepareStatement(pstr);) {
                        pp.setInt(1, i1);
                        pp.setInt(2, i);
                        try (ResultSet rset = pp.executeQuery()) {
                            if (rset.next()) { // vec ima u narudzbi, samo povecaj iznos
                                String upd = "Update inOrder set Count = Count + ? where IdI = ?";
                                try (PreparedStatement psp = conn.prepareStatement(upd);) {
                                    psp.setInt(1, i2);
                                    psp.setInt(2, rset.getInt("IdI"));
                                    psp.executeUpdate();
                                    return rset.getInt("IdI");
                                }
                            } else {
                                String q = "Insert into inOrder(IdO, IdA, Count) values(?, ?, ?)";
                                try (PreparedStatement ps = conn.prepareStatement(q, PreparedStatement.RETURN_GENERATED_KEYS);) {
                                    ps.setInt(1, i);
                                    ps.setInt(2, i1);
                                    ps.setInt(3, i2);
                                    ps.executeUpdate();
                                    try (ResultSet rs1 = ps.getGeneratedKeys()) {
                                        if (rs1.next()) {
                                            //System.out.println("Uspesno ste dodali artikl");
                                            return rs1.getInt(1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else { // nema kolicine dovoljno
                    System.out.println("Nema trazene kolicine artikla");
                    return -1;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OrderImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int removeArticle(int i, int i1) {
        Connection conn = DB.getInstance().getConnection();
        String query1 = "select * from inOrder where IdA = ? and IdO = ?";
        int count = 0;
        try (PreparedStatement ps = conn.prepareStatement(query1);) {
            ps.setInt(1, i1);
            ps.setInt(2, i);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt("Count");
                    String query2 = "delete from inOrder where IdO = ? and IdA = ?";
                    try (PreparedStatement ps1 = conn.prepareStatement(query2)) {
                        ps1.setInt(1, i);
                        ps1.setInt(2, i1);
                        ps1.executeUpdate();
                    }

                    String query3 = "update Article set Count = Count + ? where IdA = ?";
                    try (PreparedStatement ps1 = conn.prepareStatement(query3)) {
                        ps1.setInt(1, count);
                        ps1.setInt(2, i1);
                        if (ps1.executeUpdate() > 0) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public List<Integer> getItems(int i) {
        Connection conn = DB.getInstance().getConnection();
        List<Integer> l = new ArrayList();
        String query = "Select IdA from inOrder where IdO = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    l.add(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return l;
    }

    @Override
    public int completeOrder(int i) {
        /*
        *   Promeniti status u sent u Orders, sentTime
        *   Odrediti daysA u Orders + , izdracunati cenu uz aplicirani popust +
        *   Kreirati transakciju za kupca, skinuti mu pare

         */
        Connection conn = DB.getInstance().getConnection();
        String q1 = "execute SP_FINAL_PRICE ?, ?"; // +
        String q2 = "update Orders set DaysA = ?, State  = 'sent', Price = ?, sentTime = ? where IdO = ?"; // +
        String q3 = "update Buyer set Credit = Credit - ? where IdB = (select IdB from Orders where IdO = ?)";
        String q4 = "insert into Transactions(IdO, IdS, IdB, Amount, Time) values (?, NULL, (select IdB from Orders where IdO = ?), ?, ?)";
        String q6 = "select * from Orders where IdO = ?"; // +
        String q7 = "select distinct IdC from inOrder, Article, Shop where inOrder.IdO = ? and inOrder.IdA = Article.IdA and Article.IdS = Shop.IdS"; // +

        double price = 0;
        CityOperations c = new CityImpl();
        int city = 0;
        int cityAdd = 0;
        int max = -1;

        try (CallableStatement cs = conn.prepareCall(q1); PreparedStatement ps = conn.prepareStatement(q6);) {
            cs.setInt(1, i);
            cs.setDate(2, new java.sql.Date(g.getCurrentTime().getTimeInMillis()));
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    price = rs.getDouble(1); // izracunata vrednost
                    //System.out.println(price);
                }
            }
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    city = rs.getInt("IdC");
                    int[] d = dijkstra(city);
                    try (PreparedStatement ps7 = conn.prepareStatement(q7);) {
                        ps7.setInt(1, i);

                        try (ResultSet rs7 = ps7.executeQuery()) {
                            while (rs7.next()) {
                                if (d[rs7.getInt(1) - 1] > max) {
                                    cityAdd = rs7.getInt(1);
                                    max = d[rs7.getInt(1) - 1];
                                }
                            }
                        }
                    }
                }
            }

            try (PreparedStatement ps1 = conn.prepareStatement(q2)) {
                ps1.setInt(1, max);
                ps1.setDouble(2, price);
                ps1.setDate(3, new java.sql.Date(g.getCurrentTime().getTimeInMillis())); // ?
                ps1.setInt(4, i);
                
                if(ps1.executeUpdate() > 0) {
                    try (PreparedStatement ps3 = conn.prepareStatement(q3)) {
                        ps3.setDouble(1, price);
                        ps3.setDouble(2, i);
                        if(ps3.executeUpdate() > 0) {
                            try (PreparedStatement ps4 = conn.prepareStatement(q4)) {
                                ps4.setInt(1, i);
                                ps4.setInt(2, i);
                                ps4.setDouble(3, price);
                                ps4.setDate(4, new java.sql.Date(g.getCurrentTime().getTimeInMillis()));
                                if(ps4.executeUpdate() > 0) {
                                    return 1;
                                }
                            }
                        }
                    }
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(OrderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;
    }

    @Override
    public BigDecimal getFinalPrice(int i) {
        Connection conn = DB.getInstance().getConnection();
        String q = "select * from Orders where IdO = ?";
        try (PreparedStatement ps = conn.prepareStatement(q);) {
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if (rs.getString("State").equals("created")) {
                        return BigDecimal.valueOf(-1).setScale(3);
                    } else {
                        return BigDecimal.valueOf(rs.getDouble("Price")).setScale(3);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return BigDecimal.valueOf(-1).setScale(3);

    }

    @Override
    public BigDecimal getDiscountSum(int i) {
        Connection conn = DB.getInstance().getConnection();
        String q1 = "select sum(Article.Price * inOrder.Count) from Article, inOrder where inOrder.IdO = ? and inOrder.IdA = Article.IdA";

        double sum = 0;
        double price = 0;

        try (PreparedStatement ps = conn.prepareStatement(q1);) {
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    sum = rs.getDouble(1);
                    String q2 = "select Price from Orders where IdO = ?";
                    try (PreparedStatement ps1 = conn.prepareStatement(q2)) {
                        ps1.setInt(1, i);
                        try (ResultSet rs1 = ps1.executeQuery()) {
                            if (rs1.next()) {
                                price = rs1.getDouble(1);
                                double discount = 1 - price / sum;
                                return BigDecimal.valueOf(sum - price).setScale(3);
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return BigDecimal.valueOf(-1);
    }

    @Override
    public String getState(int i) {
        Connection conn = DB.getInstance().getConnection();
        String q = "select State from Orders where IdO = ?";
        try (PreparedStatement ps = conn.prepareStatement(q);) {
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "created";
    }

    @Override
    public Calendar getSentTime(int i) {
        Connection conn = DB.getInstance().getConnection();
        String s = "Select sentTime from Orders where IdO = ?";
        Calendar cal = Calendar.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(s);) {
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
//                    System.out.println(rs.getDate(1));
                    if(rs.getDate(1) != null) {
                        cal.setTime(rs.getDate(1));
                        return cal; // ?
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Calendar getRecievedTime(int i) {
        Connection conn = DB.getInstance().getConnection();
        String s = "Select receivedTime from Orders where IdO = ?";
        Calendar cal = Calendar.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(s);) {
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if(rs.getDate(1) != null) {
                        cal.setTime(rs.getDate(1));
                        return cal; // ?
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public int getBuyer(int i) {
        Connection conn = DB.getInstance().getConnection();
        String s = "Select IdB from Orders where IdO = ?";
        try (PreparedStatement ps = conn.prepareStatement(s);) {
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;
    }

    @Override
    public int getLocation(int i) {
        Connection conn = DB.getInstance().getConnection();
        String s = "select * from Orders where IdO = ?";
        
        int buyerCity = -1;
        
        try (PreparedStatement ps = conn.prepareStatement(s);) {
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    String s1 = "select IdC from Buyer where IdB = ?";
                    try (PreparedStatement p = conn.prepareStatement(s1)) {
                        p.setInt(1, rs.getInt("IdB"));
                        try (ResultSet r = p.executeQuery()) {
                            if(r.next()) {
                                buyerCity = r.getInt(1);
                            }
                        }
                    }
                    if(rs.getString("State").equals("sent")) {
                        int[] d = dijkstra(buyerCity);
                        int[] t = dijkstra2(buyerCity);
                        int sum = 0;
                        int idx = rs.getInt("IdC");
                        Date d1 = new java.util.Date(rs.getDate("sentTime").getTime());
                        long t1 = d1.getTime();
                        long t2 = g.getCurrentTime().getTimeInMillis();
                        long diff = Math.abs(t2 - t1);
                        long daysDiff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                        if(daysDiff <= rs.getInt("daysA")) { // ili < samo ?
                            return rs.getInt("IdC");
                        } else {
                            daysDiff -= rs.getInt("daysA");
                            Map<Integer, Integer> m = new HashMap<>();
                            List<Integer> l = new ArrayList<>();
                            while(idx != buyerCity) {
                                l.add(idx);
                                idx = t[idx - 1];
                            }
                            l.add(idx);
                            for(int it = l.size() - 1; it >= 0; it--) {
                                m.put(l.get(it), d[l.get(it) - 1] - sum);
                                sum = d[l.get(it) - 1];
                            }
                            
                            for(int it = 0; it < l.size(); it++) {
                                if(daysDiff < m.get(l.get(it))) {
                                    return l.get(it);
                                } else {
                                    daysDiff -= m.get(l.get(it));
                                }
                            }
                            return 0;
                        }
                    } else if(rs.getString("State").equals("arrived")) {
                        if(buyerCity != -1) {
                            return buyerCity;
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    private int[] dijkstra(int city) {
        Connection conn = DB.getInstance().getConnection();
        CityOperations c = new CityImpl();
        String s = "select * from Connections";
        int sz = c.getCities().size();
        int dist[] = new int[sz];
        Boolean bools[] = new Boolean[sz];
        try (PreparedStatement ps1 = conn.prepareStatement(s);) {
            try (ResultSet rs1 = ps1.executeQuery();) {

                int k = 0;
                int p = 0;
                int graph[][] = new int[sz][sz];
                for (k = 0; k < sz; k++) {
                    for (int j = 0; j < sz; j++) {
                        graph[k][j] = 0;
                    }
                }
                while (rs1.next()) {
                    graph[rs1.getInt("IdC1") - 1][rs1.getInt("IdC2") - 1] = rs1.getInt("Distance");
                    graph[rs1.getInt("IdC2") - 1][rs1.getInt("IdC1") - 1] = rs1.getInt("Distance");
                }
                
                for (k = 0; k < sz; k++) {
                    dist[k] = Integer.MAX_VALUE;
                    bools[k] = false;
                }

                dist[city - 1] = 0;
                
                // dijkstra
                int min = Integer.MAX_VALUE;
                int min_idx = -1;
                for (k = 0; k < sz - 1; k++) {
                    for (p = 0; p < sz; p++) {
                        if (dist[p] < min && !bools[p]) {
                            min = dist[p];
                            min_idx = p;
                        }
                    }
                    bools[min_idx] = true;

                    for (p = 0; p < sz; p++) {
                        if (!bools[p] && graph[min_idx][p] != 0
                                && dist[min_idx] + graph[min_idx][p] < dist[p]) {
                            dist[p] = dist[min_idx] + graph[min_idx][p];
                        }
                    }
                    min_idx = -1;
                    min = Integer.MAX_VALUE;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dist;
    }
    
    private int[] dijkstra2(int city) {
        Connection conn = DB.getInstance().getConnection();
        CityOperations c = new CityImpl();
        String s = "select * from Connections";
        int sz = c.getCities().size();
        int dist[] = new int[sz];
        int t[] = new int[sz];
        Boolean bools[] = new Boolean[sz];
        try (PreparedStatement ps1 = conn.prepareStatement(s);) {
            try (ResultSet rs1 = ps1.executeQuery();) {

                int k = 0;
                int p = 0;
                int graph[][] = new int[sz][sz];
                for (k = 0; k < sz; k++) {
                    for (int j = 0; j < sz; j++) {
                        graph[k][j] = 0;
                    }
                }
                while (rs1.next()) {
                    graph[rs1.getInt("IdC1") - 1][rs1.getInt("IdC2") - 1] = rs1.getInt("Distance");
                    graph[rs1.getInt("IdC2") - 1][rs1.getInt("IdC1") - 1] = rs1.getInt("Distance");
                }
                
                for (k = 0; k < sz; k++) {
                    dist[k] = Integer.MAX_VALUE;
                    if(graph[city - 1][k] != 0) {
                        t[k] = city;
                    } else {
                        t[k] = 0;
                    }
                    bools[k] = false;
                }

                dist[city - 1] = 0;
                
                // dijkstra
                int min = Integer.MAX_VALUE;
                int min_idx = -1;
                for (k = 0; k < sz - 1; k++) {
                    for (p = 0; p < sz; p++) {
                        if (dist[p] < min && !bools[p]) {
                            min = dist[p];
                            min_idx = p;
                        }
                    }
                    bools[min_idx] = true;

                    for (p = 0; p < sz; p++) {
                        if (!bools[p] && graph[min_idx][p] != 0
                                && dist[min_idx] + graph[min_idx][p] < dist[p]) {
                            dist[p] = dist[min_idx] + graph[min_idx][p];
                            t[p] = min_idx + 1;
                        }
                    }
                    min_idx = -1;
                    min = Integer.MAX_VALUE;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return t;
    }
        

}
