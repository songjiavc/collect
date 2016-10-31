package yuceHandler.dantuo;

import java.util.Date;

public class Fast3DanMa{
	
	private String issueNumber;
	
	private String danmaOne;
	
	private String danmaTwo;
	 
	private Date createTime;
	
	private char status;

	public String getIssueNumber() {
		return issueNumber;
	}

	public void setIssueNumber(String issueNumber) {
		this.issueNumber = issueNumber;
	}

	public String getDanmaOne() {
		return danmaOne;
	}

	public String getDanmaTwo() {
		return danmaTwo;
	}

	public void setDanmaOne(String danmaOne) {
		this.danmaOne = danmaOne;
	}

	public void setDanmaTwo(String danmaTwo) {
		this.danmaTwo = danmaTwo;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

	
}
