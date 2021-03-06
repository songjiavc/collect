package hebei.fast3.analysis;
import java.io.*;
import java.util.*;
import java.sql.*;

public class ConnectSrcDb{

    private static Connection connection = null;
    /**
     * @Description: 给静态变量赋值
     * @author songj@sdfcp.com
     * @date Feb 15, 2016 1:52:27 PM
     * @return
     */
    public static synchronized Connection getSrcConnection(){
        try {
            if (connection == null|| connection.isClosed()){
                connection = _getConnection();
            }
        } catch (SQLException e) {
            LogUtil.error(e.getMessage());
        }
        return connection;
    }

    /**
     * @Description: 查找数据库属性，并创建连接
     * @author songj@sdfcp.com
     * @date Feb 15, 2016 1:52:58 PM
     * @return
     */
    private static  Connection _getConnection(){
        try{
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://192.168.1.253:3306/echart3";
            String username = "echart";
            String password = "echart";
            Properties p = new Properties();
            InputStream is = ConnectSrcDb.class.getClassLoader().getResourceAsStream("db.properties");
            p.load(is);
            driver = p.getProperty("driver",driver);
            url = p.getProperty("src.url",url);
            username = p.getProperty("src.username",username);
            password = p.getProperty("src.password",password);
            Properties pr = new Properties();
            pr.put("user",username);
            pr.put("password",password);
            pr.put("characterEncoding", "GB2312");
            pr.put("useUnicode", "TRUE");
            Class.forName(driver).newInstance();
        //    LogUtil.info("源数据库连接成功！");
            return DriverManager.getConnection(url,pr);
        }catch(Exception se){
            se.printStackTrace();
            LogUtil.error("获取源数据库连接失败!");
            return null;
        }
    }

} 
