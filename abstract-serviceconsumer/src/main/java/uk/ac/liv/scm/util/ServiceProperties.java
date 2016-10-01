package uk.ac.liv.scm.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.Properties;

import org.apache.log4j.Logger;

import uk.ac.liv.scm.serviceconsumer.ServiceProvider;

public class ServiceProperties {
	
	private static final Logger logger = Logger.getLogger(ServiceProperties.class);
	private static Properties prop = null;
	
	public static String getProperty(String key) {
		if(prop == null) {
			loadPropertiesFile();
		}
		
		String propertyValue = null;
		propertyValue = prop.getProperty(key);
		return propertyValue;
	}

	private static void loadPropertiesFile() {
	
		try {
			String env_name = System.getenv(Constants.SCM_HOME_ENV_NAME);
						
			File file = new File(env_name + File.separator + "conf" 
					+ File.separator + "serviceconsumer.properties");
			logger.info("properties file: " + file.getAbsolutePath());
			
			if(file.exists()){
				prop = new Properties();
				prop.load(new FileInputStream(file));
			}else{
				logger.warn("Error loading service consumer properties file");				
			}
			
		} catch (Exception ex) {			
			logger.error("Could not load service consumer configuration: " + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	public static LinkedList<ServiceProvider> getServiceProviders() {
		LinkedList<ServiceProvider> serviceProviders = new LinkedList<ServiceProvider>();
		
		try {
			String url = "";
			String username = "";
			String password = "";
			if(getProperty("serviceprovider1.URL") != null) {
				url = getProperty("serviceprovider1.URL");
				username = getProperty("serviceprovider1.username");
				password = getProperty("serviceprovider1.password");
				ServiceProvider serviceProvider = new ServiceProvider(url,username,password);
				serviceProviders.add(serviceProvider);
			}
			
			if(getProperty("serviceprovider2.URL") != null) {
				url = getProperty("serviceprovider2.URL");
				username = getProperty("serviceprovider2.username");
				password = getProperty("serviceprovider2.password");
				ServiceProvider serviceProvider = new ServiceProvider(url,username,password);
				serviceProviders.add(serviceProvider);
			}
			
			if(getProperty("serviceprovider3.URL") != null) {
				url = getProperty("serviceprovider3.URL");
				username = getProperty("serviceprovider3.username");
				password = getProperty("serviceprovider3.password");
				ServiceProvider serviceProvider = new ServiceProvider(url,username,password);
				serviceProviders.add(serviceProvider);
			}
			
		} catch (Exception ex) {			
			logger.error("Could not load service providers: " + ex);
			throw new ExceptionInInitializerError(ex);
		}
		
		return serviceProviders;
	}
	
	public static FileInputStream getSCMStandardOWLFile() {
		FileInputStream fileInputStream = null;
		try {
			String env_name = System.getenv(Constants.SCM_HOME_ENV_NAME);
						
			File file = new File(env_name + File.separator + "owl" 
					+ File.separator + "scmstandard.owl");
			logger.info("owl file: " + file.getAbsolutePath());
			
			if(file.exists()) {				
				fileInputStream = new FileInputStream(file);
			}else{
				logger.warn("Error loading SCM standard OWL file");				
			}
			
		} catch (Exception ex) {			
			logger.error("Could not load SCM standard OWL file: " + ex);
			throw new ExceptionInInitializerError(ex);
		}
		
		return fileInputStream;
	}
	
	public static FileInputStream getSCMOWLFile() {
		FileInputStream fileInputStream = null;
		try {
			String env_name = System.getenv(Constants.SCM_HOME_ENV_NAME);
						
			File file = new File(env_name + File.separator + "owl" 
					+ File.separator + "scmowl.owl");
			logger.info("owl file: " + file.getAbsolutePath());
			
			if(file.exists()) {				
				fileInputStream = new FileInputStream(file);
			}else{
				logger.warn("Error loading SCM OWL file");				
			}
			
		} catch (Exception ex) {			
			logger.error("Could not load SCM OWL file: " + ex);
			throw new ExceptionInInitializerError(ex);
		}
		
		return fileInputStream;
	}
	
}
