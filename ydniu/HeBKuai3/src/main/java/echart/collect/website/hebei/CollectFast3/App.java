package echart.collect.website.hebei.CollectFast3;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class App {
	
    static Connection conn = null;
	
	static int maxIssueId = 0;
	
	static String websiteurl = null;
	
	static String srctable = null;
	/*
	 * 执行方法入口
	 */
	public static void main(String[] args) {
		initParam();
		Timer timer = new Timer();
		final App cellectDataTest = new App();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				cellectDataTest.collectData();
			}
		}, new Date(), 1000 * 20);// 每隔20秒输出

	}

	/*
	 * 
	 */
	private static  void initParam(){
		Properties p = new Properties();
		InputStream is = App.class.getClassLoader().getResourceAsStream("db.properties");
		try {
			p.load(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		srctable = p.getProperty("srctable");
		websiteurl = p.getProperty("websiteurl");
	}
	
	/*
	 * 连接数据采集数据方法
	 */
	private void collectData() {
		Document doc;
		try {
			doc = Jsoup.connect(
					  App.websiteurl).timeout(5000*2)
					.get();
			List<String> noList = parseDocContext(doc);
			if(noList != null && noList.size() > 0){
				DataToDb dataToDb = new DataToDb();
				dataToDb.haveDataInIssueId(noList);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private List<String> parseDocContext(Document doc){
		List<String> rtnList = new ArrayList<String>();
		Elements tableList = doc
				.getElementsByAttributeValue("id", "tabtrend");
		//获取期号所在tds
		Elements issueTds = tableList.select("#tdMaxIsuse");
		Element td = issueTds.last();
		String issueIdStr = td.text();
		int issueId = Integer.parseInt(issueIdStr.substring(2, issueIdStr.length()));
		if(issueId > maxIssueId ){
			maxIssueId = issueId;
		}else{
			return rtnList;
		}
		Element parent = td.parent();
		Elements tds = parent.select("td.fhuang,td.flanse,td.fzise");
		rtnList.add(issueId+"");
		for(int i = 0;i < tds.size();i++){
			rtnList.add(tds.eq(i).text());
		}
		return rtnList;
	}

}
