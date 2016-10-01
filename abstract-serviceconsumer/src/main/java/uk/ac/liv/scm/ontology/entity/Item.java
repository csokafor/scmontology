package uk.ac.liv.scm.ontology.entity;

public class Item {

	public static String DIRECTORY = "directory";
	public static String TEXT = "text";
	public static String BINARY = "binary";
	
	private String itemName;
	private String mimeType;
	private String itemURI;
	private byte[] itemContent;
	
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public String getItemURI() {
		return itemURI;
	}
	public void setItemURI(String itemURI) {
		this.itemURI = itemURI;
	}
	public byte[] getItemContent() {
		return itemContent;
	}
	public void setItemContent(byte[] itemContent) {
		this.itemContent = itemContent;
	}
	
	
}
