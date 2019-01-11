package de.axxepta.oxygen.configuration;

import javax.ws.rs.ApplicationPath;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.fasterxml.jackson.jaxrs.xml.JacksonJaxbXMLProvider;

import de.axxepta.configuration.InitResourceConfig;

@ApplicationPath("oxygen-rest-services")
public class OxygenRestResourceConfiguration extends ResourceConfig {

	private static final Logger LOG = Logger.getLogger(OxygenRestResourceConfiguration.class);

	public OxygenRestResourceConfiguration() {

		LOG.info("Start OXYGEN REST SERVICES");

		setApplicationName("Oxygen services application");
		
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
