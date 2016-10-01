package uk.ac.liv.scm.ontology;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import uk.ac.liv.scm.ontology.entity.ConfigurationItem;
import uk.ac.liv.scm.ontology.entity.Issue;
import uk.ac.liv.scm.ontology.entity.Person;
import uk.ac.liv.scm.ontology.entity.Repository;
import uk.ac.liv.scm.ontology.entity.Revision;
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

public class SCMOntology {
	
	private static final Logger logger = Logger.getLogger(SCMOntology.class);
	private static String SCM_OWL_BASE = "http://www.scm.org/scm.owl";
	private static String SCM_NS = SCM_OWL_BASE + "#";
	private static String SCM_STD_OWL_BASE = "http://www.scm.org/scmservice.owl";
	private static String SCM_STD_NS = SCM_STD_OWL_BASE + "#";
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	private static SCMOntology scmOntology;
	private OntModel scmModel;
	private OntModel serviceModel;
	
	private SCMOntology() {
				
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
		serviceModel.write(modelWriter);
		return modelWriter.toString();
	}
	
	public void readServiceModel(String rdfXML) {
		InputStream is = new ByteArrayInputStream(rdfXML.getBytes());
		serviceModel.read(is, SCM_OWL_BASE, "RDF/XML");	
	}
	
	public Repository getRepository(String repositoryXML) {
		Repository repository = new Repository();
		clearServiceModel();		
		readServiceModel(repositoryXML);
		
		Map<String,String> repoPropertyMap = OntologyUtil.getInstanceProperties(serviceModel, SCM_NS + "Repository");
		String repositoryName = repoPropertyMap.get("repositoryName");
		String repositoryURL = repoPropertyMap.get("repositoryURL");
		String repositoryDescription = repoPropertyMap.get("repositoryDescription");
		
		repository.setRepositoryName(repositoryName);
		repository.setRepositoryURL(repositoryURL);
		repository.setRepositoryDescription(repositoryDescription);
		
		String version = repoPropertyMap.get("lastVersion");
		if(version != null) {
			Map<String,String> versionPropertyMap = OntologyUtil.getResourceProperties(serviceModel, version);
			Revision revision = this.createRevision(versionPropertyMap);
			repository.setLastVersion(revision);
		}
		
		logger.info(repository);
		
		return repository;		
	}
	
	public Revision getRevision(String revisionXML) {
		
		Revision revision = new Revision();
		clearServiceModel();
		readServiceModel(revisionXML);
		Map<String,String> revisionPropertyMap = OntologyUtil.getInstanceProperties(serviceModel, SCM_NS + "Version");
		logger.info("revisionPropertyMap: " + revisionPropertyMap.toString());
		revision = this.createRevision(revisionPropertyMap);
		logger.info(revision);
		
		return revision;		
	}
	
	public List<Revision> getRevisionList(String revisionXML) {
		
		List<Revision> revisionList = new LinkedList<Revision>();
		clearServiceModel();
		readServiceModel(revisionXML);
		
		Map<String,Map<String,String>> instanceMap = OntologyUtil.getAllInstanceProperties(serviceModel,  SCM_NS + "Version");
		Iterator<String> instanceIt = instanceMap.keySet().iterator();
		while(instanceIt.hasNext()) {
			String instanceKey = instanceIt.next();			
			Map<String,String> revisionPropertyMap = instanceMap.get(instanceKey);
			Revision revision = this.createRevision(revisionPropertyMap);
			logger.info(revision);
			revisionList.add(revision);
		}
						
		return revisionList;		
	}
	
	protected Revision createRevision(Map<String,String> revisionPropertyMap) {
		Revision revision = new Revision();
		String versionId = revisionPropertyMap.get("versionId");
		String commitMessage = revisionPropertyMap.get("commitMessage");
		Date versionDate = new Date();
		boolean locked = false;
		Person author = new Person();
		
		if(revisionPropertyMap.get("locked") != null) {
			locked = new Boolean(revisionPropertyMap.get("locked"));
		}			
		try {
			versionDate = dateFormat.parse(revisionPropertyMap.get("versionDate"));
		} catch(ParseException pe) {
			logger.error("Cannot parse version date " + revisionPropertyMap.get("versionDate"));
		}
		
		String person = revisionPropertyMap.get("author");
		if(person != null) {
			Map<String,String> authorPropertyMap = OntologyUtil.getResourceProperties(serviceModel, person);
			author.setEmail(authorPropertyMap.get("email"));
			author.setPersonId(authorPropertyMap.get("personId"));
			author.setPersonName(authorPropertyMap.get("personName"));
		}
		
		revision.setAuthor(author);
		revision.setCommitMessage(commitMessage);
		revision.setLocked(locked);
		revision.setVersionDate(versionDate);
		revision.setVersionId(versionId);
		
		return revision;
	}
	
	public ConfigurationItem getConfigurationItem(String configItemXML) {
		
		ConfigurationItem configurationItem = new ConfigurationItem();
		clearServiceModel();
		readServiceModel(configItemXML);
		Map<String,String> configItemPropertyMap = OntologyUtil.getInstanceProperties(serviceModel, SCM_NS + "ConfigurationItem");
		
		String itemName = configItemPropertyMap.get("itemName");
		String mimeType = configItemPropertyMap.get("mimeType");
		String itemURI = configItemPropertyMap.get("itemURI");
		
		String itemContent = configItemPropertyMap.get("itemContent");
				
		configurationItem.setItemName(itemName);
		configurationItem.setItemURI(itemURI);
		configurationItem.setMimeType(mimeType);
		configurationItem.setItemContent(itemContent.getBytes());
		//logger.info("itemContent: " + new String(itemContent.getBytes()));
		
		String version = configItemPropertyMap.get("hasVersion");
		if(version != null) {
			Map<String,String> versionPropertyMap = OntologyUtil.getResourceProperties(serviceModel, version);
			Revision revision = this.createRevision(versionPropertyMap);
			configurationItem.setHasVersion(revision);
		}
		logger.info(configurationItem);
				
		return configurationItem;		
	}
	
			
	protected Issue createIssue(Map<String,String> issuePropertyMap) {
		Issue issue = new Issue();
		
		String issueId = issuePropertyMap.get("issueId");
		String issueTitle = issuePropertyMap.get("issueTitle");
		String issueDescription = issuePropertyMap.get("issueDescription");
		String issueDate = issuePropertyMap.get("issueDate");
		String issueType = issuePropertyMap.get("issueType");
		String issueStatus = issuePropertyMap.get("issueStatus");
		String issueSeverity = issuePropertyMap.get("issueSeverity");
		String buildVersion = issuePropertyMap.get("buildVersion");
		boolean approved = false;
		if(issuePropertyMap.get("approved") != null) {
			approved = new Boolean(issuePropertyMap.get("approved"));
		}
		issue.setIssueId(issueId);
		issue.setApproved(approved);
		issue.setBuildVersion(buildVersion);
		issue.setIssueSeverity(issueSeverity);
		issue.setIssueStatus(issueStatus);
		issue.setIssueTitle(issueTitle);
		issue.setIssueDescription(issueDescription);
		issue.setIssueType(issueType);
		
		try {
			issue.setIssueDate(dateFormat.parse(issueDate));
		} catch(ParseException pe) {
			logger.error("Cannot parse version date " + issueDate);
		}		
				
		Person owner = new Person();
		String person = issuePropertyMap.get("isAssignedTo");
		if(person != null) {
			Map<String,String> authorPropertyMap = OntologyUtil.getResourceProperties(serviceModel, person);
			owner.setEmail(authorPropertyMap.get("email"));
			owner.setPersonId(authorPropertyMap.get("personId"));
			owner.setPersonName(authorPropertyMap.get("personName"));
		}
		issue.setIsAssignedTo(owner);
		
		return issue;
	}
	
	public Issue getIssue(String issueXML) {
		Issue issue = null;
		clearServiceModel();		
		readServiceModel(issueXML);
		try {
			Map<String,String> issuePropertyMap = OntologyUtil.getInstanceProperties(serviceModel, SCM_NS + "Issue");
			issue = this.createIssue(issuePropertyMap);
			logger.info(issue);
		
		} catch(Exception e) {
			logger.error("getIssue Exception: " + e.getMessage());
		}
		
		return issue;
	}
	
		
	public List<Issue> getIssueList(String issueXML) {
		List<Issue> issueList = new LinkedList<Issue>();
		clearServiceModel();
		readServiceModel(issueXML);
		
		Map<String,Map<String,String>> instanceMap = OntologyUtil.getAllInstanceProperties(serviceModel,  SCM_NS + "Issue");
		Iterator<String> instanceIt = instanceMap.keySet().iterator();
		while(instanceIt.hasNext()) {
			String instanceKey = instanceIt.next();			
			Map<String,String> issuePropertyMap = instanceMap.get(instanceKey);
			Issue issue = this.createIssue(issuePropertyMap);
			logger.info(issue);
			issueList.add(issue);
		}
		
		return issueList;
	}
	
	protected Individual createIssue(String issueId, String issueTitle, String issueDescription, String issueType, 
			String issueStatus, String issueSeverity, String buildVersion, String issueDate, boolean approved) {
		
		Individual issue = null;
		OntClass ontClass = scmModel.getOntClass(SCM_NS + "Issue");
		issue = serviceModel.createIndividual(SCM_NS + issueId, ontClass);
				
		issue.setPropertyValue(getDatatypeProperty("issueId"), scmModel.createTypedLiteral(issueId));
		issue.setPropertyValue(getDatatypeProperty("issueTitle"), scmModel.createTypedLiteral(issueTitle));
		issue.setPropertyValue(getDatatypeProperty("issueDescription"), scmModel.createTypedLiteral(issueDescription));
		issue.setPropertyValue(getDatatypeProperty("issueType"), scmModel.createTypedLiteral(issueType));
		issue.setPropertyValue(getDatatypeProperty("issueStatus"), scmModel.createTypedLiteral(issueStatus));
		issue.setPropertyValue(getDatatypeProperty("issueSeverity"), scmModel.createTypedLiteral(issueSeverity));
		issue.setPropertyValue(getDatatypeProperty("buildVersion"), scmModel.createTypedLiteral(buildVersion));
		issue.setPropertyValue(getDatatypeProperty("issueDate"), scmModel.createTypedLiteral(issueDate, XSDDatatype.XSDdateTime));
		issue.setPropertyValue(getDatatypeProperty("approved"), scmModel.createTypedLiteral(approved));
				
		return issue;
	}
	
	public String createIssueXML(String issueId, String issueTitle, String issueDescription, String issueType, 
			String issueStatus, String issueSeverity, String buildVersion, String issueDate, boolean approved) {
		
		clearServiceModel();
		Individual issue = createIssue(issueId, issueTitle, issueDescription, issueType, 
				issueStatus, issueSeverity, buildVersion, issueDate, approved);
		String issueXML = printServiceModel();		
		
		return issueXML;
	}
	
	protected Individual createConfigurationItem(String itemName, String mimeType, 
			String itemURI) {
		
		Individual configItem = null;
		OntClass ontClass = scmModel.getOntClass(SCM_NS + "ConfigurationItem");
		configItem = serviceModel.createIndividual(SCM_NS + itemName, ontClass);
				
		configItem.setPropertyValue(getDatatypeProperty("itemName"), scmModel.createTypedLiteral(itemName));
		configItem.setPropertyValue(getDatatypeProperty("mimeType"), scmModel.createTypedLiteral(mimeType));
		configItem.setPropertyValue(getDatatypeProperty("itemURI"), scmModel.createTypedLiteral(itemURI, XSDDatatype.XSDanyURI));
				
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
		version.setPropertyValue(getDatatypeProperty("commitMessage"), scmModel.createTypedLiteral(commitMessage));
		version.setPropertyValue(getDatatypeProperty("locked"), scmModel.createTypedLiteral(locked));
		version.setPropertyValue(getDatatypeProperty("versionDate"), scmModel.createTypedLiteral(versionDate, XSDDatatype.XSDdateTime));
		
		version.addProperty(getObjectProperty("author"), person);
				
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
	
	protected Individual createBranch(String branchName) {
		
		Individual branch = null;
		OntClass ontClass = scmModel.getOntClass(SCM_NS + "Branch");
		branch = serviceModel.createIndividual(SCM_NS + branchName, ontClass);				
		branch.setPropertyValue(getDatatypeProperty("branchName"), scmModel.createTypedLiteral(branchName));
						
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
