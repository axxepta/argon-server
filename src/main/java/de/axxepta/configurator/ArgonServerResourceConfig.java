package de.axxepta.configurator;


import javax.ws.rs.ApplicationPath;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.fasterxml.jackson.jaxrs.xml.JacksonJaxbXMLProvider;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

@ApplicationPath("/services")
public class ArgonServerResourceConfig extends ResourceConfig {

	private static final Logger LOG = Logger.getLogger(ArgonServerResourceConfig.class);
	
	public ArgonServerResourceConfig(){
		LOG.info("Resource configuration for rest services");
		packages(true, "de.axxepta");
		
		register(MultiPartFeature.class);
		
		register(DiscoverableFeature.class);
		
		OpenApiResource openApiResource = new OpenApiResource();
        register(openApiResource);
        
        register(JacksonJaxbXMLProvider.class);
        
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        SLF4JBridgeHandler.install();		
	}
}
