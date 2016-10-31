package webSite.fivein11;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil{ 
	
	/** 
	  * @Description: 获取文件输出流
	  * @author songj@sdfcp.com
	  * @date 2015年12月12日 上午8:46:24 
	  * @return 
	  */
	public static FileOutputStream getLogFileOutputStream(){
		FileOutputStream fos = null;
	    try{
	    	Date date = new Date();
			SimpleDateFormat ym = new SimpleDateFormat("yyyyMM");
			String ymStr = ym.format(date);
			File path = new File("bakdata/"+ymStr+"/");
			if(!path.exists() && !path.isDirectory()){
				path.mkdir();
			}    			
			File file = new File("bakdata/"+ymStr+"/"+getFileName());
			if(!file.exists() && !file.isFile()){
				file.createNewFile();
			} 
	    	fos = new FileOutputStream(file,true); 
    	}catch(FileNotFoundException fileNotFound){
    		fileNotFound.printStackTrace();
    	}catch(IOException ioEx){
    		ioEx.printStackTrace();
    	}
	    return fos;
	}
	private static String getFileName(){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    return sdf.format(date)+".log";
	}
	
	public static void closeFileStream(FileOutputStream fileOutputStream){
		if(fileOutputStream != null ){
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}