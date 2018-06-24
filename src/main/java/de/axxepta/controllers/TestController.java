package de.axxepta.controllers;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.codahale.metrics.Meter;

import de.axxepta.listeners.RegisterMetricsListener;

@Path("testing")
public class TestController {

	private static final Logger LOG = Logger.getLogger(TestController.class);

	private final Meter metricRegistry = RegisterMetricsListener.requests;

	@Context
	private HttpServletRequest request;

	@GET
	@Path("test")
	@Produces(MediaType.TEXT_PLAIN)
	public Response test() {
		metricRegistry.mark();

		String token = request.getSession().getId();
		LOG.info("Session id " + token);

		
		LOG.info("Do a simple test on argon server");
		return Response.ok("Do a simple test on argon server").build();
	}

	@GET
	@Path("test-date")
	@Produces(MediaType.TEXT_PLAIN)
	public Response date() {
		metricRegistry.mark();
		LocalDateTime dateTime = LocalDateTime.now();
		LOG.info("Do a simple test on argon server on " + dateTime);
		return Response.ok("Do a simple test on argon server on " + dateTime).build();
	}
}
