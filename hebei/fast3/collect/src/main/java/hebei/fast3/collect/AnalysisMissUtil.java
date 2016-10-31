package hebei.fast3.collect;

import java.util.Arrays;


/** 
  * @ClassName: AnalysisMissUtil 
  * @Description: 分析遗漏公共方法，存放所有遗漏计算方法 
  * @author songj@sdfcp.com
  * @date Feb 17, 2016 8:49:45 AM 
  *  
  */
public class AnalysisMissUtil {
	/** 
	  * @Description: 所有组合遗漏统计方法，6以下的都是组合，在统计6以下其他类型时进行分组或过滤统计
	  * @author songj@sdfcp.com
	  * @date Feb 17, 2016 9:00:39 AM 
	  * @param srcDataBean
	  * @return 
	  */
	public static String getAllNumMiss(SrcDataBean srcDataBean){
		String rtnSql = null;
		int[] noArr = new int[]{srcDataBean.getNo1(),srcDataBean.getNo2(),srcDataBean.getNo3()};
		Arrays.sort(noArr);
		String group = Integer.toString(noArr[0])+Integer.toString(noArr[1])+Integer.toString(noArr[2]);
		rtnSql = "UPDATE " + App.descMissTbName + " SET CURRENT_MISS = 0 WHERE TYPE < 6 AND GROUP_NUMBER = " + group;
		return rtnSql;
	}
	
	/** 
	  * @Description: 对所有组合进行
	  * @author songj@sdfcp.com
	  * @date Feb 17, 2016 9:46:34 AM 
	  * @param srcDataBean
	  * @return 
	  */
	public static String getAllGroupMiss(SrcDataBean srcDataBean){
		String rtnSql = null;
		int[] noArr = getIntArr(srcDataBean);
		String inStr = getGroupByNumber(noArr);
		rtnSql = "UPDATE " + App.descMissTbName + " SET CURRENT_MISS = 0 WHERE TYPE IN (6,7) AND GROUP_NUMBER IN ("+inStr+")";
		return rtnSql;
	}
	
	
	/** 
	  * @Description: 计算两码相同组合遗漏
	  * @author songj@sdfcp.com
	  * @date Feb 17, 2016 10:09:18 AM 
	  * @param srcDataBean
	  * @return 
	  */
	public static String getTwoSameGroupMiss(SrcDataBean srcDataBean){
		String rtnSql = null;
		// 首先判断开奖号码是否为两同号码
		int status = getNumberForm(srcDataBean);
		if(status == 2){
			int[] noArr = getIntArr(srcDataBean);
			String inStr = getGroupByNumber(noArr);
			rtnSql = "UPDATE " + App.descMissTbName + " SET CURRENT_MISS = 0 WHERE TYPE = 8 AND GROUP_NUMBER IN ("+inStr+")";
		}
		return rtnSql;
	}
	
	/** 
	  * @Description: 计算四码复式遗漏值
	  * @author songj@sdfcp.com
	  * @date Feb 17, 2016 10:16:44 AM 
	  * @param srcDataBean
	  * @return 
	  */
	public static String getFourNumGroupMiss(SrcDataBean srcDataBean){
		String rtnSql = null;
		int[] noArr = new int[]{srcDataBean.getNo1(),srcDataBean.getNo2(),srcDataBean.getNo3()};
		Arrays.sort(noArr);
		String group = noArr[0]+"%"+noArr[1]+"%"+noArr[2];
		rtnSql = "UPDATE " + App.descMissTbName + " SET CURRENT_MISS = 0 WHERE TYPE = 5 AND GROUP_NUMBER LIKE '%"+group+"%'";
		return rtnSql;
	}
	
	/** 
	  * @Description: 获取大小遗漏值
	  * @author songj@sdfcp.com
	  * @date Feb 17, 2016 10:45:39 AM 
	  * @param srcDataBean
	  * @return 
	  */
	public static String getSmallOrBigMiss(SrcDataBean srcDataBean){
		String rtnSql = null;
		int[] noArr = getIntArr(srcDataBean);
		if(noArr[2] < 4){
			//全小
			rtnSql = "UPDATE " + App.descMissTbName + " SET CURRENT_MISS = 0 WHERE TYPE = 9 ";
		}else if(noArr[0] > 3){
			//全大
			rtnSql = "UPDATE " + App.descMissTbName + " SET CURRENT_MISS = 0 WHERE TYPE = 10 ";
		}else if(noArr[1] > 3){
			//两大一小
			rtnSql = "UPDATE " + App.descMissTbName + " SET CURRENT_MISS = 0 WHERE TYPE = 11 ";
		}else{
			//两小一大
			rtnSql = "UPDATE " + App.descMissTbName + " SET CURRENT_MISS = 0 WHERE TYPE = 12 ";
		}
		return rtnSql;
	}
	
	/** 
	  * @Description: 获取奇偶遗漏
	  * @author songj@sdfcp.com
	  * @date Feb 17, 2016 11:18:15 AM 
	  * @param srcDataBean
	  * @return 
	  */
	public static String getOddOrEvenMiss(SrcDataBean srcDataBean){
		String rtnSql = null;
		int odd = 0;
		int[] noArr = getIntArr(srcDataBean);
		for(int i = 0;i < noArr.length;i++){
			if(noArr[i] % 2 == 1){
				odd ++;
			}
		}
		if(odd == 0){
			//全偶
			rtnSql = "UPDATE " + App.descMissTbName + " SET CURRENT_MISS = 0 WHERE TYPE = 14 ";
		}else if(odd == 3){
			//全奇
			rtnSql = "UPDATE " + App.descMissTbName + " SET CURRENT_MISS = 0 WHERE TYPE = 13 ";
		}else if(odd == 2){
			//两奇一偶
			rtnSql = "UPDATE " + App.descMissTbName + " SET CURRENT_MISS = 0 WHERE TYPE = 15 ";
		}else{
			//两偶一奇
			rtnSql = "UPDATE " + App.descMissTbName + " SET CURRENT_MISS = 0 WHERE TYPE = 16 ";
		}
		return rtnSql;
	}
	
	/** 
	  * @Description: 获取排序号的三个号码
	  * @author songj@sdfcp.com
	  * @date Feb 17, 2016 9:51:37 AM 
	  * @param srcDataBean
	  * @return 
	  */
	public static int[] getIntArr(SrcDataBean srcDataBean){
		int[] noArr = new int[]{srcDataBean.getNo1(),srcDataBean.getNo2(),srcDataBean.getNo3()};
		Arrays.sort(noArr);
		return noArr;
	}
	
	/** 
	  * @Description: 将整形数组转换为
	  * @author songj@sdfcp.com
	  * @date Feb 17, 2016 9:24:58 AM 
	  * @param arr
	  * @return 
	  */
	public static String ArrToStr(int[] arr){
		StringBuffer rtnStr = new StringBuffer();
		if(arr!=null && arr.length>0){
			for(int i = 0;i < arr.length; i++){
				rtnStr.append(arr[i]);
			}
			return rtnStr.toString();
		}
		return null;
	}
	
	/** 
	  * @Description: 根据开奖号码获取组合
	  * @author songj@sdfcp.com
	  * @date Feb 19, 2016 3:06:57 PM 
	  * @param arr
	  * @return 
	  */
	public static String getGroupByNumber(int[] arr){
		StringBuffer rtnStr = new StringBuffer();
		if(arr.length > 0){
			rtnStr.append(arr[0]*10+arr[1]).append(",").append(arr[0]*10+arr[2]).append(",").append(arr[1]*10+arr[2]);
		}
		return rtnStr.toString();
	}
	
	/** 
	  * @Description: 获取号码形态（1:三同;2:二同;3:三不同;）
	  * @author songj@sdfcp.com
	  * @date Feb 17, 2016 9:57:56 AM 
	  * @param srcDataBean
	  * @return 
	  */
	public static int getNumberForm(SrcDataBean srcDataBean){
		int status = 0;
		if((srcDataBean.getNo1() == srcDataBean.getNo2()) && (srcDataBean.getNo2() == srcDataBean.getNo3())){
			status = 1;
		}else if((srcDataBean.getNo1() != srcDataBean.getNo2()) && (srcDataBean.getNo2() != srcDataBean.getNo3()) && (srcDataBean.getNo3() != srcDataBean.getNo1())){
			status = 3;
		}else{
			status = 2;
		}
		return status;
	}
	
	public static String getZeroOneTwo(SrcDataBean srcDataBean){
		String rtnSql = null;
		int sum = srcDataBean.getNo1()+srcDataBean.getNo2()+srcDataBean.getNo3();
		if(sum % 3 == 0){
			rtnSql = "UPDATE " + App.descMissTbName + " SET CURRENT_MISS = 0 WHERE TYPE = 17 ";
		}else if(sum % 3 == 1){
			rtnSql = "UPDATE " + App.descMissTbName + " SET CURRENT_MISS = 0 WHERE TYPE = 18 ";
		}else{
			rtnSql = "UPDATE " + App.descMissTbName + " SET CURRENT_MISS = 0 WHERE TYPE = 19 ";
		}
		return rtnSql;
	}
	
	public static String getLottoryNum012(SrcDataBean srcDataBean){
		String rtnSql = null;
		int one = srcDataBean.getNo1()%3;
		int two = srcDataBean.getNo2()%3;
		int three = srcDataBean.getNo3()%3;
		int[] arr = {one,two,three};
		Arrays.sort(arr);
		String group = Integer.toString(arr[0]) + Integer.toString(arr[1])+Integer.toString(arr[2]);
		rtnSql = "UPDATE " + App.descMissTbName + " SET CURRENT_MISS = 0 WHERE TYPE = 20 AND GROUP_NUMBER =  '"+group+"'";
		return rtnSql;
	}
	/*
	public static void main(String[] args){
//		int[][] noArr1 = new int[][]{{1,1,1},{6,6,6},{3,3,3},{4,4,4},{5,3,6},{3,3,6}};
		SrcDataBean srcDataBean = new SrcDataBean();
		srcDataBean.setNo1(3);
		srcDataBean.setNo2(3);
		srcDataBean.setNo3(3);
		String test = getLottoryNum012(srcDataBean);
		System.out.println(test);
		//getNumberForm
	}*/
	
}
