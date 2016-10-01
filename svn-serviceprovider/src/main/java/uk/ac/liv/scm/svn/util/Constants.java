package uk.ac.liv.scm.svn.util;

import uk.ac.liv.scm.svn.server.SVNHttpServer;

public class Constants {
	
	public static final String CONTENT = "Subversion Service Provider\n";    
    public static final String SCM_HOME_ENV_NAME = "SCM_HOME";
    
    public static final String PORT = "port";
    public static final String SERVICE_URI = "serviceURI";
    
    public static String PROVIDER_TITLE = "Subversion Service Provider";
    public static String PROVIDER_DESCRIPTION = "Subversion Service Provider reference implementation";
    public static String PROVIDER_URI = SVNHttpServer.BASE_URI.toString();
    public static String PROVIDER_VERSION = "1.0";
    public static String PROVIDER_PUBLISHER = "Chinedu Okafor";
    
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    
    public static final String SVN_USERNAME = "svn_username";
    public static final String SVN_PASSWORD = "svn_password";
    public static final String SVN_URL = "svn_url";
}
