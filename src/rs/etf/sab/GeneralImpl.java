/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.GeneralOperations;

/**
 *
 * @author novak
 */
public class GeneralImpl implements GeneralOperations {

    private Calendar c;

    public GeneralImpl() {
        c = Calendar.getInstance();
    }

    @Override
    public void setInitialTime(Calendar clndr) {
        c.setTime(clndr.getTime());
    }

    @Override
    public Calendar time(int i) {
        Connection conn = DB.getInstance().getConnection();
        String s = "select * from Orders where receivedTime IS NULL";
        String s1 = "Update Orders set receivedTime = ?, State = 'arrived' where IdO = ?";
        //String s2 = "Update Orders set sentTime = ? where IdO = ?";
        
        c.add(Calendar.DATE, i);
        
        try (PreparedStatement ps = conn.prepareStatement(s); ResultSet rs = ps.executeQuery()){
            while(rs.next()) {
                /**
                 * Prvo pitati da li je sentTime null?
                 * Ako jeste znaci to samo da se namesti
                 * Ili sta ako se vreme pomeri za toliko da je i prikupljena i stigla?
                 */

                Date sentTime = new Date(rs.getDate("sentTime").getTime());
                long t1 = sentTime.getTime();
                long t2 = c.getTimeInMillis();
                long diff = Math.abs(t2 - t1);
                long daysDiff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                if(daysDiff >= (long)(rs.getInt("daysA") + rs.getInt("daysB"))) {
                    try (PreparedStatement ps1 = conn.prepareStatement(s1);) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(sentTime);
                        c.add(Calendar.DATE, rs.getInt("daysA") + rs.getInt("daysB"));
                        ps1.setDate(1, new java.sql.Date(c.getTimeInMillis()));
                        ps1.setInt(2, rs.getInt("IdO"));
                        ps1.executeUpdate();
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(GeneralImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return c;
    }

    @Override
    public Calendar getCurrentTime() {
        return c;
    }

    @Override
    public void eraseAll() {
        Connection conn = DB.getInstance().getConnection();
        String q1 = "exec sp_MSForEachTable 'ALTER TABLE ? NOCHECK CONSTRAINT ALL'";
        String q4 = "Exec sp_MSforeachtable 'DBCC CHECKIDENT(''?'', RESEED, 0)'";
        String q2 = "exec sp_MSForEachTable 'DELETE FROM ?'";
        String q3 = "exec sp_MSForEachTable 'ALTER TABLE ? WITH CHECK CHECK CONSTRAINT ALL'";
        try (PreparedStatement ps1 = conn.prepareStatement(q1); 
                PreparedStatement ps2 = conn.prepareStatement(q2); 
                PreparedStatement ps3 = conn.prepareStatement(q3); 
                PreparedStatement ps4 = conn.prepareCall(q4)) {
            ps1.execute();
            ps2.execute();
            ps4.execute();
            ps3.execute();
        } catch (SQLException ex) {
            Logger.getLogger(GeneralImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
