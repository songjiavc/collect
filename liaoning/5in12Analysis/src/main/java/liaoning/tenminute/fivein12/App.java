package liaoning.tenminute.fivein12;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class App {
	
	static String maxIssueId = "";
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
		/*
		try {
			insertTestData();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	/*
	 * 初始化遗漏统计表结构，初始化过后不再使用，注释掉
	 */
	/*
	private static void insertTestData() throws SQLException {
		// TODO Auto-generated method stub
		Data2Db data2Db = new Data2Db();
		for (int i = 1; i <= 12; ++i) {
			for (int j = i + 1; j <= 12; ++j) {
					data2Db.insertTestGroupData(null, translate(i) + translate(j) , 0, 0, 2);
					for (int z = j + 1; z <= 12; ++z) {
						data2Db.insertTestGroupData(null,translate(i) + translate(j) + translate(z) , 0, 0, 3);
							for (int o = z + 1; o <= 12; ++o){
								data2Db.insertTestGroupData(null,translate(i) + translate(j) + translate(z)+ translate(o) , 0, 0, 4);
									for (int p = o + 1; p <= 12; ++p){
										data2Db.insertTestGroupData(null,translate(i) + translate(j) + translate(z)+ translate(o)+ translate(p) , 0, 0, 5);	
										for (int q = p + 1; q <= 12; ++q){
											data2Db.insertTestGroupData(null,translate(i) + translate(j) + translate(z)+ translate(o)+ translate(p)+ translate(q) , 0, 0, 6);	
											for (int r = q + 1; r <= 12; ++r){
												data2Db.insertTestGroupData(null,translate(i) + translate(j) + translate(z)+ translate(o)+ translate(p)+ translate(q)+ translate(r) , 0, 0, 7);	
												for (int s = r + 1; s <= 12; ++s){
													data2Db.insertTestGroupData(null,translate(i) + translate(j) + translate(z)+ translate(o)+ translate(p)+ translate(q)+ translate(r)+ translate(s) , 0, 0, 8);	
												}
											}
										}
									}
								}
						}
					}
				}
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
}
		
		