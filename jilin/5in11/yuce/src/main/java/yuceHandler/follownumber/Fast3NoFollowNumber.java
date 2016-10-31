package yuceHandler.follownumber;

public class Fast3NoFollowNumber implements  Comparable<Fast3NoFollowNumber>{
	
	public Integer number;
	

	public Integer count30NoFollow;
	
	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}
	
	public Integer getCount30NoFollow() {
		return count30NoFollow;
	}

	public void setCount30NoFollow(Integer count30NoFollow) {
		this.count30NoFollow = count30NoFollow;
	}

	public int compareTo(Fast3NoFollowNumber o) {
		int flag = -1;
		flag = o.getCount30NoFollow().compareTo(this.getCount30NoFollow());
		if(flag == 0){
			flag = o.getNumber().compareTo(this.getNumber());
		}
		return flag;
	}
		
}
