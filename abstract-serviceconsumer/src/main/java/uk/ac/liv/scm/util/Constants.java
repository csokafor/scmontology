package uk.ac.liv.scm.util;

import uk.ac.liv.scm.server.SCMHttpServer;

public class Constants {
	
	public static final String CONTENT = "SCM Abstract ServiceConsumer\n";    
    public static final String SCM_HOME_ENV_NAME = "SCM_HOME";
    
    public static final String PORT = "port";
    public static final String SERVICE_URI = "serviceURI";
    
    public static String CONSUMER_TITLE = "Abstract Service Consumer";
    //public static String PROVIDER_DESCRIPTION = "Abstract Service Provider reference implementation";
    public static String CONSUMER_URI = SCMHttpServer.BASE_URI.toString();
    public static String CONSUMER_VERSION = "1.0";
    public static String CONSUMER_PUBLISHER = "Chinedu Okafor";
    
    public static String PROVIDER_TITLE = "Abstract Service Provider";
    public static String PROVIDER_DESCRIPTION = "Abstract Service Provider reference implementation";
    public static String PROVIDER_URI = SCMHttpServer.BASE_URI.toString();
    public static String PROVIDER_VERSION = "1.0";
    public static String PROVIDER_PUBLISHER = "Chinedu Okafor";
    
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
}
