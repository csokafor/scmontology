package uk.ac.liv.scm.ontology.entity;

import java.util.Date;

public class Issue {

	private String issueId;
	private String issueTitle;
	private String issueStatus;
	private String issueType;
	private String issueSeverity;
	private String buildVersion;
	private String issueDescription;
	private Date issueDate;
	private boolean approved;
	private Person isAssignedTo;
	
	public String getIssueId() {
		return issueId;
	}
	public void setIssueId(String issueId) {
		this.issueId = issueId;
	}
	public String getIssueTitle() {
		return issueTitle;
	}
	public void setIssueTitle(String issueTitle) {
		this.issueTitle = issueTitle;
	}
	public String getIssueStatus() {
		return issueStatus;
	}
	public void setIssueStatus(String issueStatus) {
		this.issueStatus = issueStatus;
	}
	public String getIssueType() {
		return issueType;
	}
	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}
	public String getIssueSeverity() {
		return issueSeverity;
	}
	public void setIssueSeverity(String issueSeverity) {
		this.issueSeverity = issueSeverity;
	}
	public String getBuildVersion() {
		return buildVersion;
	}
	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}
	public boolean isApproved() {
		return approved;
	}
	public void setApproved(boolean approved) {
		this.approved = approved;
	}
		
	public String getIssueDescription() {
		return issueDescription;
	}
	public void setIssueDescription(String issueDescription) {
		this.issueDescription = issueDescription;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}
		
	public Person getIsAssignedTo() {
		return isAssignedTo;
	}
	public void setIsAssignedTo(Person isAssignedTo) {
		this.isAssignedTo = isAssignedTo;
	}
	@Override
	public String toString() {
		return "Issue [issueId=" + issueId + ", issueTitle=" + issueTitle + ", issueDescription=" + issueDescription
				+ ", issueStatus=" + issueStatus + ", issueType=" + issueType
				+ ", issueSeverity=" + issueSeverity + ", buildVersion="
				+ buildVersion + ", approved=" + approved + "]";
	}
	
	
	
}
