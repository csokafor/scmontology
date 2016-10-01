package uk.ac.liv.scm.serviceconsumer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
//import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.filter.HttpBasicAuthFilter;
import org.glassfish.jersey.filter.LoggingFilter;

import uk.ac.liv.scm.ontology.SCMOntology;
import uk.ac.liv.scm.ontology.entity.ConfigurationItem;
import uk.ac.liv.scm.ontology.entity.Issue;
import uk.ac.liv.scm.ontology.entity.Repository;
import uk.ac.liv.scm.ontology.entity.Revision;
import uk.ac.liv.scm.util.OntologyUtil;

import com.hp.hpl.jena.ontology.Individual;

public class AbstractServiceClient {
	
	private static final Logger logger = Logger.getLogger(AbstractServiceClient.class);
	protected Client client;
	protected static Map<String,ServiceProvider> serviceMap;
	protected static SCMOntology scmOntology;
	
	public AbstractServiceClient() {
		if(serviceMap == null) {
			serviceMap = new HashMap<String,ServiceProvider>();			
		}
		if(scmOntology == null) {
			scmOntology = SCMOntology.getInstance();
		}
        
	}
	
	public void addService(String serviceName, ServiceProvider serviceProvider) {
		serviceMap.put(serviceName, serviceProvider);
	}
	
	public String getServiceProviderService(String providerURI, String username, String password) {
		String serviceProviderResponse = "";
		
		client = ClientBuilder.newBuilder().build();
        client.register(new HttpBasicAuthFilter(username, password));
        WebTarget target = client.target(providerURI);
        target.register(new LoggingFilter());   
        serviceProviderResponse = target.path("/").request(MediaType.APPLICATION_XML).get(String.class);
        		
		return serviceProviderResponse;
	}
	
	public Repository getRepositoryContentService(String branchType, String branchName) {
		
		Repository repository = new Repository();
		ServiceProvider serviceProvider = serviceMap.get("getrepository");
		
		if(serviceProvider != null) {
			client = ClientBuilder.newBuilder().build();
	        client.register(new HttpBasicAuthFilter(serviceProvider.getUsername(), serviceProvider.getPassword()));
	        String repositoryXML = client.target(serviceProvider.getProviderURL())
		        .register(new LoggingFilter())	
		        .path("/serviceprovider/getrepository")		        
		        .queryParam("branchType", branchType)
		        .queryParam("branchName", branchName)
		        .request(MediaType.APPLICATION_XML).get(String.class);
	        logger.info("getRepositoryContentService: " + repositoryXML);
	        repository = scmOntology.getRepository(repositoryXML);
		} else {
			logger.warn("There is no service provider for getrepositorycontent service");
		}
						
		return repository;		
	}
	
	public Revision getRevisionService(String revisionNumber,  
			String branchType, String branchName) {
		
		Revision revision = new Revision();
		ServiceProvider serviceProvider = serviceMap.get("getrevision");
		if(serviceProvider != null) {
			client = ClientBuilder.newBuilder().build();
	        client.register(new HttpBasicAuthFilter(serviceProvider.getUsername(), serviceProvider.getPassword()));
	        String revisionXML = client.target(serviceProvider.getProviderURL())
		        .register(new LoggingFilter())
		        .path("/serviceprovider/getrevision")
		        .queryParam("revisionNumber", revisionNumber)		        
		        .queryParam("branchType", branchType)
		        .queryParam("branchName", branchName)
		        .request(MediaType.APPLICATION_XML).get(String.class);
	        
	        logger.info("getRevisionService: " + revisionXML);
	        revision = scmOntology.getRevision(revisionXML);
	        
		} else {
			logger.warn("There is no service provider for getrevision service");
		}
						
		return revision;		
	}
	
	public List<Revision> getRevisionHistoryService(String startDate, String endDate, 
			String branchType, String branchName) {
		
		List<Revision> revisionList = new LinkedList<Revision>();
		ServiceProvider serviceProvider = serviceMap.get("getrevisionhistory");
		if(serviceProvider != null) {
			client = ClientBuilder.newBuilder().build();
	        client.register(new HttpBasicAuthFilter(serviceProvider.getUsername(), serviceProvider.getPassword()));
	        String revisionXML = client.target(serviceProvider.getProviderURL())
		        .register(new LoggingFilter())
		        .path("/serviceprovider/getrevisionhistory")		        
		        .queryParam("startDate", startDate)
		        .queryParam("endDate", endDate)
		        .queryParam("branchType", branchType)
		        .queryParam("branchName", branchName)
		        .request(MediaType.APPLICATION_XML).get(String.class);
	        
	        logger.info("getRevisionHistoryService: " + revisionXML);
	        revisionList = scmOntology.getRevisionList(revisionXML);
	        
		} else {
			logger.warn("There is no service provider for getrevisionhistory service");
		}
		
		return revisionList;		
	}
	
	public ConfigurationItem getConfigurationItemService(String itemURI,  
			String branchType, String branchName) {
		
		ConfigurationItem configurationItem = new ConfigurationItem();
		ServiceProvider serviceProvider = serviceMap.get("getconfigurationitem");
		if(serviceProvider != null) {
			client = ClientBuilder.newBuilder().build();
	        client.register(new HttpBasicAuthFilter(serviceProvider.getUsername(), serviceProvider.getPassword()));
	        String configItemXML = client.target(serviceProvider.getProviderURL())
		        .register(new LoggingFilter())
		        .path("/serviceprovider/getconfigurationitem")
		        .queryParam("itemURI", itemURI)		        
		        .queryParam("branchType", branchType)
		        .queryParam("branchName", branchName)
		        .request(MediaType.APPLICATION_XML).get(String.class);
	        
	        logger.info("getConfigurationItemService: " + configItemXML);
	        configurationItem = scmOntology.getConfigurationItem(configItemXML);
	        
		} else {
			logger.warn("There is no service provider for getconfigurationitem service");
		}
		
		return configurationItem;		
	}
	
	public List<Revision> getConfigurationItemHistoryService(String itemURI, 
			String branchType, String branchName) {
		
		List<Revision> revisionList = new LinkedList<Revision>();
		ServiceProvider serviceProvider = serviceMap.get("getconfigurationitemrevisionhistory");
		if(serviceProvider != null) {
			client = ClientBuilder.newBuilder().build();
	        client.register(new HttpBasicAuthFilter(serviceProvider.getUsername(), serviceProvider.getPassword()));
	        String revisionXML = client.target(serviceProvider.getProviderURL())
		        .register(new LoggingFilter())
		        .path("/serviceprovider/getconfigurationitemrevisionhistory")
		        .queryParam("itemURI", itemURI)		        
		        .queryParam("branchType", branchType)
		        .queryParam("branchName", branchName)
		        .request(MediaType.APPLICATION_XML).get(String.class);
	        
	        logger.info("getConfigurationItemHistoryService: " + revisionXML);
	        revisionList = scmOntology.getRevisionList(revisionXML);
	        
		} else {
			logger.warn("There is no service provider for getconfigurationitemrevisionhistory service");
		}
		
		return revisionList;		
	}
	
	
	public List<Revision> getRevisionByCommitMessageService(String searchParameter,  
			String branchType, String branchName) {
		
		List<Revision> revisionList = new LinkedList<Revision>();
		ServiceProvider serviceProvider = serviceMap.get("getrevisionbycommitmessage");
		if(serviceProvider != null) {
			client = ClientBuilder.newBuilder().build();
	        client.register(new HttpBasicAuthFilter(serviceProvider.getUsername(), serviceProvider.getPassword()));
	        String revisionXML = client.target(serviceProvider.getProviderURL())
		        .register(new LoggingFilter())
		        .path("/serviceprovider/getrevisionbycommitmessage")
		        .queryParam("searchParameter", searchParameter)		        
		        .queryParam("branchType", branchType)
		        .queryParam("branchName", branchName)
		        .request(MediaType.APPLICATION_XML).get(String.class);
	        
	        logger.info("getRevisionByCommitMessageService: " + revisionXML);
	        revisionList = scmOntology.getRevisionList(revisionXML);
		} else {
			logger.warn("There is no service provider for getrevisionbycommitmessage service");
		}
		
		return revisionList;		
	}
	
	public Issue createIssueService(String issueParam) {
		Issue issue = new Issue();
		ServiceProvider serviceProvider = serviceMap.get("createissue");
		logger.info("serviceProvider: " + serviceProvider.toString());
		if(serviceProvider != null) {
			client = ClientBuilder.newBuilder().build();
	        client.register(new HttpBasicAuthFilter(serviceProvider.getUsername(), serviceProvider.getPassword()));
	        String issueXML = client.target(serviceProvider.getProviderURL())
		        .register(new LoggingFilter())
		        .path("/serviceprovider/createissue")
		        //target.queryParam("issue", issue);	        
		        .request(MediaType.APPLICATION_XML)
		        .post(Entity.entity(issueParam, MediaType.APPLICATION_XML), String.class);
	        
	        logger.info("createIssueService: " + issueXML);
	        issue = scmOntology.getIssue(issueXML);
	        
		} else {
			logger.warn("There is no service provider for createissue service");
		}
		
		return issue;
	}
	
	public Issue updateIssueService(String issueParam) {
		Issue issue = new Issue();
		ServiceProvider serviceProvider = serviceMap.get("updateissue");
		if(serviceProvider != null) {
			client = ClientBuilder.newBuilder().build();
	        client.register(new HttpBasicAuthFilter(serviceProvider.getUsername(), serviceProvider.getPassword()));
	        String issueXML = client.target(serviceProvider.getProviderURL())
		        .register(new LoggingFilter())
		        //target.queryParam("issue", issue);	        
		        .path("/serviceprovider/updateissue")
		        .request(MediaType.APPLICATION_XML)
		        .put(Entity.entity(issueParam, MediaType.APPLICATION_XML), String.class);
	        
	        logger.info("updateIssueService: " + issueXML);
	        issue = scmOntology.getIssue(issueXML);
		} else {
			logger.warn("There is no service provider for updateissue service");
		}
		
		return issue;
	}
	
	public Issue getIssueService(String issueId) {
		Issue issue = new Issue();
		ServiceProvider serviceProvider = serviceMap.get("getissue");
		if(serviceProvider != null) {
			client = ClientBuilder.newBuilder().build();
	        client.register(new HttpBasicAuthFilter(serviceProvider.getUsername(), serviceProvider.getPassword()));
	        String issueXML = client.target(serviceProvider.getProviderURL())
		        .register(new LoggingFilter())
		        .path("/serviceprovider/getissue")
		        .queryParam("issueId", issueId)
		        .request(MediaType.APPLICATION_XML).get(String.class);
	        
	        logger.info("getIssueService: " + issueXML);
	        issue = scmOntology.getIssue(issueXML);
		} else {
			logger.warn("There is no service provider for getissue service");
		}
		
		return issue;
	}
	
	public String deleteIssueService(String issueId) {
		Issue issue = new Issue();
		String issueXML = null;
		ServiceProvider serviceProvider = serviceMap.get("deleteissue");
		if(serviceProvider != null) {
			client = ClientBuilder.newBuilder().build();
	        client.register(new HttpBasicAuthFilter(serviceProvider.getUsername(), serviceProvider.getPassword()));
	        issueXML = client.target(serviceProvider.getProviderURL())
		        .register(new LoggingFilter())
		        .path("/serviceprovider/deleteissue")
		        .queryParam("issueId", issueId)
		        .request(MediaType.APPLICATION_XML).delete(String.class);
	        
	        logger.info("deleteIssueService: " + issueXML);
	        //issue = scmOntology.getIssue(issueXML);
		} else {
			logger.warn("There is no service provider for deleteissue service");
		}
		
		return issueXML;
	}
	
	public List<Issue> searchForIssueService(String searchQuery) {
		List<Issue> issueList = new LinkedList<Issue>();
		ServiceProvider serviceProvider = serviceMap.get("searchforissue");
		if(serviceProvider != null) {
			client = ClientBuilder.newBuilder().build();
	        client.register(new HttpBasicAuthFilter(serviceProvider.getUsername(), serviceProvider.getPassword()));
	        String issueXML = client.target(serviceProvider.getProviderURL())
		        .register(new LoggingFilter())
		        .path("/serviceprovider/searchforissue")
		        .queryParam("searchQuery", searchQuery)
		        .request(MediaType.APPLICATION_XML).get(String.class);
	        
	        logger.info("searchForIssueService: " + issueXML);
	        issueList = scmOntology.getIssueList(issueXML);
		} else {
			logger.warn("There is no service provider for searchforissue service");
		}
		return issueList;
	}
	
	public String createIssueXML(String issueId, String issueTitle, String issueDescription, String issueType, 
			String issueStatus, String issueSeverity, String buildVersion, String issueDate, boolean approved) {
		
		String issueXML = scmOntology.createIssueXML(issueId, issueTitle, issueDescription, issueType, 
				issueStatus, issueSeverity, buildVersion, issueDate, approved);
				
		return issueXML;
		
	}

}
