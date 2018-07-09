package de.axxepta.configurator;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.bridge.SLF4JBridgeHandler;


import de.axxepta.controllers.AuthController;
import de.axxepta.controllers.CheckSumController;
import de.axxepta.controllers.HealthController;
import de.axxepta.controllers.LicenseController;
import de.axxepta.controllers.TestController;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;

@ApplicationPath("/services")
public class ArgonServerResourceConfig extends ResourceConfig {

	private static final Logger LOG = Logger.getLogger(ArgonServerResourceConfig.class);

	@Context 
	private ServletConfig servletConfig;
	
	public ArgonServerResourceConfig() throws ClassNotFoundException, IOException {
		LOG.info("Resource configuration for rest services");
		packages(true, "de.axxepta");

		register(AuthController.class);
		register(HealthController.class);
		register(TestController.class);
		register(CheckSumController.class);
		register(LicenseController.class);
		
		register(DiscoverableFeature.class);
		
		OpenApiResource openApiResource = new OpenApiResource();
        register(openApiResource);
        
		SLF4JBridgeHandler.install();
	}

}
