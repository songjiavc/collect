/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package jilin.tenminute.fivein11;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectSrcDb {
	private static Connection connection = null;

	public static synchronized Connection getSrcConnection() {
		try {
			if ((connection == null) || (connection.isClosed()))
				connection = _getConnection();
		} catch (SQLException e) {
			LogUtil.error(e.getMessage());
		}
		return connection;
	}

	private static Connection _getConnection() {
		try {
			String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://192.168.1.253:3306/echart3";
			String username = "echart";
			String password = "echart";
			Properties p = new Properties();
			InputStream is = ConnectSrcDb.class.getClassLoader().getResourceAsStream("db.properties");
			p.load(is);
			driver = p.getProperty("driver", driver);
			url = p.getProperty("src.url", url);
			username = p.getProperty("src.username", username);
			password = p.getProperty("src.password", password);
			Properties pr = new Properties();
			pr.put("user", username);
			pr.put("password", password);
			pr.put("characterEncoding", "GB2312");
			pr.put("useUnicode", "TRUE");
			Class.forName(driver).newInstance();
			//LogUtil.info("源数据库连接成功！");
			return DriverManager.getConnection(url, pr);
		} catch (Exception se) {
			se.printStackTrace();
			LogUtil.error("获取源数据库连接失败!");
		}
		return null;
	}
}