package de.axxepta.web.author.resouces;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;

import com.codahale.metrics.Meter;

import de.axxepta.exceptions.ResponseException;
import de.axxepta.tools.CalculateMD5;
import de.axxepta.tools.ValidationString;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.servlet.RESTStatus;
import ro.sync.servlet.admin.RESTAdminPing;
import ro.sync.servlet.util.SecurityUtil;

@Path("health")
public class HeathOxygenResource {

	private static final Logger LOG = Logger.getLogger(HeathOxygenResource.class);
	
	@Inject
	private Meter metricRegistry;
	
	@Context
	private ResourceContext resourceContext;
	
	@Context
	private Application application;
	
	@Operation(summary = "Ping check service", description = "Doing ping", method = "GET", operationId = "#1_1")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "application is healtyh"),
			@ApiResponse(responseCode = "400", description = "bad request in case of number of checks is small or or unacceptably large"),
			@ApiResponse(responseCode = "500", description = "application is not healthy") })
	@Path("ping")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response ping(
			@Parameter(description = "number of checks", required = true) @QueryParam("number-checks") int numberChecks)
			throws ResponseException {
		metricRegistry.mark();
		if (numberChecks < 10 || numberChecks > 10000) {
			LOG.error("Incorrect number of checks");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(),
					"Incorrect number of checks, number of checks must be between 10 and 1000");
		}

		RESTAdminPing adminPing = resourceContext.getResource(RESTAdminPing.class);

		long timeDiff = 0;
		boolean isHealthy = true;
		String ping = adminPing.ping();
		for (int i = 0; i < numberChecks; i++) {
			long start = System.nanoTime();

			long stop = System.nanoTime();
			if (!ping.equals("pinged")) {
				isHealthy = false;
				LOG.error(ping + " is not ping");
				break;
			}
			timeDiff += stop - start;
		}

		if (isHealthy && timeDiff > numberChecks * 30000)
			isHealthy = false;

		LOG.info("Time for ping request is " + timeDiff);
		if (isHealthy)
			return Response.status(Status.OK).entity("Ping request is shows that the application is healthy").build();
		else
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity("Ping request is shows that the application is not healthy").build();
	}
	
	@Operation(summary = "Rest service for report of resources", description = "Retrieve JSON with rest report of resources from oxygen", method = "GET", operationId = "#1_2")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "report in JSON") })
	@Path("rest-report")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response restReport() {
		metricRegistry.mark();
		RESTStatus restStatus = resourceContext.getResource(RESTStatus.class);
		Map<String, Object> reportInfo = restStatus.getReportInfo();
		LOG.info("Rest report of resourse");
		return Response.status(Status.OK).entity(reportInfo).build();
	}

	@Operation(summary = "Calculate md5 sum for target", description = "Check in a simple way if the application is healthy", method = "GET", operationId = "#1_3")
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

			String messageHash = "Calculate md5 sum for " + url1.getPath() + " is obtained " + md5Hash1 + " and for "
					+ url2 + " is obtained " + md5Hash2;

			LOG.info(messageHash);

			metricRegistry.mark();

			return Response.ok(messageHash).build();

		} catch (IOException e) {
			LOG.error("Error in md5 calculation " + e.getMessage());
			throw new ResponseException(Response.Status.CONFLICT.getStatusCode(),
					"Check checksum error: " + e.getMessage());
		}
	}
	

	@Operation(summary = "Check security", description = "Response if security is enabled or not", method = "GET", operationId = "#1_4")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "report if security is enabled") })
	@Path("check-security")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response checkSecurity() {
		metricRegistry.mark();
		String response = null;
		if (SecurityUtil.isSecurityEnabled())
			response = "Securiy enabled";
		else
			response = " Security is not enbled";
		LOG.info(response);
		return Response.status(Status.OK).entity(response).build();

	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkFromSDK() {
		metricRegistry.mark();

		PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();

		Map<String, String> mapSettings = new HashMap<>() {

			private static final long serialVersionUID = 1L;

			{
				put("application name", pluginWorkspace.getApplicationName());
				put("preferences directory", pluginWorkspace.getPreferencesDirectory());
				put("user interface language", pluginWorkspace.getUserInterfaceLanguage());
				put("version build", pluginWorkspace.getVersionBuildID());
				put("version", pluginWorkspace.getVersion());
			}
		};

		return Response.status(Status.OK).entity(mapSettings).build();
	}
	
	@Operation(summary = "Check property", description = "Return value of a property if exists", method = "GET", operationId = "#1_5")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "return value of property"),
			@ApiResponse(responseCode = "204", description = "return value of property"),
			@ApiResponse(responseCode = "400", description = "error in transmited name of property"),
			@ApiResponse(responseCode = "409", description = "conflict in resource load") })

	@Path("check-configuration-property")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response checkConfigurationProperty(
			@Parameter(description = "query parameter for name of property", required = true) @QueryParam("property") String propertyName)
			throws ResponseException {
		metricRegistry.mark();
		if (!ValidationString.validationString(propertyName, "property name")) {
			LOG.error("Value transmited for property is incorrect");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(),
					"Value transmited for property is incorrect");
		}

		ResourceConfig resourceConfiguration = ResourceConfig.forApplication(application);
		
		if (resourceConfiguration == null) {
			LOG.error("Context injection for ResourceConfig has not been achieved");
			return Response.status(Status.CONFLICT).entity("Internal error related on load resource configuration")
					.build();
		}

		String valueProperty = (String) resourceConfiguration.getProperty(propertyName);

		if (valueProperty == null) {
			return Response.status(Status.NO_CONTENT).entity("Property name not found").build();
		} else {
			if (valueProperty.isEmpty())
				return Response.status(Status.OK).entity("Property has no assigned value").build();
			else
				return Response.status(Status.OK).entity("Property has value " + valueProperty).build();

		}
	}
}
