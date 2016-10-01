package uk.ac.liv.scm.server;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.AccessController;

import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.server.ResourceConfig;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;


import uk.ac.liv.scm.exception.AuthenticationExceptionMapper;
import uk.ac.liv.scm.ontology.SCMOntology;
import uk.ac.liv.scm.ontology.SCMStandardOntology;
import uk.ac.liv.scm.serviceprovider.AbstractServiceProvider;
import uk.ac.liv.scm.util.Constants;
import uk.ac.liv.scm.util.ServiceProperties;


public class SCMHttpServer {

	private static final Logger logger = Logger.getLogger(SCMHttpServer.class);
    private static HttpServer webServer;
    //public static final URI BASE_URI = getBaseURI();
    public static URI BASE_URI = null;
        	        
    private static URI getBaseURI() {
    	String uri = ServiceProperties.getProperty(Constants.SERVICE_URI);
    	String port = ServiceProperties.getProperty(Constants.PORT);
    	    	
        return UriBuilder.fromUri(uri).port(new Integer(port)).build();
    }

    
    protected static void startServer() {
    	BASE_URI = getBaseURI();
        ResourceConfig rc = new ResourceConfig();
        rc.registerClasses(AbstractServiceProvider.class, SecurityFilter.class, AuthenticationExceptionMapper.class);

        try {
        	//load SCM ontology
        	SCMStandardOntology.getInstance();
        	SCMOntology.getInstance();
        	
            webServer = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc, true);
            // start Grizzly embedded server //
            logger.info("SCM Http server started. URL " + BASE_URI + "\nHit CTRL + C to stop it...");
            webServer.start();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    protected static void stopServer() {
        webServer.stop();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) throws InterruptedException, IOException {    	    	
        startServer();
        System.in.read();
    }
}

