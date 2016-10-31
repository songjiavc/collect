package echart.collect.website.hebei.CollectFast3;
import java.io.*; 
import java.util.*; 
import java.sql.*; 


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
        		         	String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://192.168.1.253:3306/echart3";
			String username = "echart";
			String password = "echart";
		            Properties p = new Properties();
		            InputStream is = this.getClass().getClassLoader()
			                  .getResourceAsStream("db.properties");  
		            p.load(is);
		            driver = p.getProperty("driver",driver); 
		            url = p.getProperty("url",url); 
		            username = p.getProperty("username",username); 
		            password = p.getProperty("password",password);
		
		            Properties pr = new Properties(); 
		            pr.put("user",username); 
		            pr.put("password",password); 
		            pr.put("characterEncoding", "GB2312"); 
		            pr.put("useUnicode", "TRUE"); 
		
		            Class.forName(driver).newInstance(); 
            return DriverManager.getConnection(url,pr); 
        } catch(Exception se){ 
        	return null;
        } 
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
