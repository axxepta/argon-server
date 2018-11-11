package de.axxepta.controllers;

import java.time.LocalDateTime;

import javax.inject.Singleton;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Path("testing")
@Singleton
public class TestController {

	private static final Logger LOG = Logger.getLogger(TestController.class);

	private final Meter metricRegistry = RegisterMetricsListener.requests;
	
	@Context
	private HttpServletRequest request;
	
	@Operation(summary = "Doing simple test", description = "Doing a test by returning a simple message", 
			   method = "GET", operationId="#1_1")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "message test") })
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

	@Operation(summary = "Doing simple test and adding timestamp", description = "Doing a test by returning a simple message with an timestamp", 
			method = "GET", operationId="#1_2")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "message test with date") })
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
