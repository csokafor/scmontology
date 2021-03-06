package uk.ac.liv.scm.svn.provider;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;

import uk.ac.liv.scm.exception.ServiceException;
import uk.ac.liv.scm.svn.ontology.SCMOntology;
import uk.ac.liv.scm.svn.ontology.SCMStandardOntology;

@Path("/scm")
@Produces({"application/xml","text/plain"})
public class SVNServiceProvider {
	
	private static final Logger logger = Logger.getLogger(SVNServiceProvider.class);
	
	@GET
	@Path("/serviceprovider")	
	public String getServiceProvider() {
		logger.info("getServiceProvider");
		
		SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
		String serviceProvider = scmStandardOnt.getServiceProviderXML();
		logger.info("ServiceProvider RDF/XML::");
		logger.info(scmStandardOnt.printServiceModel());
				
		return serviceProvider;
	}
	
	@GET
	@Path("/serviceprovider/oauthconfiguration")
	public String getOauthConfiguration() {
		logger.info("getOauthConfiguration");
		SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
		String oauthConfiguration = scmStandardOnt.getErrorXML(ServiceException.NOTSUPPORTED_ERROR_CODE,
				ServiceException.NOTSUPPORTED_ERROR);
		
		logger.info("getOauthConfiguration RDF/XML::");
		return oauthConfiguration;
	}
	
	@GET
	@Path("/serviceprovider/getrepository")
	public String getRepositoryContent(@QueryParam("branchType") String branchType,
			@QueryParam("branchName") String branchName) {
		
		logger.info("getRepositoryContent");
		SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
		SCMOntology scmOnt = SCMOntology.getInstance();
		String response = "";
				
		response = scmOnt.getRepository(branchType, branchName);
		logger.info("response: " + response);
		
		return response;
	}
	
	@GET
	@Path("/serviceprovider/getrevision")
	public String getRevision(@QueryParam("revisionNumber") String revisionNumber,			
			@QueryParam("branchType") String branchType,
			@QueryParam("branchName") String branchName) {
		
		logger.info("getRevision");
		
		SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
		SCMOntology scmOnt = SCMOntology.getInstance();
		String response = "";
		
		if(revisionNumber == null || revisionNumber.equalsIgnoreCase("")) {
			
			response = scmStandardOnt.getErrorXML(ServiceException.MISSINGPARAMETER_ERROR_CODE,
					ServiceException.MISSINGPARAMETER_ERROR);
			
		} else {
			response = scmOnt.getRevision(revisionNumber, branchType, branchName);
		}
				
		return response;
	}
	
	@GET
	@Path("/serviceprovider/getrevisionhistory")
	public String getRevisionHistory(@QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate,
			@QueryParam("branchType") String branchType,
			@QueryParam("branchName") String branchName) {
		
		logger.info("getRevisionHistory");
		SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
		SCMOntology scmOnt = SCMOntology.getInstance();
		String response = "";
					
		response = scmStandardOnt.getErrorXML(ServiceException.MISSINGPARAMETER_ERROR_CODE,
				ServiceException.MISSINGPARAMETER_ERROR);
				
		response = scmOnt.getRevisionHistory(startDate, endDate, branchType, branchName);
				
		return response;
	}
	
	@GET
	@Path("/serviceprovider/getconfigurationitem")
	public String getConfigurationItem(@QueryParam("itemURI") String itemURI,						
			@QueryParam("branchType") String branchType,
			@QueryParam("branchName") String branchName) {
		
		logger.info("getConfigurationItem");
		SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
		SCMOntology scmOnt = SCMOntology.getInstance();
		String response = "";
		
		if(itemURI == null || itemURI.equalsIgnoreCase("")) {
			
			response = scmStandardOnt.getErrorXML(ServiceException.MISSINGPARAMETER_ERROR_CODE,
					ServiceException.MISSINGPARAMETER_ERROR);
			
		} else {
			response = scmOnt.getConfigurationItem(itemURI, branchType, branchName);
		}
				
		return response;
	}
	
	@GET
	@Path("/serviceprovider/getconfigurationitemrevisionhistory")
	public String getConfigurationItemRevisionHistory(@QueryParam("itemURI") String itemURI,					
			@QueryParam("branchType") String branchType,
			@QueryParam("branchName") String branchName) {
		
		logger.info("getConfigurationItemRevisionHistory");
		SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
		SCMOntology scmOnt = SCMOntology.getInstance();
		String response = "";
		
		if(itemURI == null || itemURI.equalsIgnoreCase("")) {
			
			response = scmStandardOnt.getErrorXML(ServiceException.MISSINGPARAMETER_ERROR_CODE,
					ServiceException.MISSINGPARAMETER_ERROR);
			
		} else {
			response = scmOnt.getConfigurationItemHistory(itemURI, branchType, branchName);
		}
				
		return response;
	}
	
	@GET
	@Path("/serviceprovider/getrevisionbycommitmessage")
	public String getRevisionByCommitMessage(@QueryParam("searchParameter") String searchParameter,						
			@QueryParam("branchType") String branchType,
			@QueryParam("branchName") String branchName) {
		
		logger.info("searchForCommitMessage");
		SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
		SCMOntology scmOnt = SCMOntology.getInstance();
		String response = "";
		
		if(searchParameter == null || searchParameter.equalsIgnoreCase("")) {
			
			response = scmStandardOnt.getErrorXML(ServiceException.MISSINGPARAMETER_ERROR_CODE,
					ServiceException.MISSINGPARAMETER_ERROR);
			
		} else {
			response = scmOnt.getRevisionByCommitMessage(searchParameter, branchType, branchName);
		}
				
		return response;
	}
	

}
