package yuceHandler.dantuo;

public class FiveInCount implements  Comparable<FiveInCount>{
	
	public String issueId;
	
	public Integer number;
	
	public Integer count3;  //号码三天出现次数
	 
	public Integer count2;  //号码两天内出现次数
	 
	public Integer count1; //号码一天内出现次数

	public String getIssueId() {
		return issueId;
	}
	
	public void setIssueId(String issueId) {
		this.issueId = issueId;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}
  
	 

	public Integer getCount3() {
		return count3;
	}

	public Integer getCount2() {
		return count2;
	}

	public Integer getCount1() {
		return count1;
	}

	public void setCount3(Integer count3) {
		this.count3 = count3;
	}

	public void setCount2(Integer count2) {
		this.count2 = count2;
	}

	public void setCount1(Integer count1) {
		this.count1 = count1;
	}

	public int compareTo(FiveInCount o) {
		
		int flag = -1;
		flag = o.getCount3().compareTo(this.getCount3());
		if(flag == 0){
			flag = o.getCount2().compareTo(this.getCount2());
		}
		if(flag == 0){
			flag = o.getCount1().compareTo(this.getCount1());
		}
		if(flag == 0){
			flag = o.getNumber().compareTo(this.getNumber());
		}
		return flag;
	}
		
}
