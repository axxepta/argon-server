package de.axxepta.configuration;

import java.io.File;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.message.DeflateEncoder;
import org.glassfish.jersey.message.GZipEncoder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.EncodingFilter;

import com.fasterxml.jackson.jaxrs.xml.JacksonJaxbXMLProvider;

import de.axxepta.bind.MeterConfigBinder;
import de.axxepta.rest.configuration.ResourceBundleReader;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;

public class InitResourceConfig {

	private static final Logger LOG = Logger.getLogger(InitResourceConfig.class);
	
	public static void initResourceConfig(ResourceConfig config) {
		final String fileName = "ArgonServerConfig";
		final File fileConfig = new File(fileName);
		final Locale locale = new Locale("en");
		
		ResourceBundleReader bundleReader = new ResourceBundleReader(fileConfig, locale);		
		ResourceConfig resourceConfig = ResourceConfig.forApplication(config);
		for(String key: bundleReader.getKeys()) {
			LOG.info("Register property " + key);
			resourceConfig.property(key, bundleReader.getValueAsString(key));
		}	
	}
	
	public static void initSwaggerProvider(ResourceConfig config) {
		LOG.info("OpenApi for Swagger is initialied");
		ResourceConfig resourceConfig = ResourceConfig.forApplication(config);
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
				config.register(openApiResource);
			}
		}
	}
	public static void initRegisterMeterBinder(ResourceConfig config) {
		LOG.info("Register meter config binder");
		config.register(new MeterConfigBinder());
	}
	
	public static void initUtilitiesXML(ResourceConfig config) {
		LOG.info("Register utilities related to XML");
		config.register(MultiPartFeature.class);	
	    config.register(JacksonJaxbXMLProvider.class);
	}
	
	public static void initEncoding(ResourceConfig config) {
		ResourceConfig resourceConfig = ResourceConfig.forApplication(config);
		String value = (String) resourceConfig.getProperty("encoding-activate");
	
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
				
				config.register(EncodingFilter.class);
				config.register(GZipEncoder.class);
				config.register(DeflateEncoder.class);
			}
		}
	}
}
