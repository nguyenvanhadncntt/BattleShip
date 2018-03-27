package serverAndDbPkg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectDatabase {
    Connection conn = null;

    public Connection connectDatabase() {
        try {
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/battleship?useUnicode=true&characterEncoding=utf-8";
            Class.forName(driver);
            conn = DriverManager.getConnection(url, "root", "");
            System.out.println("Connected Database");
        } catch (Exception ex) {
            Logger.getLogger(ConnectDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }
    
    public Player login(String email,String password) {

        String sql = "select * from nguoichoi where Email='"+email+"' and MatKhau='"+password+"'";
        Statement smt;
        try {
            smt = conn.createStatement();
            ResultSet rs = smt.executeQuery(sql);
            if(rs.next()) {
            	return new Player(rs.getString("Email"),rs.getString("MatKhau"),rs.getString("TenNhanVat"),rs.getString("Cap"),"",-1)	;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConnectDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
