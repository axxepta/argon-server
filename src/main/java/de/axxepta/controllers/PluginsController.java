package de.axxepta.controllers;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.codahale.metrics.Meter;
import com.google.common.collect.Lists;

import de.axxepta.exceptions.ResponseException;
import de.axxepta.listeners.RegisterMetricsListener;
import de.axxepta.models.PluginDescriptionModel;
import de.axxepta.services.interfaces.IPluginService;
import de.axxepta.tools.ValidationString;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Path("plugins")
public class PluginsController {

	private static final Logger LOG = Logger.getLogger(PluginsController.class);

	@Inject
	@Named("PluginService")
	private IPluginService pluginService;

	private final Meter metricRegistry = RegisterMetricsListener.requests;

	@Operation(summary = "Default plugin directory", description = "Get default plugin directory", method = "GET", operationId = "#7_1")
	@ApiResponse(responseCode = "200", description = "default directory as string")
	@GET
	@Path("get-default-directory")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getPluginsDefaultDirectory() {
		metricRegistry.mark();
		return Response.ok("Default plugin directory is " + pluginService.getPluginDefaultDirectory()).build();
	}

	@Operation(summary = "Plugin directories", description = "Get all plugin directory", method = "GET", operationId = "#7_1")
	@ApiResponse(responseCode = "200", description = "list of plugin directories")
	@GET
	@Path("get-directories")
	@Produces(MediaType.APPLICATION_XML)
	public Response getPluginsDirectory() {
		metricRegistry.mark();
		List<File> pluginsDirectoryList = pluginService.getPluginDirectories();
		List<String> directoriesStringList = pluginsDirectoryList.stream().map(file -> file.toString())
				.collect(Collectors.toList());
		GenericEntity<List<String>> entityResponse = new GenericEntity<List<String>>(
				Lists.newArrayList(directoriesStringList)) {
		};
		return Response.ok(entityResponse).build();
	}

	@Operation(summary = "set directory", description = "Set a new path for plugin", method = "POST", operationId = "#7_3")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "value of path is set"),
			@ApiResponse(responseCode = "400", description = "error in transmited path in request"),
			@ApiResponse(responseCode = "409", description = "path not exist or isn't a directory") })
	@POST
	@Path("change-directory")
	@Produces(MediaType.TEXT_PLAIN)
	public Response setPluginDirectory(
			@Parameter(description = "path and name of directory", required = true) @FormParam("directory") String path)
			throws ResponseException {
		metricRegistry.mark();
		LOG.info("Set plugin directory as " + path);
		if (!ValidationString.validationString(path, "path")) {
			LOG.error("Value transmited for path is incorrect");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(),
					"Value transmited for path is incorrect");
		}
		boolean r = pluginService.changeDirectoryPlugin(path);
		if (r)
			return Response.ok("Directory " + path + " is set").build();
		else {
			LOG.error("Value transmited for path not exist or isn't a directory");
			throw new ResponseException(Response.Status.CONFLICT.getStatusCode(),
					"Value transmited for path not exist or isn't a directory");
		}
	}

	@Operation(summary = "Plugin descriptions", description = "Get descriptions for plugins", method = "GET", operationId = "#7_4")
	@ApiResponse(responseCode = "200", description = "list of descriptions for plugins")
	@GET
	@Path("get-plugin-descriptions")
	@Produces(MediaType.APPLICATION_XML)
	public Response getPluginDescriptions() {
		metricRegistry.mark();
		List<PluginDescriptionModel> pluginDescriptionsList = pluginService.getDescriptionPlugins();
		GenericEntity<List<PluginDescriptionModel>> entityResponse = new GenericEntity<List<PluginDescriptionModel>>(
				Lists.newArrayList(pluginDescriptionsList)) {
		};
		return Response.ok(entityResponse).build();
	}
}
