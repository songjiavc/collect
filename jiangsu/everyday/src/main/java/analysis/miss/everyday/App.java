package analysis.miss.everyday;

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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class App {
	
	static String maxIssueId = "";
	
	static String lineCount = null;
	
	static String srcNumberTbName = null;
	
	static String descNumberTbName = null;

	static String descMissTbName = null;
	
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
        descNumberTbName = p.getProperty("descNumberTbName");
		descMissTbName = p.getProperty("descMissTbName");
	}

	public static void main(String[] args) {
		initParam();
		Timer timer = new Timer();
		final App cellectDataTest = new App();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				cellectDataTest.collectData();
			}
		}, new Date(), 1000 * 10);// 每隔20秒输出

	}

	/** 
	  * @Description: 
	  * @author songjia
	  * @date Feb 15, 2016 11:31:49 AM  
	  */
	private void collectData(){
		Document doc;
		try {
			doc = Jsoup.connect(
					"http://chart.ydniu.com/trend/k3js/").timeout(5000*2)
					.get();
			SrcDataBean srcDataBean = parseDocContext(doc);
			if(srcDataBean != null){
				Data2Db data2Db = new Data2Db();
				data2Db.insertBaseData(srcDataBean);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	private SrcDataBean parseDocContext(Document doc) {
		SrcDataBean srcDataBean = new SrcDataBean();
		Elements tableList = doc
				.getElementsByAttributeValue("id", "tabtrend");
		//获取期号所在tds
		Elements issueTds = tableList.select("#tdMaxIsuse");
		Element td = issueTds.last();
		String issueId = td.text();
		if (issueId.compareTo(maxIssueId) > 0) {
			maxIssueId = issueId;
		} else {
			return null;
		}
		Element parent = td.parent();
		Elements tds = parent.select("td.fhuang,td.flanse,td.fzise");
		String issueIdStr = issueId;
		try {
			srcDataBean.setIssueId(issueIdStr.substring(2, issueIdStr.length()));
			srcDataBean.setNo1(Integer.parseInt(tds.eq(0).text()));
			srcDataBean.setNo2(Integer.parseInt(tds.eq(1).text()));
			srcDataBean.setNo3(Integer.parseInt(tds.eq(2).text()));
		} catch (NumberFormatException numEx) {
			LogUtil.error(issueIdStr + "@@网页内容解析错误@@" + numEx.getStackTrace());
		}
		return srcDataBean;
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
}
