package uk.ac.liv.scm.svn.ontology;

import org.apache.log4j.Logger;

import uk.ac.liv.scm.exception.SVNServiceException;
import uk.ac.liv.scm.exception.ServiceException;
import uk.ac.liv.scm.svn.util.Constants;

import com.hp.hpl.jena.ontology.Individual;


public class SCMStandardOntology extends uk.ac.liv.scm.ontology.SCMStandardOntology {
	
	private static final Logger logger = Logger.getLogger(SCMStandardOntology.class);
	private static SCMStandardOntology scmStandardOnt;
	
	private SCMStandardOntology() {
		super();
	}
	
	
	public static SCMStandardOntology getInstance() {
		if(scmStandardOnt == null) {
			scmStandardOnt = new SCMStandardOntology();			
		}
		
		return scmStandardOnt;
	}
	
	public String getServiceProviderXML() {
		clearServiceModel();
		initServiceProvider();
		validateServiceModel();
		String rdfXML = this.printServiceModel();
				
		return rdfXML;
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
			
			Individual pathNotFoundError = this.createError(SVNServiceException.PATHNOTFOUND_ERROR_CODE, 
					SVNServiceException.PATHNOTFOUND_ERROR);
			serviceProvider.addProperty(getObjectProperty("error"), pathNotFoundError);
			Individual revisionNotFoundError = this.createError(SVNServiceException.REVISIONNOTFOUND_ERROR_CODE, 
					SVNServiceException.REVISIONNOTFOUND_ERROR);
			serviceProvider.addProperty(getObjectProperty("error"), revisionNotFoundError);
			
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
						
						
		} catch(Exception e) {
			logger.error("Could not initialise Service Provider: " + e.getMessage(), e);
		}
	}

}
