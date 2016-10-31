package hebei.tenminute.fivein11;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import com.mysql.jdbc.StringUtils;

public class App {
	
	static String maxIssueId = "16091979";
	/*
	 * 执行方法入口
	 */
	public static void main(String[] args) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				collectData();
			}
		}, new Date(), 1000 * 1);// 每隔5秒搜索一次
	}
	/*
	 * 初始化遗漏统计表结构，初始化过后不再使用，注释掉
	 */
	/*
	private static void insertTestData() throws SQLException {
		// TODO Auto-generated method stub
		Data2Db data2Db = new Data2Db();
		int t = 0,q=0;
		for(int i = 1;i <= 11;i++){
			for(int j = 1;j <=11;j++){
				if(i != j){
					data2Db.insertTestGroupData(null,translate(i)+translate(j),0 ,0,11);
					//System.out.println(i+":"+j);
					//t++;
					for(int z = 1;z<=11;z++){
						if(i != z && j != z){
							data2Db.insertTestGroupData(null,translate(i)+translate(j)+translate(z),0 ,0,12);
							//System.out.println(i+":"+j+":"+z);
						//	q++;
						}
					}
				}
			}
		}
		System.out.println(t+":"+q);
	}

	private static String translate(int temp){
		String rtn = null;
		if(temp < 10){
			rtn = temp+"";
		}else{
			if(temp == 10){
				rtn = "A";
			}else if(temp == 11){
				rtn = "J";
			}else if(temp == 12){
				rtn = "Q";
			}
		}
		return rtn;
	}
	*/
	/** 
	  * @Description: 
	  * @author songjia
	  * @date Feb 15, 2016 11:31:49 AM  
	  */
	private static void collectData(){
		Data2Db data2Db = new Data2Db();
		// 将源数据库中数据插入到目的库中
		if(StringUtils.isNullOrEmpty(maxIssueId)){
			maxIssueId = data2Db.findMaxIssueIdFromDesDb();
		}
		SrcDataBean srcData = data2Db.getRecordByIssueId(getNextIssueNumber(maxIssueId));
		if(!StringUtils.isNullOrEmpty(srcData.getIssueId())){
			maxIssueId = srcData.getIssueId();
			data2Db.insertBaseData(srcData);
		}
	}
	/**
	 * @return 系统时间的第二天
	 * @throws ParseException
	 */
	public static String getNextDay(String day) {   //yyMMdd
		SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
		Calendar calendar = new GregorianCalendar();
		String dateString = null;
		Date date;
		try {
			date = formatter.parse(day);
			calendar.setTime(date);
			calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动
			date=calendar.getTime(); //这个时间就是日期往后推一天的结果
			dateString = formatter.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dateString;
	}
	
	public static String getNextIssueNumber(String issueNumber){
		String nextIssueNumber = null;
		String issueCode = issueNumber.substring(issueNumber.length()-2,issueNumber.length());
		if(issueCode.equals("85")){
			nextIssueNumber = getNextDay(issueNumber.substring(0,6)) + "01";
		}else{
			int codeInt = Integer.parseInt(issueCode)+1;
			if(codeInt < 10){
				nextIssueNumber = issueNumber.substring(0, issueNumber.length()-2) + "0" +codeInt;
			}else{
				nextIssueNumber = issueNumber.substring(0, issueNumber.length()-2) + codeInt;
			}
		}
		return nextIssueNumber;
	}
}
		
		