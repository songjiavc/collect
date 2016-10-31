/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package jilin.tenminute.fivein11;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectDesDb {
	public static synchronized Connection getDesConnection() {
		return _getConnection();
	}

	private static Connection _getConnection() {
		try {
			String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://192.168.1.253:3306/echart3";
			String username = "";
			String password = "";
			Properties p = new Properties();
			InputStream is = ConnectSrcDb.class.getClassLoader().getResourceAsStream("db.properties");
			p.load(is);
			driver = p.getProperty("driver", driver);
			url = p.getProperty("des.url", url);
			username = p.getProperty("des.username", username);
			password = p.getProperty("des.password", password);
			Properties pr = new Properties();
			pr.put("user", username);
			pr.put("password", password);
			pr.put("characterEncoding", "GB2312");
			pr.put("useUnicode", "TRUE");
			Class.forName(driver).newInstance();
			return DriverManager.getConnection(url, pr);
		} catch (Exception se) {
			se.printStackTrace();
			LogUtil.error("获取目标数据库连接失败!");
		}
		return null;
	}

	public static void closeDesConnection(Connection conn) throws SQLException {
		if ((!(conn.isClosed())) && (conn != null))
			conn.close();
	}
}