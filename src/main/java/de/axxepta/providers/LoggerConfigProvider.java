package de.axxepta.providers;

import javax.annotation.PostConstruct;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.slf4j.bridge.SLF4JBridgeHandler;

import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

@Provider
public class LoggerConfigProvider implements ContextResolver<Object>{

	private static final Logger LOG = Logger.getLogger(LoggerConfigProvider.class);

	@PostConstruct
	private void runConfigProvider() {
		LOG.info("logger config provider is initialized");
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		SLF4JBridgeHandler.install();
	}
	
	@Override
	public Object getContext(Class<?> arg0) {
		
		
		return null;
	}
	
}
