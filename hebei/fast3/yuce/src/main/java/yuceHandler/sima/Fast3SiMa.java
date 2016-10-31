package yuceHandler.sima;

import java.util.Date;

public class Fast3SiMa{
	
	private int id;
	
	private String yuceIssueStart;
	
	private String yuceIssueStop;
	
	private String drownPlan;
	
	private String drownIssueNumber;
	 
	private String drownNumber;
	
	private int drownCycle;
	
	private String status;
	
	private Date createTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getYuceIssueStart() {
		return yuceIssueStart;
	}

	public void setYuceIssueStart(String yuceIssueStart) {
		this.yuceIssueStart = yuceIssueStart;
	}

	public String getYuceIssueStop() {
		return yuceIssueStop;
	}

	public void setYuceIssueStop(String yuceIssueStop) {
		this.yuceIssueStop = yuceIssueStop;
	}

	public String getDrownPlan() {
		return drownPlan;
	}

	public void setDrownPlan(String drownPlan) {
		this.drownPlan = drownPlan;
	}

	public String getDrownIssueNumber() {
		return drownIssueNumber;
	}

	public void setDrownIssueNumber(String drownIssueNumber) {
		this.drownIssueNumber = drownIssueNumber;
	}

	public String getDrownNumber() {
		return drownNumber;
	}

	public void setDrownNumber(String drownNumber) {
		this.drownNumber = drownNumber;
	}

	public int getDrownCycle() {
		return drownCycle;
	}

	public void setDrownCycle(int drownCycle) {
		this.drownCycle = drownCycle;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
