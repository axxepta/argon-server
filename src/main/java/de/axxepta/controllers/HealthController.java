package de.axxepta.controllers;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.glassfish.jersey.server.monitoring.ExecutionStatistics;
import org.glassfish.jersey.server.monitoring.MonitoringStatistics;
import org.glassfish.jersey.server.monitoring.ResourceStatistics;
import org.glassfish.jersey.server.monitoring.ResponseStatistics;

import com.codahale.metrics.Meter;
import com.codahale.metrics.health.HealthCheck.Result;

import de.axxepta.health.HealthCheckImpl;
import de.axxepta.listeners.RegisterMetricsListener;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import ro.sync.servlet.admin.RESTAdminPing;

@Path("health")
public class HealthController {

	private static final Logger LOG = Logger.getLogger(HealthController.class);

	@Inject
	private Provider<MonitoringStatistics> monitoringStatistics;

	@Context
	private ResourceContext resourceContext;

	private final Meter metricRegistry = RegisterMetricsListener.requests;
	
	@Operation(summary = "Simple health check service", description = "Check in a simple way if the application is healthy", 
			method = "GET", operationId="#2_1")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "aplication is healthy"),
			@ApiResponse(responseCode = "409", description = "check healthy internal error") })
	@GET
	@Path("simple-test")
	@Produces(MediaType.TEXT_PLAIN)
	public Response test() {
		LOG.info("Do simple health test");
		metricRegistry.mark();
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
			return Response.status(Status.CONFLICT).entity("Check healthy error: " + e.getMessage()).build();
		}
	}

	@Operation(summary = "Execution statistics", description = "Provide execution statistics", 
			method = "GET", operationId="#2_2")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "execution statistics json") })
	@Path("execution-statistics")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response requestStatistics() {
		MonitoringStatistics monitorFrame = monitoringStatistics.get();
		metricRegistry.mark();
		ExecutionStatistics executionStatistics = monitorFrame.getRequestStatistics();

		return Response.status(Status.OK).entity(executionStatistics).build();
	}

	@Operation(summary = "Response statistics", description = "Provide response statistics", 
			method = "GET", operationId="#2_3")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "response statistics json") })
	@Path("response-statistics")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response responseStatistics() {
		MonitoringStatistics monitorFrame = monitoringStatistics.get();
		ResponseStatistics responseStatistics = monitorFrame.getResponseStatistics();

		return Response.status(Status.OK).entity(responseStatistics).build();
	}

	@Operation(summary = "Resource statistics", description = "Provide resource statistics", 
			method = "GET", operationId="#2_4")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "resource statistics") })
	@Path("resource-statistics")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response resourceStatistics() {
		metricRegistry.mark();
		MonitoringStatistics monitorFrame = monitoringStatistics.get();
		Map<String, ResourceStatistics> mapResourceStatistics = monitorFrame.getUriStatistics();

		return Response.status(Status.OK).entity(mapResourceStatistics).build();
	}

	@Operation(summary = "Ping check service", description = "Doing ping", 
			method = "GET", operationId="#2_5")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "ping service") })
	@Path("ping")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response ping() {
		metricRegistry.mark();
		RESTAdminPing adminPing = resourceContext.getResource(RESTAdminPing.class);
		String ping = adminPing.ping();
		LOG.info("ping =>" + ping);
		return Response.status(Status.OK).entity("ping =>" + ping).build();
	}
}
