package yuceHandler.samenumber;

public class Fast3SameNumber implements  Comparable<Fast3SameNumber>{
	
	public Integer number;
	
	public Integer count10;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getCount10() {
		return count10;
	}

	public void setCount10(Integer count10) {
		this.count10 = count10;
	}

	public int compareTo(Fast3SameNumber o) {
		int flag = -1;
		flag = o.getCount10().compareTo(this.getCount10());
		if(flag == 0){
			flag = o.getNumber().compareTo(this.getNumber());
		}
		return flag;
	}
		
}
