package shandong.tenminute.fivein20;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mysql.jdbc.PreparedStatement;

/** 
  * @ClassName: Data2Db 
  * @Description: 数据库操作方法全部在这个类中 
  * @author songj@sdfcp.com
  * @date Feb 15, 2016 3:29:15 PM 
  *  
  */
public class Data2Db {

	/** 
	  * @Description: 在源库中查找最新的期号
	  * @author songj@sdfcp.com
	  * @date Feb 15, 2016 3:29:13 PM 
	  * @return 
	  */
	public String findMaxIssueIdFromSrcDb(){
		Connection srcConn = ConnectSrcDb.getSrcConnection();
		String issueId = null;
		PreparedStatement pstmt = null;
		String sql = "SELECT max(issue_id) FROM echart_shandong_5in20_t";
		try {
			pstmt = (PreparedStatement) srcConn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				issueId = rs.getString(1);
			}
			if(rs != null && !rs.isClosed()){
				rs.close();
			}
		} catch (SQLException e) {
			LogUtil.error(e.getMessage());
		}
		return issueId;
	}
	
	
	/** 
	  * @Description: 根据期号在源数据库中获取记录
	  * @author songj@sdfcp.com
	  * @date Feb 15, 2016 4:24:40 PM 
	  * @param issueId
	  * @return 
	  */
	/*
	public List<SrcDataBean> getAllRecord(){
		Connection srcConn = ConnectSrcDb.getSrcConnection();
		List<SrcDataBean> srcList = new ArrayList<SrcDataBean>();
		PreparedStatement pstmt = null;
		String sql = "SELECT issue_id,no_1,no_2,no_3,no_4,no_5 FROM echart_shandong_5in20_t where issue_id > '160807073'";
		try {
			pstmt = (PreparedStatement) srcConn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				SrcDataBean srcDataBean = new SrcDataBean();
				srcDataBean.setIssueId(rs.getString(1));
				srcDataBean.setNo1(rs.getInt(2));
				srcDataBean.setNo2(rs.getInt(3));
				srcDataBean.setNo3(rs.getInt(4));
				srcDataBean.setNo4(rs.getInt(5));
				srcDataBean.setNo5(rs.getInt(6));
				srcList.add(srcDataBean);
			}
			if(rs != null && !rs.isClosed()){
				rs.close();
			}
		} catch (SQLException e) {
			LogUtil.error(e.getMessage());
		}
		return srcList;
	}
	*/
	/** 
	  * @Description: 根据期号在源数据库中获取记录
	  * @author songj@sdfcp.com
	  * @date Feb 15, 2016 4:24:40 PM 
	  * @param issueId
	  * @return 
	  */
	public SrcDataBean getRecordByIssueId(String issueId){
		Connection srcConn = ConnectSrcDb.getSrcConnection();
		SrcDataBean srcDataBean = new SrcDataBean();
		PreparedStatement pstmt = null;
		String sql = "SELECT issue_id,no_1,no_2,no_3,no_4,no_5  FROM echart_shandong_5in20_t WHERE issue_id = '"+issueId+"'";
		try {
			pstmt = (PreparedStatement) srcConn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				srcDataBean.setIssueId(rs.getString(1));
				srcDataBean.setNo1(rs.getInt(2));
				srcDataBean.setNo2(rs.getInt(3));
				srcDataBean.setNo3(rs.getInt(4));
				srcDataBean.setNo4(rs.getInt(5));
				srcDataBean.setNo5(rs.getInt(6));
			}
			if(rs != null && !rs.isClosed()){
				rs.close();
			}
		} catch (SQLException e) {
			LogUtil.error(e.getMessage());
		}
		return srcDataBean;
	}
	/** 
	  * @Description: 向目标库中插入数据
	  * @author songj@sdfcp.com
	  * @date Feb 15, 2016 4:05:50 PM 
	  * @param issue_id
	  * @param one
	  * @param two
	  * @param three
	  * @return 
	  */
	@SuppressWarnings("finally")
	public boolean insertBaseData(SrcDataBean srcDataBean) {
		boolean flag = true;
		Connection conn = ConnectDesDb.getDesConnection();
		try{
			if(!haveDataInIssueId(srcDataBean.getIssueId(),conn)){
				insertData(srcDataBean,conn);
				LogUtil.info(srcDataBean.getIssueId()+"插入成功!");
			}
		}catch (SQLException e) {
			flag = false;
			LogUtil.error(srcDataBean.getIssueId()+e.getMessage());
		}finally{
			try {
				ConnectDesDb.closeDesConnection(conn);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LogUtil.error("关闭目标数据库连接失败！"+e.getCause());
			}finally{
				return flag;
			}
		}
	}
	
	/** 
	  * @Description:  
	  * @author songj
	  * @date Feb 15, 2016 4:00:04 PM 
	  * @param issue_id
	  * @param one
	  * @param two
	  * @param three
	  * @param conn
	  * @throws SQLException 
	  */
	private void insertData(SrcDataBean srcDataBean,Connection conn) throws SQLException{
		String sql = "INSERT INTO T_SD_5IN20_NUMBER (issue_number,no1,no2,no3,no4,no5,create_time,origin) values(?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			pstmt.setString(1, srcDataBean.getIssueId());
			pstmt.setInt(2, srcDataBean.getNo1());
			pstmt.setInt(3, srcDataBean.getNo2());
			pstmt.setInt(4, srcDataBean.getNo3());
			pstmt.setInt(5, srcDataBean.getNo4());
			pstmt.setInt(6, srcDataBean.getNo5());

			pstmt.setTimestamp(7, new java.sql.Timestamp(new Date().getTime()));
			pstmt.setInt(8, 1);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			LogUtil.error("插入基础数据表异常!"+e.getCause());
		}finally{
			if(!pstmt.isClosed() && pstmt != null){
				pstmt.close();
			}
		}
		
		
	}
	/** 
	  * @Description: 判断库中是否有该条记录
	  * @author songj@sdfcp.com
	  * @date Feb 15, 2016 4:39:24 PM 
	  * @param issueId
	  * @param conn
	  * @return
	  * @throws SQLException 
	  */
	private boolean haveDataInIssueId(String issueId,Connection conn) throws SQLException{
		boolean flag = false;
		int count = 0;
		String sql = "SELECT COUNT(*) FROM T_SD_5IN20_NUMBER WHERE ISSUE_NUMBER = '"+issueId+"'";
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
			}
		} catch (SQLException e) {
			LogUtil.error("haveDataInIssueId方法异常！"+e.getCause());
		}finally{
			if(rs != null && !rs.isClosed()){
    			rs.close(); 
			}
			if(pstmt!=null && !pstmt.isClosed()){
				pstmt.close(); 
    		}
		}
		return flag;
	}

}
