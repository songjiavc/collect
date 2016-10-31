package yuceHandler.sima;

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
import yuceHandler.DateUtil;
import yuceHandler.LogUtil;
import yuceHandler.SrcDataBean;
import yuceHandler.dantuo.Fast3Count;

/** 
  * @ClassName: Data2Db 
  * @Description: 数据库操作方法全部在这个类中 
  * @author songj@sdfcp.com
  * @date Feb 15, 2016 3:29:15 PM 
  *  
  */
public class Data2DbSima {  //02478815484      6228480128008022377   何湘琪

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
	 * @param issueNumber
	 * @return  根据期数获取期号
	 */
	private String getIssueCodeByIssueNumber(String issueNumber){
		if(issueNumber!=null && issueNumber.length() > 2){
			return issueNumber.substring(issueNumber.length()-2,issueNumber.length());
		}else{
			LogUtil.error("获取期号错误！","sima");
			return null;
		}
	}
	
	public void execDrawnSima(String issueNumber) throws SQLException{
		// 获取开奖信息
		SrcDataBean srcDataBean = this.getRecordByIssueNumber(issueNumber);
		// 获取预测计划
		Fast3SiMa fast3SiMa =  getSiMaYuceRecordByIssueCode(issueNumber);
		// 判断中出情况
		if(fast3SiMa != null){
			if(!issueNumber.equals(fast3SiMa.getDrownIssueNumber())){   //避免反复执行该段代码，有时重启程序时可能有的问题
				String status = judgeDownStatus(srcDataBean,fast3SiMa);
				//将状态结果入库，并判断是否需要生成新的计划
				if(status == "1"){   //预测结果中出
					fast3SiMa.setDrownCycle(fast3SiMa.getDrownCycle()+1);
					fast3SiMa.setDrownIssueNumber(issueNumber);
					fast3SiMa.setDrownNumber(Integer.toString(srcDataBean.getNo1())+Integer.toString(srcDataBean.getNo2())+Integer.toString(srcDataBean.getNo3()));
					fast3SiMa.setStatus(status);
					// 更新结果内容
					this.updateDanMaStatus(fast3SiMa);
					//中出后立即启动新的预测计划
					execSima(issueNumber,fast3SiMa);
				}else{
					fast3SiMa.setDrownCycle(fast3SiMa.getDrownCycle()+1);
					fast3SiMa.setDrownIssueNumber(issueNumber);
					fast3SiMa.setDrownNumber(Integer.toString(srcDataBean.getNo1())+Integer.toString(srcDataBean.getNo2())+Integer.toString(srcDataBean.getNo3()));
					fast3SiMa.setStatus(status);
					this.updateDanMaStatus(fast3SiMa);
					if(fast3SiMa.getDrownCycle() == 3){
						execSima(issueNumber,fast3SiMa);
					}
				}
			}
		}else{
			execSima(issueNumber,fast3SiMa);
		}
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
			LogUtil.error(e.getMessage(),"sima");
		}
		return srcDataBean;
	}
	/** 
	  * @Description: 在源库中查找最新的预测计划，并保证期号在预测范围内，否则报错
	  * @author songj@sdfcp.com
	  * @date Feb 15, 2016 3:29:13 PM 
	  * @return 
	  */
	public Fast3SiMa getSiMaYuceRecordByIssueCode(String issueNumber){
		Connection srcConn = ConnectSrcDb.getSrcConnection();
		PreparedStatement pstmt = null;
		Fast3SiMa data = null;
		String sql = "SELECT ID,YUCE_ISSUE_START,YUCE_ISSUE_STOP,DROWN_PLAN,DROWN_CYCLE,DROWN_ISSUE_NUMBER  FROM "+App.simaTbName+" WHERE "+ issueNumber +" BETWEEN YUCE_ISSUE_START AND YUCE_ISSUE_STOP   ORDER BY ID DESC LIMIT 1";
		try {
			pstmt = (PreparedStatement) srcConn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				data = new Fast3SiMa();
				data.setId(rs.getInt(1));
				data.setYuceIssueStart(rs.getString(2));
				data.setYuceIssueStop(rs.getString(3));
				data.setDrownPlan(rs.getString(4));
				data.setDrownCycle(rs.getInt(5));
				data.setDrownIssueNumber(rs.getString(6));
			}
			if(rs != null && !rs.isClosed()){
				rs.close();
			}
		}catch (SQLException e) {
			LogUtil.error(e.getMessage(),"sima");
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
	public List<SrcDataBean> getYucePool(String issueCode){
		Connection srcConn = ConnectSrcDb.getSrcConnection();
		List<SrcDataBean> srcList = new ArrayList<SrcDataBean>();
		PreparedStatement pstmt = null;
		String startDay = DateUtil.getNextNDay(-7);
		//System.out.println("开始日期"+startDay);
		String endDay = DateUtil.getNextNDay(-1);
		//System.out.println("结束日期"+endDay);
		String code1 = DateUtil.getNextIssueCodeByCurrentIssue(issueCode);
		String code2= DateUtil.getNextIssueCodeByCurrentIssue(code1);
		String code3= DateUtil.getNextIssueCodeByCurrentIssue(code2);
		String sql = "SELECT issue_number,no1,no2,no3 FROM "+App.srcNumberTbName+" WHERE substr(ISSUE_NUMBER,1,6) between '"+startDay+"' and '"+endDay+"' AND substr(ISSUE_NUMBER,8) IN ('"+code1+"','"+code2+"','"+code3+"') ORDER BY ISSUE_NUMBER DESC";
	    //System.out.println(sql);
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
			e.printStackTrace();
			LogUtil.error(e.getMessage(),"sima/");
		}
		//System.out.println(srcList.size());
		return srcList;
	}
	
	/**通过最近20天数据统计出现次数
	 * @param noList
	 * @return
	 */
	public List<Fast3Count> getTimesForNumber( List<SrcDataBean> noList){
		List<Fast3Count> fast3CountList = new ArrayList<Fast3Count>();       //一维0 代表最近7期出现次数，1为最近14期出现次数，2为最近20天出现次数
		int[] arr3 = {0,0,0,0,0,0};
		int[] arr5 = {0,0,0,0,0,0};
		int[] arr7 = {0,0,0,0,0,0};
		int i = 0;
		for(SrcDataBean no : noList){
			int[] temp = {no.getNo1(),no.getNo2(),no.getNo3()};
			Integer[] numIntArr = getUniqueArr(temp);
			if(i < 9){
				for(int j = 0; j < numIntArr.length; j++){
					arr3[numIntArr[j]-1]++;
				}
			}
			if(i < 15){
				for(int j = 0; j < numIntArr.length; j++){
					arr5[numIntArr[j]-1]++;
				}
			}
			if(i < 21){
				for(int j = 0; j < numIntArr.length; j++){
					arr7[numIntArr[j]-1]++;
				}
			}
			i++;
		}
		for(int j = 0;j < 6;j++){
			Fast3Count fast3Count = new Fast3Count();
			fast3Count.setNumber(j+1);
			fast3Count.setCount7(arr3[j]);
			fast3Count.setCount14(arr5[j]);
			fast3Count.setCount20(arr7[j]);
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
    public void execSima(String issueNumber,Fast3SiMa fast3SiMa) throws SQLException{
    	//获取即将预测的20数据
    	List<SrcDataBean> noList = this.getYucePool(issueNumber.substring(issueNumber.length()-2,issueNumber.length()));
    	//计算出现次数最多的数组
    	List<Fast3Count> fast3CountList = this.getTimesForNumber(noList);   	//计算胆码
    	Collections.sort(fast3CountList);
    	//插入新纪录时需要判断
		if(fast3SiMa == null || fast3SiMa.getDrownCycle() > 0){   //drowncycle = 0说明是新生成的记录，无需再一次生成
			insertData2Db(issueNumber,fast3CountList);
		}
    }
    
    /**
     * @param issueNumber
     * @param fast3CountList
     * @throws SQLException
     * 四码预测插入预测计划方法
     */
    private void insertData2Db(String issueNumber,List<Fast3Count> fast3CountList) throws SQLException{
    	Connection conn = ConnectSrcDb.getSrcConnection();
    	String sql = "insert into "+App.simaTbName+" (YUCE_ISSUE_START,YUCE_ISSUE_STOP,DROWN_PLAN,CREATE_TIME) values(?,?,?,?)";
		String code1 = App.getNextIssueByCurrentIssue(issueNumber);
		String code2 =  App.getNextIssueByCurrentIssue(code1);
		String code3 =  App.getNextIssueByCurrentIssue(code2);
    	int[] numArr = {fast3CountList.get(0).getNumber(),fast3CountList.get(1).getNumber(),fast3CountList.get(2).getNumber(),fast3CountList.get(3).getNumber()};
    	Arrays.sort(numArr);
		PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(sql);
		pstmt.setString(1,code1);
		pstmt.setString(2,code3);
		pstmt.setString(3,Integer.toString(numArr[0])+Integer.toString(numArr[1])+Integer.toString(numArr[2])+Integer.toString(numArr[3]));
		pstmt.setTimestamp(4, new java.sql.Timestamp(new Date().getTime()));
		pstmt.executeUpdate();
    }

    /**
     * @param number
     * @param fast3DanMa
     * @return 判断中出状态
     */
    private String judgeDownStatus(SrcDataBean number,Fast3SiMa fast3SiMa){
    	String status = "1";   //假设中出
    	int[] numArr = {number.getNo1(),number.getNo2(),number.getNo3()};
    	String drownPlan = fast3SiMa.getDrownPlan();
    	for(int i=0;i<numArr.length;i++){
    		if(drownPlan.indexOf(Integer.toString(numArr[i])) >= 0){
    			continue;  
    		}else{
    			status = "0";  //没中
    			break;
    		}
    	}
    	//System.out.println("status="+status);
    	return status;
    }
    
	/**
	 * @param status
	 * @throws SQLException
	 * 更新胆码表状态内容
	 */
	private void updateDanMaStatus(Fast3SiMa fast3SiMa) throws SQLException{
		Connection conn = ConnectSrcDb.getSrcConnection();
		String sql = "UPDATE "+App.simaTbName+" SET DROWN_ISSUE_NUMBER=?,DROWN_NUMBER=?,status = ?,DROWN_CYCLE=?  where ID = ?";
		//System.out.println(sql);
		PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(sql);
		pstmt.setString(1, fast3SiMa.getDrownIssueNumber());
		pstmt.setString(2, fast3SiMa.getDrownNumber());
		pstmt.setString(3, fast3SiMa.getStatus());
		pstmt.setInt(4, fast3SiMa.getDrownCycle());
		pstmt.setInt(5,fast3SiMa.getId() );
		pstmt.executeUpdate();
	}
	
}
