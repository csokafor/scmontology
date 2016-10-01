package uk.ac.liv.scm.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class OntologyUtil {
	
	private static final Logger logger = Logger.getLogger(OntologyUtil.class);
	
	public static Map<String,String> getInstanceProperties(OntModel ontModel, String classURI) {
		Map<String,String> propertyMap = new HashMap<String,String>();
		OntResource ontResource = ontModel.getOntResource(classURI);
		OntClass ontClass = ontResource.as(OntClass.class);      
    	
    	ExtendedIterator instances = ontClass.listInstances();
    	if(instances.hasNext()) {
    		Individual individual = (Individual) instances.next();
    		//logger.info("instance URI" + individual.getURI());
    		
    		StmtIterator stmtIt = individual.listProperties();
    		while(stmtIt.hasNext()) {
    			Statement stmt = stmtIt.next();
    			//logger.info("property : " + stmt.getPredicate().getLocalName());
    			
    			RDFNode rdfNode = stmt.getObject();
    			if(rdfNode.isResource()) {
                	Resource nodeResource = rdfNode.asResource();                   
                	if(!nodeResource.canAs(Restriction.class)) {  
                		                		
                		if(propertyMap.containsKey(stmt.getPredicate().getLocalName())) {
                			String currentValue = propertyMap.get(stmt.getPredicate().getLocalName());                			
                			currentValue = currentValue + "," + nodeResource.getURI();                			
                			propertyMap.put(stmt.getPredicate().getLocalName(), currentValue);                			
                		} else {                			
                			propertyMap.put(stmt.getPredicate().getLocalName(), nodeResource.getURI());
                		}                		  
                		//logger.info("rdfNode resource: " + nodeResource.getURI());
                	}
                 } else {                 
                	 Literal nodeLiteral = rdfNode.asLiteral();
                	 if(propertyMap.containsKey(stmt.getPredicate().getLocalName())) {
             			String currentValue = propertyMap.get(stmt.getPredicate().getLocalName());
             			currentValue.concat("," + nodeLiteral.getString());
             			propertyMap.put(stmt.getPredicate().getLocalName(), currentValue);                			
             		} else {
             			propertyMap.put(stmt.getPredicate().getLocalName(), nodeLiteral.getString());
             		}
                	 //logger.info("rdfNode literal: " + nodeLiteral.getString());
                 }
    		}
    	    		
    	} else {
    		logger.warn("No instances of " + classURI + " found.");
    	}
		
		return propertyMap;
	}
	
	public static Map<String,Map<String,String>> getAllInstanceProperties(OntModel ontModel, String classURI) {
		
		Map<String,Map<String,String>> instanceMap = new HashMap<String,Map<String,String>>();
		Map<String,String> propertyMap = new HashMap<String,String>();
		OntResource ontResource = ontModel.getOntResource(classURI);
		OntClass ontClass = ontResource.as(OntClass.class);      
    	
    	ExtendedIterator instances = ontClass.listInstances();
    	while(instances.hasNext()) {
    		Individual individual = (Individual) instances.next();    		
    		//logger.info("instance URI" + individual.getURI());
    		propertyMap = new HashMap<String,String>();
    		
    		StmtIterator stmtIt = individual.listProperties();
    		while(stmtIt.hasNext()) {
    			Statement stmt = stmtIt.next();
    			//logger.info("property : " + stmt.getPredicate().getLocalName());
    			
    			RDFNode rdfNode = stmt.getObject();
    			if(rdfNode.isResource()) {
                	Resource nodeResource = rdfNode.asResource();                   
                	if(!nodeResource.canAs(Restriction.class)) {  
                		                		
                		if(propertyMap.containsKey(stmt.getPredicate().getLocalName())) {
                			String currentValue = propertyMap.get(stmt.getPredicate().getLocalName());                			
                			currentValue = currentValue + "," + nodeResource.getURI();                			
                			propertyMap.put(stmt.getPredicate().getLocalName(), currentValue);                			
                		} else {                			
                			propertyMap.put(stmt.getPredicate().getLocalName(), nodeResource.getURI());
                		}                		  
                		//logger.info("rdfNode resource: " + nodeResource.getURI());
                	}
                 } else {                 
                	 Literal nodeLiteral = rdfNode.asLiteral();
                	 if(propertyMap.containsKey(stmt.getPredicate().getLocalName())) {
             			String currentValue = propertyMap.get(stmt.getPredicate().getLocalName());
             			currentValue.concat("," + nodeLiteral.getString());
             			propertyMap.put(stmt.getPredicate().getLocalName(), currentValue);                			
             		} else {
             			propertyMap.put(stmt.getPredicate().getLocalName(), nodeLiteral.getString());
             		}
                	 //logger.info("rdfNode literal: " + nodeLiteral.getString());
                 }
    		}
    		
    		instanceMap.put(individual.getURI(), propertyMap);    	    		
    	}
		
		return instanceMap;
	}
	
	public static Map<String,String> getResourceProperties(OntModel ontModel, String resourceURI) {
		Map<String,String> propertyMap = new HashMap<String,String>();
		OntResource ontResource = ontModel.getOntResource(resourceURI);
		//OntClass ontClass = ontResource.as(OntClass.class);      
		  	    	
		StmtIterator stmtIt = ontResource.listProperties();
		while(stmtIt.hasNext()) {
			Statement stmt = stmtIt.next();
			//logger.info("property : " + stmt.getPredicate().getLocalName());
			
			RDFNode rdfNode = stmt.getObject();
			if(rdfNode.isResource()) {
            	Resource nodeResource = rdfNode.asResource();                   
            	if(!nodeResource.canAs(Restriction.class)) {  
            		                		
            		if(propertyMap.containsKey(stmt.getPredicate().getLocalName())) {
            			String currentValue = propertyMap.get(stmt.getPredicate().getLocalName());                			
            			currentValue = currentValue + "," + nodeResource.getURI();                			
            			propertyMap.put(stmt.getPredicate().getLocalName(), currentValue);                			
            		} else {                			
            			propertyMap.put(stmt.getPredicate().getLocalName(), nodeResource.getURI());
            		}                		  
            		//logger.info("rdfNode resource: " + nodeResource.getURI());
            	}
             } else {                 
            	 Literal nodeLiteral = rdfNode.asLiteral();
            	 if(propertyMap.containsKey(stmt.getPredicate().getLocalName())) {
         			String currentValue = propertyMap.get(stmt.getPredicate().getLocalName());
         			currentValue.concat("," + nodeLiteral.getString());
         			propertyMap.put(stmt.getPredicate().getLocalName(), currentValue);                			
         		} else {
         			propertyMap.put(stmt.getPredicate().getLocalName(), nodeLiteral.getString());
         		}
            	 //logger.info("rdfNode literal: " + nodeLiteral.getString());
             }
		}    	    		
    			
		return propertyMap;
	}
	
	public static void listInstances(OntModel ontModel, String NS, String className) {
		
		OntResource ontResource = ontModel.getOntResource(NS + className);
               
        if(ontResource.isClass()) {
        	//logger.info("resource is a class");
        	OntClass ontClass = ontResource.as(OntClass.class);      
        	
        	ExtendedIterator instances = ontClass.listInstances();        	
        	while (instances.hasNext()) {
        		Individual individual = (Individual) instances.next();
        		//logger.info("Found Individual: " + individual.toString());
        		
        		StmtIterator stmtIt = individual.listProperties();
        		while(stmtIt.hasNext()) {
        			Statement stmt = stmtIt.next();
        			//logger.info("property : " + stmt.getPredicate().getLocalName());
        			
        			RDFNode rdfNode = stmt.getObject();
        			if(rdfNode.isResource()) {
                    	Resource nodeResource = rdfNode.asResource();                   
                    	if(nodeResource.canAs(Restriction.class)) {
                    		Restriction restriction = nodeResource.as(Restriction.class);    
                    		//logger.info("rdfNode restriction: on property " + restriction.getOnProperty());
                    	} else {
                    		//logger.info("rdfNode resource: " + nodeResource.getURI());  
                    	}
                     } else {                 
                    	 Literal nodeLiteral = rdfNode.asLiteral();
                    	 //logger.info("rdfNode literal: " + nodeLiteral.getString());
                     }
        		}
        	}
        }
	}
		
	public static void listOntClass(OntModel ontModel, String NS, String className) {
        OntResource ontResource = ontModel.getOntResource(NS + className);
        logger.info("ontResource " + ontResource.getURI());
       
        if(ontResource.isClass()) {
        	//logger.info("resource is a class");
        	OntClass ontClass = ontResource.as(OntClass.class);        	
        }
               
        StmtIterator i = ontResource.listProperties();
        
        while (i.hasNext()) {
        	Statement stmt = i.next();
            //Resource resource = stmt.getSubject();  
            Property property = stmt.getPredicate();
            RDFNode rdfNode = stmt.getObject();
                        
            if(property.isResource()) {
            	//logger.info("property isResource: " + property.getLocalName());
            	//Resource propertyResource = property.asResource();
            	
            } else  {
            	//logger.info("property isProperty: " + property.getLocalName());
            }
            
            if(rdfNode.isResource()) {
            	Resource nodeResource = rdfNode.asResource();
            	if(nodeResource.canAs(Restriction.class)) {
            		Restriction restriction = nodeResource.as(Restriction.class);    
            		//logger.info("rdfNode restriction: on property " + restriction.getOnProperty());
            	} else {
            		//logger.info("rdfNode resource: " + nodeResource.getLocalName());
            	}
             } else {                 
            	 Literal nodeLiteral = rdfNode.asLiteral();
            	 logger.info("rdfNode literal: " + nodeLiteral.getString());
             }
            logger.info("------------------------------------");
            /*
            NodeIterator nodeIt = resourceClass.listPropertyValues(property);
            while(nodeIt.hasNext()) {
            	//logger.info("resource property: " + resource.toString());
            	RDFNode rdfNode = nodeIt.next();
            	rdfNode.
            	logger.info("property: " + property.getLocalName() + ", value: " + rdfNode.toString());
            }
            */
        }
    }
	
	protected String getValueAsString(Resource r, Property p) {
        Statement s = r.getProperty(p);
        if (s == null) {
            return "";
        } else {
            return s.getObject().isResource() ? s.getResource().getURI() : s.getString();
        }
    }

}
