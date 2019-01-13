package de.axxepta.configuration;

import javax.ws.rs.ApplicationPath;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.fasterxml.jackson.jaxrs.xml.JacksonJaxbXMLProvider;

@ApplicationPath("/services")
public class ArgonServerResourceConfig extends ResourceConfig {

	private static final Logger LOG = Logger.getLogger(ArgonServerResourceConfig.class);
		
	public ArgonServerResourceConfig() {
		
		LOG.info("Start REST SERVICES");
		
		setApplicationName("Argon server application");
		
		InitResourceConfig.initResourceConfig(this);
		
		packages(true, "de.axxepta");
		
		InitResourceConfig.initRegisterMeterBinder(this);
		
		InitResourceConfig.initUtilitiesXML(this);
		
		register(MultiPartFeature.class);
		
	    register(JacksonJaxbXMLProvider.class);
	
		InitResourceConfig.initSwaggerProvider(this);
		
		InitResourceConfig.initEncoding(this);
	}
	
}
