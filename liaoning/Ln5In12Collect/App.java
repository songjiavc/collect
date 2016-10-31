package webSite.Ln5In12;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class App {
	
	static Connection conn = null;
	
	static String maxIssueId = "";
	/*
	 * 执行方法入口
	 */
	public static void main(String[] args) {
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
	 * 连接数据采集数据方法
	 */
	private void collectData() {
		Document doc;
		try {
			doc = Jsoup.connect(
					"http://61.189.21.4:9080/sino/selectGamesData").timeout(5000*2)
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
		Elements ListDiv = doc.getElementsByAttributeValue("class",
				"tbody_tr");
		Element tr = ListDiv.first();
		String issueIdOdd = tr.select("td").first().text();
		Calendar a = Calendar.getInstance();
		Integer yearOdd = a.get(Calendar.YEAR);
		String year = yearOdd.toString().substring(2, 4);
		String issueIdTemp = year + issueIdOdd;
		String issueIdOdd1 = issueIdTemp.substring(0, 6);
		String issueIdOdd2 = issueIdTemp.substring(
				issueIdTemp.length() - 2, issueIdTemp.length());
		String issueId = issueIdOdd1 + issueIdOdd2;
		if(!issueId.equals(maxIssueId)){
			maxIssueId = issueId;
		}else{
			return rtnList;
		}
		// System.out.println(">>>>>>>>"+tr);
		String no1 = tr.select("td").eq(1).text();
		String no2 = tr.select("td").eq(2).text();
		String no3 = tr.select("td").eq(3).text();
		String no4 = tr.select("td").eq(4).text();
		String no5 = tr.select("td").eq(5).text();
		rtnList.add(issueId);
		rtnList.add(no1);
		rtnList.add(no2);
		rtnList.add(no3);
		rtnList.add(no4);
		rtnList.add(no5);
		return rtnList;
	}

}
