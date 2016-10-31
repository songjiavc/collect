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
		String sql = "SELECT max(ISSUE_NUMBER) FROM T_SD_5IN20_NUMBER";
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
		String sql = "SELECT ISSUE_NUMBER,NO1,NO2,NO3,NO4,NO5  FROM T_SD_5IN20_NUMBER WHERE ISSUE_NUMBER = '"+issueId+"'";
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
			//  遗漏统计暂时不做处理，等数据处理完毕在统计
			if(!haveMissDataInIssueId(srcDataBean.getIssueId(),conn)){
				//获取执行语句
				List<String> updateSqlList = this.updateSqlList(srcDataBean);
				batchUpdateMiss(updateSqlList,conn);
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
		String sql = "SELECT COUNT(*) FROM T_SD_5IN20_MISSANALYSIS WHERE ISSUE_NUMBER = '"+issueId+"'";
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
	
	private void batchUpdateMiss(List<String> updateSqlList,Connection conn) throws SQLException{
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
				  for(String updateSql : updateSqlList){
					  //System.out.println(updateSql);
					  stmt.addBatch(updateSql);
				  }
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
	
	
	/*组织update
	 * 
	 */
	private List<String> updateSqlList(SrcDataBean srcDataBean){
		List<String> rtnList = new ArrayList<String>();
		//首先添加任二相关内容（任二、任二三码复试\四码、五码）
		rtnList.add("UPDATE T_SD_5IN20_MISSANALYSIS SET ISSUE_NUMBER = " + srcDataBean.getIssueId() + ",CURRENT_MISS = CURRENT_MISS+1");
		rtnList.addAll(AnalysisMissUtil.updateGroupMiss(srcDataBean, 2));
		rtnList.addAll(AnalysisMissUtil.updateGroupMiss(srcDataBean, 3));
		rtnList.addAll(AnalysisMissUtil.updateGroupMiss(srcDataBean, 4));
		rtnList.addAll(AnalysisMissUtil.updateGroupMiss(srcDataBean, 5));
		rtnList.add(AnalysisMissUtil.updateGreatFiveGroupMiss(srcDataBean, 6));
		rtnList.add(AnalysisMissUtil.updateGreatFiveGroupMiss(srcDataBean, 7));
		rtnList.add(AnalysisMissUtil.updateGreatFiveGroupMiss(srcDataBean, 8));
		rtnList.add(AnalysisMissUtil.updateGreatFiveGroupMiss(srcDataBean, 9));
		rtnList.add(AnalysisMissUtil.updateGreatFiveGroupMiss(srcDataBean, 10));
		rtnList.add(AnalysisMissUtil.updateBeforeRen2GroupMiss(srcDataBean));
		rtnList.add(AnalysisMissUtil.updateBeforeRen3GroupMiss(srcDataBean));
		rtnList.add(AnalysisMissUtil.updateBeforeRen4GroupMiss(srcDataBean));
		rtnList.add(AnalysisMissUtil.updateDirect1GroupMiss(srcDataBean));
		rtnList.add(AnalysisMissUtil.updateDirect2GroupMiss(srcDataBean));
		rtnList.add(AnalysisMissUtil.updateDirect3GroupMiss(srcDataBean));
		rtnList.add("UPDATE T_SD_5IN20_MISSANALYSIS SET MAX_MISS = CURRENT_MISS WHERE CURRENT_MISS > MAX_MISS AND CURRENT_MISS <> 0;");
		//遗漏统计完毕像查询表中初始化数据
		//truncate 掉查询表内容
		rtnList.add("TRUNCATE TABLE T_SD_5IN20_MISSANALYSIS_SELECT ");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_REN2 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_REN3 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_REN4 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_REN5 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_REN6 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_REN7 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_REN8 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_REN9 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_REN10 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_SHUN1 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_SHUN2 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_SHUN3 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_WEI2 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_WEI3 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_WEI4 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_REN2_FUSHI3 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_REN2_FUSHI4 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_REN2_FUSHI5 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_REN3_FUSHI4 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_REN3_FUSHI5 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_REN3_FUSHI6 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_REN4_FUSHI5 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_REN4_FUSHI6 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");
		rtnList.add("INSERT INTO T_SD_5IN20_MISSANALYSIS_SELECT SELECT ISSUE_NUMBER,GROUP_NUMBER,CURRENT_MISS,MAX_MISS,TYPE FROM T_SD_5IN20_MISSANALYSIS WHERE TYPE = " + Constants.TYPE_REN4_FUSHI7 + " ORDER BY  CURRENT_MISS DESC LIMIT 20");

		return rtnList;
	}

}
