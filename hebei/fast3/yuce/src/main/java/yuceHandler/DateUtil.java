package yuceHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


/** 
  * @ClassName: DateUtil 
  * @Description: 日期通用方法
  * @author songjia
  * @date Feb 17, 2016 8:49:45 AM 
  *  
  */
public class DateUtil {
	
		/**
		 * @return 系统时间的第二天
		 */
		public static String getNextDay(){
			 Calendar calendar = new GregorianCalendar();
			 Date date = new Date();
			 calendar.setTime(date);
			 calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动
			 date=calendar.getTime(); //这个时间就是日期往后推一天的结果
			 SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
			 String dateString = formatter.format(date);
			 return dateString;
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
		
		/**
		 * @param n
		 * @return获取N天后日期
		 */
		public static String getNextNDay(int n){
			 Calendar calendar = new GregorianCalendar();
			 Date date = new Date();
			 calendar.setTime(date);
			 calendar.add(calendar.DATE,n);//把日期往后增加一天.整数往后推,负数往前移动
			 date=calendar.getTime(); //这个时间就是日期往后推一天的结果
			 SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
			 String dateString = formatter.format(date);
			 return dateString;
		}
		
		
		/**
		 * @param issueCode
		 * @return 获取下一期期号
		 */
		public static String getNextIssueCodeByCurrentIssue(String issueCode){
			String nextIssueCode = null;
			int next = (Integer.parseInt(issueCode)+1) % Integer.parseInt(App.lineCount);
			if(next < 10){
				if(next == 0){
					nextIssueCode = App.lineCount;
				}else{
					nextIssueCode = "0" + next;
				}
			}else{
				nextIssueCode = Integer.toString(next);
			}
			return nextIssueCode;
		}
		
		
		/**
		 * @param stringList
		 * @return 列表转字符串
		 */
		public static String listToString(List<String> stringList){
	        if (stringList==null) {
	            return null;
	        }
	        StringBuilder result=new StringBuilder();
	        boolean flag=false;
	        for (String string : stringList) {
	            if (flag) {
	                result.append(",");
	            }else {
	                flag=true;
	            }
	            result.append(string);
	        }
	        return result.toString();
	    }
		
}
