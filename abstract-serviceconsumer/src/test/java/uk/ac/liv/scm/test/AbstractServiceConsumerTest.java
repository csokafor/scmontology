package uk.ac.liv.scm.test;

import static org.junit.Assert.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import uk.ac.liv.scm.ontology.entity.ConfigurationItem;
import uk.ac.liv.scm.ontology.entity.Issue;
import uk.ac.liv.scm.ontology.entity.Repository;
import uk.ac.liv.scm.ontology.entity.Revision;
import uk.ac.liv.scm.server.SCMHttpServer;
import uk.ac.liv.scm.serviceconsumer.AbstractServiceClient;

@Ignore
public class AbstractServiceConsumerTest {

	AbstractServiceClient serviceClient;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		
	
	@Before
    public void setUp() throws Exception {
		SCMHttpServer.startServer();
		serviceClient = new AbstractServiceClient();
    }

    @After
    public void tearDown() throws Exception {
    	SCMHttpServer.stopServer();
    }
    
    @Test
    public void testGetRepositoryContentService() {
    	Repository repository = serviceClient.getRepositoryContentService(
    				"branch", "prototype");
    	assertNotNull(repository);
    	
    }
    
    @Test
    public void testGetRevision() {
    	Revision revision = serviceClient.getRevisionService("testRevisionNumber", 
    			"branch", "prototype");
    	
    	assertNotNull(revision);
    	
    }
    
    @Test
    public void testGetRevisionHistory() {
    	List<Revision> revisionList = serviceClient.getRevisionHistoryService( 
    			"2013-10-01", "2013-10-30", "branch", "prototype");
    	
    	assertNotNull(revisionList);
    	
    }
    
    @Test
    public void testGetConfigurationItem() {
    	ConfigurationItem configurationItem = serviceClient.getConfigurationItemService("item34", 
    			"branch", "prototype");
    	
    	assertNotNull(configurationItem);
    	
    }
    
    @Test
    public void testGetConfigurationItemRevisionHistory() {
    	List<Revision> revisionList = serviceClient.getConfigurationItemHistoryService("item21",  
    			 "branch", "prototype");
    	
    	assertNotNull(revisionList);
    	
    }
    
    @Test
    public void testGetRevisionByCommitMessage() {
    	List<Revision> revisionList = serviceClient.getRevisionByCommitMessageService("Modification", 
    			 "branch", "prototype");
    	
    	assertNotNull(revisionList);
    	
    }
    
    @Test
    public void testCreateIssue() {
    	String issueDate = dateFormat.format(new Date());
    	String newIssue = serviceClient.createIssueXML("BG1", "create issue test", "issue description", 
    			"bug", "open", "critical", "2.1", issueDate, true);
    	Issue issue = serviceClient.createIssueService(newIssue);
    	assertNotNull(issue);
    }
    
    @Test
    public void testUpdateIssue() {
    	String issueDate = dateFormat.format(new Date());
    	String newIssue = serviceClient.createIssueXML("EH1", "update issue test", "issue description", 
    			"enhancement", "open", "low", "2.1", issueDate, true);
    	Issue issue = serviceClient.updateIssueService(newIssue);
    	assertNotNull(issue);
    }
    
    @Test
    public void testGetIssue() {
    	String issueId = "bug7";
    	Issue issue = serviceClient.getIssueService(issueId);
    	assertNotNull(issue);
    }
    
    @Test
    public void testDeleteIssue() {
    	String issueId = "bug8";
    	String response = serviceClient.deleteIssueService(issueId);
    	assertNotNull(response);
    }
    
    @Test
    public void testSearchForIssue() {    	
    	List<Issue> issueList = serviceClient.searchForIssueService("status=closed");
    	assertNotNull(issueList);
    }
    
    
    
}
