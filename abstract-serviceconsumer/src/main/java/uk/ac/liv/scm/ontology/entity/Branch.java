package uk.ac.liv.scm.ontology.entity;

public class Branch {

	private static String TAG = "tag";
	private static String BRANCH = "branch";
	
	private String branchName;
	private String branchType;

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getBranchType() {
		return branchType;
	}

	public void setBranchType(String branchType) {
		this.branchType = branchType;
	}

	@Override
	public String toString() {
		return "Branch [branchName=" + branchName + ", branchType="
				+ branchType + "]";
	}
		
	
}
