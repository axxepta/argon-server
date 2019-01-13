package de.axxepta.server.embedded;

import javax.inject.Singleton;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;
import org.jvnet.hk2.annotations.Service;

import de.axxepta.configuration.ArgonServerResourceConfig;
import de.axxepta.resources.TestResource;
import de.axxepta.server.embedded.interfaces.IServerEmbedded;

@Service(name = "JettyServerEmbedded")
@Singleton
public class ServerEmbedded implements IServerEmbedded{

	private int port;

	private Server jettyServerEmbedded;

	public ServerEmbedded(int port) {
		this.port = port;
	}
	
	public String startServer() {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");

		jettyServerEmbedded = new Server(port);
		jettyServerEmbedded.setHandler(context);

		ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
		jerseyServlet.setInitOrder(0);
		jerseyServlet.setDisplayName("Test service");
		
		 jerseyServlet.setInitParameter(
		 "com.sun.jersey.config.property.resourceConfigClass",
		 ArgonServerResourceConfig.class.getCanonicalName());
		
		//jerseyServlet.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "de.axxepta.resources");

		jerseyServlet.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");

		try {
			jettyServerEmbedded.start();
		} catch (Exception e) {
			return e.getMessage();
		}

		return null;
	}

	public String stopServer() {
		if (jettyServerEmbedded != null && jettyServerEmbedded.isStarted()) {
			try {
				jettyServerEmbedded.stop();
			} catch (Exception e) {
				return e.getMessage();
			}
		}
		return null;
	}
}
