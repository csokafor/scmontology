package uk.ac.liv.scm.svn.ontology;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import uk.ac.liv.scm.exception.PathNotFoundException;
import uk.ac.liv.scm.exception.RevisionNotFoundException;
import uk.ac.liv.scm.exception.SVNServiceException;
import uk.ac.liv.scm.exception.ServiceException;
import uk.ac.liv.scm.ontology.entity.Branch;
import uk.ac.liv.scm.ontology.entity.ConfigurationItem;
import uk.ac.liv.scm.ontology.entity.Person;
import uk.ac.liv.scm.ontology.entity.Repository;
import uk.ac.liv.scm.ontology.entity.Revision;
import uk.ac.liv.scm.ontology.entity.Version;
import uk.ac.liv.scm.svn.SVNClient;

import com.hp.hpl.jena.ontology.Individual;

public class SCMOntology extends uk.ac.liv.scm.ontology.SCMOntology {
	
	private static final Logger logger = Logger.getLogger(SCMOntology.class);
	private static SCMOntology scmOntology;

	private SCMOntology() {
		super();
	}
	
	public static SCMOntology getInstance() {
		if(scmOntology == null) {
			scmOntology = new SCMOntology();			
		}		
		return scmOntology;
	}
	
	public String getRepository(String branchType, String branchName) {
		String repositoryXML = "";
		clearServiceModel();
		//get repository
		try {
			SVNClient svnClient = SVNClient.getInstance();
			Repository repository = svnClient.getRepositoryDetails(branchType, branchName);
						
			Individual repositoryOnt = this.createRepository(repository.getRepositoryName(),
					repository.getRepositoryDescription(), repository.getRepositoryURL());
			
			if(repository.getBranches() != null) {
				Iterator branchIt = repository.getBranches().iterator();
				while(branchIt.hasNext()) {
					Branch repoBranch = (Branch)branchIt.next();
					Individual branch = this.createBranch(repoBranch.getBranchName(), repoBranch.getBranchType());
					repositoryOnt.addProperty(this.getObjectProperty("branches"), branch);
				}
			}
			
			if(repository.getLastVersion() != null) {
				Version version = repository.getLastVersion();
				Person person = version.getAuthor();
				Individual personOnt = this.createPerson(person.getPersonId(), person.getPersonName(), person.getEmail());
				Individual versionOnt = this.createVersion(version.getVersionId(), dateFormat.format(version.getVersionDate()), 
						"", false, personOnt);
			}
									
			repositoryXML = this.printServiceModel();
			
		} catch(PathNotFoundException pe) {
			SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
			repositoryXML = scmStandardOnt.getErrorXML(SVNServiceException.PATHNOTFOUND_ERROR_CODE,
					SVNServiceException.PATHNOTFOUND_ERROR);
		} catch(SVNServiceException se) {
			SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
			repositoryXML = scmStandardOnt.getErrorXML(SVNServiceException.SERVER_ERROR_CODE,
					SVNServiceException.SERVER_ERROR);
		}
				
		return repositoryXML;		
	}
	
	public String getRevision(String revisionNumber, String branchType, String branchName) {
		
		String revisionXML = "";
		clearServiceModel();
		//get revision
		try {
			SVNClient svnClient = SVNClient.getInstance();
			Version version = svnClient.getRevisionDetails(revisionNumber, branchType, branchName);
			
			Person person = version.getAuthor();
			Individual personOnt = this.createPerson(person.getPersonId(), person.getPersonName(), person.getEmail());
			Individual versionOnt = this.createVersion(version.getVersionId(), dateFormat.format(version.getVersionDate()), 
					version.getCommitMessage(), false, personOnt);
			
			if(version.getModifiedItems() != null && (!version.getModifiedItems().isEmpty())) {
				Iterator itemIt = version.getModifiedItems().iterator();
				while(itemIt.hasNext()) {
					ConfigurationItem item = (ConfigurationItem)itemIt.next();
					Individual itemOnt = this.createConfigurationItem(item.getItemName(), item.getMimeType(),
							item.getItemURI(), item.getItemContent(), versionOnt);
					
					versionOnt.addProperty(getObjectProperty("modifiedItems"), itemOnt);
				}
			}
			revisionXML = this.printServiceModel();
			
		} catch(RevisionNotFoundException re) {
			SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
			revisionXML = scmStandardOnt.getErrorXML(SVNServiceException.REVISIONNOTFOUND_ERROR_CODE,
					SVNServiceException.REVISIONNOTFOUND_ERROR);
		} catch(PathNotFoundException pe) {
			SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
			revisionXML = scmStandardOnt.getErrorXML(SVNServiceException.PATHNOTFOUND_ERROR_CODE,
					SVNServiceException.PATHNOTFOUND_ERROR);
		} catch(SVNServiceException se) {
			SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
			revisionXML = scmStandardOnt.getErrorXML(SVNServiceException.SERVER_ERROR_CODE,
					SVNServiceException.SERVER_ERROR);
		}
				
		return revisionXML;		
	}
	
	public String getRevisionHistory(String startDate, String endDate, 
			String branchType, String branchName) {
		
		String revisionXML = "";
		clearServiceModel();
		//get revision history
		
		try {
			SVNClient svnClient = SVNClient.getInstance();
			List<Version> versionList = svnClient.getRevisionHistory(startDate, endDate, branchType, branchName);
			Iterator versionIt = versionList.iterator();
			while(versionIt.hasNext()) {
				Version version = (Version)versionIt.next();
				Person person = version.getAuthor();
				Individual personOnt = null;
				if(person != null) {
					personOnt = this.createPerson(person.getPersonId(), person.getPersonName(), person.getEmail());
				}
				Individual versionOnt = this.createVersion(version.getVersionId(), dateFormat.format(version.getVersionDate()), 
						version.getCommitMessage(), false, personOnt);
								
			}
			revisionXML = this.printServiceModel();
						
		} catch(RevisionNotFoundException re) {
			SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
			revisionXML = scmStandardOnt.getErrorXML(SVNServiceException.REVISIONNOTFOUND_ERROR_CODE,
					SVNServiceException.REVISIONNOTFOUND_ERROR);
		} catch(PathNotFoundException pe) {
			SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
			revisionXML = scmStandardOnt.getErrorXML(SVNServiceException.PATHNOTFOUND_ERROR_CODE,
					SVNServiceException.PATHNOTFOUND_ERROR);
		} catch(SVNServiceException se) {
			SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
			revisionXML = scmStandardOnt.getErrorXML(SVNServiceException.SERVER_ERROR_CODE,
					SVNServiceException.SERVER_ERROR);
		}
		
		return revisionXML;		
	}
	
	public String getConfigurationItem(String itemURI, String branchType, String branchName) {
		
		String configItemXML = "";
		clearServiceModel();
		//get configuration item
		try {
			SVNClient svnClient = SVNClient.getInstance();
			ConfigurationItem item = svnClient.getConfigurationItem(itemURI, branchType, branchName);
			Version version = item.getHasVersion();
			Person person = version.getAuthor();
			Individual personOnt = this.createPerson(person.getPersonId(), person.getPersonName(), person.getEmail());
			Individual versionOnt = this.createVersion(version.getVersionId(), dateFormat.format(version.getVersionDate()), 
					version.getCommitMessage(), false, personOnt);
			Individual itemOnt = this.createConfigurationItem(item.getItemName(), item.getMimeType(),
					item.getItemURI(), item.getItemContent(), versionOnt);
					
			configItemXML = this.printServiceModel();
						
		
		} catch(PathNotFoundException pe) {
			SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
			configItemXML = scmStandardOnt.getErrorXML(SVNServiceException.PATHNOTFOUND_ERROR_CODE,
					SVNServiceException.PATHNOTFOUND_ERROR);
		} catch(Exception ex) {
			SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
			configItemXML = scmStandardOnt.getErrorXML(SVNServiceException.SERVER_ERROR_CODE,
					SVNServiceException.SERVER_ERROR);
		}
						
		return configItemXML;		
	}
	
	public String getConfigurationItemHistory(String itemURI, String branchType, String branchName) {
		
		String revisionXML = "";
		clearServiceModel();
		//get configuration item version history
		try {
			SVNClient svnClient = SVNClient.getInstance();
			List<Version> versionList = svnClient.getConfigurationItemHistory(itemURI, branchType, branchName);
			Iterator versionIt = versionList.iterator();
			while(versionIt.hasNext()) {
				Version version = (Version)versionIt.next();
				Person person = version.getAuthor();
				Individual personOnt = null;
				if(person != null) {
					personOnt = this.createPerson(person.getPersonId(), person.getPersonName(), person.getEmail());
				}
				Individual versionOnt = this.createVersion(version.getVersionId(), dateFormat.format(version.getVersionDate()), 
						version.getCommitMessage(), false, personOnt);
								
			}
			revisionXML = this.printServiceModel();
						
		} catch(RevisionNotFoundException re) {
			SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
			revisionXML = scmStandardOnt.getErrorXML(SVNServiceException.REVISIONNOTFOUND_ERROR_CODE,
					SVNServiceException.REVISIONNOTFOUND_ERROR);
		} catch(PathNotFoundException pe) {
			SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
			revisionXML = scmStandardOnt.getErrorXML(SVNServiceException.PATHNOTFOUND_ERROR_CODE,
					SVNServiceException.PATHNOTFOUND_ERROR);
		} catch(SVNServiceException se) {
			logger.warn("caught SVNServiceException");
			SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
			revisionXML = scmStandardOnt.getErrorXML(SVNServiceException.SERVER_ERROR_CODE,
					SVNServiceException.SERVER_ERROR);
		}		
								
		return revisionXML;		
	}
	
	
	public String getRevisionByCommitMessage(String searchParameter, String branchType, String branchName) {
		
		String revisionXML = "";
		clearServiceModel();
		//get revisions with commit message that match searchParamer
		
		try {
			SVNClient svnClient = SVNClient.getInstance();
			List<Version> versionList = svnClient.getRevisionByMessage(searchParameter, branchType, branchName);
			Iterator versionIt = versionList.iterator();
			while(versionIt.hasNext()) {
				Version version = (Version)versionIt.next();
				Person person = version.getAuthor();
				Individual personOnt = null;
				if(person != null) {
					personOnt = this.createPerson(person.getPersonId(), person.getPersonName(), person.getEmail());
				}
				Individual versionOnt = this.createVersion(version.getVersionId(), dateFormat.format(version.getVersionDate()), 
						version.getCommitMessage(), false, personOnt);
								
			}
			revisionXML = this.printServiceModel();
						
		} catch(RevisionNotFoundException re) {
			SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
			revisionXML = scmStandardOnt.getErrorXML(SVNServiceException.REVISIONNOTFOUND_ERROR_CODE,
					SVNServiceException.REVISIONNOTFOUND_ERROR);
		} catch(PathNotFoundException pe) {
			SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
			revisionXML = scmStandardOnt.getErrorXML(SVNServiceException.PATHNOTFOUND_ERROR_CODE,
					SVNServiceException.PATHNOTFOUND_ERROR);
		} catch(SVNServiceException se) {
			logger.warn("caught SVNServiceException");
			SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
			revisionXML = scmStandardOnt.getErrorXML(SVNServiceException.SERVER_ERROR_CODE,
					SVNServiceException.SERVER_ERROR);
		}	
		
		return revisionXML;		
	}
}
