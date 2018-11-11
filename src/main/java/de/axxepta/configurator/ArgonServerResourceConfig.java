package de.axxepta.configurator;

import java.io.File;
import java.util.Locale;

import javax.ws.rs.ApplicationPath;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.message.DeflateEncoder;
import org.glassfish.jersey.message.GZipEncoder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.EncodingFilter;

import com.fasterxml.jackson.jaxrs.xml.JacksonJaxbXMLProvider;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;

@ApplicationPath("/services")
public class ArgonServerResourceConfig extends ResourceConfig {

	private static final Logger LOG = Logger.getLogger(ArgonServerResourceConfig.class);
		
	public ArgonServerResourceConfig() {
		setApplicationName("Argon server application");
		
		initResourceConfig();
		
		packages(true, "de.axxepta");
		
		register(MultiPartFeature.class);
		
		register(JacksonJaxbXMLProvider.class);
	
		initSwaggerProvider();
		
		initEncoding();
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
		LOG.info("Get property for testing " +  ResourceConfig.forApplication(this).getProperty("/databases-services/create-database"));
	}
	
	private void initSwaggerProvider() {
		LOG.info("OpenApi for Swagger is initialied");
		ResourceConfig resourceConfig = ResourceConfig.forApplication(this);
		String value = (String) resourceConfig.getProperty("activation-swagger");
		if(value == null || value.isEmpty()) {
			LOG.error("Property for activation-swagger not exist");
		}
		else {
			value = value.trim();
			if(!value.equals("true") && !value.equals("false")) {
				LOG.error("Property activation-swagger have setting wrong value");
			}
			else if(value.equals("true")) {
				LOG.info("Swagger is registered");
				
				OpenApiResource openApiResource = new OpenApiResource();
				register(openApiResource);
			}
		}
	}
	
	private void initEncoding() {
		ResourceConfig resourceConfig = ResourceConfig.forApplication(this);
		String value = (String) resourceConfig.getProperty("encoding-active");
	
		if(value == null || value.isEmpty()) {
			LOG.error("Property for activation encoding not exist");
		}
		else {
			value = value.trim();
			if(!value.equals("true") && !value.equals("false")) {
				LOG.error("Property activation encoding have setting wrong value");
			}
			else if(value.equals("true")) {
				LOG.info("Activated encoding");
				
				register(EncodingFilter.class);
				register(GZipEncoder.class);
				register(DeflateEncoder.class);
			}
		}
	}
}
