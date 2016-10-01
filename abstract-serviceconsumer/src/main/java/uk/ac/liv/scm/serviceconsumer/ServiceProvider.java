package uk.ac.liv.scm.serviceconsumer;


public class ServiceProvider {
	
	private String providerTitle;	
	private String providerURL;
	
	//for Basic HTTP Authentication
	private String username;
	private String password;
	
	public ServiceProvider(String providerURL, String username, String password) {
		this.providerURL = providerURL;
		this.username = username;
		this.password = password;
	}
	
	public String getProviderTitle() {
		return providerTitle;
	}
	public void setProviderTitle(String providerTitle) {
		this.providerTitle = providerTitle;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getProviderURL() {
		return providerURL;
	}
	public void setProviderURL(String providerURL) {
		this.providerURL = providerURL;
	}
	
	

}
