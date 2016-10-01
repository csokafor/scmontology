package uk.ac.liv.scm.trac.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class TracServiceProperties {
	
	private static final Logger logger = Logger.getLogger(TracServiceProperties.class);
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
					+ File.separator + "tracserviceprovider.properties");
			logger.info("properties file: " + file.getAbsolutePath());
			
			if(file.exists()){
				prop = new Properties();
				prop.load(new FileInputStream(file));
			}else{
				logger.warn("Error loading service provider properties file");				
			}
			
		} catch (Exception ex) {			
			logger.error("Could not load service provider configuration: " + ex);
			throw new ExceptionInInitializerError(ex);
		}
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
