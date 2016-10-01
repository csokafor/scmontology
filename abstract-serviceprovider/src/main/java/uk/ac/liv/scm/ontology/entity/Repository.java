package uk.ac.liv.scm.ontology.entity;

import java.util.ArrayList;
import java.util.Collection;

public class Repository {

	private String repositoryName;
	private String repositoryURL;
	private String repositoryDescription;
	private Version lastVersion;
	private Collection<Branch> branches = new ArrayList<Branch>();
	private Collection<ConfigurationItem> configurationItems = new ArrayList<ConfigurationItem>();
	
	public String getRepositoryName() {
		return repositoryName;
	}
	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}
	public String getRepositoryURL() {
		return repositoryURL;
	}
	public void setRepositoryURL(String repositoryURL) {
		this.repositoryURL = repositoryURL;
	}
	public String getRepositoryDescription() {
		return repositoryDescription;
	}
	public void setRepositoryDescription(String repositoryDescription) {
		this.repositoryDescription = repositoryDescription;
	}
			
	public Version getLastVersion() {
		return lastVersion;
	}
	public void setLastVersion(Version lastVersion) {
		this.lastVersion = lastVersion;
	}
	public void addBranches(Branch newBranch) {
       branches.add(newBranch);
    }

    public void removeBranches(Branch oldBranch) {
       branches.remove(oldBranch);
    }
    
	public Collection<Branch> getBranches() {
		return branches;
	}
	public void setBranches(Collection<Branch> branches) {
		this.branches = branches;
	}
	
	
	public Collection<ConfigurationItem> getConfigurationItems() {
		return configurationItems;
	}
	public void setConfigurationItems(
			Collection<ConfigurationItem> configurationItems) {
		this.configurationItems = configurationItems;
	}
	
	public void addConfigurationItems(ConfigurationItem configurationItem) {
		configurationItems.add(configurationItem);
    }

    public void removeConfigurationItems(ConfigurationItem configurationItem) {
    	configurationItems.remove(configurationItem);
    }
	    
	@Override
	public String toString() {
		return "Repository [repositoryName=" + repositoryName
				+ ", repositoryURL=" + repositoryURL
				+ ", repositoryDescription=" + repositoryDescription
				+ ", branches=" + branches + "]";
	}	
		
	
}
