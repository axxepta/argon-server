package de.axxepta.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.codahale.metrics.health.HealthCheck.Result;

import de.axxepta.health.HealthCheckImpl;

@Path("health")
public class HealthController {

	private static final Logger LOG = Logger.getLogger(HealthController.class);

	@GET
	@Path("simple-test")
	@Produces(MediaType.TEXT_PLAIN)
	public Response test() {
		LOG.info("Do simple health test");
		HealthCheckImpl health = new HealthCheckImpl();
		Result result = null;
		try {
			result = health.check();
			if (result.isHealthy()) {
				LOG.info("Application is healthy");
				return Response.ok("Application is healthy").build();
			} else {
				LOG.error("Application is not healthy");
				return Response.ok("Application is not healthy").build();
			}
		} catch (Exception e) {
			LOG.error("Check healthy error: " + e.getMessage());
			return Response.status(403)
            .entity("Check healthy error: " + e.getMessage()).build();
		}
	}

}
