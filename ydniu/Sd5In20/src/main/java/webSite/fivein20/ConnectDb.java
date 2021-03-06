package webSite.fivein20;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import com.mysql.jdbc.PreparedStatement;

public class ConnectDb{ 
    private static ConnectDb instance = null;
    //取得连接 
    public static synchronized Connection getConnection() { 
        if (instance == null){ 
            instance = new ConnectDb(); 
        } 
        return instance._getConnection(); 
    } 

    private ConnectDb(){ 
        super(); 
    } 

    private  Connection _getConnection(){ 
    	try{ 
    		Properties p = new Properties();
			InputStream is = this.getClass().getClassLoader().getResourceAsStream("db.properties");  
            p.load(is);
            String driver = p.getProperty("driver",""); 
            String url = p.getProperty("url",""); 
            String username = p.getProperty("username",""); 
            String password = p.getProperty("password","");
            Properties pr = new Properties(); 
            pr.put("user",username); 
            pr.put("password",password); 
            pr.put("characterEncoding", "GB2312"); 
            pr.put("useUnicode", "TRUE"); 
            Class.forName(driver).newInstance(); 
            return DriverManager.getConnection(url,pr); 
    	} catch(Exception se){
    		LogUtil.error(se.getMessage());
    	}
    	return null;
	} 

    //释放资源 
    public static void dbClose(Connection conn,PreparedStatement ps,ResultSet rs) throws SQLException 
    { 
    	if(rs!=null && !rs.isClosed()){
    			rs.close(); 
		}
    	if(ps!=null && !ps.isClosed()){
    			ps.close(); 
    		}
    	if(conn!=null && !conn.isClosed()){
    			conn.close(); 
    		}
    }
} 
