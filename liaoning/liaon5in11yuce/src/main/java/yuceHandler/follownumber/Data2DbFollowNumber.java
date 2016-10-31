package yuceHandler.follownumber;

import com.mysql.jdbc.PreparedStatement;
import yuceHandler.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** 
  * @ClassName: Data2Db 
  * @Description: 数据库操作方法全部在这个类中 
  * @author songj@sdfcp.com
  * @date Feb 15, 2016 3:25859:15 PM 
  *  
  */
public class Data2DbFollowNumber {  //02478815484      6228480128008022377   何湘琪

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
		String sql = "SELECT issue_number,no1,no2,no3,no4,no5,three_span,five_sum FROM "+App.srcNumberTbName+" WHERE ISSUE_NUMBER = '"+issueNumber+"'";
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
			LogUtil.error(e.getMessage(),"same/");
		}
		return srcDataBean;
	}

	/**
	 * @desc  判断本期和上期是否连续，如果不连续将重新初始化一些表内容
	 * @param currentIssueNumber
	 * @return
     */
	public boolean isSerial(String currentIssueNumber){
		boolean flag = true;
		Connection srcConn = ConnectSrcDb.getSrcConnection();
		PreparedStatement pstmt = null;
		String beforeIssueNumber = null;
		String sql = "SELECT issue_number FROM " + App.followIssue ;
		try {
			pstmt = (PreparedStatement) srcConn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			if(rs!=null && rs.next()){
				beforeIssueNumber = rs.getString(1);
			}
			LogUtil.info("上次计算的期数为......"+beforeIssueNumber,"hot/");
			LogUtil.info("本期为......"+currentIssueNumber,"hot/");
			if(rs != null && !rs.isClosed()){
				rs.close();
			}
			if(beforeIssueNumber.length() == 8) {
				String temp = DateUtil.getNextNDayByIssueDay(beforeIssueNumber.substring(0, 6), 0) + DateUtil.getNextIssueCodeByCurrentIssue(beforeIssueNumber.substring(6, 8));
				if(!temp.equals(currentIssueNumber)){
					flag = false;
				}
			}else{
				flag = false;
			}

		}catch (SQLException e) {
			LogUtil.error(e.getMessage(),"same/");
		}
		return flag;
	}

	public void initFollowIssueNumber() throws SQLException{
		// 将每个号码的近30出现的期号 开奖号放到表中
		Connection srcConn = ConnectSrcDb.getSrcConnection();
		String truncateTb = "TRUNCATE TABLE " + App.followIssueNumber; // +App.followIssueNumber;
		String sql = "insert into " + App.followIssueNumber + " (NUMBER,ISSUE_NUMBER,NO1,NO2,NO3,NO4,NO5) " +
				" SELECT ? AS NUMBER,ISSUE_NUMBER,NO1,NO2,NO3,NO4,NO5 FROM "+App.srcNumberTbName+" WHERE NO1=? OR NO2=? OR NO3=? OR NO4=? OR NO5=? ORDER BY ISSUE_NUMBER DESC LIMIT 30";
		srcConn.setAutoCommit(false);
		PreparedStatement pstmt = (PreparedStatement) srcConn.prepareStatement(sql);
		pstmt.addBatch(truncateTb);
		for(int i = 1;i <= 11;i++){
			pstmt.setInt(1,i);
			pstmt.setInt(2,i);
			pstmt.setInt(3,i);
			pstmt.setInt(4,i);
			pstmt.setInt(5,i);
			pstmt.setInt(6,i);
			pstmt.addBatch();
		}
		pstmt.executeBatch();
		srcConn.commit();
		pstmt.clearBatch();
		srcConn.setAutoCommit(true);
		LogUtil.info("初始化FOLLOW_ISSUE表完成.......","hot/");
	}
	/*
	public static void main(String[] args){

		try {
			long start = System.currentTimeMillis();
			initFollowTableData();
			long end = System.currentTimeMillis();
			LogUtil.info("初始化操作一共用了："+(end-start)/1000+"秒!","hot/");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	*/
	/**
	 * @Description: 获取上期的follow号码统计结果
	 * @author songjia
	 * @date Feb 15, 2016 3:29:13 PM
	 * @return
	 */
	public List<Fast3FollowNumber> getFollowNumberListForLastIssueNumber(){
		List<Fast3FollowNumber> rtnList = new ArrayList<Fast3FollowNumber>();
		Connection srcConn = ConnectSrcDb.getSrcConnection();
		PreparedStatement pstmt = null;
		Fast3FollowNumber fast3FollowNumber = null;
		String sql = "SELECT NUMBER,FOLLOW_NUMBER,FOLLOW_COUNT,THREE_FOLLOW_COUNT,NO_FOLLOW_COUNT,THREE_NO_FOLLOW_COUNT FROM "+App.followNumTbName;
		try {
			pstmt = (PreparedStatement) srcConn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				fast3FollowNumber = new Fast3FollowNumber();
				fast3FollowNumber.setNumber(rs.getInt(1));
				fast3FollowNumber.setFollowNumber(rs.getInt(2));
				fast3FollowNumber.setFollowCount(rs.getInt(3));
				fast3FollowNumber.setThreeFollowCount(rs.getInt(4));
				fast3FollowNumber.setNoFollowCount(rs.getInt(5));
				fast3FollowNumber.setThreeNoFollowCount(rs.getInt(6));
				rtnList.add(fast3FollowNumber);
			}
			if(rs != null && !rs.isClosed()){
				rs.close();
			}
		}catch (SQLException e) {
			LogUtil.error(e.getMessage(),"hot/");
		}
		return rtnList;
	}

    /** 代码入库主方法
     * @param issueCode
     */
    public void execFollowNum(String issueNumber) throws SQLException{
    	//根据期号获取开奖号码
    	if(!this.isSerial(issueNumber)){   //重新初始化issue表内容，保证连续
			LogUtil.info("初始化开始.....","hot/");
			this.initFollowIssueNumber();  //如果不连续则初始化数据池
			this.initAddFollow();          //重新进行冷热号的计算
		}else {
			//数据库中获取上期的统计结果
			LogUtil.info("连续计算开始.....","hot/");
			List<Fast3FollowNumber> allList = getFollowNumberListForLastIssueNumber();
			execAddFollow(allList, issueNumber);
			insertData2Db(allList);
		}
		this.updateLastIssueNumber(issueNumber);
	}

	/**
	 * @desc   更新当前统计期数
	 * @param issueNumber
	 * @throws SQLException
     */
	private void updateLastIssueNumber(String issueNumber) throws SQLException{
		Connection conn = ConnectSrcDb.getSrcConnection();
		//插入前删除表中所有记录
		String initSql = "UPDATE "+App.followIssue + " SET ISSUE_NUMBER = '" + issueNumber + "'";
		PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(initSql);
		pstmt.executeUpdate();
		LogUtil.info("更新followissue完成"+initSql,"hot/");
	}

	private SrcDataBean getLast30IssueNumber(String number){
		Connection srcConn = ConnectSrcDb.getSrcConnection();
		PreparedStatement pstmt = null;
		SrcDataBean srcDataBean = null;
		String sql = "SELECT issue_number,no1,no2,no3,no4,no5 FROM "+App.followIssueNumber+" WHERE NUMBER = '"+number+"' ORDER BY ISSUE_NUMBER ASC LIMIT 1";
		try {
			pstmt = (PreparedStatement) srcConn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			if(rs != null&&rs.next()){
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
			LogUtil.error(e.getMessage(),"same/");
		}
		return srcDataBean;
	}


	/**
	 * @desc  该方法进行冷热号的累加
	 * @param allList
	 * @param threeNum
	 * @param numbers
	 */
	private void initAddFollow(){
		//初始化  T_LN_5IN12_FOLLOWNUMBER 表中内容  准备初始化
		try {
			initFollowNumberTable();
			LogUtil.info("初始化FOLLOWCOUNT完成....","hot/");
			//获取  T_LN_5IN12_FOLLOW_ISSUE_NUMBER 数据池内容
			List<FollowIssueNumber> numberList = this.getFollowIssueNumber();
			//获取统计表数据
			List<Fast3FollowNumber> followList = getFollowNumberListForLastIssueNumber();
			List<Fast3FollowNumber> temp = null;
			List<FollowIssueNumber> tempNumbers = null;
			for(int i = 1;i <= 11;i++){
				tempNumbers = numberList.subList((i-1)*30,i*30);
				for(FollowIssueNumber number : tempNumbers){
					int[] threeNum = {number.getNo1(),number.getNo2(),number.getNo3()};
					int[] numbers = {number.getNo1(),number.getNo2(),number.getNo3(),number.getNo4(),number.getNo5()};
					Arrays.sort(threeNum);
					Arrays.sort(numbers);
					if(Arrays.binarySearch(numbers,i) >= 0){  //如果i存在于开奖号码中我们进行操作，如果不在则不进行任何操作
						temp = followList.subList((i-1)*10,i*10);
						for(Fast3FollowNumber fast3FollowNumber : temp){ //正向执行一遍 反向执行一遍
							execForwardAddFollow(fast3FollowNumber,threeNum,numbers);
						}
						continue;
					}
				}
			}

			//12    个号码循环执行正向addfollow方法
			// 将新生成的表放入库中
			this.insertData2Db(followList);
			LogUtil.info("初始化FOLLOWCOUNT 计算完成....","hot/");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}



	private void initFollowNumberTable() throws SQLException{
		Connection conn = ConnectSrcDb.getSrcConnection();
		//插入前删除表中所有记录
		String initSql = "UPDATE "+App.followNumTbName + " SET FOLLOW_COUNT = 0,THREE_FOLLOW_COUNT = 0, NO_FOLLOW_COUNT = 0,THREE_NO_FOLLOW_COUNT = 0";
		PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(initSql);
		pstmt.executeUpdate();
	}

	/**
	 *
     */
	private List<FollowIssueNumber> getFollowIssueNumber(){
		List<FollowIssueNumber> rtnList = new ArrayList<FollowIssueNumber>();
		Connection conn = ConnectSrcDb.getSrcConnection();
		//获取sql
		String execSql = "SELECT NUMBER,ISSUE_NUMBER,NO1,NO2,NO3,NO4,NO5 FROM "+App.followIssueNumber+" ORDER BY NUMBER,ISSUE_NUMBER";
		PreparedStatement pstmt = null;
		FollowIssueNumber followIssueNumber = null;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(execSql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				followIssueNumber = new FollowIssueNumber();
				followIssueNumber.setNumber(rs.getInt(1));
				followIssueNumber.setIssueNumber(rs.getString(2));
				followIssueNumber.setNo1(rs.getInt(3));
				followIssueNumber.setNo2(rs.getInt(4));
				followIssueNumber.setNo3(rs.getInt(5));
				followIssueNumber.setNo4(rs.getInt(6));
				followIssueNumber.setNo5(rs.getInt(7));
				rtnList.add(followIssueNumber);
			}
			if(rs != null && !rs.isClosed()){
				rs.close();
			}
		}catch (SQLException e) {
			LogUtil.error(e.getMessage(),"hot/");
		}
		return rtnList;
	}
	/**
	 * @desc  该方法进行冷热号的累加
	 * @param allList
	 * @param threeNum
	 * @param numbers
     */
	private void execAddFollow(List<Fast3FollowNumber> allList,String issueNumber) throws SQLException{
		List<Fast3FollowNumber> temp = null;
		SrcDataBean srcDataBean = this.getRecordByIssueNumber(issueNumber);
		LogUtil.info("获取当前期正常!","hot/");
		int[] threeNum = {srcDataBean.getNo1(),srcDataBean.getNo2(),srcDataBean.getNo3()};
		int[] numbers = {srcDataBean.getNo1(),srcDataBean.getNo2(),srcDataBean.getNo3(),srcDataBean.getNo4(),srcDataBean.getNo5()};
		Arrays.sort(threeNum);
		Arrays.sort(numbers);
		for(int i = 1;i <= 11;i++){
			if(Arrays.binarySearch(numbers,i) >= 0){  //如果i存在于开奖号码中我们进行操作，如果不在则不进行任何操作
				SrcDataBean last30Data = this.getLast30IssueNumber(i+"");   //获取开奖号码相同的30期并且将它的第一条获取进行反向的操作。
				LogUtil.info("获取第上三十期数正常!","hot/");
				int[] lastThreeNum = {last30Data.getNo1(),last30Data.getNo2(),last30Data.getNo3()};
				int[] lastNumbers = {last30Data.getNo1(),last30Data.getNo2(),last30Data.getNo3(),last30Data.getNo4(),last30Data.getNo5()};
				Arrays.sort(lastThreeNum);
				Arrays.sort(lastNumbers);
				temp = allList.subList((i-1)*10,i*10);
				for(Fast3FollowNumber fast3FollowNumber : temp){ //正向执行一遍 反向执行一遍
					execForwardAddFollow(fast3FollowNumber,threeNum,numbers);
					LogUtil.info("正向添加成功！","hot/");
					//反向执行一次
					execRevertAddFollow(fast3FollowNumber,lastThreeNum,lastNumbers);
					LogUtil.info("逆向添加成功！","hot/");
				}
				//如果它在开奖结果中则要更换一下数据池中的数据，因为数据池要保证一直是存在该开奖号码最近的30期
				updateLast30DataTable(srcDataBean,last30Data.getIssueId(),i);
			}
		}
	}

	/**
	 *
	 * @param currentIssue
	 * @param last30IssueNumber
	 * @param number
	 * @throws SQLException
     */
	public void updateLast30DataTable(SrcDataBean currentIssue,String last30IssueNumber,int number) throws SQLException{
		Connection conn = ConnectSrcDb.getSrcConnection();
		//插入前删除表中所有记录
		String delSql = " DELETE FROM " + App.followIssueNumber +" WHERE ISSUE_NUMBER = '" + last30IssueNumber + "' AND NUMBER = "+ number;
		String insertSql = "INSERT INTO "+ App.followIssueNumber + "(NUMBER,ISSUE_NUMBER,NO1,NO2,NO3,NO4,NO5)VALUES(?,?,?,?,?,?,?)";
		conn.setAutoCommit(false);
		PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(insertSql);
		pstmt.addBatch(delSql);
		pstmt.setInt(1,number);
		pstmt.setString(2,currentIssue.getIssueId());
		pstmt.setInt(3,currentIssue.getNo1());
		pstmt.setInt(4,currentIssue.getNo2());
		pstmt.setInt(5,currentIssue.getNo3());
		pstmt.setInt(6,currentIssue.getNo4());
		pstmt.setInt(7,currentIssue.getNo5());
		pstmt.addBatch();
		pstmt.executeBatch();
		conn.commit();
		conn.setAutoCommit(true);
		LogUtil.info("数据池删除添加成功!","hot/");
	}

	/**
	 *
	 * @desc  对于此类follow添加进行反向操作，这样可以保证30以上的内容会被覆盖
	 * @param allList
	 * @param threeNum
	 * @param numbers
	 */

	private void execForwardAddFollow(Fast3FollowNumber fast3FollowNumber,int[] threeNum,int[] numbers){
		if(Arrays.binarySearch(numbers,fast3FollowNumber.getFollowNumber()) >= 0){
			fast3FollowNumber.setFollowCount(fast3FollowNumber.getFollowCount()+1);
			if(Arrays.binarySearch(threeNum,fast3FollowNumber.getFollowNumber()) >= 0){
				fast3FollowNumber.setThreeFollowCount(fast3FollowNumber.getThreeFollowCount()+1);
			}else{
				fast3FollowNumber.setThreeNoFollowCount(fast3FollowNumber.getThreeNoFollowCount()+1);
			}
		}else{
			fast3FollowNumber.setNoFollowCount(fast3FollowNumber.getNoFollowCount()+1);
			fast3FollowNumber.setThreeNoFollowCount(fast3FollowNumber.getThreeNoFollowCount()+1);
		}
	}


	/**
	 *
	 * @desc  对于此类follow添加进行反向操作，这样可以保证30以上的内容会被覆盖
	 * @param allList
	 * @param threeNum
	 * @param numbers
     */

	private void execRevertAddFollow(Fast3FollowNumber fast3FollowNumber,int[] threeNum,int[] numbers){
		if(Arrays.binarySearch(numbers,fast3FollowNumber.getFollowNumber()) >= 0){
			fast3FollowNumber.setFollowCount(fast3FollowNumber.getFollowCount()-1);
			if(Arrays.binarySearch(threeNum,fast3FollowNumber.getFollowNumber()) >= 0){
				fast3FollowNumber.setThreeFollowCount(fast3FollowNumber.getThreeFollowCount()-1);
			}else{
				fast3FollowNumber.setThreeNoFollowCount(fast3FollowNumber.getThreeNoFollowCount()-1);
			}
		}else{
			fast3FollowNumber.setNoFollowCount(fast3FollowNumber.getNoFollowCount()-1);
			fast3FollowNumber.setThreeNoFollowCount(fast3FollowNumber.getThreeNoFollowCount()-1);
		}
	}
    /**
     * @throws SQLException
     * 四码预测插入预测计划方法
     */
    private static void insertData2Db(List<Fast3FollowNumber> allList) throws SQLException{
    	Connection conn = ConnectSrcDb.getSrcConnection();
    	//插入前删除表中所有记录
    	String truncateTb = "TRUNCATE TABLE "+App.followNumTbName;
    	String sql = "insert into "+App.followNumTbName+"(NUMBER,FOLLOW_NUMBER, FOLLOW_COUNT,THREE_FOLLOW_COUNT, NO_FOLLOW_COUNT,THREE_NO_FOLLOW_COUNT) values(?,?,?,?,?,?)";
    	conn.setAutoCommit(false);
		PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(sql);
		pstmt.addBatch(truncateTb);
		for(Fast3FollowNumber fast3FollowNumber : allList){
			pstmt.setInt(1,fast3FollowNumber.getNumber());
			pstmt.setInt(2,fast3FollowNumber.getFollowNumber());
			pstmt.setInt(3,fast3FollowNumber.getFollowCount());
			pstmt.setInt(4,fast3FollowNumber.getThreeFollowCount());
			pstmt.setInt(5,fast3FollowNumber.getNoFollowCount());
			pstmt.setInt(6,fast3FollowNumber.getThreeNoFollowCount());
			pstmt.addBatch();
		}
		pstmt.executeBatch();
		conn.commit();
		conn.setAutoCommit(true);
    }

	/*
    private static void initFollowTableData() throws SQLException{
		Connection conn = ConnectSrcDb.getSrcConnection();
		//插入前删除表中所有记录
		//String truncateTb = "TRUNCATE TABLE "+App.followNumTbName;
		String sql = "insert into T_LN_5IN12_FOLLOWNUMBER(NUMBER,FOLLOW_NUMBER, FOLLOW_COUNT,THREE_FOLLOW_COUNT, NO_FOLLOW_COUNT,THREE_NO_FOLLOW_COUNT) values(?,?,?,?,?,?)";
		conn.setAutoCommit(false);
		PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(sql);
		//pstmt.addBatch(truncateTb);

		for(int i = 1;i <= 12;i++){
			for(int j = 1;j <= 12;j++){
				if(i == j)continue;
				pstmt.setInt(1,i);
				pstmt.setInt(2,j);
				pstmt.setInt(3,0);
				pstmt.setInt(4,0);
				pstmt.setInt(5,0);
				pstmt.setInt(6,0);
				pstmt.addBatch();
			}
		}
		pstmt.executeBatch();
		conn.commit();
	}
	*/
	
}
