package de.axxepta.resteasy.services;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.codahale.metrics.health.HealthCheck.Result;

import de.axxepta.resteasy.health.AppHealth;

@Path("health")
public class AppHealthService {

	private static final Logger LOG = Logger.getLogger(AppHealthService.class);

	@GET
	@Path("simple-test")
	@Produces(MediaType.TEXT_PLAIN)
	public String test() {
		LOG.info("Do simple health test");
		AppHealth health = new AppHealth();
		Result result = null;
		try {
			result = health.check();
			if (result.isHealthy()) {
				LOG.info("Application is healthy");
				return "Application is healthy ";
			} else {
				LOG.error("Application is not healthy");
				return "Application is not healthy";
			}
		} catch (Exception e) {
			LOG.error("Check healthy error: " + e.getMessage());
			return e.getMessage();
		}
	}

}
