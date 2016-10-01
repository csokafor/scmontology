package uk.ac.liv.scm.ontology.entity;

public class ConfigurationItem extends Item {

	private Version hasVersion;
	
	
	public Version getHasVersion() {
		return hasVersion;
	}


	public void setHasVersion(Version hasVersion) {
		this.hasVersion = hasVersion;
	}


	@Override
	public String toString() {
		return "ConfigurationItem [itemName=" + getItemName()
				+ ", mimeType=" + getMimeType() + ", itemURI="
				+ getItemURI() + ", hasVersion=" + hasVersion.toString() + "]";
	}

	
}
