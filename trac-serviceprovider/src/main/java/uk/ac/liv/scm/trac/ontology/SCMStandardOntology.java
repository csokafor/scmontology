package uk.ac.liv.scm.trac.ontology;

import org.apache.log4j.Logger;

import uk.ac.liv.scm.exception.TracServiceException;
import uk.ac.liv.scm.exception.ServiceException;
import uk.ac.liv.scm.trac.util.Constants;

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
									
			//set services
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

}
