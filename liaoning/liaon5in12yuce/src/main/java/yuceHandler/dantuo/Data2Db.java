package yuceHandler.dantuo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.mysql.jdbc.PreparedStatement;

import yuceHandler.App;
import yuceHandler.ConnectSrcDb;
import yuceHandler.Constants;
import yuceHandler.LogUtil;
import yuceHandler.SrcDataBean;

/** 
  * @ClassName: Data2Db 
  * @Description: 数据库操作方法全部在这个类中 
  * @author songj@sdfcp.com
  * @date Feb 15, 2016 3:29:15 PM 
  *  
  */
public class Data2Db {  //02478815484      6228480128008022377   何湘琪

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
			LogUtil.error(e.getMessage(),"danma");
		}
		return flag;
	}
	
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
		String sql = "SELECT max(issue_number) FROM "+App.srcNumberTbName;
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
			LogUtil.error(e.getMessage(),"danma");
		}
		return issueId;
	}
	
	/** 
	  * @Description: 在源库中查找最新的期号
	  * @author songj@sdfcp.com
	  * @date Feb 15, 2016 3:29:13 PM 
	  * @return 
	  */
	public SrcDataBean getRecordByIssueCode(String issueCode){
		Connection srcConn = ConnectSrcDb.getSrcConnection();
		PreparedStatement pstmt = null;
		SrcDataBean srcDataBean = null;
		String sql = "SELECT issue_number,no1,no2,no3,no4,no5 FROM "+ App.srcNumberTbName +" WHERE ISSUE_NUMBER = '"+issueCode+"'";
		try {
			pstmt = (PreparedStatement) srcConn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				srcDataBean = new SrcDataBean();
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
		}catch (SQLException e) {
			LogUtil.error(e.getMessage(),"danma");
		}
		return srcDataBean;
	}
	
	
	/** 
	  * @Description: 在源库中查找最新的期号
	  * @author songj@sdfcp.com
	  * @date Feb 15, 2016 3:29:13 PM 
	  * @return 
	  */
	public Fast3DanMa getYuceRecordByIssueCode(String issueCode){
		Connection srcConn = ConnectSrcDb.getSrcConnection();
		PreparedStatement pstmt = null;
		Fast3DanMa data = null;
		String sql = "SELECT ISSUE_NUMBER,DANMA_ONE,DANMA_TWO,CREATE_TIME FROM "+App.danMaTbName+" WHERE ISSUE_NUMBER = '"+issueCode+"'";
		try {
			pstmt = (PreparedStatement) srcConn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				data = new Fast3DanMa();
				data.setIssueNumber(rs.getString(1));
				data.setDanmaOne(rs.getString(2));
				data.setDanmaTwo(rs.getString(3));
				data.setCreateTime(rs.getDate(4));
			}
			if(rs != null && !rs.isClosed()){
				rs.close();
			}
		}catch (SQLException e) {
			LogUtil.error(e.getMessage(),"danma");
		}
		return data;
	}
	/** 
	  * @Description: 根据期号在源数据库中获取记录
	  * @author songj@sdfcp.com
	  * @date Feb 15, 2016 4:24:40 PM 
	  * @param issueId
	  * @return 
	  */
	public List<SrcDataBean> getLast8Record(String issueCode){
		Connection srcConn = ConnectSrcDb.getSrcConnection();
		List<SrcDataBean> srcList = new ArrayList<SrcDataBean>();
		PreparedStatement pstmt = null;
		String sql = "SELECT issue_number,no1,no2,no3,no4,no5 FROM "+App.srcNumberTbName+"  where issue_number like '%"+issueCode+"' order by issue_number desc limit 8 ";
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
			LogUtil.error(e.getMessage(),"danma");
		}
		return srcList;
	}
	
	/**通过最近20天数据统计出现次数
	 * @param noList
	 * @return
	 */
	public List<FiveInCount> getTimesForNumber( List<SrcDataBean> noList){
		List<FiveInCount> fast3CountList = new ArrayList<FiveInCount>();       //一维0 代表最近7期出现次数，1为最近14期出现次数，2为最近20天出现次数
		//
		int[] arr6 = {0,0,0,0,0,0,0,0,0,0,0,0};
		int[] arr7 = {0,0,0,0,0,0,0,0,0,0,0,0};
		int[] arr8 = {0,0,0,0,0,0,0,0,0,0,0,0};
		int i = 0;
		for(SrcDataBean no : noList){
			int[] numIntArr = {no.getNo1(),no.getNo2(),no.getNo3(),no.getNo4(),no.getNo5()};
			if(i < 6){
				for(int j = 0; j < numIntArr.length; j++){
					arr6[numIntArr[j]-1]++;
				}
			}
			if(i < 7){
				for(int j = 0; j < numIntArr.length; j++){
					arr7[numIntArr[j]-1]++;
				}
			}
			if(i < 8){
				for(int j = 0; j < numIntArr.length; j++){
					arr8[numIntArr[j]-1]++;
				}
			}
			i++;
		}
		for(int j = 0;j < 12;j++){
			FiveInCount fiveInCount = new FiveInCount();
			fiveInCount.setNumber(j+1);
			fiveInCount.setCount1(arr6[j]);
			fiveInCount.setCount2(arr8[j]);
			fiveInCount.setCount3(arr7[j]);
			fast3CountList.add(fiveInCount);
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
    public void execDanMa(String issueNumber) throws SQLException{
    	//获取即将预测的20数据
    	String issueCode = issueNumber.substring(issueNumber.length()-2,issueNumber.length());
    	List<SrcDataBean> noList = this.getLast8Record(issueCode);
    	//计算出现次数最多的数组
    	List<FiveInCount> fast3CountList = this.getTimesForNumber(noList);   	//计算胆码
    	Collections.sort(fast3CountList);
    	if(!hasRecordByIssueNumber(issueNumber,App.danMaTbName)){
    		insertData2Db(issueNumber,fast3CountList);
    	}
    	
    }
    
    private void insertData2Db(String issueNumber,List<FiveInCount> fast3CountList) throws SQLException{
    	Connection conn = ConnectSrcDb.getSrcConnection();
    	String sql = "insert into "+App.danMaTbName+" (issue_number,DANMA_ONE,DANMA_TWO,CREATE_TIME) values(?,?,?,?)";
		PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(sql);
		pstmt.setString(1, issueNumber);
		pstmt.setString(2, App.translate(fast3CountList.get(0).getNumber())+App.translate(fast3CountList.get(1).getNumber())+App.translate(fast3CountList.get(2).getNumber()));
		pstmt.setString(3, App.translate(fast3CountList.get(0).getNumber())+App.translate(fast3CountList.get(1).getNumber()));
		pstmt.setTimestamp(4, new java.sql.Timestamp(new Date().getTime()));
		pstmt.executeUpdate();
    }
    
    ////////////判断中出算法
    public void execDrawnPrize(String issueCode) throws SQLException{
    	//undo 获取中奖号码
    	SrcDataBean number = this.getRecordByIssueCode(issueCode);
    	//undo 判断中奖情况
    	//获取预测结果信息
    	Fast3DanMa fast3DanMa = this.getYuceRecordByIssueCode(issueCode);
    	if(fast3DanMa != null){
	    	//undo 通过算法判断中出结果
	    	String status = judgeDownStatus(number,fast3DanMa);
	    	updateDanMaStatus(status,number,issueCode);
    	}
    	//更新中奖状态
    }
    
    /**
     * @param number
     * @param fast3DanMa
     * @return 判断中出状态
     */
    private String judgeDownStatus(SrcDataBean number,Fast3DanMa fast3DanMa){
    	String status = null;
    	String numStr = App.translate(number.getNo1())+App.translate(number.getNo2())+App.translate(number.getNo3())+App.translate(number.getNo4())+App.translate(number.getNo5());
    	String danmaOne = fast3DanMa.getDanmaOne();
    	int oneDan = 0,twoDan = 0;
    	for(int i = 0;i < danmaOne.length();i++){
    		char temp = danmaOne.charAt(i);
    		if(numStr.indexOf(temp) >= 0 && i < 2){
    			oneDan++;
    			twoDan++;
    		}else if(numStr.indexOf(temp) >= 0){
    			oneDan++;
    		}
    	}
    	if(oneDan == 3){    //三胆全中
    		status = "1";
    	}else if(twoDan == 2){  //两胆全中
    		status = "2";
    	}else if(oneDan == 2){   //三胆中二
    		status = "3";
    	}else if(twoDan == 1){     //双胆中一
    		status = "4";
    	}else if(oneDan == 1){       //三胆中一
    		status = "5";
    	}else{
    		status = "6";              //没中
    	}
    	return status;
    }
    
	/**
	 * @param status
	 * @throws SQLException
	 * 更新胆码表状态内容
	 */
	private void updateDanMaStatus(String status,SrcDataBean srcDataBean,String issueNumber) throws SQLException{
		Connection conn = ConnectSrcDb.getSrcConnection();
		String sql = "UPDATE "+App.danMaTbName+" SET status = ?,drown_number=?  where issue_number = ?";
		PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(sql);
		pstmt.setString(1, status);
		pstmt.setString(2, App.translate(srcDataBean.getNo1())+App.translate(srcDataBean.getNo2())+App.translate(srcDataBean.getNo3())+App.translate(srcDataBean.getNo4())+App.translate(srcDataBean.getNo5()));
		pstmt.setString(3, issueNumber);
		pstmt.executeUpdate();
	}	
	
}
