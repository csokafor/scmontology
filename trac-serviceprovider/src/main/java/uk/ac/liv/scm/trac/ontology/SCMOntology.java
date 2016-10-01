package uk.ac.liv.scm.trac.ontology;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import redstone.xmlrpc.XmlRpcFault;

import uk.ac.liv.scm.exception.IssueNotFoundException;
import uk.ac.liv.scm.exception.TracServiceException;
import uk.ac.liv.scm.exception.ServiceException;
import uk.ac.liv.scm.ontology.entity.Branch;
import uk.ac.liv.scm.ontology.entity.ConfigurationItem;
import uk.ac.liv.scm.ontology.entity.Issue;
import uk.ac.liv.scm.ontology.entity.Person;
import uk.ac.liv.scm.ontology.entity.Repository;
import uk.ac.liv.scm.ontology.entity.Revision;
import uk.ac.liv.scm.ontology.entity.Version;
import uk.ac.liv.scm.trac.ontology.SCMStandardOntology;
import uk.ac.liv.scm.trac.TracXMLClient;
import uk.ac.liv.scm.util.OntologyUtil;


import com.hp.hpl.jena.ontology.Individual;

public class SCMOntology extends uk.ac.liv.scm.ontology.SCMOntology {
	
	private static final Logger logger = Logger.getLogger(SCMOntology.class);
	private static SCMOntology scmOntology;
	private TracXMLClient tracClient;

	private SCMOntology() {
		super();
		tracClient = TracXMLClient.getInstance();
	}
	
	public static SCMOntology getInstance() {
		if(scmOntology == null) {
			scmOntology = new SCMOntology();			
		}		
		return scmOntology;
	}
	
	public String createIssue(String issue) {
		String issueXML = "";
		clearServiceModel();
		
		try {
			InputStream is = new ByteArrayInputStream(issue.getBytes());
			serviceModel.read(is, SCM_OWL_BASE, "RDF/XML");			
			Map<String,String> issuePropertyMap = OntologyUtil.getInstanceProperties(serviceModel, SCM_NS + "Issue");
						
			clearServiceModel();
			Issue newIssue = tracClient.createIssue(issuePropertyMap);
			String issueDate = dateFormat.format(newIssue.getIssueDate());
			//Individual personOnt = this.createPerson(newIssue.getIsAssignedTo().getPersonId(), 
			//		newIssue.getIsAssignedTo().getPersonName(), newIssue.getIsAssignedTo().getEmail());
			
			Individual newIssueOnt = this.createIssue(newIssue.getIssueId(), newIssue.getIssueTitle(), 
					newIssue.getIssueDescription(), newIssue.getIssueType(), newIssue.getIssueStatus(), 
					newIssue.getIssueSeverity(), newIssue.getBuildVersion(), issueDate, null, false);
			
			issueXML = this.printServiceModel();
			
		} catch(Exception e) {
			logger.error("createIssue exception: " + e.getMessage());
			SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
			issueXML = scmStandardOnt.getErrorXML(TracServiceException.SERVER_ERROR_CODE,
					TracServiceException.SERVER_ERROR);
		}		
		
		return issueXML;
	}
	
	public String updateIssue(String issue) {
		String issueXML = "";
		clearServiceModel();
		
		try {
			InputStream is = new ByteArrayInputStream(issue.getBytes());
			serviceModel.read(is, SCM_OWL_BASE, "RDF/XML");
			
			Map<String,String> issuePropertyMap = OntologyUtil.getInstanceProperties(serviceModel, SCM_NS + "Issue");
					
			clearServiceModel();
			//update issue
			Issue newIssue = tracClient.updateIssue(issuePropertyMap);
			String issueDate = dateFormat.format(newIssue.getIssueDate());
			Individual personOnt = null;
			Person person = newIssue.getIsAssignedTo();
			if(person.getPersonId() != null && (!person.getPersonId().isEmpty())) {
				personOnt = this.createPerson(person.getPersonId(), 
						person.getPersonName(), person.getEmail());
			}
			
			Individual newIssueOnt = this.createIssue(newIssue.getIssueId(), newIssue.getIssueTitle(), 
					newIssue.getIssueDescription(), newIssue.getIssueType(), newIssue.getIssueStatus(), 
					newIssue.getIssueSeverity(), newIssue.getBuildVersion(), issueDate, personOnt, newIssue.isApproved());
									
			issueXML = this.printServiceModel();
			
		} catch(Exception e) {
			logger.error("updateIssue exception: " + e.getMessage());
			SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
			issueXML = scmStandardOnt.getErrorXML(TracServiceException.SERVER_ERROR_CODE,
					TracServiceException.SERVER_ERROR);
		}
		
		return issueXML;
	}
	
	public String getIssue(String issueId) {
		String issueXML = "";
		clearServiceModel();
		
		try {
			//get issue by issue id
			Issue issue = tracClient.getIssue(issueId);
			String issueDate = dateFormat.format(issue.getIssueDate());
			Individual personOnt = null;
			Person person = issue.getIsAssignedTo();
			if(person.getPersonId() != null && (!person.getPersonId().isEmpty())) {
				personOnt = this.createPerson(issue.getIsAssignedTo().getPersonId(), 
					issue.getIsAssignedTo().getPersonName(), issue.getIsAssignedTo().getEmail());
			}
			
			Individual issueOnt = this.createIssue(issueId, issue.getIssueTitle(), 
					issue.getIssueDescription(), issue.getIssueType(), issue.getIssueStatus(), 
					issue.getIssueSeverity(), issue.getBuildVersion(), issueDate, personOnt, issue.isApproved());
			
			issueXML = this.printServiceModel();
		
		} catch(Exception e) {
			logger.error("getIssue exception: " + e.getMessage());
			SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
			issueXML = scmStandardOnt.getErrorXML(TracServiceException.SERVER_ERROR_CODE,
					TracServiceException.SERVER_ERROR);
		}
		
		return issueXML;
	}
	
	
	public String deleteIssue(String issueId) {
		String issueXML = "";
		clearServiceModel();
		//delete issue by issue id
		try {
			Integer id = tracClient.deleteIssue(issueId);
			serviceModel.createTypedLiteral(issueId);
			issueXML = this.printServiceModel();
		
		} catch(Exception e) {
			logger.error("deleteIssue Exception: " + e.getMessage(), e);
			SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
			issueXML = scmStandardOnt.getErrorXML(TracServiceException.SERVER_ERROR_CODE,
					TracServiceException.SERVER_ERROR);
		}
		
		return issueXML;
	}
	
	public String searchForIssue(String searchQuery) {
		String issueXML = "";
		clearServiceModel();
		//search for issues
		try {
			List<Issue> issues = tracClient.findIssueByParameter(searchQuery);
			Iterator issueIt = issues.iterator();
			while(issueIt.hasNext()) {
				Issue issue = (Issue)issueIt.next();
				String issueDate = dateFormat.format(issue.getIssueDate());
				Individual personOnt = null;
				Person person = issue.getIsAssignedTo();
				if(person.getPersonId() != null && (!person.getPersonId().isEmpty())) {
					personOnt = this.createPerson(issue.getIsAssignedTo().getPersonId(), 
						issue.getIsAssignedTo().getPersonName(), issue.getIsAssignedTo().getEmail());
				}
				
				Individual issueOnt = this.createIssue(issue.getIssueId(), issue.getIssueTitle(), 
						issue.getIssueDescription(), issue.getIssueType(), issue.getIssueStatus(), 
						issue.getIssueSeverity(), issue.getBuildVersion(), issueDate, personOnt, issue.isApproved());
			}
			issueXML = this.printServiceModel();
		
		} catch(Exception e) {
			logger.error("searchForIssue Exception: " + e.getMessage(), e);
			SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
			issueXML = scmStandardOnt.getErrorXML(TracServiceException.SERVER_ERROR_CODE,
					TracServiceException.SERVER_ERROR);
		}
						
		return issueXML;
	}
	
}
