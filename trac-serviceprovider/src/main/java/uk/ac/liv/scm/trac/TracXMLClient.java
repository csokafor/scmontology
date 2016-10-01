package uk.ac.liv.scm.trac;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import redstone.xmlrpc.XmlRpcArray;
import redstone.xmlrpc.XmlRpcClient;
import redstone.xmlrpc.XmlRpcFault;
import redstone.xmlrpc.XmlRpcProxy;
import redstone.xmlrpc.XmlRpcStruct;
import uk.ac.liv.scm.ontology.entity.Issue;
import uk.ac.liv.scm.ontology.entity.Person;
import uk.ac.liv.scm.trac.util.Constants;
import uk.ac.liv.scm.trac.util.TracServiceProperties;

public class TracXMLClient {
	
	private static final Logger logger = Logger.getLogger(TracXMLClient.class);
	private static TracXMLClient tracClient;
	private XmlRpcClient rpcClient;
	private ticket tracTicket;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private TracXMLClient() {
		try {
			URL url = new URL(TracServiceProperties.getProperty(Constants.TRAC_URL));
			String username = TracServiceProperties.getProperty(Constants.TRAC_USERNAME);
			String password = TracServiceProperties.getProperty(Constants.TRAC_PASSWORD);
			String auth = javax.xml.bind.DatatypeConverter.printBase64Binary(new String(username + ":" + password).getBytes());
			
			rpcClient = new XmlRpcClient(url, false);
			rpcClient.setRequestProperty("Authorization", "Basic " + auth);
			tracTicket = (ticket) (XmlRpcProxy.createProxy(url, new Class[] {ticket.class}, false));
									
		} catch(MalformedURLException me) {
			logger.error("trac url exception: " + me.getMessage(), me);
		}		
		
	}
	
	public static TracXMLClient getInstance() {
		if(tracClient == null) {
			tracClient = new TracXMLClient();
		}
		
		return tracClient;
	}
	
	public Issue getIssue(String issueId) throws XmlRpcFault {
		Issue issue = new Issue();
		/*
		try {
			Object systemVersion = rpcClient.invoke("system.getAPIVersion", new Object[] {});
			logger.info("systemVersion: " + systemVersion.toString());
		} catch(XmlRpcFault e) {
			logger.error("XmlRpcFault: " + e.getMessage(), e);
		}
		*/
		
		XmlRpcArray rpcArray = tracTicket.get(new Integer(issueId)); 
		logger.info("issue vector:" + rpcArray.toString());
		issue = this.getIssue(rpcArray);
								
		return issue;
	}
	
	public Issue createIssue(Map<String,String> issueMap) {
		Issue newIssue = new Issue();
		Hashtable issueTable = new Hashtable();
		issueTable.put("version", issueMap.get("buildVersion"));
		issueTable.put("status", issueMap.get("issueStatus"));
		issueTable.put("type", issueMap.get("issueType"));
		issueTable.put("priority", issueMap.get("issueSeverity"));
		
		Integer issueId = tracTicket.create(issueMap.get("issueTitle"), 
				issueMap.get("issueDescription"), issueTable);
		
		XmlRpcArray rpcArray = tracTicket.get(new Integer(issueId)); 
		logger.info("issue vector:" + rpcArray.toString());
		newIssue = this.getIssue(rpcArray);
		
		return newIssue;
	}
	
	public Issue updateIssue(Map<String,String> issueMap) {
		Issue newIssue = new Issue();
		Hashtable issueTable = new Hashtable();
		issueTable.put("version", issueMap.get("buildVersion"));
		issueTable.put("status", issueMap.get("issueStatus"));
		issueTable.put("type", issueMap.get("issueType"));
		issueTable.put("priority", issueMap.get("issueSeverity"));
		issueTable.put("summary", issueMap.get("issueTitle"));
		issueTable.put("description", issueMap.get("issueDescription"));
		
		XmlRpcArray rpcArray = tracTicket.update(new Integer(issueMap.get("issueId")), 
				"Trac SCM service provider ticket update", issueTable);
				
		logger.info("issue vector:" + rpcArray.toString());
		newIssue = this.getIssue(rpcArray);
		
		return newIssue;
	}
	
	public Integer deleteIssue(String issueId) throws XmlRpcFault {
		logger.info("deleteIssue issueId: " + issueId);
		Integer id = tracTicket.delete(new Integer(issueId));
		logger.info("issue id:" + id);
		
		return id;
	}
	
	public List<Issue> findIssueByParameter(String searchParameter) throws XmlRpcFault {
		List<Issue> issueList = new ArrayList<Issue>();
				
		XmlRpcArray searchArray = tracTicket.query(searchParameter); 
		logger.info("search vector:" + searchArray.toString());
		Iterator rpcIt = searchArray.iterator();
		while(rpcIt.hasNext()) {
			Integer issueId = (Integer)rpcIt.next();
			XmlRpcArray rpcArray = tracTicket.get(new Integer(issueId));
			logger.info("issue vector:" + rpcArray.toString());
			Issue issue = this.getIssue(rpcArray);
			issueList.add(issue);
		}
										
		return issueList;
	}
	
	private Issue getIssue(XmlRpcArray rpcArray) {
		Issue issue = new Issue();
		 		
		Integer tracId = rpcArray.getInteger(0);		
		XmlRpcStruct issueStruct = rpcArray.getStruct(3);							
		issue.setBuildVersion(issueStruct.getString("version"));
		issue.setIssueTitle(issueStruct.getString("summary"));
		issue.setIssueId(tracId.toString());
		issue.setIssueStatus(issueStruct.getString("status"));
		issue.setIssueType(issueStruct.getString("type"));
		issue.setIssueSeverity(issueStruct.getString("priority"));
		issue.setIssueDescription(issueStruct.getString("description"));
		issue.setIssueDate(issueStruct.getDate("time"));
		
		String assignedTo = issueStruct.getString("owner");
		if(assignedTo == null || assignedTo.equalsIgnoreCase("")) {
			issue.setApproved(false);
		} else {
			issue.setApproved(true);
		}
		Person owner = new Person();
		owner.setPersonId(assignedTo);
		owner.setPersonName(assignedTo);
		issue.setIsAssignedTo(owner);
		
		return issue;
		
	}
	
}
