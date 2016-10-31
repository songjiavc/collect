package echart.collect.website.hebei.CollectFast3;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.mysql.jdbc.PreparedStatement;

public class DataToDb{

	public void haveDataInIssueId(List<String> noList) {
		int count = 1;
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
			if(count == 0){
				SrcDataBean srcDataBean = caluExtentInfo(noList);
				insertData(srcDataBean,conn);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try {
				ConnectDb.dbClose(conn, pstmt,rs);
			} catch (SQLException e) {
			}
		}
	}
	private SrcDataBean caluExtentInfo(List<String> noList){
		SrcDataBean srcDataBean = new SrcDataBean();
		int oneInt = Integer.parseInt(noList.get(1).toString());
		int twoInt =  Integer.parseInt(noList.get(2).toString());
		int threeInt = Integer.parseInt(noList.get(3).toString());
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
		srcDataBean.setIssueId(noList.get(0));
		srcDataBean.setNo1(oneInt);
		srcDataBean.setNo2(twoInt);
		srcDataBean.setNo3(threeInt);
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
	private void insertData(SrcDataBean srcDataBean,Connection conn) throws SQLException{
		String sql = "insert into "+App.srctable+"(issue_number,no1,no2,no3,three_sum,three_span,big_count,small_count,odd_count,even_count,num_status,create_time,origin) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			pstmt.setString(1, srcDataBean.getIssueId());
			pstmt.setInt(2, srcDataBean.getNo1());
			pstmt.setInt(3, srcDataBean.getNo2());
			pstmt.setInt(4, srcDataBean.getNo3());
			pstmt.setInt(5, srcDataBean.getThreeSum());
			pstmt.setInt(6, srcDataBean.getThreeSpan());
			pstmt.setInt(7, srcDataBean.getBigNum());
			pstmt.setInt(8, srcDataBean.getSmallNum());
			pstmt.setInt(9, srcDataBean.getOddNum());
			pstmt.setInt(10, srcDataBean.getEvenNum());
			pstmt.setInt(11, srcDataBean.getNoStatus());
			pstmt.setTimestamp(12, new java.sql.Timestamp(new Date().getTime()));
			pstmt.setInt(13, Constants.ORIGIN_YDNIU);
			pstmt.executeUpdate();
			LogUtil.info("一定牛插入基础数据表成功!");
		} catch (SQLException e) {
			LogUtil.error("一定牛插入基础数据表异常!"+e.getCause());
		}finally{
			if(!pstmt.isClosed() && pstmt != null){
				pstmt.close();
			}
		}
		
	}

}
