package hebei.tenminute.fivein11;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
	public String findMaxIssueIdFromDesMissTable(){
		Connection srcConn = ConnectDesDb.getDesConnection();
		String issueId = null;
		PreparedStatement pstmt = null;
		String sql = "SELECT max(issue_number) FROM T_HEBEI_5IN11_MISSANALYSIS";
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
	public SrcDataBean getRecordByIssueId(String issueId){
		Connection srcConn = ConnectSrcDb.getSrcConnection();
		SrcDataBean srcDataBean = new SrcDataBean();
		PreparedStatement pstmt = null;
		String sql = "SELECT issue_number,no1,no2,no3,no4,no5  FROM T_HEBEI_5IN11_NUMBER WHERE issue_number = '"+issueId+"'";
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
		int oneInt = srcDataBean.getNo1();
		int twoInt = srcDataBean.getNo2();
		int threeInt = srcDataBean.getNo3();
		int fourInt = srcDataBean.getNo4();
		int fiveInt = srcDataBean.getNo5();
		int threeSpan=0,threeSum=0,fiveSpan,fiveSum,oddNumber=0,bigCount=0;
		threeSum = oneInt + twoInt+threeInt;
		fiveSum = oneInt + twoInt + threeInt + fourInt + fiveInt;
		int three[] = {oneInt,twoInt,threeInt};
		int five[] = {oneInt,twoInt,threeInt,fourInt,fiveInt};
		for(int i = 0;i < five.length;i++){
			if(five[i]%2 != 0){
				oddNumber++;
			}
			if(five[i] > 6){
				bigCount++;
			}
		}
		Arrays.sort(three);
		threeSpan = three[2] - three[0];
		Arrays.sort(five);
		fiveSpan = five[4] - five[0];
		srcDataBean.setOddNum(oddNumber);
		srcDataBean.setBigCount(bigCount);
		srcDataBean.setThreeSpan(threeSpan);
		srcDataBean.setFiveSpan(fiveSpan);
		srcDataBean.setThreeSum(threeSum);
		srcDataBean.setFiveSum(fiveSum);
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
			if(!haveMissDataInIssueId(srcDataBean.getIssueId(),conn)){
				batchUpdateMiss(srcDataBean,conn);
				LogUtil.info(srcDataBean.getIssueId()+"遗漏分析完成!");
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
	
	public void insertTestGroupData(String issueNumber,String groupNumber,int currentMiss,int maxMiss,int type) throws SQLException{
		Connection conn = ConnectDesDb.getDesConnection();
		String sql = "insert into T_HEBEI_5IN11_MISSANALYSIS (ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE) values(?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			pstmt.setString(1, null);
			pstmt.setString(2, groupNumber);
			pstmt.setInt(3,currentMiss );
			pstmt.setInt(4, maxMiss);
			pstmt.setInt(5, type);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			LogUtil.error("插入基础数据表异常!"+e.getCause());
		}finally{
			ConnectDesDb.closeDesConnection(conn);
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
	private boolean haveMissDataInIssueId(String issueId,Connection conn) throws SQLException {
		boolean flag = false;
		int count = 0;
		String sql = "SELECT COUNT(*) FROM T_HEBEI_5IN11_MISSANALYSIS WHERE ISSUE_NUMBER = '"+issueId+"'";
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
			LogUtil.error("查询分析表是否存在数据异常!"+e.getCause());
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
	/** 
	  * @Description: 更新遗漏值
	  * @author songj@sdfcp.com
	 * @throws SQLException 
	  * @date Feb 17, 2016 11:21:29 AM  
	  */
	private void batchUpdateMiss(SrcDataBean srcDataBean,Connection conn) throws SQLException{
		PreparedStatement stmt = null;
		try{
			DatabaseMetaData dbmd= conn.getMetaData();
			boolean a = dbmd.supportsBatchUpdates();
			if(a){
				  //保存当前自动提交模式
				  boolean booleanautoCommit = conn.getAutoCommit();
				  //关闭自动提交
				  conn.setAutoCommit(false);
				  stmt = (PreparedStatement) conn.prepareStatement("");
				  //使用Statement同时收集多条sql语句
				  stmt.addBatch("UPDATE T_HEBEI_5IN11_MISSANALYSIS SET ISSUE_NUMBER = "+srcDataBean.getIssueId()+","
				  		+ "CURRENT_MISS = CURRENT_MISS+1,OPTIONAL_COMPOUND = OPTIONAL_COMPOUND+1,"
				  		+ "TWOCODE_COMPOUND=TWOCODE_COMPOUND+1,THREECODE_COMPOUND=THREECODE_COMPOUND+1;");

				  List<String> sqlList = null;
				  sqlList = AnalysisMissUtil.updateGroupMiss(srcDataBean,2);
				  sqlList.addAll(AnalysisMissUtil.updateGroupMiss(srcDataBean,3));
				  sqlList.addAll(AnalysisMissUtil.updateGroupMiss(srcDataBean,4));
				  sqlList.addAll(AnalysisMissUtil.updateGroupMiss(srcDataBean,5));
				  for(String sql : sqlList){
					  stmt.addBatch(sql);  //任六
				  }
				  stmt.addBatch(AnalysisMissUtil.updateGreatFiveGroupMiss(srcDataBean,6));  //任六
				  stmt.addBatch(AnalysisMissUtil.updateGreatFiveGroupMiss(srcDataBean,7));  //任七
				  stmt.addBatch(AnalysisMissUtil.updateGreatFiveGroupMiss(srcDataBean,8));  //任八
				  String[] temp = AnalysisMissUtil.updateBeforeRen2GroupMiss(srcDataBean);
				  stmt.addBatch(temp[0]);   //前二组选
				  stmt.addBatch(temp[1]);   //前二组选三码复式
				  temp = AnalysisMissUtil.updateBeforeRen3GroupMiss(srcDataBean);
				  stmt.addBatch(temp[0]);   //前三组选
				  stmt.addBatch(temp[1]);   //前三组选四码复式
				  stmt.addBatch(AnalysisMissUtil.updateDirectBeforeRen2GroupMiss(srcDataBean)); //前二直选
				  stmt.addBatch(AnalysisMissUtil.updateDirectBeforeRen3GroupMiss(srcDataBean)); //前三直选
				  //同时提交所有的sql语句
				  stmt.addBatch("UPDATE T_HEBEI_5IN11_MISSANALYSIS SET MAX_MISS = CURRENT_MISS WHERE CURRENT_MISS > MAX_MISS AND CURRENT_MISS <> 0;");
				  stmt.addBatch("UPDATE T_HEBEI_5IN11_MISSANALYSIS SET TWOCODE_COMPOUND_MAXMISS = TWOCODE_COMPOUND WHERE TWOCODE_COMPOUND > TWOCODE_COMPOUND_MAXMISS AND TWOCODE_COMPOUND <> 0;");
				  stmt.addBatch("UPDATE T_HEBEI_5IN11_MISSANALYSIS SET THREECODE_COMPOUND_MAXMISS = THREECODE_COMPOUND WHERE THREECODE_COMPOUND > THREECODE_COMPOUND_MAXMISS AND THREECODE_COMPOUND <> 0;");
				  stmt.addBatch("UPDATE T_HEBEI_5IN11_MISSANALYSIS SET OPTIONAL_COMPOUND_MAXMISS = OPTIONAL_COMPOUND WHERE OPTIONAL_COMPOUND > OPTIONAL_COMPOUND_MAXMISS AND OPTIONAL_COMPOUND <> 0;");
				  stmt.executeBatch();
				  //提交修改
				  conn.commit();
				  conn.setAutoCommit(booleanautoCommit);
			}
		}catch(SQLException sqlEx){
			LogUtil.error("batchUpdateMiss执行异常！"+sqlEx.getCause());
		}finally{
			if(stmt!=null && !stmt.isClosed()){
				stmt.close(); 
    		}
		}
		
	}
	

}
