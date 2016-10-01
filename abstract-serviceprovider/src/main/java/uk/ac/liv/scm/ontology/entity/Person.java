package uk.ac.liv.scm.ontology.entity;

public class Person {

	private String personId;
	private String email = "";
	private String personName = "";
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPersonName() {
		return personName;
	}
	public void setPersonName(String personName) {
		this.personName = personName;
	}
	@Override
	public String toString() {
		return "Person [personId=" + personId + ", email=" + email
				+ ", personName=" + personName + "]";
	}
	
	
}
