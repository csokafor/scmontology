package uk.ac.liv.scm.ontology;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

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
import uk.ac.liv.scm.exception.ServiceException;
import uk.ac.liv.scm.serviceconsumer.AbstractServiceClient;
import uk.ac.liv.scm.serviceconsumer.ServiceProvider;
import uk.ac.liv.scm.util.Constants;
import uk.ac.liv.scm.util.OntologyUtil;
import uk.ac.liv.scm.util.ServiceProperties;

public class SCMStandardOntology {

	private static final Logger logger = Logger.getLogger(SCMStandardOntology.class);
	private static SCMStandardOntology scmStandardOnt;	
	private static String SCM_STD_OWL_BASE = "http://www.scm.org/scmservice.owl";
	private static String SCM_STD_NS = SCM_STD_OWL_BASE + "#";
	private static String SCM_OWL_BASE = "http://www.scm.org/scm.owl";
	private static String SCM_NS = SCM_OWL_BASE + "#";	
		
	private OntModel scmStandardModel;	
	private OntModel scmModel;
	private OntModel serviceModel;
	private OntModel serviceConsumerModel;
		
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
		
		//create service consumer model
		serviceConsumerModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF);
		scmStandardModel.addSubModel(serviceConsumerModel);
		scmModel.addSubModel(serviceConsumerModel);
		serviceConsumerModel.setNsPrefix("scmservice", SCM_STD_NS);
		serviceConsumerModel.setNsPrefix("scm", SCM_NS);
						
		loadServiceProviders();
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
	
	public OntModel getServiceConsumerModel() {
		return this.serviceConsumerModel;
	}
	
	public void clearServiceModel() {
		this.serviceModel.removeAll();
	}
			
	public String printServiceModel() {
		StringWriter modelWriter = new StringWriter();				
		serviceModel.write(modelWriter);
		return modelWriter.toString();
	}
	
	public String printServiceConsumerModel() {
		StringWriter modelWriter = new StringWriter();				
		serviceConsumerModel.write(modelWriter);
		return modelWriter.toString();
	}
	
	public void readServiceConsumerModel(String rdfXML, String base) {
		InputStream is = new ByteArrayInputStream(rdfXML.getBytes());
		serviceConsumerModel.read(is, base, "RDF/XML");		
	}
	
	public void readServiceModel(String rdfXML, String base) {
		InputStream is = new ByteArrayInputStream(rdfXML.getBytes());
		serviceModel.read(is, base, "RDF/XML");		
		logger.info("readServiceModel");
	}
	
	public String getServiceConsumerXML() {
		clearServiceModel();
		initServiceConsumer();
		String rdfXML = this.printServiceConsumerModel();
				
		return rdfXML;
	}
	
	public String getErrorXML(int errorCode, String errorMessage) {
		clearServiceModel();
		this.createError(errorCode, errorMessage);
		String rdfXML = this.printServiceModel();
		
		return rdfXML;
	}
	
	public void loadServiceProviders() {
		AbstractServiceClient serviceClient = new AbstractServiceClient();
		logger.info("loadServiceProviders");
		LinkedList<ServiceProvider> serviceProviders = ServiceProperties.getServiceProviders();
		
		Iterator<ServiceProvider> it = serviceProviders.iterator();
		while(it.hasNext()) {
			clearServiceModel();
			ServiceProvider serviceProvider = it.next();
			String providerXML = serviceClient.getServiceProviderService(serviceProvider.getProviderURL() + "serviceprovider",
					serviceProvider.getUsername(), serviceProvider.getPassword());
			readServiceModel(providerXML, SCM_STD_NS);			
			
			Map<String,String> providerPropertyMap = OntologyUtil.getInstanceProperties(serviceModel, SCM_STD_NS + "ServiceProvider");
			Iterator<String> keyIt = providerPropertyMap.keySet().iterator();
			while(keyIt.hasNext()) {
				String key = keyIt.next();
				if(key.equalsIgnoreCase("service")) {
					String serviceNode = providerPropertyMap.get(key);
					logger.info("services: " + serviceNode);
					String[] serviceNodes = serviceNode.split(",");
					for(int i = 0; i < serviceNodes.length;i++) {
						Map<String,String> servicePropertyMap = OntologyUtil.getResourceProperties(serviceModel, serviceNodes[i]);
						String serviceName = servicePropertyMap.get("serviceName");
						serviceClient.addService(serviceName, serviceProvider);
					}
				}
			}
			
			readServiceConsumerModel(providerXML, SCM_STD_NS);
		}		
		
	}
	
	protected void initServiceConsumer() {
		try {
			Individual serviceConsumer = this.createServiceConsumer(Constants.CONSUMER_TITLE, 
					Constants.CONSUMER_URI, Constants.CONSUMER_PUBLISHER, Constants.CONSUMER_VERSION);
						
						
		} catch(Exception e) {
			logger.error("Could not initialise Service Consumer: " + e.getMessage(), e);
		}
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
			serviceProvider.addProperty(getObjectProperty("service"), getRepositoryContentService());
			
			//getrevision
			serviceProvider.addProperty(getObjectProperty("service"), getRevisionService());
			
			//getrevisionhistory
			serviceProvider.addProperty(getObjectProperty("service"), getRevisionHistoryService());
			
			//getrevisionbycommitmessage
			serviceProvider.addProperty(getObjectProperty("service"), getRevisionByMessageService());
			
			//getconfigurationitem
			serviceProvider.addProperty(getObjectProperty("service"), getConfigurationItemService());
			
			//getconfigurationitemrevisionhistory
			serviceProvider.addProperty(getObjectProperty("service"), getConfigurationItemRevHistoryService());		
						
			//createissue
			serviceProvider.addProperty(getObjectProperty("service"), createIssueService());
			
			//updateissue
			serviceProvider.addProperty(getObjectProperty("service"), updateIssueService());
			
			//getissue
			serviceProvider.addProperty(getObjectProperty("service"), getIssueService());
			
			//deleteissue
			serviceProvider.addProperty(getObjectProperty("service"), deleteIssueService());
			
			//issuesearch
			serviceProvider.addProperty(getObjectProperty("service"), searchForIssueService());
			
		} catch(Exception e) {
			logger.error("Could not initialise Service Provider: " + e.getMessage(), e);
		}
	}
	
	protected Individual getRevisionHistoryService() {
		
		Individual getRevisionHistoryService = this.createService("getrevisionhistory", 
				"The service returns the version history between two date, the start date and the end date", 
				Constants.PROVIDER_URI + "serviceprovider/getrevisionhistory", "http://www.scm.org/scm.owl#Revision");
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
	
	protected Individual getRevisionService() {
		
		Individual getRevisionService = this.createService("getrevision", 
				"The service returns the description of a revision given the revision number.", 
				Constants.PROVIDER_URI + "serviceprovider/getrevision",	"http://www.scm.org/scm.owl#Revision");
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
	
	protected Individual getRevisionByMessageService() {
		
		Individual getRevisionByMessageService = this.createService("getrevisionbycommitmessage", 
				"The service searches for a given string in the repository revision commit messages.", 
				Constants.PROVIDER_URI + "serviceprovider/getrevisionbycommitmessage", "http://www.scm.org/scm.owl#Revision");
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
	
	protected Individual getConfigurationItemService() {
		
		Individual getConfigItemService = this.createService("getconfigurationitem", 
				"The service gets the configuration item and latest repository version of the configuration item",
				Constants.PROVIDER_URI + "serviceprovider/getconfigurationitem", "http://www.scm.org/scm.owl#ConfigurationItem");
		
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
	
	protected Individual getConfigurationItemRevHistoryService() {
		
		Individual getConfigItemHistoryService = this.createService("getconfigurationitemrevisionhistory", 
				"The service returns the version history of a configuration item.",
				Constants.PROVIDER_URI + "serviceprovider/getconfigurationitemrevisionhistory", "http://www.scm.org/scm.owl#Revision");
		
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
	
	protected Individual getRepositoryContentService() {
		
		Individual getRepoContentService = this.createService("getrepositorycontent", 
				"Returns the contents of a repository", Constants.PROVIDER_URI + "serviceprovider/getrepositorycontent", 
				"http://www.scm.org/scm.owl#Repository");
		Individual repositoryName = this.createServiceParameter("repositoryName", "http://www.w3.org/2001/XMLSchema#string");
		Individual branchType = this.createServiceParameter("branchType", "http://www.w3.org/2001/XMLSchema#string");
		Individual branchName = this.createServiceParameter("branchName", "http://www.w3.org/2001/XMLSchema#string");
		
		getRepoContentService.addProperty(getObjectProperty("input"), repositoryName);
		getRepoContentService.addProperty(getObjectProperty("input"), branchType);
		getRepoContentService.addProperty(getObjectProperty("input"), branchName);
		
		return getRepoContentService;
	}
	
	protected Individual createIssueService() {
		
		Individual issueService = this.createService("createissue", 
				"The service creates a new issue", Constants.PROVIDER_URI + "serviceprovider/createissue", 
				"http://www.scm.org/scm.owl#Issue");
		Individual issueParam = this.createServiceParameter("issue", "http://www.scm.org/scm.owl#Issue");				
		issueService.addProperty(getObjectProperty("input"), issueParam);			
		
		return issueService;
	}
	
	protected Individual updateIssueService() {
		
		Individual issueService = this.createService("updateissue", 
				"The service updates an existing issue", Constants.PROVIDER_URI + "serviceprovider/updateissue", 
				"http://www.scm.org/scm.owl#Issue");
		Individual issueParam = this.createServiceParameter("issue", "http://www.scm.org/scm.owl#Issue");				
		issueService.addProperty(getObjectProperty("input"), issueParam);			
		
		return issueService;
	}
	
	protected Individual getIssueService() {
		
		Individual issueService = this.createService("getissue", 
				"The services returns an issue that matches a given issue identifier", 
				Constants.PROVIDER_URI + "serviceprovider/getissue", "http://www.scm.org/scm.owl#Issue");
		Individual issueParam = this.createServiceParameter("issueId", "http://www.w3.org/2001/XMLSchema#string");				
		issueService.addProperty(getObjectProperty("input"), issueParam);			
		
		return issueService;
	}
	
	protected Individual deleteIssueService() {
		
		Individual issueService = this.createService("deleteissue", 
				"The service deletes an issue", Constants.PROVIDER_URI + "serviceprovider/deleteissue", 
				"http://www.scm.org/scm.owl#Issue");
		Individual issueParam = this.createServiceParameter("issueId", "http://www.w3.org/2001/XMLSchema#string");				
		issueService.addProperty(getObjectProperty("input"), issueParam);			
		
		return issueService;
	}
	
	protected Individual searchForIssueService() {
		
		Individual issueService = this.createService("searchforissue", 
				"The service returns a list of issues that matches the search criteria", 
				Constants.PROVIDER_URI + "serviceprovider/searchforissue", "http://www.scm.org/scm.owl#Issue");
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
	
	protected Individual createServiceConsumer(String title, 
			String consumerURI, String publisher, String version) {
		
		Individual serviceConsumer = null;
		OntClass ontClass = scmStandardModel.getOntClass(SCM_STD_NS + "ServiceConsumer");
		serviceConsumer = serviceConsumerModel.createIndividual(SCM_STD_NS + title, ontClass);
				
		serviceConsumer.setPropertyValue(getDatatypeProperty("consumerTitle"), scmStandardModel.createTypedLiteral(title));
		serviceConsumer.setPropertyValue(getDatatypeProperty("consumerURI"), scmStandardModel.createTypedLiteral(consumerURI, XSDDatatype.XSDanyURI));
		serviceConsumer.setPropertyValue(getDatatypeProperty("consumerVersion"), scmStandardModel.createTypedLiteral(version));
		serviceConsumer.setPropertyValue(getDatatypeProperty("consumerPublisher"), scmStandardModel.createTypedLiteral(publisher));
		
		return serviceConsumer;
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
