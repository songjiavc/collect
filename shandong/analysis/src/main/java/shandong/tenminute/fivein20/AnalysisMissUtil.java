/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package shandong.tenminute.fivein20;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AnalysisMissUtil {

	public static List<String> updateGroupMiss(SrcDataBean srcDataBean, int n) {
		List<String> sqlList = new ArrayList<String>();
		int[] noArr = getIntArr(srcDataBean);
		String inStr = getGroupByNumber(noArr, n);//获取开奖号码组合
		
		//首先将任N的遗漏统计加入待更新list中
		sqlList.add("UPDATE T_SD_5IN20_MISSANALYSIS SET CURRENT_MISS = 0 WHERE TYPE = " + n + "  AND GROUP_NUMBER IN ("+ inStr + ")");
		//当N=2 时 处理任二，三码复式问题
		if(n == 2){
			List<String > likeGroupList = getLikeGroupByNumber(noArr,n);
			for(String likeGroup : likeGroupList){
				//任二三码
				sqlList.add("UPDATE T_SD_5IN20_MISSANALYSIS SET CURRENT_MISS = 0 WHERE TYPE = " + Constants.TYPE_REN2_FUSHI3 + "  AND GROUP_NUMBER LIKE ("+ likeGroup + ")");
			    //任二四码
				sqlList.add("UPDATE T_SD_5IN20_MISSANALYSIS SET CURRENT_MISS = 0 WHERE TYPE = " + Constants.TYPE_REN2_FUSHI4 + "  AND GROUP_NUMBER LIKE ("+ likeGroup + ")");
				//任二五码
				sqlList.add("UPDATE T_SD_5IN20_MISSANALYSIS SET CURRENT_MISS = 0 WHERE TYPE = " + Constants.TYPE_REN2_FUSHI5 + "  AND GROUP_NUMBER LIKE ("+ likeGroup + ")");
			}
		}else if(n == 3){
			//任三四码复式
			List<String > likeGroupList = getLikeGroupByNumber(noArr,n);
			for(String likeGroup : likeGroupList){
				//任二三码
				sqlList.add("UPDATE T_SD_5IN20_MISSANALYSIS SET CURRENT_MISS = 0 WHERE TYPE = " + Constants.TYPE_REN3_FUSHI4 + "  AND GROUP_NUMBER LIKE ("+ likeGroup + ")");
			    //任二四码
				sqlList.add("UPDATE T_SD_5IN20_MISSANALYSIS SET CURRENT_MISS = 0 WHERE TYPE = " + Constants.TYPE_REN3_FUSHI5 + "  AND GROUP_NUMBER LIKE ("+ likeGroup + ")");
				//任二五码
				sqlList.add("UPDATE T_SD_5IN20_MISSANALYSIS SET CURRENT_MISS = 0 WHERE TYPE = " + Constants.TYPE_REN3_FUSHI6 + "  AND GROUP_NUMBER LIKE ("+ likeGroup + ")");
			}
		}else if(n == 4){
			List<String > likeGroupList = getLikeGroupByNumber(noArr,n);
			for(String likeGroup : likeGroupList){
				//任四五码复式
				sqlList.add("UPDATE T_SD_5IN20_MISSANALYSIS SET CURRENT_MISS = 0 WHERE TYPE = " + Constants.TYPE_REN4_FUSHI5 + "  AND GROUP_NUMBER LIKE ("+ likeGroup + ")");
				//任四六码复式
				sqlList.add("UPDATE T_SD_5IN20_MISSANALYSIS SET CURRENT_MISS = 0 WHERE TYPE = " + Constants.TYPE_REN4_FUSHI6 + "  AND GROUP_NUMBER LIKE ("+ likeGroup + ")");
				//任四七码复式
				sqlList.add("UPDATE T_SD_5IN20_MISSANALYSIS SET CURRENT_MISS = 0 WHERE TYPE = " + Constants.TYPE_REN4_FUSHI7 + "  AND GROUP_NUMBER LIKE ("+ likeGroup + ")");
			}
		}
		return sqlList;
	}

	public static String updateGreatFiveGroupMiss(SrcDataBean srcDataBean, int n) {
		String rtnSql = null;
		int[] noArr = getIntArr(srcDataBean);
		rtnSql = "UPDATE T_SD_5IN20_MISSANALYSIS SET CURRENT_MISS = 0 WHERE TYPE = " + n + "  AND GROUP_NUMBER LIKE  '%"
				+ App.translate(noArr[0]) + "%" + App.translate(noArr[1]) + "%" + App.translate(noArr[2]) + "%"
				+ App.translate(noArr[3]) + "%" + App.translate(noArr[4]) + "%'";
		return rtnSql;
	}
	/**********************************围选遗漏统计区域********************************************/
	/**
	 *     @param srcDataBean
	 *     add by songjia  前二组选遗漏统计和前二组三、四、五、六、七、八
	 * * @return
	 */
	public static String updateBeforeRen2GroupMiss(SrcDataBean srcDataBean) {
		int[] noArr = { srcDataBean.getNo1(), srcDataBean.getNo2() };
		Arrays.sort(noArr);
		return "UPDATE T_SD_5IN20_MISSANALYSIS SET CURRENT_MISS = 0 WHERE TYPE = " + Constants.TYPE_WEI2 + "  AND GROUP_NUMBER = '"+ App.translate(noArr[0]) + App.translate(noArr[1]) + "'";
	}

	public static String updateBeforeRen3GroupMiss(SrcDataBean srcDataBean) {
		int[] noArr = { srcDataBean.getNo1(), srcDataBean.getNo2(),srcDataBean.getNo3()};
		Arrays.sort(noArr);
		return "UPDATE T_SD_5IN20_MISSANALYSIS SET CURRENT_MISS = 0 WHERE TYPE = " + Constants.TYPE_WEI3 + "  AND GROUP_NUMBER = '"+ App.translate(noArr[0]) + App.translate(noArr[1]) + App.translate(noArr[2]) + "'";
	}
	
	
	public static String updateBeforeRen4GroupMiss(SrcDataBean srcDataBean) {
		int[] noArr = { srcDataBean.getNo1(), srcDataBean.getNo2(),srcDataBean.getNo3(),srcDataBean.getNo4()};
		Arrays.sort(noArr);
		return "UPDATE T_SD_5IN20_MISSANALYSIS SET CURRENT_MISS = 0 WHERE TYPE = " + Constants.TYPE_WEI4 + "  AND GROUP_NUMBER = '"+ App.translate(noArr[0]) + App.translate(noArr[1]) + App.translate(noArr[2])+ App.translate(noArr[3]) + "'";
	}
	
	
	/**********************************围选遗漏统计区域 end ********************************************/
	
	/**********************************顺选遗漏统计区域 start******************************************/
	
	public static String updateDirect1GroupMiss(SrcDataBean srcDataBean) {
		String rtnSql = null;
		rtnSql = "UPDATE T_SD_5IN20_MISSANALYSIS SET CURRENT_MISS = 0 WHERE TYPE = "+ Constants.TYPE_SHUN1 +"  AND GROUP_NUMBER = '"
				+ App.translate(srcDataBean.getNo1()) + "'";
		return rtnSql;
	}
	
	public static String updateDirect2GroupMiss(SrcDataBean srcDataBean) {
		String rtnSql = null;
		rtnSql = "UPDATE T_SD_5IN20_MISSANALYSIS SET CURRENT_MISS = 0 WHERE TYPE = "+ Constants.TYPE_SHUN2 +"  AND GROUP_NUMBER = '"
				+ App.translate(srcDataBean.getNo1()) + App.translate(srcDataBean.getNo2()) + "'";
		return rtnSql;
	}
	
	public static String updateDirect3GroupMiss(SrcDataBean srcDataBean) {
		String rtnSql = null;
		rtnSql = "UPDATE T_SD_5IN20_MISSANALYSIS SET CURRENT_MISS = 0 WHERE TYPE = "+ Constants.TYPE_SHUN3 + "  AND GROUP_NUMBER = '"
				+ App.translate(srcDataBean.getNo1()) + App.translate(srcDataBean.getNo2()) + App.translate(srcDataBean.getNo3())
				+ "'";
		return rtnSql;
	}

	public static int[] getIntArr(SrcDataBean srcDataBean) {
		int[] noArr = { srcDataBean.getNo1(), srcDataBean.getNo2(), srcDataBean.getNo3(), srcDataBean.getNo4(),
				srcDataBean.getNo5() };
		Arrays.sort(noArr);
		return noArr;
	}

	public static String getGroupByNumber(int[] arr, int n) {
		StringBuffer rtnStr = new StringBuffer();
		if (arr.length > 0) {
			for (int i = 0; i < arr.length; ++i) {
				for (int j = i + 1; j < arr.length; ++j) {
					if (n == 2) {
						if (j != 1) {
							rtnStr.append(",");
						}
						rtnStr.append("'" + App.translate(arr[i]) + App.translate(arr[j]) + "'");
					} else {
						for (int z = j + 1; z < arr.length; ++z) {
							if (n == 3) {
								if (z != 2) {
									rtnStr.append(",");
								}
								rtnStr.append("'" + App.translate(arr[i]) + App.translate(arr[j]) + App.translate(arr[z]) + "'");
							} else {
								for (int o = z + 1; o < arr.length; ++o)
									if (n == 4) {
										if (o != 3) {
											rtnStr.append(",");
										}
										rtnStr.append("'" + App.translate(arr[i]) + App.translate(arr[j]) + App.translate(arr[z])
												+ App.translate(arr[o]) + "'");
									} else {
										for (int p = o + 1; p < arr.length; ++p)
											if (n == 5) {
												if (p != 4) {
													rtnStr.append(",");
												}
												rtnStr.append(
														"'" + App.translate(arr[i]) + App.translate(arr[j]) + App.translate(arr[z])
																+ App.translate(arr[o]) + App.translate(arr[p]) + "'");
											}
									}
							}
						}
					}
				}
			}
		}
		return rtnStr.toString();
	}

	public static List<String> getLikeGroupByNumber(int[] arr, int n) {
		List<String> rtnList = new ArrayList<String>();
		if (arr.length > 0) {
			for (int i = 0; i < arr.length; ++i) {
				for (int j = i + 1; j < arr.length; ++j) {
					if (n == 2) {
						rtnList.add("'%" + App.translate(arr[i]) + "%" + App.translate(arr[j]) + "%'");
					} else {
						for (int z = j + 1; z < arr.length; ++z) {
							if (n == 3) {
								rtnList.add("'%" + App.translate(arr[i]) + "%" + App.translate(arr[j]) +"%" + App.translate(arr[z]) + "%'");
							}else {
								for (int o = z + 1; o < arr.length; ++o){
										rtnList.add("'%" + App.translate(arr[i]) + "%" + App.translate(arr[j]) +"%" + App.translate(arr[z]) +"%" + App.translate(arr[o]) + "%'");
								}
							}
						}
					}
				}
			}
		}
		return rtnList;
	}

}