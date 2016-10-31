package yuceHandler;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import yuceHandler.dantuo.Data2Db;
import yuceHandler.samenumber.Data2DbSameNumber;
import yuceHandler.sima.Data2DbSima;

/**
 * @author songjia
 * 该套程序主入口，计算两个内容，1.胆码预测。2,四码复式预测内容
 */
public class App {
	
	static Connection conn = null;
	
	static String maxIssueId = "";
	
	public static String lineCount= "0";
	
	public static String srcNumberTbName=null;
	
	public static String danMaTbName=null;
	
	public static String simaTbName=null;
	
	public static String sameNumTbName = null;
	
	public static String province= null;
	
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
        danMaTbName = p.getProperty("danMaTbName");
        simaTbName = p.getProperty("simaTbName");
        sameNumTbName = p.getProperty("sameNumTbName");
        province = p.getProperty("province");
        
	}
	
	/*
	 * 执行方法入口
	 */
	public static void main(String[] args) {
		initParam();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				execData();                    //
			}
		}, new Date(), 1000 * 20);// 每隔20秒输出
	}

	/** 
	  * @Description: 
	  * @author songjia
	  * @date Feb 15, 2016 11:31:49 AM
	  */
	private static void execData(){
		Data2Db data2Db = new Data2Db();
		String maxIssueNumber = data2Db.findMaxIssueIdFromSrcDb();
		if(!maxIssueId.equals(maxIssueNumber)){
			maxIssueId = maxIssueNumber;
			startDanMa(maxIssueId);                //胆码计算放发组开始
			// 四码复式计算组  
			startSiMa(maxIssueId);
			//同码预测
	   		startSameNumber(maxIssueId);
		}
	}
 	
	/**
	 * @param issueNumber
	 * 计算胆码
	 */
	private static void startDanMa(String issueNumber){
		try{
			Data2Db data2Db = new Data2Db();
			//当发现有新的开奖号码的时候做两件事
			//判断预测结果
			data2Db.execDrawnPrize(issueNumber);
			// 预测下一期内容
			String nextIssueNumber = getNextIssueByCurrentIssue(issueNumber);
			data2Db.execDanMa(nextIssueNumber);
			LogUtil.info(issueNumber+"预测成功！",App.province+"/danma");
		}catch(SQLException sqlEx){
			sqlEx.printStackTrace();
			LogUtil.error(issueNumber+"预测失败！"+sqlEx.getMessage(),App.province+"/danma");
		}
	}
	
	
	   public static String getNextIssueByCurrentIssue(String issueNumber){
		   String issueCode = issueNumber.substring(issueNumber.length()-2,issueNumber.length());
		   int issue = Integer.parseInt(issueCode);
		   int nextIssue = ((issue+1) % Integer.parseInt(lineCount));
		   if(nextIssue > 9){
			   return issueNumber.substring(0,issueNumber.length()-2)+nextIssue;
		   }else{
			   if(nextIssue == 0){
				  return issueNumber.substring(0,issueNumber.length()-2)+App.lineCount;
			   }else if(nextIssue == 1 ){
				   return DateUtil.getNextDay() + "001";
			   }else{
				   return issueNumber.substring(0,issueNumber.length()-2)+"0"+nextIssue;
			   }
		   }
	   }
	   
	   
   /**
    * @param issueNumber
    * 计算噝码复式开始
    */
   public static void startSiMa(String issueNumber){
	   //四码复式计算
	   try{
	     //undo 判断是否中出
		   Data2DbSima data2DbSima = new Data2DbSima();
		   data2DbSima. execDrawnSima(issueNumber);
			LogUtil.info(issueNumber+"预测成功！",App.province+"/sima");
	   }catch(SQLException sqlEx){
		   LogUtil.info(issueNumber+"预测失败！",App.province+"/sima");
	   }
	 }
   
   public static void startSameNumber(String issueNumber){
	   //插入统计结果
	   try{
		   Data2DbSameNumber data2DbSameNumber = new Data2DbSameNumber();
		   data2DbSameNumber.execSameNum(issueNumber);
		   LogUtil.info(issueNumber+"预测成功！",App.province+"/same");
	   }catch(SQLException sqlEx){
		   sqlEx.printStackTrace();
		   LogUtil.error(issueNumber+"预测失败！",App.province+"/same");
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	   }
   
}
