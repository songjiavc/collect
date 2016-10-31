package hebei.tenminute.fivein11;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import com.mysql.jdbc.StringUtils;

public class App {
	
	static String maxIssueId = "";
	
	static String lineCount = null;

	static String srcNumberTbName = null;

	static String descMissTbName = null;
	
	static String province = null;
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
		}, new Date(), 1000 * 5);// 每隔5秒搜索一次
	}
	
	private static  void initParam(){
		Properties p = new Properties();
		InputStream is = App.class.getClassLoader().getResourceAsStream("db.properties");
		try {
			p.load(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lineCount  = p.getProperty("lineCount","79");
		srcNumberTbName = p.getProperty("srcNumberTbName");
		descMissTbName = p.getProperty("descMissTbName");
		province = p.getProperty("province");
	}
	/** 
	  * @Description: 
	  * @author songjia
	  * @date Feb 15, 2016 11:31:49 AM  
	  */
	private static void collectData(){
		
		initParam();
		Data2Db data2Db = new Data2Db();
		// 将源数据库中数据插入到目的库中
		if(StringUtils.isNullOrEmpty(maxIssueId)){
			maxIssueId = data2Db.findMaxIssueIdFromDesMissTable();
		}
		SrcDataBean srcData = data2Db.getRecordByIssueId(getNextIssueNumber(maxIssueId));
		if(srcData != null && !StringUtils.isNullOrEmpty(srcData.getIssueId())){
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
		if(issueCode.equals(App.lineCount)){
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
		
		