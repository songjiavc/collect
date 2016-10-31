/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package jilin.tenminute.fivein11;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {
	public static synchronized void info(String info) {
		File path = new File("/home/server/logs/collect/"+ App.province +"/");
		if ((!(path.exists())) && (!(path.isDirectory()))) {
			path.mkdir();
		}
		File file = new File("/home/server/logs/collect/"+ App.province +"/info.log");
		if ((!(file.exists())) && (!(file.isFile()))) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file, true);
			fos.write((info + "\r\n").getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static synchronized void error(String error) {
		File path = new File("/home/server/logs/collect/"+ App.province +"/");
		if ((!(path.exists())) && (!(path.isDirectory()))) {
			path.mkdir();
		}
		File file = new File("/home/server/logs/collect/"+ App.province +"/error.log");
		if ((!(file.exists())) && (!(file.isFile()))) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file, true);
			fos.write((error + "\r\n").getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getNowTimeStr() {
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return dateFormat.format(now);
	}
}