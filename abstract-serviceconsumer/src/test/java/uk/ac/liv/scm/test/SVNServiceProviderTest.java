package uk.ac.liv.scm.test;


import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.liv.scm.ontology.entity.ConfigurationItem;
import uk.ac.liv.scm.ontology.entity.Repository;
import uk.ac.liv.scm.ontology.entity.Revision;
import uk.ac.liv.scm.server.SCMHttpServer;
import uk.ac.liv.scm.serviceconsumer.AbstractServiceClient;

public class SVNServiceProviderTest {

	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static SimpleDateFormat paramDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static final Logger logger = Logger.getLogger(SVNServiceProviderTest.class);
	private static String BRANCHES = "branches";
	private static String TAGS = "tags";
	AbstractServiceClient serviceClient;
	
	
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
    public void testGetRepository() {
    	//repository root test
    	logger.info("testGetRepository()");    	
    	String testURL = "http://core.svn.wordpress.org";
    	Repository repository = serviceClient.getRepositoryContentService(null, null);
    	logger.info(repository.toString());    	
    	    	    	
    	assertEquals("Repository not found", testURL,repository.getRepositoryURL());
    }
    
    @Test
    public void testGetRepositoryBranch() {
    	logger.info("testGetRepositoryBranch()");    	
    	String testURL = "http://core.svn.wordpress.org/branches/3.6";
    	
    	Repository repository = serviceClient.getRepositoryContentService(
				"branch", "3.6");
    	logger.info(repository.toString());
    	    	    	    	
    	assertEquals("Repository not found",testURL,repository.getRepositoryURL());
    }
    
    @Test
    public void testGetRepositoryTag() {
    	logger.info("testGetRepositoryTag()");    	
    	String testURL = "http://core.svn.wordpress.org/tags/3.5";
    	
    	Repository repository = serviceClient.getRepositoryContentService(
				"tag", "3.5");
    	logger.info(repository.toString());
    	    	    	    	
    	assertEquals("Repository not found",testURL,repository.getRepositoryURL());
    }
    
    
    
    @Test
    public void testGetRevision() {
    	logger.info("testGetRevision()");
    	//String revisionId = "8";
    	String revisionId = "23268";
    	String message = "Adding svntest branch";
    	Revision revision = serviceClient.getRevisionService(revisionId, 
    			null, null);
    	logger.info(revision.toString());
    	    	
    	assertEquals(revisionId, revision.getVersionId());    	
    }
    
    
    @Test
    public void testGetRevisionBranch() {
    	logger.info("testGetRevisionBranch()");
    	//String revisionId = "8";
    	String revisionId = "25332";
    	
    	Revision revision = serviceClient.getRevisionService(revisionId, 
    			"branch", "3.6");
    	logger.info(revision.toString());
    	    	
    	assertEquals(revisionId, revision.getVersionId());    	
    }
    
    @Test
    public void testGetRevisionTag() {
    	logger.info("testGetRevisionTag()");
    	//String revisionId = "8";
    	String revisionId = "23169";
    	
    	Revision revision = serviceClient.getRevisionService(revisionId, 
    			"tag", "3.5");
    	logger.info(revision.toString());
    	    	
    	assertEquals(revisionId, revision.getVersionId());    	
    }
    
    
    @Test
    public void testGetRevisionHistory() {
    	logger.info("testGetRevisionHistory()");
    	String sDate = "2012-12-21";
    	String eDate = "2012-12-23";
    	
    	List<Revision> revisionList = serviceClient.getRevisionHistoryService( 
    			sDate, eDate, null, null);
    	assertTrue(revisionList.size() > 0);
    
    	try {
    		Date startDate = paramDateFormat.parse(sDate);
    		Date endDate = paramDateFormat.parse(eDate);    	
    	  	
    		
			Iterator revisionIt = revisionList.iterator();
			while(revisionIt.hasNext()) {
				Revision revision = (Revision)revisionIt.next();
				assertTrue(revision.getVersionDate().after(startDate) 
						&& revision.getVersionDate().before(endDate));
				logger.info(revision.toString());
			}
    	
    	} catch(ParseException pe) {
    		logger.error("Parse Exception:" + pe.getMessage());
    	}
    	   	
    	
    }
    
    @Test
    public void testGetBranchRevisionHistory() {
    	logger.info("testGetBranchRevisionHistory()");
    	String sDate = "2013-09-12";
    	String eDate = "2013-09-14";
    	
    	List<Revision> revisionList = serviceClient.getRevisionHistoryService( 
    			sDate, eDate,"branch", "3.6");
    	assertTrue(revisionList.size() > 0);
    
    	try {
    		Date startDate = paramDateFormat.parse(sDate);
    		Date endDate = paramDateFormat.parse(eDate);    	
    	  	
    		
			Iterator revisionIt = revisionList.iterator();
			while(revisionIt.hasNext()) {
				Revision revision = (Revision)revisionIt.next();
				assertTrue(revision.getVersionDate().after(startDate) 
						&& revision.getVersionDate().before(endDate));
				logger.info(revision.toString());
			}
    	
    	} catch(ParseException pe) {
    		logger.error("Parse Exception:" + pe.getMessage());
    	}
    	   	
    	
    }              
    
    @Test
    public void testGetConfigurationItem() {
    	logger.info("testGetConfigurationItem()");    	
    	String item = "wp-content/index.php";
    	String itemURI = "/trunk/" + item;
    	
    	ConfigurationItem configurationItem = serviceClient.getConfigurationItemService(item, 
    			null, null);
    	logger.info(configurationItem.toString());
    	    	    	
    	assertEquals(item, configurationItem.getItemName());
    	assertEquals(itemURI, configurationItem.getItemURI());
    }
    
    @Test
    public void testGetBranchConfigurationItem() {
    	logger.info("testGetBranchConfigurationItem()");    	
    	String item = "wp-content/themes/index.php";
    	String branchType = "branch";
    	String branchName = "3.7";
    	String itemURI = "/branches/" + branchName + "/" + item;
    	
    	ConfigurationItem configurationItem = serviceClient.getConfigurationItemService(item, 
    			branchType, branchName);
    	logger.info(configurationItem.toString());
    	    	
    	assertEquals(item, configurationItem.getItemName());
    	assertEquals(itemURI, configurationItem.getItemURI());
    }
    
    
    
    @Test
    public void testGetConfigurationItemRevisionHistory() {
    	logger.info("testGetConfigurationItemRevisionHistory()");    	
    	String item = "wp-content/index.php";
    	String itemURI = "/trunk/" + item;
    	
    	List<Revision> revisionList = serviceClient.getConfigurationItemHistoryService(item,  
    			 null, null);    	
    	assertTrue(revisionList.size() > 0);
    	
    	Iterator revisionIt = revisionList.iterator();
    	while(revisionIt.hasNext()) {
    		Revision revision = (Revision)revisionIt.next();
    		logger.info(revision.toString());
    	}
    	   	
    }
    
    
    @Test
    public void testGetBranchConfigurationItemRevisionHistory() {
    	logger.info("testGetBranchConfigurationItemRevisionHistory()");    	
    	String item = "wp-content/themes/index.php";
    	String branchType = "branch";
    	String branchName = "3.7";
    	String itemURI = "/branches/" + branchName + "/" + item;
    	List<Revision> revisionList = serviceClient.getConfigurationItemHistoryService(item,  
    			 branchType, branchName);
    	assertTrue(revisionList.size() > 0);
    	
    	Iterator revisionIt = revisionList.iterator();
    	while(revisionIt.hasNext()) {
    		Revision revision = (Revision)revisionIt.next();
    		logger.info(revision.toString());
    	}
    	
    }    
    
    
    @Test
    public void testGetRevisionByCommitMessage() {
    	logger.info("testGetRevisionByCommitMessage()");    	
    	String searchMessage = "#22975";
    	
    	List<Revision> revisionList = serviceClient.getRevisionByCommitMessageService(searchMessage, 
    			 null, null);    	
    	assertTrue(revisionList.size() > 0);
    	
    	Iterator revisionIt = revisionList.iterator();
    	while(revisionIt.hasNext()) {
    		Revision revision = (Revision)revisionIt.next();
    		logger.info(revision.toString());
    		assertTrue(revision.getCommitMessage().contains(searchMessage));
    	}
    	    	
    }
	
    
    @Test
    public void testGetBranchRevisionByCommitMessage() {
    	logger.info("testGetBranchRevisionByCommitMessage()");    	
    	String searchMessage = "#25682";
    	String branchType = "branch";
    	String branchName = "3.7";
    	List<Revision> revisionList = serviceClient.getRevisionByCommitMessageService(searchMessage, 
    			branchType, branchName);    	
    	assertTrue(revisionList.size() > 0);
    	
    	Iterator revisionIt = revisionList.iterator();
    	while(revisionIt.hasNext()) {
    		Revision revision = (Revision)revisionIt.next();
    		logger.info(revision.toString());
    		assertTrue(revision.getCommitMessage().contains(searchMessage));
    	}
    	    	
    }
    

}
