package uk.ac.liv.scm.serviceconsumer;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;

import uk.ac.liv.scm.exception.ServiceException;
import uk.ac.liv.scm.ontology.SCMOntology;
import uk.ac.liv.scm.ontology.SCMStandardOntology;

@Path("/scm")
@Produces({"application/xml","text/plain"})
public class AbstractServiceConsumer {
	
	private static final Logger logger = Logger.getLogger(AbstractServiceConsumer.class);
	
	@GET
	@Path("/serviceconsumer")	
	public String getServiceConsumer() {
		logger.info("getServiceConsumer");
		
		SCMStandardOntology scmStandardOnt = SCMStandardOntology.getInstance();
		String serviceConsumer = scmStandardOnt.getServiceConsumerXML();
		logger.info("ServiceConsumer RDF/XML::");
		logger.info(scmStandardOnt.printServiceModel());
				
		return serviceConsumer;
	}
	
	

}
