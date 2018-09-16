package de.axxepta.configurator;

import java.io.IOException;

import javax.ws.rs.ApplicationPath;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.bridge.SLF4JBridgeHandler;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;

@ApplicationPath("/services")
public class ArgonServerResourceConfig extends ResourceConfig {

	private static final Logger LOG = Logger.getLogger(ArgonServerResourceConfig.class);
	
	public ArgonServerResourceConfig() throws ClassNotFoundException, IOException {
		LOG.info("Resource configuration for rest services");
		packages(true, "de.axxepta");
		
		register(MultiPartFeature.class);
		
		register(DiscoverableFeature.class);
		
		OpenApiResource openApiResource = new OpenApiResource();
        register(openApiResource);
        
		SLF4JBridgeHandler.install();
	}
}
