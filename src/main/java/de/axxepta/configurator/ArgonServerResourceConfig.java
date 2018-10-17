package de.axxepta.configurator;

import java.io.File;
import java.util.Locale;

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
		
	public ArgonServerResourceConfig() {
		setApplicationName("Argon server application");
		
		initResourceConfig();
		
		packages(true, "de.axxepta");
		
		register(MultiPartFeature.class);
		
		OpenApiResource openApiResource = new OpenApiResource();
		register(openApiResource);
		
		register(JacksonJaxbXMLProvider.class);
	
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		SLF4JBridgeHandler.install();
	}
	
	private void initResourceConfig() {
		final String fileName = "ArgonServerConfig";
		final File fileConfig = new File(fileName);
		final Locale locale = new Locale("en");
		
		ResourceBundleReader bundleReader = new ResourceBundleReader(fileConfig, locale);		
		ResourceConfig resourceConfig = ResourceConfig.forApplication(this);
		for(String key: bundleReader.getKeys()) {
			LOG.info("Register property " + key);
			resourceConfig.property(key, bundleReader.getValueAsString(key));
		}	
		LOG.info("Get property for testing " +  ResourceConfig.forApplication(this).getProperty("license-services/set-register-license"));
	}
	
}
