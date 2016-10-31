package shandong.tenminute.fivein20;

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
	}
	/*
	 * 初始化遗漏统计表结构，初始化过后不再使用，注释掉
	 */
	/*
	private static void insertTestData() throws SQLException {
		// TODO Auto-generated method stub
		Connection conn = ConnectDesDb.getDesConnection();
		Data2Db data2Db = new Data2Db();
		for (int i = 1; i <= 20; ++i) {
			data2Db.insertTestGroupData(null, translate(i) , 0, 0, 11,conn);
			/*for (int j = i+1 ; j <= 20; ++j) {
			//	data2Db.insertTestGroupData(null, translate(i) + translate(j) , 0, 0, 10);
				for (int z = j + 1; z <= 20; ++z) {
					for(int o = z+1;o <= 20;o++){
						for(int p = o+1;p <= 20;p++){
							for(int q = p+1;q <=20;q++){
								for(int w = q+1;w <= 20;w++){
									for(int e = w+1;e <= 20;e++){
										for(int f = e+1;f<=20;f++){
											for(int g = f+1;g <= 20;g++){
												data2Db.insertTestGroupData(null,translate(i) + translate(j) + translate(z)+ translate(o)+ translate(p)+ translate(q)+ translate(w)+ translate(e)+ translate(f)+ translate(g) , 0, 0, 10,conn);
											}
										}
									}
								}
							}
						}
					}
				}
		}
	}*/

	public static String translate(int temp){
		String rtn = null;
		if(temp < 10){
			rtn = temp+"";
		}else{
			if(temp == 10){
				rtn = "A";
			}else if(temp == 11){
				rtn = "B";
			}else if(temp == 12){
				rtn = "C";  
			}else if(temp == 13){
				rtn = "D";
			}else if(temp == 14){
				rtn = "E";
			}else if(temp == 15){
				rtn = "F";
			}else if(temp == 16){
				rtn = "G";
			}else if(temp == 17){
				rtn = "H";
			}else if(temp == 18){
				rtn = "I";
			}else if(temp == 19){
				rtn = "J";
			}else if(temp == 20){
				rtn = "K";
			}
		}
		return rtn;
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
}
		
		