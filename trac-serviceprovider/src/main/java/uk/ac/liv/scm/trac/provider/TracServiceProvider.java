package uk.ac.liv.scm.trac.provider;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;

import uk.ac.liv.scm.exception.ServiceException;
import uk.ac.liv.scm.trac.ontology.SCMOntology;
import uk.ac.liv.scm.trac.ontology.SCMStandardOntology;

@Path("/scm")
@Produces({"application/xml","text/plain"})
public class TracServiceProvider {
	
	private static final Logger logger = Logger.getLogger(TracServiceProvider.class);
	
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
	
	@POST
	@Path("/serviceprovider/createissue")
	public String createIssue(String issue) {
		
		logger.info("createIssue: " + issue);
		SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
		SCMOntology scmOnt = SCMOntology.getInstance();
		String response = "";
		
		if(issue == null || issue.equalsIgnoreCase("")) {
			
			response = scmStandardOnt.getErrorXML(ServiceException.MISSINGPARAMETER_ERROR_CODE,
					ServiceException.MISSINGPARAMETER_ERROR);
			
		} else {
			response = scmOnt.createIssue(issue);
		}
				
		return response;
	}
	/*
	<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:scm="http://www.scm.org/scm.owl#"
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:scmservice="http://www.scm.org/scmservice.owl#" > 
	<rdf:Description rdf:about="http://www.scm.org/scm.owl#chinedu">
	<scm:email rdf:datatype="http://www.w3.org/2001/XMLSchema#string"/>
	<scm:personName rdf:datatype="http://www.w3.org/2001/XMLSchema#string">chinedu</scm:personName>
	<scm:personId rdf:datatype="http://www.w3.org/2001/XMLSchema#string">chinedu</scm:personId>
	<rdf:type rdf:resource="http://www.scm.org/scm.owl#Person"/></rdf:Description>
	<rdf:Description rdf:about="http://www.scm.org/scm.owl#2">
	<scm:issueType rdf:datatype="http://www.w3.org/2001/XMLSchema#string">enhancement</scm:issueType>
	<scm:buildVersion rdf:datatype="http://www.w3.org/2001/XMLSchema#string">1.0</scm:buildVersion>
	<scm:issueDescription rdf:datatype="http://www.w3.org/2001/XMLSchema#string">Add support for subversion file protocol</scm:issueDescription>
	<scm:issueId rdf:datatype="http://www.w3.org/2001/XMLSchema#string">2</scm:issueId>
	<scm:issueTitle rdf:datatype="http://www.w3.org/2001/XMLSchema#string">Subversion file protocol</scm:issueTitle>
	<scm:approved rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean">true</scm:approved>
	<scm:issueSeverity rdf:datatype="http://www.w3.org/2001/XMLSchema#string">minor</scm:issueSeverity>
	<rdf:type rdf:resource="http://www.scm.org/scm.owl#Issue"/><scm:isAssignedTo rdf:resource="http://www.scm.org/scm.owl#chinedu"/>
	<scm:issueDate rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime">2013-11-10T21:05:39</scm:issueDate>
	<scm:issueStatus rdf:datatype="http://www.w3.org/2001/XMLSchema#string">new</scm:issueStatus>
	</rdf:Description>
	</rdf:RDF>
	*/
	
	@PUT
	@Path("/serviceprovider/updateissue")
	public String updateIssue(String issue) {
		
		logger.info("updateIssue: " + issue);
		SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
		SCMOntology scmOnt = SCMOntology.getInstance();
		String response = "";
		
		if(issue == null || issue.equalsIgnoreCase("")) {
			
			response = scmStandardOnt.getErrorXML(ServiceException.MISSINGPARAMETER_ERROR_CODE,
					ServiceException.MISSINGPARAMETER_ERROR);
			
		} else {
			response = scmOnt.updateIssue(issue);
		}
				
		return response;
	}
	
	@GET
	@Path("/serviceprovider/getissue")
	public String getIssue(@QueryParam("issueId") String issueId) {
		
		logger.info("getIssue");
		SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
		SCMOntology scmOnt = SCMOntology.getInstance();
		String response = "";
		
		if(issueId == null || issueId.equalsIgnoreCase("")) {
			
			response = scmStandardOnt.getErrorXML(ServiceException.MISSINGPARAMETER_ERROR_CODE,
					ServiceException.MISSINGPARAMETER_ERROR);
			
		} else {
			response = scmOnt.getIssue(issueId);
		}
				
		return response;
	}
	
	@DELETE
	@Path("/serviceprovider/deleteissue")
	public String deleteIssue(@QueryParam("issueId") String issueId) {
		
		logger.info("deleteIssue");
		SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
		SCMOntology scmOnt = SCMOntology.getInstance();
		String response = "";
		
		if(issueId == null || issueId.equalsIgnoreCase("")) {
			
			response = scmStandardOnt.getErrorXML(ServiceException.MISSINGPARAMETER_ERROR_CODE,
					ServiceException.MISSINGPARAMETER_ERROR);
			
		} else {
			response = scmOnt.deleteIssue(issueId);
		}
				
		return response;
	}
	
	@GET
	@Path("/serviceprovider/searchforissue")
	public String searchForIssue(@QueryParam("searchQuery") String searchQuery) {
		//status!=closed
		
		logger.info("issueSearch");
		SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
		SCMOntology scmOnt = SCMOntology.getInstance();
		String response = "";
		
		if(searchQuery == null || searchQuery.equalsIgnoreCase("")) {
			
			response = scmStandardOnt.getErrorXML(ServiceException.MISSINGPARAMETER_ERROR_CODE,
					ServiceException.MISSINGPARAMETER_ERROR);
			
		} else {
			response = scmOnt.searchForIssue(searchQuery);
		}
				
		return response;
	}


}
