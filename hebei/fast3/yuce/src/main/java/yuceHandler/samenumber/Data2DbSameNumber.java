package yuceHandler.samenumber;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.mysql.jdbc.PreparedStatement;

import yuceHandler.App;
import yuceHandler.ConnectSrcDb;
import yuceHandler.DateUtil;
import yuceHandler.LogUtil;
import yuceHandler.SrcDataBean;

/** 
  * @ClassName: Data2Db 
  * @Description: 数据库操作方法全部在这个类中 
  * @author songj@sdfcp.com
  * @date Feb 15, 2016 3:29:15 PM 
  *  
  */
public class Data2DbSameNumber {  //02478815484      6228480128008022377   何湘琪

	/** 
	  * @Description: 在源库中查找最新的期号
	  * @author songj@sdfcp.com
	  * @date Feb 15, 2016 3:29:13 PM 
	  * @return 
	  */
	public boolean hasRecordByIssueNumber(String issueNumber,String tbName){
		Connection srcConn = ConnectSrcDb.getSrcConnection();
		boolean flag = false;
		int count = 0; 
		PreparedStatement pstmt = null;
		String sql = "SELECT count(*) count FROM "+tbName + " where issue_number = '"+issueNumber+"'";
		try {
			pstmt = (PreparedStatement) srcConn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				count = rs.getInt(1);
			}
			if(count > 0){
				flag = true;
			}
			if(rs != null && !rs.isClosed()){
				rs.close();
			}
		} catch (SQLException e) {
			LogUtil.error(e.getMessage(),"sima");
		}
		return flag;
	}
	
	/** 
	  * @Description: 在源库中查找最新的期号
	  * @author songjia
	  * @date Feb 15, 2016 3:29:13 PM 
	  * @return 
	  */
	public SrcDataBean getRecordByIssueNumber(String issueNumber){
		Connection srcConn = ConnectSrcDb.getSrcConnection();
		PreparedStatement pstmt = null;
		SrcDataBean srcDataBean = null;
		String sql = "SELECT issue_number,no1,no2,no3 FROM "+App.srcNumberTbName+" WHERE ISSUE_NUMBER = '"+issueNumber+"'";
		try {
			pstmt = (PreparedStatement) srcConn.prepareStatement(sql);
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
		}catch (SQLException e) {
			LogUtil.error(e.getMessage(),"same/");
		}
		return srcDataBean;
	}
	/** 
	  * @Description: 根据期号在源数据库中获取记录
	  * @author songj@sdfcp.com
	  * @date Feb 15, 2016 4:24:40 PM 
	  * @param issueId
	  * @return 
	  */
	private List<String> getSameNumIssue(SrcDataBean  srcDataBean){
		Connection srcConn = ConnectSrcDb.getSrcConnection();
		List<String> srcList = new ArrayList<String>();
		PreparedStatement pstmt = null;
		//获取相同号码下一期期号列表期号列表
		String getIssueList = "SELECT ISSUE_NUMBER FROM " + App.srcNumberTbName + " WHERE no1=? and no2=? and no3=? ORDER BY ISSUE_NUMBER DESC LIMIT 10 ";
		try {
			pstmt = (PreparedStatement) srcConn.prepareStatement(getIssueList);
			pstmt.setInt(1,srcDataBean.getNo1());
			pstmt.setInt(2,srcDataBean.getNo2());
			pstmt.setInt(3,srcDataBean.getNo3());
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				String srcBean = rs.getString(1);
				srcList.add(srcBean);
			}
			if(rs != null && !rs.isClosed()){
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			LogUtil.error(e.getMessage(),"same/");
		}
		return srcList;
	}
	
	/**
	 * @param nextIssue
	 * @return 
	 */
	private List<SrcDataBean> getNextIssueRecordList(List<String> nextIssue){
		List<SrcDataBean> srcList = new ArrayList<SrcDataBean>();
		srcList = new ArrayList<SrcDataBean>();
		Connection srcConn = ConnectSrcDb.getSrcConnection();
		PreparedStatement pstmt = null;
		//获取相同号码下一期期号列表期号列表
		String getIssueList = "SELECT ISSUE_NUMBER,NO1,NO2,NO3 FROM  " + App.srcNumberTbName + " WHERE issue_number IN ("+DateUtil.listToString(nextIssue)+") ORDER BY ISSUE_NUMBER DESC";
		try {
			pstmt = (PreparedStatement) srcConn.prepareStatement(getIssueList);
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
			e.printStackTrace();
			LogUtil.error(e.getMessage(),"same/");
		}
		return srcList;
	}
	

	
	/**通过最近20天数据统计出现次数
	 * @param noList
	 * @return
	 */
	public List<Fast3SameNumber> getTimesForNumber( List<SrcDataBean> noList){
		List<Fast3SameNumber> fast3CountList = new ArrayList<Fast3SameNumber>();       //一维0 代表最近7期出现次数，1为最近14期出现次数，2为最近20天出现次数
		int[] arr10 = {0,0,0,0,0,0};
		for(SrcDataBean no : noList){
			int[] temp = {no.getNo1(),no.getNo2(),no.getNo3()};
			Integer[] numIntArr = getUniqueArr(temp);
			for(int j = 0; j < numIntArr.length; j++){
				arr10[numIntArr[j]-1]++;
			}
			}
		for(int j = 0;j < 6;j++){
			Fast3SameNumber fast3Count = new Fast3SameNumber();
			fast3Count.setNumber(j+1);
			fast3Count.setCount10(arr10[j]);
			fast3CountList.add(fast3Count);
		}
		return fast3CountList;
	}
  
	
	//去除数组中重复的记录  
    private Integer[] getUniqueArr(int[] a) {  
        // array_unique  
        List<Integer> list = new LinkedList<Integer>();  
        for(int i = 0; i < a.length; i++) {  
            if(!list.contains(a[i])) {  
                list.add(a[i]);  
            }  
        }  
        return (Integer[])list.toArray(new Integer[list.size()]);  
    }  
    
    /** 代码入库主方法
     * @param issueCode
     */
    public void execSameNum(String issueNumber) throws SQLException{
    	//根据期号获取开奖号码
    	SrcDataBean srcDataBean = this.getRecordByIssueNumber(issueNumber);
    	//获取即将预测的20数据
    	List<String> issueList = this.getSameNumIssue(srcDataBean);
		if(issueList != null &&  issueList.size()>0){
			//获取下一期期号
			List<String> nextIssueList = new ArrayList<String>();
			for(String issue : issueList){
				String nextIssue = App.getNextIssueByCurrentIssue(issue);
				nextIssueList.add(nextIssue);
			}
			List<SrcDataBean> noList = getNextIssueRecordList(nextIssueList);
	    	//计算出现次数最多的数组
	    	List<Fast3SameNumber> fast3CountList = this.getTimesForNumber(noList);   	//计算胆码
	    	//因为下个期号中会少本期查询的结果，而插入的时候要多给一条记录才能正确
	    	Collections.sort(fast3CountList);
	    	SrcDataBean param = new SrcDataBean();
	    	param.setIssueId(nextIssueList.get(0));
	    	noList.add(0,param);
	    	//插入新纪录时需要判断
	    	if(issueList.size() == 10){
	    		insertData2Db(issueList,noList,fast3CountList); 
	    	}
		}
    }
    
    /**
     * @param issueNumber
     * @param fast3CountList
     * @throws SQLException
     * 四码预测插入预测计划方法
     */
    private void insertData2Db(List<String> issueList,List<SrcDataBean> noList,List<Fast3SameNumber> fast3CountList) throws SQLException{
    	Connection conn = ConnectSrcDb.getSrcConnection();
    	//插入前删除表中所有记录
    	String truncateTb = "TRUNCATE TABLE "+App.sameNumTbName;
    	String sql = "insert into "+App.sameNumTbName+" (CURRENT_ISSUE,LOTTORY_NUMBER,NEXT_ISSUE,NEXT_LOTTORY_NUMBER,CREATE_TIME) values(?,?,?,?,?)";
    	//SrcDataBean currentRecord = noList.get(1);
    	SrcDataBean currentRecord = this.getRecordByIssueNumber(issueList.get(0));
    	conn.setAutoCommit(false);
		PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(sql);
		pstmt.addBatch(truncateTb);
		for(int i = noList.size()-1;i>=0;i--){
			if(i == 0){
				pstmt.setString(4,Integer.toString(fast3CountList.get(0).getNumber())+Integer.toString(fast3CountList.get(1).getNumber())+Integer.toString(fast3CountList.get(4).getNumber())+Integer.toString(fast3CountList.get(5).getNumber()));
			}else{
				pstmt.setString(4, Integer.toString(noList.get(i).getNo1())+Integer.toString(noList.get(i).getNo2())+Integer.toString(noList.get(i).getNo3()));
			}
			pstmt.setString(1,issueList.get(i));
			pstmt.setString(2,Integer.toString(currentRecord.getNo1())+Integer.toString(currentRecord.getNo2())+Integer.toString(currentRecord.getNo3()));
			pstmt.setString(3,noList.get(i).getIssueId());
			pstmt.setTimestamp(5, new java.sql.Timestamp(new Date().getTime()));
			pstmt.addBatch();
		}
		pstmt.executeBatch();
		conn.commit();
		pstmt.clearBatch();
		conn.setAutoCommit(true);
    }
	
}
