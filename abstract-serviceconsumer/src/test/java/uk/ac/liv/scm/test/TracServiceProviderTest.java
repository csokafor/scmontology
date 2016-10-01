package uk.ac.liv.scm.test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.liv.scm.ontology.entity.Issue;
import uk.ac.liv.scm.server.SCMHttpServer;
import uk.ac.liv.scm.serviceconsumer.AbstractServiceClient;

public class TracServiceProviderTest {
	
	AbstractServiceClient serviceClient;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static final Logger logger = Logger.getLogger(TracServiceProviderTest.class);
	
	
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
    public void testCreateIssue() {
	   logger.info("testCreateIssue");
    	String issueDate = dateFormat.format(new Date());
    	String issueTitle = "SCM unit test ticket";
    	String issueDescription = "Ticket created by trac service provider";
    	String issueType = "task";
    	String issueSeverity = "minor";
    	
    	String newIssue = serviceClient.createIssueXML("", issueTitle, issueDescription, 
    			issueType, "new", issueSeverity, "2.0", issueDate, true);
    	Issue issue = serviceClient.createIssueService(newIssue);
    	logger.info(issue);
    	
    	assertTrue(issue.getIssueId().length() > 0);
    	assertEquals(issueTitle, issue.getIssueTitle());
    	assertEquals(issueDescription, issue.getIssueDescription());
    	assertEquals(issueType, issue.getIssueType());
    	assertEquals(issueSeverity, issue.getIssueSeverity());
    }
   
   
    @Test
    public void testUpdateIssue() {
    	logger.info("testUpdateIssue");
    	String issueDate = dateFormat.format(new Date());
    	String issueId = "8";
    	String issueTitle = "Ontology unit test ticket";
    	String issueDescription = "ticket updated by trac service provider";
    	String issueType = "enhancement";
    	String issueSeverity = "critical";
    	
    	String newIssue = serviceClient.createIssueXML(issueId, issueTitle, issueDescription, 
    			issueType, "open", issueSeverity, "2.0", issueDate, true);
    	Issue issue = serviceClient.updateIssueService(newIssue);
    	logger.info(issue);
    	
    	assertEquals(issueId, issue.getIssueId());
    	assertEquals(issueTitle, issue.getIssueTitle());
    	assertEquals(issueType, issue.getIssueType());
    	assertEquals(issueSeverity, issue.getIssueSeverity());
    }
    
    
    @Test
    public void testGetIssue() {
    	logger.info("testGetIssue");
    	String issueId = "8";
    	Issue issue = serviceClient.getIssueService(issueId);
    	logger.info(issue);
    	assertEquals(issueId, issue.getIssueId());
    }
    
    
    
    @Test
    public void testDeleteIssue() {
    	logger.info("testDeleteIssue");
    	String issueId = "8";
    	String response = serviceClient.deleteIssueService(issueId);
    	logger.info(response);
    	    	
    	Issue issue = serviceClient.getIssueService(issueId);    	
    	assertNull(issue);
    }
    
    
    @Test
    public void testSearchForIssueType() {    	
    	logger.info("testSearchForIssueType");    	
    	String searchParam = "type=defect&priority=critical";
    	List<Issue> issueList = serviceClient.searchForIssueService(searchParam);    	
    	assertTrue(issueList.size() > 0);
    	
    	Iterator issueIt = issueList.iterator();
    	while(issueIt.hasNext()) {
    		Issue issue = (Issue)issueIt.next();
    		assertTrue(issue.getIssueType().equalsIgnoreCase("defect"));
    		assertTrue(issue.getIssueSeverity().equalsIgnoreCase("critical"));
    	}
    }
    
    @Test
    public void testSearchForClosedIssue() {    	
    	logger.info("testSearchForClosedIssue");    	
    	String searchParam = "status=closed&version=1.0";
    	List<Issue> issueList = serviceClient.searchForIssueService(searchParam);    	
    	assertTrue(issueList.size() > 0);
    	
    	Iterator issueIt = issueList.iterator();
    	while(issueIt.hasNext()) {
    		Issue issue = (Issue)issueIt.next();
    		assertTrue(issue.getIssueStatus().equalsIgnoreCase("closed"));
    		assertTrue(issue.getBuildVersion().equalsIgnoreCase("1.0"));
    	}
    }
	
}
