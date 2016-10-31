package webSite.fivein20;

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
				LogUtil.info(noList.get(0).toString()+"insert success!");
			}
		}catch(SQLException e){
			LogUtil.error(noList.get(0).toString()+":"+e.getErrorCode()+"insert failue！");
		}finally{
			try {
				ConnectDb.dbClose(conn, pstmt,rs);
			} catch (SQLException e) {
				LogUtil.error(noList.get(0).toString()+":"+e.getErrorCode()+"close dbline error！");
			}finally{
				return flag;
			}
		}
	}
	
	private void insertBaseData(List<String> noList,Connection conn,PreparedStatement pstmt)  throws SQLException{
		String sql = "INSERT INTO "+ App.srctable +" (issue_number,no1,no2,no3,no4,no5,create_time,origin) values(?,?,?,?,?,?,?,?)";
		Integer no1 = Integer.parseInt(noList.get(1).toString());
		Integer no2 = Integer.parseInt(noList.get(2).toString());
		Integer no3 = Integer.parseInt(noList.get(3).toString());
		Integer no4 = Integer.parseInt(noList.get(4).toString());
		Integer no5 = Integer.parseInt(noList.get(5).toString());
		
		pstmt = (PreparedStatement) conn.prepareStatement(sql);
		pstmt.setString(1, noList.get(0).toString());
		pstmt.setInt(2, no1);
		pstmt.setInt(3, no2);
		pstmt.setInt(4, no3);
		pstmt.setInt(5, no4);
		pstmt.setInt(6, no5);
		pstmt.setTimestamp(7, new java.sql.Timestamp(new Date().getTime()));
		pstmt.setInt(8, 3);   //lecai
		pstmt.executeUpdate();
	}
}
