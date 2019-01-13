package de.axxepta.resources;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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

import de.axxepta.exceptions.ResponseException;
import de.axxepta.services.interfaces.IDatabaseResourceService;
import de.axxepta.tools.HealthCheckImpl;
import de.axxepta.tools.ValidationString;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


@Path("health")
public class HealthResource{

	private static final Logger LOG = Logger.getLogger(HealthResource.class);

	@Inject
	private Provider<MonitoringStatistics> monitoringStatistics;

	@Inject
	@Named("DatabaseBaseXServiceImplementation")
	private IDatabaseResourceService documentsResourceService;

	@Inject
	private Meter metricRegistry;

	@Operation(summary = "Simple health check service", description = "Check in a simple way if the application is healthy", method = "GET", operationId = "#2_1")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "aplication is healthy"),
			@ApiResponse(responseCode = "409", description = "check healthy internal error") })
	@GET
	@Path("simple-test")
	@Produces(MediaType.TEXT_PLAIN)
	public Response test() throws ResponseException {
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
			throw new ResponseException(Response.Status.CONFLICT.getStatusCode(),
					"Check healthy error: " + e.getMessage());
		}
	}

	@Operation(summary = "Execution statistics", description = "Provide execution statistics", method = "GET", operationId = "#2_2")
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

	@Operation(summary = "Response statistics", description = "Provide response statistics", method = "GET", operationId = "#2_3")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "response statistics json") })
	@Path("response-statistics")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response responseStatistics() {
		MonitoringStatistics monitorFrame = monitoringStatistics.get();
		ResponseStatistics responseStatistics = monitorFrame.getResponseStatistics();

		return Response.status(Status.OK).entity(responseStatistics).build();
	}

	@Operation(summary = "URI statistics", description = "Provide uri statistics", method = "GET", operationId = "#2_4")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "resource statistics") })
	@Path("uri-statistics")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response resourceStatistics() {
		metricRegistry.mark();
		MonitoringStatistics monitorFrame = monitoringStatistics.get();
		Map<String, ResourceStatistics> mapResourceStatistics = monitorFrame.getUriStatistics();
		Map<String, String> mapResponse = new HashMap<>();
		for (Map.Entry<String, ResourceStatistics> entry : mapResourceStatistics.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue().getResourceMethodStatistics().keySet().toString();
			mapResponse.put(key, value);
		}
		return Response.status(Status.OK).entity(mapResponse).build();
	}

	@Operation(summary = "Check database", description = "Check if xbase resource is functional", method = "GET", operationId = "#2_9")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "report if dabase is functional"),
			@ApiResponse(responseCode = "400", description = "error in transmited resource name in get request") })
	@Path("check-database")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response checkDatabase(
			@Parameter(description = "query parameter for name of resource", required = true) @QueryParam("resource") String resourceName)
			throws ResponseException {
		metricRegistry.mark();
		if (!ValidationString.validationString(resourceName, "resourceName")) {
			LOG.error("Value transmited for name of resource is incorrect");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(),
					"Value transmited for name of resource is incorrect");
		}
		String response;
		if (documentsResourceService.testDB(resourceName))
			response = "Connection with database can be done";
		else
			response = "Error in connection with database";
		LOG.info(response);
		return Response.status(Status.OK).entity(response).build();

	}
	
}
