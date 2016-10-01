package uk.ac.liv.scm.ontology;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import uk.ac.liv.scm.util.OntologyUtil;
import uk.ac.liv.scm.util.ServiceProperties;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.ValidityReport;

public class SCMOntology {
	private static final Logger logger = Logger.getLogger(SCMOntology.class);
	protected static String SCM_OWL_BASE = "http://www.scm.org/scm.owl";
	protected static String SCM_NS = SCM_OWL_BASE + "#";
	protected static String SCM_STD_OWL_BASE = "http://www.scm.org/scmservice.owl";
	protected static String SCM_STD_NS = SCM_STD_OWL_BASE + "#";
	protected static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	
	private static SCMOntology scmOntology;
	protected OntModel scmModel;
	protected OntModel serviceModel;
	
	protected SCMOntology() {
				
		//load SCM ontology
		scmModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF);
		scmModel.read(ServiceProperties.getSCMOWLFile(), SCM_OWL_BASE, "RDF/XML");
		
		//create service model
		serviceModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF);		
		scmModel.addSubModel(serviceModel);		
		serviceModel.setNsPrefix("scmservice", SCM_STD_NS);
		serviceModel.setNsPrefix("scm", SCM_NS);					    			    		
						
	}
	
	public static SCMOntology getInstance() {
		if(scmOntology == null) {
			scmOntology = new SCMOntology();			
		}
		
		return scmOntology;
	}
	
	public OntModel getSCMModel() {
		return this.scmModel;
	}
	
	public OntModel getServiceModel() {
		return this.serviceModel;
	}
	
	public void clearServiceModel() {
		this.serviceModel.removeAll();
	}
			
	public String printServiceModel() {
		StringWriter modelWriter = new StringWriter();		
		validateServiceModel();
		serviceModel.write(modelWriter);
		return modelWriter.toString();
	}
	
	protected boolean validateServiceModel() {
		ValidityReport validity = serviceModel.validate();
	    if (validity.isValid()) {
	        logger.info("Service model is valid");
	    } else {
	        logger.warn("Conflicts");
	        for (Iterator i = validity.getReports(); i.hasNext(); ) {
	            logger.warn(" - " + i.next());
	        }
	    }
	    
	    return validity.isValid();
	}
	
	public String getRepository() {
		String repositoryXML = "";
		clearServiceModel();
		//get repository
		
		Individual repository = this.createRepository("repositoryX", "repositoryX", "http://repositoryurl");
		repositoryXML = this.printServiceModel();
		
		return repositoryXML;		
	}
	
	public String getRevision(String revisionNumber, String branchType, String branchName) {
		
		String revisionXML = "";
		clearServiceModel();
		//get revision
		
		Individual person = this.createPerson("staffId", "FirstName LastName", "person@email.com");
		String commitMessage = "commit message";
		String versionDate = dateFormat.format(new Date());
		Individual revision = this.createVersion(revisionNumber, versionDate, commitMessage, false, person);
		revisionXML = this.printServiceModel();
		
		return revisionXML;		
	}
	
	public String getRevisionHistory(String startDate, String endDate, 
			String branchType, String branchName) {
		
		String revisionXML = "";
		clearServiceModel();
		//get revision history
		
		Individual person = this.createPerson("staffId", "FirstName LastName", "person@email.com");
		String commitMessage = "commit message";
		String versionDate = dateFormat.format(new Date());
		Individual revision1 = this.createVersion("revision1", versionDate, commitMessage, false, person);
		Individual revision2 = this.createVersion("revision2" + "rh", versionDate, commitMessage, false, person);
		revisionXML = this.printServiceModel();
		
		return revisionXML;		
	}
	
	public String getConfigurationItem(String itemURI, String branchType, String branchName) {
		
		String configItemXML = "";
		clearServiceModel();
		//get configuration item
		
		Individual person = this.createPerson("staffId", "FirstName LastName", "person@email.com");
		String commitMessage = "commit message";
		String versionDate = dateFormat.format(new Date());
		Individual revision = this.createVersion("revisionNo", versionDate, commitMessage, false, person);
		
		String configItemContent = "test content";
		Individual configItem = this.createConfigurationItem("testitem", "txt", itemURI,
				configItemContent.getBytes(), revision);
		configItem.setPropertyValue(this.getObjectProperty("hasVersion"), revision);
		configItemXML = this.printServiceModel();
		
		return configItemXML;		
	}
	
	public String getConfigurationItemHistory(String itemURI, String branchType, String branchName) {
		
		String revisionXML = "";
		clearServiceModel();
		//get configuration item version history
		
		Individual person = this.createPerson("staffId", "FirstName LastName", "person@email.com");
		String commitMessage = "commit message";
		String versionDate = dateFormat.format(new Date());
		Individual revision1 = this.createVersion("123a", versionDate, commitMessage, false, person);
		Individual revision2 = this.createVersion("123b", versionDate, commitMessage, false, person);
		
		revisionXML = this.printServiceModel();
		
		return revisionXML;		
	}
	
	
	public String getRevisionByCommitMessage(String searchParameter, String branchType, String branchName) {
		
		String revisionXML = "";
		clearServiceModel();
		//get revisions with commit message that match searchParamer
		
		Individual person = this.createPerson("staffId", "FirstName LastName", "person@email.com");
		String commitMessage = "commit message";
		String versionDate = dateFormat.format(new Date());
		Individual revision1 = this.createVersion("123a", versionDate, commitMessage, false, person);
		Individual revision2 = this.createVersion("123cm", versionDate, commitMessage, false, person);
		
		revisionXML = this.printServiceModel();
		
		return revisionXML;		
	}
	
	public String createIssue(String issue) {
		String issueXML = "";
		clearServiceModel();
		
		InputStream is = new ByteArrayInputStream(issue.getBytes());
		serviceModel.read(is, SCM_OWL_BASE, "RDF/XML");
		
		Map<String,String> issuePropertyMap = OntologyUtil.getInstanceProperties(serviceModel, SCM_NS + "Issue");
		String issueId = issuePropertyMap.get("issueId");
		String issueTitle = issuePropertyMap.get("issueTitle");
		String issueDescription = issuePropertyMap.get("issueDescription");
		String issueType = issuePropertyMap.get("issueType");
		String issueStatus = issuePropertyMap.get("issueStatus");
		String issueSeverity = issuePropertyMap.get("issueSeverity");
		String buildVersion = issuePropertyMap.get("buildVersion");
		boolean approved = false;
		//String approved = issuePropertyMap.get("approved");
		String issueDate = issuePropertyMap.get("issueDate");
		Individual person = this.createPerson("staffId", "FirstName LastName", "person@email.com");
		
		clearServiceModel();
		//create issue
		
		Individual issueIndividual = this.createIssue(issueId, issueTitle, issueDescription, issueType, issueStatus,
				issueSeverity, buildVersion, issueDate, person, approved);
		issueXML = this.printServiceModel();
		
		return issueXML;
	}
	
	public String updateIssue(String issue) {
		String issueXML = "";
		clearServiceModel();
		
		InputStream is = new ByteArrayInputStream(issue.getBytes());
		serviceModel.read(is, SCM_OWL_BASE, "RDF/XML");
		
		Map<String,String> issuePropertyMap = OntologyUtil.getInstanceProperties(serviceModel, SCM_NS + "Issue");
		String issueId = issuePropertyMap.get("issueId");
		String issueTitle = issuePropertyMap.get("issueTitle");
		String issueDescription = issuePropertyMap.get("issueDescription");
		String issueType = issuePropertyMap.get("issueType");
		String issueStatus = issuePropertyMap.get("issueStatus");
		String issueSeverity = issuePropertyMap.get("issueSeverity");
		String buildVersion = issuePropertyMap.get("buildVersion");
		boolean approved = false;
		if(issuePropertyMap.get("approved") != null) {
			approved = Boolean.getBoolean(issuePropertyMap.get("approved"));
		}
		String issueDate = issuePropertyMap.get("issueDate");
		Individual person = this.createPerson("staffId", "FirstName LastName", "person@email.com");
		
		clearServiceModel();
		//update issue
		
		Individual issueIndividual = this.createIssue(issueId, issueTitle, issueDescription, issueType, issueStatus,
				issueSeverity, buildVersion, issueDate, person, approved);
		issueXML = this.printServiceModel();
		
		return issueXML;
	}
	
	public String getIssue(String issueId) {
		String issueXML = "";
		clearServiceModel();
		//get issue by issue id
		Individual person = this.createPerson("staffId", "FirstName LastName", "person@email.com");
		String issueDate = dateFormat.format(new Date());
		Individual issue = this.createIssue(issueId, "issueTitle", "description", "issueType", "issueStatus",
				"issueSeverity", "buildVersion", issueDate, person, true);
		issueXML = this.printServiceModel();
		
		return issueXML;
	}
	
	public String deleteIssue(String issueId) {
		String issueXML = "";
		clearServiceModel();
		//delete issue by issue id
		serviceModel.createTypedLiteral(issueId);
		issueXML = this.printServiceModel();
		
		return issueXML;
	}
	
	public String searchForIssue(String searchQuery) {
		String issueXML = "";
		clearServiceModel();
		//search for issues
		Individual person = this.createPerson("staffId", "FirstName LastName", "person@email.com");
		String issueDate = dateFormat.format(new Date());
		Individual issue1 = this.createIssue("issueId1", "issueTitle", "description", "issueType", "issueStatus",
				"issueSeverity", "buildVersion", issueDate, person, true);
		Individual issue2 = this.createIssue("issueId2", "issueTitle", "description", "issueType", "issueStatus",
				"issueSeverity", "buildVersion", issueDate, person, true);
		issueXML = this.printServiceModel();
		
		return issueXML;
	}
	
	protected Individual createIssue(String issueId, String issueTitle, String issueDescription, String issueType, 
			String issueStatus, String issueSeverity, String buildVersion, String issueDate, 
			 Individual person, boolean approved) {
		
		Individual issue = null;
		OntClass ontClass = scmModel.getOntClass(SCM_NS + "Issue");
		issue = serviceModel.createIndividual(SCM_NS + issueId, ontClass);
				
		issue.setPropertyValue(getDatatypeProperty("issueId"), scmModel.createTypedLiteral(issueId));
		issue.setPropertyValue(getDatatypeProperty("issueTitle"), scmModel.createTypedLiteral(issueTitle));
		issue.setPropertyValue(getDatatypeProperty("issueType"), scmModel.createTypedLiteral(issueType));
		issue.setPropertyValue(getDatatypeProperty("issueStatus"), scmModel.createTypedLiteral(issueStatus));
		issue.setPropertyValue(getDatatypeProperty("issueSeverity"), scmModel.createTypedLiteral(issueSeverity));
		issue.setPropertyValue(getDatatypeProperty("buildVersion"), scmModel.createTypedLiteral(buildVersion));
		issue.setPropertyValue(getDatatypeProperty("approved"), scmModel.createTypedLiteral(approved));
		issue.setPropertyValue(getDatatypeProperty("issueDescription"), scmModel.createTypedLiteral(issueDescription));
		issue.setPropertyValue(getDatatypeProperty("issueDate"), scmModel.createTypedLiteral(issueDate, XSDDatatype.XSDdateTime));
				
		if(person != null) {
			issue.addProperty(getObjectProperty("isAssignedTo"), person);
		}
		
		return issue;
	}
	
	protected Individual createConfigurationItem(String itemName, String mimeType, 
			String itemURI, byte[] itemContent, Individual version) {
		
		Individual configItem = null;
		OntClass ontClass = scmModel.getOntClass(SCM_NS + "ConfigurationItem");
		configItem = serviceModel.createIndividual(SCM_NS + itemName, ontClass);
				
		configItem.setPropertyValue(getDatatypeProperty("itemName"), scmModel.createTypedLiteral(itemName));
		configItem.setPropertyValue(getDatatypeProperty("mimeType"), scmModel.createTypedLiteral(mimeType));
		if(itemContent != null) {
			configItem.setPropertyValue(getDatatypeProperty("itemContent"), scmModel.createTypedLiteral(itemContent));
		}
		configItem.setPropertyValue(getDatatypeProperty("itemURI"), scmModel.createTypedLiteral(itemURI, XSDDatatype.XSDanyURI));
		if(version != null) {
			configItem.addProperty(getObjectProperty("hasVersion"), version);
		}
				
		return configItem;
	}
	
	protected Individual createRepository(String name, String description, 
			String repositoryURL) {
		
		Individual repository = null;
		OntClass ontClass = scmModel.getOntClass(SCM_NS + "Repository");
		repository = serviceModel.createIndividual(SCM_NS + name, ontClass);
				
		repository.setPropertyValue(getDatatypeProperty("repositoryName"), scmModel.createTypedLiteral(name));
		repository.setPropertyValue(getDatatypeProperty("repositoryDescription"), scmModel.createTypedLiteral(description));
		repository.setPropertyValue(getDatatypeProperty("repositoryURL"), scmModel.createTypedLiteral(repositoryURL, XSDDatatype.XSDanyURI));
				
		return repository;
	}
	
	protected Individual createVersion(String versionId, String versionDate, 
			String commitMessage, boolean locked, Individual person) {
		
		Individual version = null;
		OntClass ontClass = scmModel.getOntClass(SCM_NS + "Version");
		version = serviceModel.createIndividual(SCM_NS + versionId, ontClass);
				
		version.setPropertyValue(getDatatypeProperty("versionId"), scmModel.createTypedLiteral(versionId));
		if(commitMessage != null) {
			version.setPropertyValue(getDatatypeProperty("commitMessage"), scmModel.createTypedLiteral(commitMessage));
		} else {
			version.setPropertyValue(getDatatypeProperty("commitMessage"), scmModel.createTypedLiteral(""));
		}
		version.setPropertyValue(getDatatypeProperty("locked"), scmModel.createTypedLiteral(locked));
		version.setPropertyValue(getDatatypeProperty("versionDate"), scmModel.createTypedLiteral(versionDate, XSDDatatype.XSDdateTime));
		if(person != null) {
			version.addProperty(getObjectProperty("author"), person);
		}
				
		return version;
	}
	
	protected Individual createPerson(String personId, String personName, String email) {
		
		Individual person = null;
		OntClass ontClass = scmModel.getOntClass(SCM_NS + "Person");
		person = serviceModel.createIndividual(SCM_NS + personId, ontClass);
				
		person.setPropertyValue(getDatatypeProperty("personId"), scmModel.createTypedLiteral(personId));
		person.setPropertyValue(getDatatypeProperty("personName"), scmModel.createTypedLiteral(personName));
		person.setPropertyValue(getDatatypeProperty("email"), scmModel.createTypedLiteral(email));
				
		return person;
	}
	
	protected Individual createBranch(String branchName, String branchType) {
		
		Individual branch = null;
		OntClass ontClass = scmModel.getOntClass(SCM_NS + "Branch");
		branch = serviceModel.createIndividual(SCM_NS + branchName, ontClass);				
		branch.setPropertyValue(getDatatypeProperty("branchName"), scmModel.createTypedLiteral(branchName));
		branch.setPropertyValue(getDatatypeProperty("branchType"), scmModel.createTypedLiteral(branchType));
						
		return branch;
	}
	
	protected ObjectProperty getObjectProperty(String propertyName) {
		ObjectProperty objectProperty = scmModel.getObjectProperty(SCM_NS + propertyName);
		return objectProperty;
	}
	
	protected DatatypeProperty getDatatypeProperty(String propertyName) {
		DatatypeProperty datatypeProperty = scmModel.getDatatypeProperty(SCM_NS + propertyName);
		return datatypeProperty;
	}	


}
