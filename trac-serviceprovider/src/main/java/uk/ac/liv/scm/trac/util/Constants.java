package uk.ac.liv.scm.trac.util;

import uk.ac.liv.scm.trac.server.TracHttpServer;

public class Constants {
	
	public static final String CONTENT = "Trac Service Provider\n";    
    public static final String SCM_HOME_ENV_NAME = "SCM_HOME";
    
    public static final String PORT = "port";
    public static final String SERVICE_URI = "serviceURI";
    
    public static String PROVIDER_TITLE = "Trac Service Provider";
    public static String PROVIDER_DESCRIPTION = "Trac Service Provider reference implementation";
    public static String PROVIDER_URI = TracHttpServer.BASE_URI.toString();
    public static String PROVIDER_VERSION = "1.0";
    public static String PROVIDER_PUBLISHER = "Chinedu Okafor";
    
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    
    public static final String TRAC_USERNAME = "trac_username";
    public static final String TRAC_PASSWORD = "trac_password";
    public static final String TRAC_URL = "trac_url";
}
