package de.axxepta.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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

import de.axxepta.exceptions.ResponseException;
import de.axxepta.listeners.RegisterMetricsListener;
import de.axxepta.services.interfaces.IDatabaseResourceService;
import de.axxepta.tools.CalculateMD5;
import de.axxepta.tools.HealthCheckImpl;
import de.axxepta.tools.ValidationString;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import ro.sync.servlet.RESTStatus;
import ro.sync.servlet.admin.RESTAdminPing;
import ro.sync.servlet.util.SecurityUtil;

@Path("health")
public class HealthController {

	private static final Logger LOG = Logger.getLogger(HealthController.class);

	@Inject
	private Provider<MonitoringStatistics> monitoringStatistics;

	@Context
	private ResourceContext resourceContext;

	@Inject
	@Named("DatabaseBaseXServiceImplementation")
	
	private IDatabaseResourceService documentsResourceService;
	
	private final Meter metricRegistry = RegisterMetricsListener.requests;
	
	@Operation(summary = "Simple health check service", description = "Check in a simple way if the application is healthy", 
			method = "GET", operationId="#2_1")
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
	
	@Operation(summary = "Rest service for report of resources", description = "Retrieve JSON with rest report of resources from oxygen", 
			method = "GET", operationId="#2_6")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "report in JSON") })	
	@Path("rest-report")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response restReport() {
		metricRegistry.mark();
		RESTStatus  restStatus = resourceContext.getResource(RESTStatus.class);
		Map <String, Object > reportInfo = restStatus.getReportInfo();
		LOG.info("Rest report of resourse");
		return Response.status(Status.OK).entity(reportInfo).build();
	}
	
	@Operation(summary = "Calculate md5 sum for target", description = "Check in a simple way if the application is healthy", 
			method = "GET", operationId="#2_7")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "calculation of md5 sum is complete"),
			@ApiResponse(responseCode = "409", description = "Internal error in md5 sum calculation") })
	@GET
	@Path("md5sum-app")
	@Produces(MediaType.TEXT_PLAIN)
	public Response computeMD5() throws ResponseException {
		try {
			URL url1 = Paths.get("target", "jetty_overlays").toUri().toURL();
			URL url2 = Paths.get("target", "classes").toUri().toURL();
			String md5Hash1 = CalculateMD5.calcMD5Hash(new File(url1.getPath()));
			String md5Hash2 = CalculateMD5.calcMD5Hash(new File(url2.getPath()));
			
			String messageHash = "Calculate md5 sum for "  + url1.getPath() +  " is obtained " + md5Hash1 + 
					" and for " + url2 + " is obtained " + md5Hash2;
			
			LOG.info(messageHash); 
			
			metricRegistry.mark();
			
			return Response.ok(messageHash).build();			
			
		} catch (IOException e) {
			LOG.error("Error in md5 calculation " + e.getMessage());
			throw new ResponseException(Response.Status.CONFLICT.getStatusCode(),
					"Check checksum error: " + e.getMessage());
		} 
	}
	
	@Operation(summary = "Check security", description = "Response if security is enabled or not", 
			method = "GET", operationId="#2_8")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "report if security is enabled") })	
	@Path("check-security")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response checkSecurity() {
		metricRegistry.mark();
		String response = null;
		if(SecurityUtil.isSecurityEnabled()) 
			response = "Securiy enabled";
		else
			response = " Security is not enbled";
		LOG.info(response);
		return Response.status(Status.OK).entity(response).build();
		
	}
	
	@Operation(summary = "Check database", description = "Check if xbase resource is functional", 
			method = "GET", operationId="#2_9")
	@ApiResponses({@ApiResponse(responseCode = "200", description = "report if dabase is functional"), 
		@ApiResponse(responseCode = "400", description = "error in transmited resource name in get request")})	
	@Path("check-database")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response checkDatabase(@QueryParam("resource") String resourceName) throws ResponseException {
		metricRegistry.mark();
		if (!ValidationString.validationString(resourceName, "resourceName")) {
			LOG.error("Value transmited for name of resource is incorrect");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(),
					"Value transmited for name of resource is incorrect");
		}
		String response;
		if(documentsResourceService.testDB(resourceName)) 
			response = "Connection with database can be done";
		else
			response = "Error in connection with database";
		LOG.info(response);
		return Response.status(Status.OK).entity(response).build();
		
	}
}
