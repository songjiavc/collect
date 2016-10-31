package hebei.fast3.analysis;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.StringUtils;

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
		String sql = "SELECT MAX(ISSUE_NUMBER) FROM " + App.descMissTbName;
		try {
			pstmt = (PreparedStatement) srcConn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				issueId = rs.getString(1);
			}
			if(!StringUtils.isNullOrEmpty(issueId)){
				if(!judgeIssueNumber(issueId)){
					issueId = null;
				}
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
	 * @Description: 在目标库中查找最新的期号
	 * @author songj@sdfcp.com
	 * @date Feb 15, 2016 3:29:13 PM
	 * @return
	 */
	public String findMaxIssueIdFromDescDb(){
		Connection srcConn = ConnectDesDb.getDesConnection();
		String issueId = null;
		PreparedStatement pstmt = null;
		String sql = "SELECT max(issue_number) FROM "+App.descNumberTbName;
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
	 * 期号校验器  必须为 160724072
	 */
	public boolean judgeIssueNumber(String issueNumber){
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(issueNumber);
		if( !isNum.matches() )
		{
			return false;
		}
		return true;
	}


	private boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
	/**
	 * @Description: 根据期号在源数据库中获取记录
	 * @author songj@sdfcp.com
	 * @date Feb 15, 2016 4:24:40 PM
	 * @param issueId
	 * @return
	 */
	public List<SrcDataBean> getAllRecord(){
		Connection srcConn = ConnectSrcDb.getSrcConnection();
		List<SrcDataBean> srcList = new ArrayList<SrcDataBean>();
		PreparedStatement pstmt = null;
		String sql = "SELECT issue_number,no1,no2,no3 FROM "+App.srcNumberTbName+" WHERE issue_number >= '160601001'";
		try {
			pstmt = (PreparedStatement) srcConn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				SrcDataBean srcDataBean = new SrcDataBean();
				srcDataBean.setIssueId(rs.getString(1));
				srcDataBean.setNo1(rs.getInt(2));
				srcDataBean.setNo2(rs.getInt(3));
				srcDataBean.setNo3(rs.getInt(4));
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

	/**
	 * @Description: 根据期号在源数据库中获取记录
	 * @author songj@sdfcp.com
	 * @date Feb 15, 2016 4:24:40 PM
	 * @param issueId
	 * @return
	 */
	public SrcDataBean getRecordByIssueId(String issueId){
		Connection srcConn = ConnectSrcDb.getSrcConnection();
		SrcDataBean srcDataBean = null;
		PreparedStatement pstmt = null;
		String sql = "SELECT issue_number,no1,no2,no3 FROM "+App.srcNumberTbName+" where issue_number = ?";
		try {
			pstmt = (PreparedStatement) srcConn.prepareStatement(sql);
			pstmt.setString(1, issueId);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				srcDataBean = new SrcDataBean();
				srcDataBean.setIssueId(rs.getString(1));
				srcDataBean.setNo1(rs.getInt(2));
				srcDataBean.setNo2(rs.getInt(3));
				srcDataBean.setNo3(rs.getInt(4));
			}
			if(rs != null && !rs.isClosed()){
				rs.close();
			}
		} catch (SQLException e) {
			LogUtil.error(e.getMessage());
		}
		return srcDataBean;
	}

	private SrcDataBean caluExtentInfo(SrcDataBean srcDataBean){
		int oneInt = srcDataBean.getNo1();
		int twoInt = srcDataBean.getNo2();
		int threeInt = srcDataBean.getNo3();
		int threeSpan=0,threeSum=0,bigNumber = 0,smallNumber=0,oddNumber=0,evenNumber =0,noStatus = 0,bigCount=0,smallCount=0;
		threeSum = oneInt + twoInt+threeInt;
		int b[] = {oneInt,twoInt,threeInt};
		for(int i=0;i<b.length;i++){
			if(b[i] <= 3){
				smallCount++;
			}else{
				bigCount++;
			}
		}
		Arrays.sort(b);
		bigNumber  = b[b.length-1];
		smallNumber =  b[0];
		threeSpan = bigNumber - smallNumber;
		if(oneInt%2 == 0){
			evenNumber++;
		}else{
			oddNumber++;
		}
		if(twoInt%2 == 0){
			evenNumber++;
		}else{
			oddNumber++;
		}
		if(threeInt%2 == 0){
			evenNumber++;
		}else{
			oddNumber++;
		}
		if(oneInt != twoInt && twoInt != threeInt && oneInt != threeInt){
			noStatus = 3;
		}else if(oneInt == twoInt && twoInt == threeInt && oneInt == threeInt){
			noStatus = 1;
		}else{
			noStatus = 2;
		}
		srcDataBean.setBigNum(bigCount);
		srcDataBean.setEvenNum(evenNumber);
		srcDataBean.setNoStatus(noStatus);
		srcDataBean.setOddNum(oddNumber);
		srcDataBean.setSmallNum(smallCount);
		srcDataBean.setThreeSpan(threeSpan);
		srcDataBean.setThreeSum(threeSum);
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
		String sql = "SELECT COUNT(*) FROM " +App.descMissTbName+ " WHERE ISSUE_NUMBER = '"+issueId+"'";
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
				stmt.addBatch("UPDATE " +App.descMissTbName+ " SET ISSUE_NUMBER = "+srcDataBean.getIssueId()+",CURRENT_MISS = CURRENT_MISS+1;");
				stmt.addBatch(AnalysisMissUtil.getAllGroupMiss(srcDataBean));
				stmt.addBatch(AnalysisMissUtil.getAllNumMiss(srcDataBean));
				stmt.addBatch(AnalysisMissUtil.getFourNumGroupMiss(srcDataBean));
				stmt.addBatch(AnalysisMissUtil.getOddOrEvenMiss(srcDataBean));
				stmt.addBatch(AnalysisMissUtil.getSmallOrBigMiss(srcDataBean));
				stmt.addBatch(AnalysisMissUtil.getTwoSameGroupMiss(srcDataBean));
				stmt.addBatch(AnalysisMissUtil.getZeroOneTwo(srcDataBean));
				stmt.addBatch(AnalysisMissUtil.getLottoryNum012(srcDataBean));
				//同时提交所有的sql语句
				stmt.addBatch("UPDATE " +App.descMissTbName+ " SET MAX_MISS = CURRENT_MISS WHERE CURRENT_MISS > MAX_MISS AND CURRENT_MISS <> 0;");
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
