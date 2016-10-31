package yuceHandler;

import sun.rmi.log.LogInputStream;
import yuceHandler.dantuo.Data2Db;
import yuceHandler.follownumber.Data2DbFollowNumber;
import yuceHandler.samenumber.Data2DbSameNumber;
import yuceHandler.sima.Data2DbSima;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

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

	public static String followNumTbName = null;

	public static String followIssue = null;

	public static String followIssueNumber = null;
	
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
        lineCount  = p.getProperty("lineCount","80");
        srcNumberTbName = p.getProperty("srcNumberTbName");
        danMaTbName = p.getProperty("danMaTbName");
        simaTbName = p.getProperty("simaTbName");
        sameNumTbName = p.getProperty("sameNumTbName");
		followNumTbName = p.getProperty("followNumTbName");
		followIssue = p.getProperty("followIssue");
		followIssueNumber = p.getProperty("followIssueNumber");
		province = p.getProperty("province");
        
	}
	
	/*
	 * 执行方法入口
	 */
	public static void main(String[] args) {
		
//		judge5In11();
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
//		LogUtil.info("计算冷热号码开始.......",null);
		Data2Db data2Db = new Data2Db();
		String maxIssueNumber = data2Db.findMaxIssueIdFromSrcDb();
		if(!maxIssueId.equals(maxIssueNumber)){
//			long startTime = System.currentTimeMillis();
			maxIssueId = maxIssueNumber;
//			LogUtil.info("有新期数产生我们将进行数据分析.......",null);
			//胆码计算开始.....
            startDanMa(maxIssueId);                //胆码计算放发组开始
			// 四码复式计算组  
			startSiMa(maxIssueId);
			//同码预测
	   		startSameNumber(maxIssueId);
			//开始冷热号执行
			startHotColdNumber(maxIssueId);
//			LogUtil.info("共计耗时" + (System.currentTimeMillis()-startTime)/1000,null);
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
			LogUtil.info(issueNumber+"预测成功！","/danma");
		}catch(SQLException sqlEx){
			sqlEx.printStackTrace();
			LogUtil.error(issueNumber+"预测失败！"+sqlEx.getMessage(),"/danma");
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
				   return DateUtil.getNextDay() + "01";
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
	   //  四码复式计算
	   try{
	     //  判断是否中出
		   Data2DbSima data2DbSima = new Data2DbSima();
		   data2DbSima. execDrawnSima(issueNumber);
		   LogUtil.info(issueNumber+"预测成功！","/sima");
	   }catch(SQLException sqlEx){
		   LogUtil.info(issueNumber+"预测失败！","/sima");
	   }
	 }
   //相同号码推荐
   public static void startSameNumber(String issueNumber){
	   //插入统计结果
	   try{
		   Data2DbSameNumber data2DbSameNumber = new Data2DbSameNumber();
		   data2DbSameNumber.execSameNum(issueNumber);
		   LogUtil.info(issueNumber+"预测成功！","/same");
	   }catch(SQLException sqlEx){
		   sqlEx.printStackTrace();
		   LogUtil.error(issueNumber+"预测失败！","/same");
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	}
   
   //冷热号码推荐
   public static void startHotColdNumber(String issueNumber){
	   //插入统计结果
	   try{
		   Data2DbFollowNumber data2DbFollowNumber = new Data2DbFollowNumber();
		   LogUtil.info("进入冷热号分析流中...",null);
		   data2DbFollowNumber.execFollowNum(issueNumber);
		   LogUtil.info(issueNumber+"预测成功！","/follow");
	   }catch(SQLException sqlEx){
		   sqlEx.printStackTrace();
		   LogUtil.error(issueNumber+"预测失败！","/follow");
	   }catch(Exception e){
		   e.printStackTrace();
	   }
   }

   public static String translate(int temp) {
		String rtn = null;
		if (temp < 10) {
			rtn = temp + "";
		} else if (temp == 10)
			rtn = "A";
		else if (temp == 11)
			rtn = "J";
		else if (temp == 12) {
			rtn = "Q";
		}

		return rtn;
   }


   /**
    * 测试11选5和12选5相同号码是否靠谱的程序，
    * 11选5通过和值和乘和可以唯一找出一组
    * 12选5要通过和值和乘和三码跨度唯一确认一组
    */
/*
	private static void judge5In11(){
		List<int[]> strList = new ArrayList<int[]>();
		int pp = 0;
		int even = 0;
		for(int i = 1;i <= 12;i++){
			for(int j = i+1;j<=12;j++){
				for(int z = j+1;z<=12;z++){
					for(int p = z+1;p<=12;p++){
						for(int q=p+1;q<=12;q++){
							int[] str = new int[4];
							pp++;
							//System.out.println(i+":"+j+":"+z+":"+p+":"+q);
							str[0] = i+j+z+p+q;
							
							str[1] = i*j*z*p*q;
							str[2] = q - i;
							if(i % 2 == 0){
								even++;
							}
							if(j % 2 == 0){
								even++;
							}
							if(z % 2 == 0){
								even++;
							}
							if(p % 2 == 0){
								even++;
							}
							if(q % 2 == 0){
								even++;
							}
							str[3] = even;
							if(str[0] == 31 &&str[1] == 2160){
								System.out.println(i+":"+j+":"+z+":"+p+":"+q);
							}
							strList.add(str);
							even = 0;
						}
					}
				}
			}

		}
		System.out.println(strList.size()+":"+pp);
		for(int l =0;l < strList.size();l++){
			for(int j = l+1;j<strList.size();j++){
				if(strList.get(l)[0] == strList.get(j)[0] &&strList.get(l)[1] == strList.get(j)[1]&&strList.get(l)[2] == strList.get(j)[2]&&strList.get(l)[3] == strList.get(j)[3]){
					System.out.println("玩肚子了!"+strList.get(l)[0] + ":" + strList.get(l)[1]+":"+strList.get(l)[2]+":"+strList.get(l)[3]);
					break;
				}
			}
		}
	}

*/
}
