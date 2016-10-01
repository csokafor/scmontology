package uk.ac.liv.scm.svn;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import uk.ac.liv.scm.exception.PathNotFoundException;
import uk.ac.liv.scm.exception.RevisionNotFoundException;
import uk.ac.liv.scm.exception.SVNServiceException;
import uk.ac.liv.scm.ontology.entity.Branch;
import uk.ac.liv.scm.ontology.entity.ConfigurationItem;
import uk.ac.liv.scm.ontology.entity.Item;
import uk.ac.liv.scm.ontology.entity.Person;
import uk.ac.liv.scm.ontology.entity.Repository;
import uk.ac.liv.scm.ontology.entity.Revision;
import uk.ac.liv.scm.ontology.entity.Version;
import uk.ac.liv.scm.svn.util.Constants;
import uk.ac.liv.scm.svn.util.SVNServiceProperties;

public class SVNClient {
	
	private static final Logger logger = Logger.getLogger(SVNClient.class);
	private static SVNClient svnClient;
	private SVNRepository svnRepository = null;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static String BRANCHES = "branches";
	private static String TAGS = "tags";
	String url = "";
		
	private SVNClient() {
		url = SVNServiceProperties.getProperty(Constants.SVN_URL);
		String username = SVNServiceProperties.getProperty(Constants.SVN_USERNAME);
		String password = SVNServiceProperties.getProperty(Constants.SVN_PASSWORD);
		try {
			DAVRepositoryFactory.setup();
            svnRepository = DAVRepositoryFactory.create(SVNURL.parseURIEncoded(url));
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
            svnRepository.setAuthenticationManager(authManager);
            
            if(!this.isValidRepositoryPath()) {
            	throw new RuntimeException();
            }
            
        } catch (SVNException svne) {           
            logger.error("error while creating an SVNRepository for location '" + url + "': " + svne.getMessage());         
            throw new RuntimeException();
        }
	}
	
	public static SVNClient getInstance() {
		if(svnClient == null) {
			svnClient = new SVNClient();
		}
		
		return svnClient;
	}
	
	private boolean isValidRepositoryPath() {
		boolean validPath = true;
		try {
			SVNNodeKind nodeKind = svnRepository.checkPath("", -1);
			
	        if (nodeKind == SVNNodeKind.NONE) {
	            logger.error("There is no entry at '" + url + "'.");
	            validPath = false;
	        } else if (nodeKind == SVNNodeKind.FILE) {
	            logger.error("The entry at '" + url + "' is a file while a directory was expected.");
	            validPath = false;
	        }
	        
		} catch (SVNException svne) {           
	        logger.error("The " + url + " is not repository folder: " + svne.getMessage());            
	    }
		return validPath;
	}
	
	public Version getRevisionDetails(String revision, String branchType, 
			String branchName) throws SVNServiceException, PathNotFoundException, RevisionNotFoundException {
		
		Version version = new Version();
		String path = "";
		if(branchName != null && (!branchName.equals(""))) {
			if(branchType.equalsIgnoreCase(Branch.BRANCH)) {
				this.getRepositoryBranch(branchName, true);
				path = "/" + BRANCHES + "/" + branchName;
			} else {
				this.getRepositoryBranch(branchName, false);
				path = "/" + TAGS + "/" + branchName;
			}
		} 
		
		try {
			SVNLogEntry svnLogEntry = this.getRevisionDetails(revision, path);
			if(svnLogEntry == null) {
				throw new RevisionNotFoundException();
			}
			
			Person author = new Person();
			author.setPersonId(svnLogEntry.getAuthor());
			author.setPersonName(svnLogEntry.getAuthor());
						
			version.setVersionId(revision);
			if(svnLogEntry.getAuthor() != null) {
				version.setAuthor(author);
			}
			version.setCommitMessage(svnLogEntry.getMessage());
			version.setVersionDate(svnLogEntry.getDate());
		
			Collection<ConfigurationItem> modifiedItems = new ArrayList<ConfigurationItem>();
			for (String changedPath : svnLogEntry.getChangedPaths().keySet() ) {
				logger.info("changedPath: " + changedPath);
				SVNLogEntryPath logEntryPath = (SVNLogEntryPath)svnLogEntry.getChangedPaths().get(changedPath);
				
				ConfigurationItem configItem = new ConfigurationItem();
				configItem.setItemName(logEntryPath.getPath());
				configItem.setItemURI(logEntryPath.getPath());
				SVNNodeKind nodeKind = logEntryPath.getKind();
				if(nodeKind == SVNNodeKind.DIR) {
					configItem.setMimeType(ConfigurationItem.DIRECTORY);
				} else {
					configItem.setMimeType(ConfigurationItem.TEXT);
				}
				modifiedItems.add(configItem);
			}
			version.setModifiedItems(modifiedItems);			
		
		} catch(SVNException se) {
			logger.error("getRevisionDetails exception: " + se.getMessage(),  se);
			throw new SVNServiceException();
		}
		
		return version;
		
	}
	
	public List<Version> getRevisionHistory(String startDate, String endDate, String branchType, 
			String branchName) throws SVNServiceException, PathNotFoundException, RevisionNotFoundException {
		
		List<Version> versionList = new ArrayList<Version>();
		
		String path = "";
		if(branchName != null && (!branchName.equals(""))) {
			if(branchType.equalsIgnoreCase(Branch.BRANCH)) {
				this.getRepositoryBranch(branchName, true);
				path = "/" + BRANCHES + "/" + branchName;
			} else {
				this.getRepositoryBranch(branchName, false);
				path = "/" + TAGS + "/" + branchName;
			}
		} 
		
		try {
			List<SVNLogEntry> logEntries = this.getSVNRevisionHistory(startDate, endDate, path);
			if(logEntries == null || logEntries.isEmpty()) {
				throw new RevisionNotFoundException();
			}
			Iterator iterator = logEntries.iterator();
			while(iterator.hasNext()) {
				SVNLogEntry logEntry = (SVNLogEntry)iterator.next();
				Version version = new Version();
				Person author = new Person();
				author.setPersonId(logEntry.getAuthor());
				author.setPersonName(logEntry.getAuthor());
							
				version.setVersionId(new Long(logEntry.getRevision()).toString());
				if(logEntry.getAuthor() != null) {
					version.setAuthor(author);
					//logger.info("version author: " + version.getAuthor().toString());
				}
				version.setCommitMessage(logEntry.getMessage());
				version.setVersionDate(logEntry.getDate());
				versionList.add(version);
				
			}
		
		} catch(SVNException se) {
			logger.error("getRevisionHistory exception: " + se.getMessage(),  se);
			throw new SVNServiceException();
		}
		
		return versionList;
		
	}
	
	public ConfigurationItem getConfigurationItem(String itemURI, 
			String branchType, String branchName) throws PathNotFoundException {
		
		ConfigurationItem item = new ConfigurationItem();
		String path = "/trunk"  + "/" + itemURI;
		if(branchName != null && (!branchName.equals(""))) {
			if(branchType.equalsIgnoreCase(Branch.BRANCH)) {
				this.getRepositoryBranch(branchName, true);
				path = "/" + BRANCHES + "/" + branchName + "/" + itemURI;
			} else {
				this.getRepositoryBranch(branchName, false);
				path = "/" + TAGS + "/" + branchName + "/" + itemURI;
			}
		}
		
		item = this.getRepositoryItem(itemURI, path);
		
		return item;
	}
	
	public List<Version> getConfigurationItemHistory(String itemURI, String branchType, 
			String branchName) throws SVNServiceException, PathNotFoundException, 
				RevisionNotFoundException {
		
		List<Version> versionList = new ArrayList<Version>();
		
		String path = "/trunk"  + "/" + itemURI;
		if(branchName != null && (!branchName.equals(""))) {
			if(branchType.equalsIgnoreCase(Branch.BRANCH)) {
				this.getRepositoryBranch(branchName, true);
				path = "/" + BRANCHES + "/" + branchName + "/" + itemURI;
			} else {
				this.getRepositoryBranch(branchName, false);
				path = "/" + TAGS + "/" + branchName + "/" + itemURI;
			}
		}
		
		try {
			List<SVNLogEntry> logEntries = this.getSVNRevisionHistory(null, null, path);
			if(logEntries == null || logEntries.isEmpty()) {
				throw new RevisionNotFoundException();
			}
			Iterator iterator = logEntries.iterator();
			while(iterator.hasNext()) {
				SVNLogEntry logEntry = (SVNLogEntry)iterator.next();
				Version version = new Version();
				Person author = new Person();
				author.setPersonId(logEntry.getAuthor());
				author.setPersonName(logEntry.getAuthor());
							
				version.setVersionId(new Long(logEntry.getRevision()).toString());
				if(logEntry.getAuthor() != null) {
					version.setAuthor(author);
					//logger.info("version author: " + version.getAuthor().toString());
				}
				version.setCommitMessage(logEntry.getMessage());
				version.setVersionDate(logEntry.getDate());
				versionList.add(version);				
			}
		
		} catch(SVNException se) {
			logger.error("getConfigurationItemHistory exception: " + se.getMessage(),  se);
			throw new SVNServiceException();
		}
		
		return versionList;
		
	}
	
	public List<Version> getRevisionByMessage(String searchParam, String branchType, 
			String branchName) throws SVNServiceException, PathNotFoundException, 
				RevisionNotFoundException {
		
		List<Version> versionList = new ArrayList<Version>();
		String path = "/trunk";
		if(branchName != null && (!branchName.equals(""))) {
			if(branchType.equalsIgnoreCase(Branch.BRANCH)) {
				this.getRepositoryBranch(branchName, true);
				path = "/" + BRANCHES + "/" + branchName;
			} else {
				this.getRepositoryBranch(branchName, false);
				path = "/" + TAGS + "/" + branchName;
			}
		}
		
		try {
			List<SVNLogEntry> logEntries = this.getSVNRevisionHistory(null, null, path);
			if(logEntries == null || logEntries.isEmpty()) {
				throw new RevisionNotFoundException();
			}
			Iterator iterator = logEntries.iterator();
			while(iterator.hasNext()) {
				SVNLogEntry logEntry = (SVNLogEntry)iterator.next();
				if(logEntry.getMessage() != null && logEntry.getMessage().contains(searchParam)) {
					Version version = new Version();
					Person author = new Person();
					author.setPersonId(logEntry.getAuthor());
					author.setPersonName(logEntry.getAuthor());
								
					version.setVersionId(new Long(logEntry.getRevision()).toString());
					if(logEntry.getAuthor() != null) {
						version.setAuthor(author);
						//logger.info("version author: " + version.getAuthor().toString());
					}
					logger.info("logEntry.getMessage(): " + logEntry.getMessage());
					version.setCommitMessage(logEntry.getMessage());
					version.setVersionDate(logEntry.getDate());
					versionList.add(version);			
				}
			}
		
		} catch(SVNException se) {
			logger.error("getConfigurationItemHistory exception: " + se.getMessage(),  se);
			throw new SVNServiceException();
		}
		logger.info("versionList.size(): " + versionList.size());
		return versionList;
		
	}
	
	public Repository getRepositoryDetails(String branchType, String branchName)
			 throws PathNotFoundException, SVNServiceException {
		Repository repository = new Repository();
		try {
			if(branchName == null || branchName.equals("")) {
				repository.setRepositoryName(svnRepository.getRepositoryUUID(true));
				repository.setRepositoryURL(svnRepository.getRepositoryRoot(true).toString());
				repository.setRepositoryDescription("Description");
								
				List<String> branchList  = this.getRepositoryBranches(true);
				Iterator branchIt = branchList.iterator();
				while(branchIt.hasNext()) {
					String repoBranch = (String)branchIt.next();
					Branch branch = new Branch();
					branch.setBranchName(repoBranch);
					branch.setBranchType(Branch.BRANCH);
					repository.addBranches(branch);
				}
				
				List<String> tagList  = this.getRepositoryBranches(false);
				Iterator tagIt = tagList.iterator();
				while(tagIt.hasNext()) {
					String repoBranch = (String)tagIt.next();
					Branch branch = new Branch();
					branch.setBranchName(repoBranch);
					branch.setBranchType(Branch.TAG);
					repository.addBranches(branch);
				}
			
			} else {
				String path = "";
				SVNDirEntry repositoryEntry = null;
				if(branchType.equalsIgnoreCase(Branch.BRANCH)) {
					repositoryEntry = this.getRepositoryBranch(branchName, true);
				} else if(branchType.equalsIgnoreCase(Branch.TAG)) {
					repositoryEntry = this.getRepositoryBranch(branchName, false);
				}
								
				repository.setRepositoryName(svnRepository.getRepositoryUUID(true));
				repository.setRepositoryDescription(branchName);
				repository.setRepositoryURL(repositoryEntry.getURL().toString());
				
				long lastRevision = -1;
				Person author = new Person();
				author.setPersonId(repositoryEntry.getAuthor());
				author.setPersonName(repositoryEntry.getAuthor());
				lastRevision = repositoryEntry.getRevision();
				Version version = new Version();
				version.setVersionId(new Long(lastRevision).toString());
				version.setAuthor(author);
				version.setVersionDate(repositoryEntry.getDate());
				repository.setLastVersion(version);
				
			}
			
		} catch (SVNException svne) {           
	        logger.error("Error getting repository details: " + svne.getMessage());           
	        throw new SVNServiceException();
	    }
		
		return repository;
	}
	
	private ConfigurationItem getRepositoryItem(String itemURI, String path) throws PathNotFoundException {
		ConfigurationItem configItem = new ConfigurationItem();
		try {
			SVNProperties fileProperties = new SVNProperties();
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
			SVNNodeKind nodeKind = svnRepository.checkPath(path, -1);
            
            if (nodeKind == SVNNodeKind.NONE) {
                logger.warn("There is no entry at '" + path + "'.");
                throw new PathNotFoundException();
            } else if (nodeKind == SVNNodeKind.FILE) {
                logger.info("The entry at '" + path + "' is a file.");
                
                svnRepository.getFile(path, -1, fileProperties, baos);
                String mimeType = fileProperties.getStringValue(SVNProperty.MIME_TYPE);                
                boolean isTextType = SVNProperty.isTextMimeType(mimeType);

                Iterator iterator = fileProperties.nameSet().iterator();               
                while (iterator.hasNext()) {
                    String propertyName = (String) iterator.next();
                    String propertyValue = fileProperties.getStringValue(propertyName);
                    logger.info("File property: " + propertyName + "="
                            + propertyValue);
                }
                
                configItem.setItemName(itemURI);
            	configItem.setItemURI(path);
            	            	
            	Person author = new Person();
				author.setPersonId(fileProperties.getStringValue("svn:entry:last-author"));
				author.setPersonName(fileProperties.getStringValue("svn:entry:last-author"));
				
				Version version = new Version();
				version.setVersionId(fileProperties.getStringValue("svn:entry:revision"));
				version.setAuthor(author);
				version.setCommitMessage("");
				String sDate = fileProperties.getStringValue("svn:entry:committed-date");
				try {
					version.setVersionDate(dateFormat.parse(sDate));
				} catch(ParseException pe) {
					logger.warn("Error parsing date: " + sDate);
				}
				configItem.setHasVersion(version);
				
                if (isTextType) {
                    logger.info("getting file contents:");
                    
                    try {
                    	byte[] fileContent = baos.toByteArray();
                    	configItem.setItemContent(fileContent);
                    	configItem.setMimeType(Item.TEXT);
                        //baos.writeTo(System.out);
                    } catch (Exception ioe) {
                        logger.error("File contents error: " + ioe.getLocalizedMessage() , ioe);
                    }
                } else {
                    logger.warn("File contents can not be displayed it is not a text file.");
                    configItem.setMimeType(Item.BINARY);
                }
                                				
            } else {
            	logger.info("getRepositoryItem: " + path);
            	Collection entries = new ArrayList();
            	SVNDirEntry dirEntry = svnRepository.getDir(path, -1, true, entries);
            	logger.info("dirEntry: " + dirEntry.toString());
            	configItem.setItemName(itemURI);
            	configItem.setItemURI(path);
            	configItem.setMimeType(Item.DIRECTORY);
            	Person author = new Person();
				author.setPersonId(dirEntry.getAuthor());
				author.setPersonName(dirEntry.getAuthor());
				
				Version version = new Version();
				version.setVersionId(new Long(dirEntry.getRevision()).toString());
				version.setAuthor(author);
				version.setCommitMessage(dirEntry.getCommitMessage());
				version.setVersionDate(dirEntry.getDate());
				configItem.setHasVersion(version);
            }
            
			
		} catch (SVNException svne) {           
	        logger.error("Error getting repository path: " + svne.getMessage());            
	    }
		
		return configItem;
	}
	
	private SVNDirEntry getRepositoryBranch(String branchName, boolean isBranch) 
			throws PathNotFoundException {
		SVNDirEntry dirEntry = null;		
		String path = "";
		if(isBranch) {
			path = BRANCHES;
		} else {
			path = TAGS;
		}
		
		try {
			Collection entries = svnRepository.getDir(path, -1, null,(Collection) null);
	        Iterator iterator = entries.iterator();
	        while (iterator.hasNext()) {
	            SVNDirEntry entry = (SVNDirEntry) iterator.next();
	            logger.info("/" + (path.equals("") ? "" : path + "/")
	                    + entry.getName() + " (author: '" + entry.getAuthor()
	                    + "'; revision: " + entry.getRevision() + "; date: " + entry.getDate() + ")");
	            if(entry.getName().equalsIgnoreCase(branchName)) {
	            	dirEntry = entry;	            	
	            }
	        }
	        
	        if(dirEntry == null) {
	        	throw new PathNotFoundException();
	        }
		} catch (SVNException svne) {           
	        logger.error("Error getting repository branches: " + svne.getMessage());            
	    }
		return dirEntry;
	}
		
	private List<String> getRepositoryBranches(boolean isBranch) {
		List<String> branchList = new ArrayList<String>();
		String path = "";
		if(isBranch) {
			path = BRANCHES;
		} else {
			path = TAGS;
		}
		
		try {
			Collection entries = svnRepository.getDir(path, -1, null,(Collection) null);
	        Iterator iterator = entries.iterator();
	        while (iterator.hasNext()) {
	            SVNDirEntry entry = (SVNDirEntry) iterator.next();
	            logger.info("/" + (path.equals("") ? "" : path + "/")
	                    + entry.getName() + " (author: '" + entry.getAuthor()
	                    + "'; revision: " + entry.getRevision() + "; date: " + entry.getDate() + ")");
	            /*
	             * Checking up if the entry is a directory.
	             */
	            if (entry.getKind() == SVNNodeKind.DIR) {
	            	branchList.add(entry.getName());
	            }
	        }
		} catch (SVNException svne) {           
	        logger.error("Error getting repository branches: " + svne.getMessage());            
	    }
		return branchList;
	}
	
	private SVNDirEntry getRevision(String revisionNo, String path) throws SVNException {
       
		SVNDirEntry dirEntry = null;
        Collection entries = svnRepository.getDir(path, -1, null,
                (Collection) null);
        Iterator iterator = entries.iterator();
        while (iterator.hasNext()) {
            SVNDirEntry entry = (SVNDirEntry) iterator.next();
            logger.info("/" + (path.equals("") ? "" : path + "/")
                    + entry.getName() + " (author: '" + entry.getAuthor()
                    + "'; revision: " + entry.getRevision() + "; date: " + entry.getDate() + ")");
            
            if(new Long(entry.getRevision()).toString().equalsIgnoreCase(revisionNo)) {
            	dirEntry = entry;
            	break;
            }
            /*
             * Checking up if the entry is a directory.
             */
            if (entry.getKind() == SVNNodeKind.DIR) {
            	dirEntry = getRevision(revisionNo, (path.equals("")) ? entry.getName()
                        : path + "/" + entry.getName());
            }
        }
        
        return dirEntry;
    }
	
	private SVNLogEntry getRevisionDetails(String revisionNo, String path) throws SVNException {
	       
		SVNLogEntry dirEntry = null;
		long startRevision = 0;
        long endRevision = -1;//HEAD (the latest) revision
        
		Collection logEntries = svnRepository.log(new String[] {path}, null,
                startRevision, endRevision, true, true);
		        
        Iterator iterator = logEntries.iterator();
        while (iterator.hasNext()) {
        	SVNLogEntry logEntry = (SVNLogEntry) iterator.next();
            logger.info("/" + (path.equals("") ? "" : path + "/")
                    + " (author: '" + logEntry.getAuthor() + "; message: " + logEntry.getMessage()
                    + "'; revision: " + logEntry.getRevision() + "; date: " + logEntry.getDate() + ")");
            
            if(new Long(logEntry.getRevision()).toString().equalsIgnoreCase(revisionNo)) {
            	dirEntry = logEntry;
            	break;
            }
                       
        }
        
        return dirEntry;
	}
        
    private List<SVNLogEntry> getSVNRevisionHistory(String startDate, String endDate, String path) throws SVNException {
       
    	List<SVNLogEntry> revisionList =  new ArrayList<SVNLogEntry>();    		
		long startRevision = 0;
        long endRevision = -1;//HEAD (the latest) revision
        Date sDate = new Date();
        Date eDate = new Date();
        
        if(startDate == null) {
        	Calendar cal = Calendar.getInstance();    		
    		cal.add(Calendar.YEAR, -1);
    		sDate = cal.getTime();
    		logger.info("startDate null:" + sDate.toString());
        } else {
        	try {
        		sDate = this.dateFormat.parse(startDate);
        		logger.info("startDate:" + sDate.toString());
        	} catch(Exception e) {
        		logger.error("Could not parse startDate: " + startDate);
        	}
        }
        if(endDate != null) {
        	try {
        		eDate = this.dateFormat.parse(endDate);
        		logger.info("eDate:" + eDate.toString());
        	} catch(Exception e) {
        		logger.error("Could not parse endDate: " + endDate);
        	}
        }
        logger.info("getSVNRevisionHistory path: " + path);
		Collection logEntries = svnRepository.log(new String[] {path}, null,
                startRevision, endRevision, true, true);
		        
        Iterator iterator = logEntries.iterator();
        while (iterator.hasNext()) {
        	SVNLogEntry logEntry = (SVNLogEntry) iterator.next();            
            
            if(logEntry.getDate().before(eDate) && logEntry.getDate().after(sDate)) {
            	logger.info("/" + (path.equals("") ? "" : path + "/")
                        + " (author: '" + logEntry.getAuthor()
                        + "'; revision: " + logEntry.getRevision() + "; date: " + logEntry.getDate() + ")");
            	
            	revisionList.add(logEntry);
            }
                       
        }
        logger.info("revisionList.size()" + revisionList.size());
        return revisionList;
    }

}
