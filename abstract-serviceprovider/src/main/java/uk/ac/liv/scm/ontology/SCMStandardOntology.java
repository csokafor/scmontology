package uk.ac.liv.scm.ontology;

import java.io.StringWriter;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.ValidityReport;

import uk.ac.liv.scm.exception.ServiceException;
import uk.ac.liv.scm.util.Constants;
import uk.ac.liv.scm.util.ServiceProperties;

public class SCMStandardOntology {

	private static final Logger logger = Logger.getLogger(SCMStandardOntology.class);
	private static SCMStandardOntology scmStandardOnt;	
	protected static String SCM_STD_OWL_BASE = "http://www.scm.org/scmservice.owl";
	protected static String SCM_STD_NS = SCM_STD_OWL_BASE + "#";
	protected static String SCM_OWL_BASE = "http://www.scm.org/scm.owl";
	protected static String SCM_NS = SCM_OWL_BASE + "#";	
		
	protected OntModel scmStandardModel;	
	protected OntModel scmModel;
	protected OntModel serviceModel;
		
	protected SCMStandardOntology() {
		//load SCM standard ontology
		scmStandardModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF);
		scmStandardModel.read(ServiceProperties.getSCMStandardOWLFile(), SCM_STD_OWL_BASE, "RDF/XML");
		
		//load SCM ontology
		scmModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF);
		scmModel.read(ServiceProperties.getSCMOWLFile(), SCM_OWL_BASE, "RDF/XML");
		
		//create service model
		serviceModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF);
		scmStandardModel.addSubModel(serviceModel);
		scmModel.addSubModel(serviceModel);
		serviceModel.setNsPrefix("scmservice", SCM_STD_NS);
		serviceModel.setNsPrefix("scm", SCM_NS);					    			    		
		
	}
	
	public static SCMStandardOntology getInstance() {
		if(scmStandardOnt == null) {
			scmStandardOnt = new SCMStandardOntology();			
		}
		
		return scmStandardOnt;
	}
	
	public OntModel getSCMStandardModel() {
		return this.scmStandardModel;
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
	
	public String getServiceProviderXML() {
		clearServiceModel();
		initServiceProvider();
		validateServiceModel();
		String rdfXML = this.printServiceModel();
				
		return rdfXML;
	}
	
	public String getErrorXML(int errorCode, String errorMessage) {
		clearServiceModel();
		this.createError(errorCode, errorMessage);
		String rdfXML = this.printServiceModel();
		
		return rdfXML;
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
	
	protected void initServiceProvider() {
		try {
			Individual serviceProvider = this.createServiceProvider(Constants.PROVIDER_TITLE, Constants.PROVIDER_DESCRIPTION, 
					Constants.PROVIDER_URI, Constants.PROVIDER_PUBLISHER, Constants.PROVIDER_VERSION);

			//set error values
			Individual notSupportedError = this.createError(ServiceException.NOTSUPPORTED_ERROR_CODE, 
					ServiceException.NOTSUPPORTED_ERROR);
			serviceProvider.addProperty(getObjectProperty("error"), notSupportedError);
			Individual invalidParamError = this.createError(ServiceException.INVALIDPARAMETER_ERROR_CODE, 
					ServiceException.INVALIDPARAMETER_ERROR);
			serviceProvider.addProperty(getObjectProperty("error"), invalidParamError);
			Individual missingParamError = this.createError(ServiceException.MISSINGPARAMETER_ERROR_CODE, 
					ServiceException.MISSINGPARAMETER_ERROR);
			serviceProvider.addProperty(getObjectProperty("error"), missingParamError);
			Individual serverError = this.createError(ServiceException.SERVER_ERROR_CODE, 
					ServiceException.SERVER_ERROR);
			serviceProvider.addProperty(getObjectProperty("error"), serverError);
			
			//set services
			//getrepositorycontent			
			serviceProvider.addProperty(getObjectProperty("service"), getRepositoryContentService(Constants.PROVIDER_URI));
			
			//getrevision
			serviceProvider.addProperty(getObjectProperty("service"), getRevisionService(Constants.PROVIDER_URI));
			
			//getrevisionhistory
			serviceProvider.addProperty(getObjectProperty("service"), getRevisionHistoryService(Constants.PROVIDER_URI));
			
			//getrevisionbycommitmessage
			serviceProvider.addProperty(getObjectProperty("service"), getRevisionByMessageService(Constants.PROVIDER_URI));
			
			//getconfigurationitem
			serviceProvider.addProperty(getObjectProperty("service"), getConfigurationItemService(Constants.PROVIDER_URI));
			
			//getconfigurationitemrevisionhistory
			serviceProvider.addProperty(getObjectProperty("service"), getConfigurationItemRevHistoryService(Constants.PROVIDER_URI));		
						
			//createissue
			serviceProvider.addProperty(getObjectProperty("service"), createIssueService(Constants.PROVIDER_URI));
			
			//updateissue
			serviceProvider.addProperty(getObjectProperty("service"), updateIssueService(Constants.PROVIDER_URI));
			
			//getissue
			serviceProvider.addProperty(getObjectProperty("service"), getIssueService(Constants.PROVIDER_URI));
			
			//deleteissue
			serviceProvider.addProperty(getObjectProperty("service"), deleteIssueService(Constants.PROVIDER_URI));
			
			//issuesearch
			serviceProvider.addProperty(getObjectProperty("service"), searchForIssueService(Constants.PROVIDER_URI));
			
		} catch(Exception e) {
			logger.error("Could not initialise Service Provider: " + e.getMessage(), e);
		}
	}
	
	protected Individual getRevisionHistoryService(String providerURI) {
		
		Individual getRevisionHistoryService = this.createService("getrevisionhistory", 
				"The service returns the version history between two date, the start date and the end date", 
				providerURI + "serviceprovider/getrevisionhistory", "http://www.scm.org/scm.owl#Revision");
		Individual startDate = this.createServiceParameter("startDate", "http://www.w3.org/2001/XMLSchema#date");
		Individual endDate = this.createServiceParameter("endDate", "http://www.w3.org/2001/XMLSchema#date");
		Individual repositoryName = this.createServiceParameter("repositoryName", "http://www.w3.org/2001/XMLSchema#string");
		Individual branchType = this.createServiceParameter("branchType", "http://www.w3.org/2001/XMLSchema#string");
		Individual branchName = this.createServiceParameter("branchName", "http://www.w3.org/2001/XMLSchema#string");
		
		getRevisionHistoryService.addProperty(getObjectProperty("input"), startDate);
		getRevisionHistoryService.addProperty(getObjectProperty("input"), endDate);
		getRevisionHistoryService.addProperty(getObjectProperty("input"), repositoryName);
		getRevisionHistoryService.addProperty(getObjectProperty("input"), branchType);
		getRevisionHistoryService.addProperty(getObjectProperty("input"), branchName);
		
		return getRevisionHistoryService;
	}
	
	protected Individual getRevisionService(String providerURI) {
		
		Individual getRevisionService = this.createService("getrevision", 
				"The service returns the description of a revision given the revision number.", 
				providerURI + "serviceprovider/getrevision",	"http://www.scm.org/scm.owl#Revision");
		Individual revisionNumber = this.createServiceParameter("revisionNumber", "http://www.w3.org/2001/XMLSchema#string");
		Individual repositoryName = this.createServiceParameter("repositoryName", "http://www.w3.org/2001/XMLSchema#string");
		Individual branchType = this.createServiceParameter("branchType", "http://www.w3.org/2001/XMLSchema#string");
		Individual branchName = this.createServiceParameter("branchName", "http://www.w3.org/2001/XMLSchema#string");
		
		getRevisionService.addProperty(getObjectProperty("input"), revisionNumber);
		getRevisionService.addProperty(getObjectProperty("input"), repositoryName);
		getRevisionService.addProperty(getObjectProperty("input"), branchType);
		getRevisionService.addProperty(getObjectProperty("input"), branchName);
		
		return getRevisionService;
	}
	
	protected Individual getRevisionByMessageService(String providerURI) {
		
		Individual getRevisionByMessageService = this.createService("getrevisionbycommitmessage", 
				"The service searches for a given string in the repository revision commit messages.", 
				providerURI + "serviceprovider/getrevisionbycommitmessage", "http://www.scm.org/scm.owl#Revision");
		Individual searchParameter = this.createServiceParameter("searchParameter", "http://www.w3.org/2001/XMLSchema#string");
		Individual repositoryName = this.createServiceParameter("repositoryName", "http://www.w3.org/2001/XMLSchema#string");
		Individual branchType = this.createServiceParameter("branchType", "http://www.w3.org/2001/XMLSchema#string");
		Individual branchName = this.createServiceParameter("branchName", "http://www.w3.org/2001/XMLSchema#string");
		
		getRevisionByMessageService.addProperty(getObjectProperty("input"), searchParameter);
		getRevisionByMessageService.addProperty(getObjectProperty("input"), repositoryName);
		getRevisionByMessageService.addProperty(getObjectProperty("input"), branchType);
		getRevisionByMessageService.addProperty(getObjectProperty("input"), branchName);
		
		return getRevisionByMessageService;
	}
	
	protected Individual getConfigurationItemService(String providerURI) {
		
		Individual getConfigItemService = this.createService("getconfigurationitem", 
				"The service gets the configuration item and latest repository version of the configuration item",
				providerURI + "serviceprovider/getconfigurationitem", "http://www.scm.org/scm.owl#ConfigurationItem");
		
		Individual itemURI = this.createServiceParameter("itemURI", "http://www.w3.org/2001/XMLSchema#string");
		Individual repositoryName = this.createServiceParameter("repositoryName", "http://www.w3.org/2001/XMLSchema#string");
		Individual branchType = this.createServiceParameter("branchType", "http://www.w3.org/2001/XMLSchema#string");
		Individual branchName = this.createServiceParameter("branchName", "http://www.w3.org/2001/XMLSchema#string");
		
		getConfigItemService.addProperty(getObjectProperty("input"), itemURI);
		getConfigItemService.addProperty(getObjectProperty("input"), repositoryName);
		getConfigItemService.addProperty(getObjectProperty("input"), branchType);
		getConfigItemService.addProperty(getObjectProperty("input"), branchName);
		
		return getConfigItemService;
	}
	
	protected Individual getConfigurationItemRevHistoryService(String providerURI) {
		
		Individual getConfigItemHistoryService = this.createService("getconfigurationitemrevisionhistory", 
				"The service returns the version history of a configuration item.",
				providerURI + "serviceprovider/getconfigurationitemrevisionhistory", "http://www.scm.org/scm.owl#Revision");
		
		Individual itemURI = this.createServiceParameter("itemURI", "http://www.w3.org/2001/XMLSchema#string");
		Individual repositoryName = this.createServiceParameter("repositoryName", "http://www.w3.org/2001/XMLSchema#string");
		Individual branchType = this.createServiceParameter("branchType", "http://www.w3.org/2001/XMLSchema#string");
		Individual branchName = this.createServiceParameter("branchName", "http://www.w3.org/2001/XMLSchema#string");
		
		getConfigItemHistoryService.addProperty(getObjectProperty("input"), itemURI);
		getConfigItemHistoryService.addProperty(getObjectProperty("input"), repositoryName);
		getConfigItemHistoryService.addProperty(getObjectProperty("input"), branchType);
		getConfigItemHistoryService.addProperty(getObjectProperty("input"), branchName);
		
		return getConfigItemHistoryService;
	}
	
	protected Individual getRepositoryContentService(String providerURI) {
		
		Individual getRepoContentService = this.createService("getrepository", 
				"Returns the contents of a repository", providerURI + "serviceprovider/getrepository", 
				"http://www.scm.org/scm.owl#Repository");
		Individual repositoryName = this.createServiceParameter("repositoryName", "http://www.w3.org/2001/XMLSchema#string");
		Individual branchType = this.createServiceParameter("branchType", "http://www.w3.org/2001/XMLSchema#string");
		Individual branchName = this.createServiceParameter("branchName", "http://www.w3.org/2001/XMLSchema#string");
		
		getRepoContentService.addProperty(getObjectProperty("input"), repositoryName);
		getRepoContentService.addProperty(getObjectProperty("input"), branchType);
		getRepoContentService.addProperty(getObjectProperty("input"), branchName);
		
		return getRepoContentService;
	}
	
	protected Individual createIssueService(String providerURI) {
		
		Individual issueService = this.createService("createissue", 
				"The service creates a new issue", providerURI + "serviceprovider/createissue", 
				"http://www.scm.org/scm.owl#Issue");
		Individual issueParam = this.createServiceParameter("issue", "http://www.scm.org/scm.owl#Issue");				
		issueService.addProperty(getObjectProperty("input"), issueParam);			
		
		return issueService;
	}
	
	protected Individual updateIssueService(String providerURI) {
		
		Individual issueService = this.createService("updateissue", 
				"The service updates an existing issue", providerURI + "serviceprovider/updateissue", 
				"http://www.scm.org/scm.owl#Issue");
		Individual issueParam = this.createServiceParameter("issue", "http://www.scm.org/scm.owl#Issue");				
		issueService.addProperty(getObjectProperty("input"), issueParam);			
		
		return issueService;
	}
	
	protected Individual getIssueService(String providerURI) {
		
		Individual issueService = this.createService("getissue", 
				"The services returns an issue that matches a given issue identifier", 
				providerURI + "serviceprovider/getissue", "http://www.scm.org/scm.owl#Issue");
		Individual issueParam = this.createServiceParameter("issueId", "http://www.w3.org/2001/XMLSchema#string");				
		issueService.addProperty(getObjectProperty("input"), issueParam);			
		
		return issueService;
	}
	
	protected Individual deleteIssueService(String providerURI) {
		
		Individual issueService = this.createService("deleteissue", 
				"The service deletes an issue", providerURI + "serviceprovider/deleteissue", 
				"http://www.scm.org/scm.owl#Issue");
		Individual issueParam = this.createServiceParameter("issueId", "http://www.w3.org/2001/XMLSchema#string");				
		issueService.addProperty(getObjectProperty("input"), issueParam);			
		
		return issueService;
	}
	
	protected Individual searchForIssueService(String providerURI) {
		
		Individual issueService = this.createService("searchforissue", 
				"The service returns a list of issues that matches the search criteria", 
				providerURI + "serviceprovider/searchforissue", "http://www.scm.org/scm.owl#Issue");
		Individual issueParam = this.createServiceParameter("searchQuery", "http://www.w3.org/2001/XMLSchema#string");				
		issueService.addProperty(getObjectProperty("input"), issueParam);			
		
		return issueService;
	}
	
	protected Individual createError(int errorCode, String errorMessage) {
		Individual error = null;
		OntClass ontClass = scmStandardModel.getOntClass(SCM_STD_NS + "Error");		
		error = serviceModel.createIndividual(SCM_STD_NS + errorMessage, ontClass);
		
		Literal errorCodeLit = scmStandardModel.createTypedLiteral(errorCode);
		Literal errorMsgLit = scmStandardModel.createTypedLiteral(errorMessage);
		
		error.setPropertyValue(getDatatypeProperty("errorCode"), errorCodeLit);
		error.setPropertyValue(getDatatypeProperty("errorMessage"), errorMsgLit);
		
		return error;
	}
	
		
	protected Individual createService(String name, String description, 
			String serviceURI, String output) {
		
		Individual service = null;
		OntClass ontClass = scmStandardModel.getOntClass(SCM_STD_NS + "Service");
		service = serviceModel.createIndividual(SCM_STD_NS + name, ontClass);
				
		service.setPropertyValue(getDatatypeProperty("serviceName"), scmStandardModel.createTypedLiteral(name));
		service.setPropertyValue(getDatatypeProperty("serviceDescription"), scmStandardModel.createTypedLiteral(description));
		service.setPropertyValue(getDatatypeProperty("serviceURI"), scmStandardModel.createTypedLiteral(serviceURI, XSDDatatype.XSDanyURI));
		service.setPropertyValue(getDatatypeProperty("output"), scmStandardModel.createTypedLiteral(output));
		
		return service;
	}
	
	protected Individual createServiceParameter(String name, String parameterURI) {
		
		Individual serviceParam = null;
		OntClass ontClass = scmStandardModel.getOntClass(SCM_STD_NS + "ServiceParameter");
		serviceParam = serviceModel.createIndividual(SCM_STD_NS + name, ontClass);
				
		serviceParam.setPropertyValue(getDatatypeProperty("parameterName"), scmStandardModel.createTypedLiteral(name));
		serviceParam.setPropertyValue(getDatatypeProperty("parameterURI"), scmStandardModel.createTypedLiteral(parameterURI, XSDDatatype.XSDanyURI));
				
		return serviceParam;
	}
	
	protected Individual createServiceProvider(String title, String description, 
			String providerURI, String publisher, String version) {
		
		Individual serviceProvider = null;
		OntClass ontClass = scmStandardModel.getOntClass(SCM_STD_NS + "ServiceProvider");
		serviceProvider = serviceModel.createIndividual(SCM_STD_NS + title, ontClass);
				
		serviceProvider.setPropertyValue(getDatatypeProperty("providerTitle"), scmStandardModel.createTypedLiteral(title));
		serviceProvider.setPropertyValue(getDatatypeProperty("providerDescription"), scmStandardModel.createTypedLiteral(description));
		serviceProvider.setPropertyValue(getDatatypeProperty("providerURI"), scmStandardModel.createTypedLiteral(providerURI, XSDDatatype.XSDanyURI));
		serviceProvider.setPropertyValue(getDatatypeProperty("providerVersion"), scmStandardModel.createTypedLiteral(version));
		serviceProvider.setPropertyValue(getDatatypeProperty("providerPublisher"), scmStandardModel.createTypedLiteral(publisher));
		
		return serviceProvider;
	}
	
	protected ObjectProperty getObjectProperty(String propertyName) {
		ObjectProperty objectProperty = scmStandardModel.getObjectProperty(SCM_STD_NS + propertyName);
		return objectProperty;
	}
	
	protected DatatypeProperty getDatatypeProperty(String propertyName) {
		DatatypeProperty datatypeProperty = scmStandardModel.getDatatypeProperty(SCM_STD_NS + propertyName);
		return datatypeProperty;
	}	

}
