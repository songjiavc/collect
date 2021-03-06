package heilongjiang.tenminute.fivein11;

import java.util.List;


public class App {
	
	static String maxIssueId = "";
	/*
	 * 执行方法入口
	 */
	public static void main(String[] args) {
		/*
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				collectData();
			}
		}, new Date(), 1000 * 5);// 每隔5秒搜索一次
		*/
		initData();
	}
	
	/** 
	  * @Description: 
	  * @author songjia
	  * @date Feb 15, 2016 11:31:49 AM  
	  */
	private static void collectData(){
		Data2Db data2Db = new Data2Db();
		String maxIssueNumber = data2Db.findMaxIssueIdFromSrcDb();
		if(!maxIssueId.equals(maxIssueNumber)){
			maxIssueId = maxIssueNumber;
			// 将源数据库中数据插入到目的库中
			SrcDataBean srcDataBean = data2Db.getRecordByIssueId(maxIssueId);
			data2Db.insertBaseData(srcDataBean);
		}
	}
	/** 
	  * @Description: 
	  * @author songjia
	  * @date Feb 15, 2016 11:31:49 AM  
	  */
	private static void initData(){
		Data2Db data2Db = new Data2Db();
		List<SrcDataBean> dataList = data2Db.getAllNumber();
		// 将源数据库中数据插入到目的库中
		for(SrcDataBean srcDataBean : dataList){
			data2Db.insertBaseData(srcDataBean);
		}
		
	}
}
		
		