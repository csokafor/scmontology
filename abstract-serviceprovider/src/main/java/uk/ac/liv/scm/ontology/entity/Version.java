package uk.ac.liv.scm.ontology.entity;

import java.util.Collection;
import java.util.Date;

public class Version {
	
	private String versionId;
	private Date versionDate;
	private Person author;
	private String commitMessage;
	private boolean locked;
	private Collection<ConfigurationItem> modifiedItems;
	public String getVersionId() {
		return versionId;
	}
	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}
	public Date getVersionDate() {
		return versionDate;
	}
	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
	}
	public Person getAuthor() {
		return author;
	}
	public void setAuthor(Person author) {
		this.author = author;
	}
	public String getCommitMessage() {
		return commitMessage;
	}
	public void setCommitMessage(String commitMessage) {
		this.commitMessage = commitMessage;
	}
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	public Collection<ConfigurationItem> getModifiedItems() {
		return modifiedItems;
	}
	public void setModifiedItems(Collection<ConfigurationItem> modifiedItems) {
		this.modifiedItems = modifiedItems;
	}
	
	public void addModifiedItems(ConfigurationItem configurationItem) {
		modifiedItems.add(configurationItem);
    }

    public void removeModifiedItems(ConfigurationItem configurationItem) {
    	modifiedItems.remove(configurationItem);
    }
	@Override
	public String toString() {
		return "Version [versionId=" + versionId + ", versionDate="
				+ versionDate + ", author=" + author + ", commitMessage="
				+ commitMessage + ", locked=" + locked + "]";
	}
    
    

}
