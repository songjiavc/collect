package analysis.miss.everyday;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
  * @ClassName: FileUtil 
  * @Description: 存放创建日志文件 
  * @author songj@sdfcp.com
  * @date Feb 15, 2016 1:58:12 PM 
  *  
  */
public class LogUtil {
	/** 
	  * @Description: 打印提示信息
	  * @author songj@sdfcp.com
	  * @date Feb 15, 2016 2:14:06 PM 
	  * @param info 
	  */
	public static synchronized void info(String info){
		StringBuffer sb = new StringBuffer(getNowTimeStr());
		File path = new File("/home/server/logs/collect/jiangsu/");
		if(!path.exists() && !path.isDirectory()){
			path.mkdir();
		} 			
		File file = new File("/home/server/logs/collect/jiangsu/info.log");
		if(!file.exists() && !file.isFile()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file,true);
			fos.write(sb.append(info).append("\r\n").toString().getBytes());
			//System.out.println(sb.append(info).append("\r\n").toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 
	  * @Description: 打印错误信息
	  * @author songj@sdfcp.com
	  * @date Feb 15, 2016 2:14:06 PM 
	  * @param info 
	  */
	public static synchronized void error(String error){
		StringBuffer sb = new StringBuffer(getNowTimeStr());
		File path = new File("/home/server/logs/collect/jiangxi/");
		if(!path.exists() && !path.isDirectory()){
			path.mkdir();
		} 			
		File file = new File("/home/server/logs/collect/jiangxi/error.log");
		if(!file.exists() && !file.isFile()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file,true);
			fos.write(sb.append(error).append("\r\n").toString().getBytes());
			//System.out.println(sb.append(error).append("\r\n").toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 
	  * @Description: 获取系统当前时间
	  * @author songj@sdfcp.com
	  * @date Feb 27, 2016 12:09:30 PM 
	  * @return 
	  */
	private static String getNowTimeStr(){
		Date now = new Date(); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
		return dateFormat.format( now ); 
	}
}
