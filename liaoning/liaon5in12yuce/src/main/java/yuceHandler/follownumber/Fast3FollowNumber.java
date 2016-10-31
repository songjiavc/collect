package yuceHandler.follownumber;

public class Fast3FollowNumber implements  Comparable<Fast3FollowNumber>{


	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer number;

	public Integer getFollowNumber() {
		return followNumber;
	}

	public void setFollowNumber(Integer followNumber) {
		this.followNumber = followNumber;
	}

	public Integer followNumber;

	public Integer getFollowCount() {
		return followCount;
	}

	public void setFollowCount(Integer followCount) {
		this.followCount = followCount;
	}

	public Integer getNoFollowCount() {
		return noFollowCount;
	}

	public void setNoFollowCount(Integer noFollowCount) {
		this.noFollowCount = noFollowCount;
	}

	public String getSortStyle() {
		return sortStyle;
	}

	public void setSortStyle(String sortStyle) {
		this.sortStyle = sortStyle;
	}

	public Integer followCount;

	public Integer getThreeFollowCount() {
		return threeFollowCount;
	}

	public void setThreeFollowCount(Integer threeFollowCount) {
		this.threeFollowCount = threeFollowCount;
	}

	public Integer getThreeNoFollowCount() {
		return threeNoFollowCount;
	}

	public void setThreeNoFollowCount(Integer threeNoFollowCount) {
		this.threeNoFollowCount = threeNoFollowCount;
	}

	public Integer threeFollowCount;

	public Integer noFollowCount;

	public Integer threeNoFollowCount;

	public String sortStyle;   //1  按照follow数量排序   2  按照nofollow排序；



	public int compareTo(Fast3FollowNumber o) {
		int flag = -1;
		if("1".equals(o.getSortStyle())){
			flag = o.getFollowCount().compareTo(this.getFollowCount());
		}else if("2".equals(o.getSortStyle())){
			flag = o.getThreeFollowCount().compareTo(this.getThreeFollowCount());
		}else if("3".equals(o.getSortStyle())){
			flag = o.getNoFollowCount().compareTo(this.getNoFollowCount());
		}else{
			flag = o.getThreeNoFollowCount().compareTo(this.getThreeNoFollowCount());
		}
		if(flag == 0){
			flag = o.getFollowNumber().compareTo(this.getFollowNumber());
		}
		return flag;
	}
		
}
