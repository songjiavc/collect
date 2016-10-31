package webSite.Ln5In12;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.mysql.jdbc.PreparedStatement;


public class DataToDb{

	@SuppressWarnings("finally")
	public boolean haveDataInIssueId(List<String> noList) {
		System.out.println("haveDataInIssueId"+new Date().toString());
		boolean flag = false;
		int count = 0;
		String sql = "SELECT COUNT(*) FROM T_LN_5IN12_NUMBER WHERE issue_number = '"+noList.get(0).toString()+"'";
		Connection conn = ConnectDb.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()){
				count = rs.getInt(1);
			}
			if(count > 0){
				flag = true;
			}else{
				insertBaseData(noList,conn,pstmt);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try {
				ConnectDb.dbClose(conn, pstmt,rs);
			} catch (SQLException e) {
			}finally{
				return flag;
			}
		}
	}
	
	private void insertBaseData(List<String> noList,Connection conn,PreparedStatement pstmt)  throws SQLException{
		Integer a1 = Integer.parseInt(noList.get(1).toString());
		Integer a2 = Integer.parseInt(noList.get(2).toString());
		Integer a3 = Integer.parseInt(noList.get(3).toString());
		Integer a4 = Integer.parseInt(noList.get(4).toString());
		Integer a5 = Integer.parseInt(noList.get(5).toString());
		Integer threeSpan, fiveSpan, max3, min3, max5, min5, threeSum, fiveSum;
		// 计算三码跨度
		max3 = a1 > a2 ? a1 : a2;
		max3 = max3 > a3 ? max3 : a3;
		min3 = (a1 < a2 ? a1 : a2) < a3 ? (a1 < a2 ? a1 : a2) : a3;
		threeSpan = max3 - min3;
		// 计算五码跨度
		max5 = a1 > a2 ? a1 : a2;
		max5 = max5 > a3 ? max5 : a3;
		max5 = max5 > a4 ? max5 : a4;
		max5 = max5 > a5 ? max5 : a5;
		min5 = a1 < a2 ? a1 : a2;
		min5 = min5 < a3 ? min5 : a3;
		min5 = min5 < a4 ? min5 : a4;
		min5 = min5 < a5 ? min5 : a5;
		fiveSpan = max5 - min5;
		// 计算三码和值
		threeSum = a1 + a2 + a3;
		// 计算五码和值
		fiveSum = a1 + a2 + a3 + a4 + a5;
		String sql = "insert into T_LN_5IN12_NUMBER (issue_number,no1,no2,no3,no4,no5,three_span,five_span,three_sum,five_sum,origin,create_time) values(?,?,?,?,?,?,?,?,?,?,?,?)";
		pstmt = (PreparedStatement) conn.prepareStatement(sql);
		pstmt.setString(1, noList.get(0));
		pstmt.setInt(2, Integer.parseInt(noList.get(1)));
		pstmt.setInt(3, Integer.parseInt(noList.get(2)));
		pstmt.setInt(4, Integer.parseInt(noList.get(3)));
		pstmt.setInt(5, Integer.parseInt(noList.get(4)));
		pstmt.setInt(6, Integer.parseInt(noList.get(5)));
		pstmt.setInt(7, threeSpan);
		pstmt.setInt(8, fiveSpan);
		pstmt.setInt(9, threeSum);
		pstmt.setInt(10, fiveSum);
		pstmt.setString(11, "sino");
		pstmt.setTimestamp(12, new java.sql.Timestamp(new Date().getTime()));
		pstmt.executeUpdate();
	}
}
