package webSite.fivein20;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
public class App {
	
	static Connection conn = null;
	
	static String maxIssueId = "";
	
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
		String regexp = "\'";
		Map<String, Object> map = new HashMap<String, Object>();
		Element script = doc.select("script").eq(22).first();
//		System.out.println(scripts.html());
		String[] datas = script.data().toString().split("var");
		for(String data : datas){
			if(data.contains("=")){
				 if(data.contains("latest_draw_result") || data.contains("latest_draw_phase")){
					 String[]  kvp = data.split("=");  
					 if(!map.containsKey(kvp[0].trim())){
                            map.put(kvp[0].trim(), kvp[1].trim().substring(0, kvp[1].trim().length()-1).toString());  
                    }  
				 }
			}
		}
		String issueNumber = ((String)map.get("latest_draw_phase")).replaceAll(regexp,"");
		if(!maxIssueId.equals(issueNumber)){
			maxIssueId = issueNumber;
		}else{
			return rtnList;
		}
		String dataStr = (String)map.get("latest_draw_result");
		JSONObject job = JSONObject.parseObject(dataStr);
		JSONArray ja = job.getJSONArray("red");
		rtnList.add(issueNumber.substring(2, issueNumber.length()));
		rtnList.add(ja.getString(0));
		rtnList.add(ja.getString(1));
		rtnList.add(ja.getString(2));
		rtnList.add(ja.getString(3));
		rtnList.add(ja.getString(4));
		return rtnList;
	}

}
