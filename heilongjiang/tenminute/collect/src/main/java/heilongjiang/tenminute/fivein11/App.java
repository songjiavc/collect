package heilongjiang.tenminute.fivein11;

import java.util.List;

public class App {
	
	static String maxIssueId = "";
	/*
	 * 执行方法入口
	 */
	public static void main(String[] args) {
		/*	Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				collectData();
			}
		}, new Date(), 1000 * 5);// 每隔5秒搜索一次
		*/
			insertTestData();
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
		String maxIssueNumber = data2Db.findMaxIssueIdFromSrcDb();
		if(!maxIssueId.equals(maxIssueNumber)){
			maxIssueId = maxIssueNumber;
			// 将源数据库中数据插入到目的库中
			SrcDataBean srcDataBean = data2Db.getRecordByIssueId(maxIssueId);
			data2Db.insertBaseData(srcDataBean);
		}
	}
	
	private static void insertTestData(){
		Data2Db data2Db = new Data2Db();
		List<SrcDataBean> dataList = data2Db.getAllRecords();
		// 将源数据库中数据插入到目的库中
		for(SrcDataBean param : dataList){
			SrcDataBean srcDataBean = data2Db.getRecordByIssueIdTest(param);
			data2Db.insertBaseData(srcDataBean);
		}
	}
}
		
		