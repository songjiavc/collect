package yuceHandler.dantuo;

import java.util.Date;

public class Fast3DanMa{
	
	private String issueNumber;
	
	private Integer danmaOne;
	
	private Integer danmaTwo;
	 
	private Date createTime;
	
	private char status;

	public String getIssueNumber() {
		return issueNumber;
	}

	public void setIssueNumber(String issueNumber) {
		this.issueNumber = issueNumber;
	}

	public Integer getDanmaOne() {
		return danmaOne;
	}

	public void setDanmaOne(Integer danmaOne) {
		this.danmaOne = danmaOne;
	}

	public Integer getDanmaTwo() {
		return danmaTwo;
	}

	public void setDanmaTwo(Integer danmaTwo) {
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
