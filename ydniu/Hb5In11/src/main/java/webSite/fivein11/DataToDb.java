package webSite.fivein11;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.mysql.jdbc.PreparedStatement;


public class DataToDb{

	@SuppressWarnings("finally")
	public boolean haveDataInIssueId(List<String> noList) {
		boolean flag = false;
		int count = 0;
		String sql = "SELECT COUNT(*) FROM "+ App.srctable +" WHERE issue_number = '"+noList.get(0).toString()+"'";
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
		String sql = "INSERT INTO "+ App.srctable +" (issue_number,no1,no2,no3,no4,no5,three_sum,three_span,five_sum,five_span,big_count,odd_count,create_time,origin) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Integer no1 = Integer.parseInt(noList.get(1).toString());
		Integer no2 = Integer.parseInt(noList.get(2).toString());
		Integer no3 = Integer.parseInt(noList.get(3).toString());
		Integer no4 = Integer.parseInt(noList.get(4).toString());
		Integer no5 = Integer.parseInt(noList.get(5).toString());
		Integer threeSpan, fiveSpan, max3, min3, max5, min5, threeSum, fiveSum,oddNumber=0,bigCount=0;
		// 计算三码跨度
		max3 = no1 > no2 ? no1 : no2;
		max3 = max3 > no3 ? max3 : no3;
		min3 = (no1 < no2 ? no1 : no2) < no3 ? (no1 < no2 ? no1 : no2) : no3;
		threeSpan = max3 - min3;
		// 计算五码跨度
		max5 = no1 > no2 ? no1 : no2;
		max5 = max5 > no3 ? max5 : no3;
		max5 = max5 > no4 ? max5 : no4;
		max5 = max5 > no5 ? max5 : no5;
		min5 = no1 < no2 ? no1 : no2;
		min5 = min5 < no3 ? min5 : no3;
		min5 = min5 < no4 ? min5 : no4;
		min5 = min5 < no5 ? min5 : no5;
		fiveSpan = max5 - min5;
		// 计算三码和值
		threeSum = no1 + no2 + no3;
		// 计算五码和值
		fiveSum = no1 + no2 + no3 + no4 + no5;
		int five[] = {no1,no2,no3,no4,no5};
		for(int i = 0;i < five.length;i++){
			if(five[i]%2 != 0){
				oddNumber++;
			}
			if(five[i] > 6){
				bigCount++;
			}
		}
		pstmt = (PreparedStatement) conn.prepareStatement(sql);
		pstmt.setString(1, noList.get(0).toString());
		pstmt.setInt(2, no1);
		pstmt.setInt(3, no2);
		pstmt.setInt(4, no3);
		pstmt.setInt(5, no4);
		pstmt.setInt(6, no5);
		pstmt.setInt(7, threeSum);
		pstmt.setInt(8, threeSpan);
		pstmt.setInt(9, fiveSum);
		pstmt.setInt(10, fiveSpan);
		pstmt.setInt(11, bigCount);
		pstmt.setInt(12, oddNumber);
		pstmt.setTimestamp(13, new java.sql.Timestamp(new Date().getTime()));
		pstmt.setInt(14, 2);   //ydniu
		pstmt.executeUpdate();
	}
}
