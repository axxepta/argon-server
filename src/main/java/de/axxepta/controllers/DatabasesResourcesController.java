package de.axxepta.controllers;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.codahale.metrics.Meter;

import de.axxepta.exceptions.ResponseException;
import de.axxepta.listeners.RegisterMetricsListener;
import de.axxepta.services.dao.interfaces.IDocumentDAO;
import de.axxepta.tools.ValidationString;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Path("databases-services")
public class DatabasesResourcesController {

	private static final Logger LOG = Logger.getLogger(DatabasesResourcesController.class);

	private final Meter metricRegistry = RegisterMetricsListener.requests;

	@Inject
	@Named("BaseXDao")
	private IDocumentDAO documentDAO;

	@Operation(summary = "Show databases", description = "Show existing databse on BaseX server", method = "GET", operationId = "#7_1")
	@ApiResponses(@ApiResponse(responseCode = "200", description = "databases names"))
	@GET
	@Path("show-databases")
	@Produces(MediaType.APPLICATION_JSON)
	public Response showDatabases() {
		metricRegistry.mark();
		LOG.info("show databases service");
		
		Map<String, Map<String , String>> infoDatabases = documentDAO.showDatabases();
			
		return Response.status(Status.OK).entity(infoDatabases).build();
	}

	@Operation(summary = "Show database infos", description = "Show infos about database existing on BaseX server", method = "GET", operationId = "#7_2")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "database infos"),
			@ApiResponse(responseCode = "204", description = "error internal on server"),
			@ApiResponse(responseCode = "204", description = "name of database is incorrect")})
	@GET
	@Path("show-infos-database")
	@Produces(MediaType.TEXT_PLAIN)
	public Response showInfosDatabase(@QueryParam("database-name") String databaseName) throws ResponseException {
		metricRegistry.mark();
		if (!ValidationString.validationString(databaseName, "databaseName")) {
			LOG.error("Value transmited for database name is incorrect");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(),
					"Value transmited for database name is incorrect");
		}
		
		databaseName = databaseName.trim();
		
		LOG.info("show info for database " + databaseName);

		String infos = documentDAO.showInfoDatabase(databaseName);

		if (infos == null) {
			return Response.status(Status.NO_CONTENT).entity("Database with name  " + databaseName + " not exist")
					.build();
		} else {
			return Response.status(Status.OK).entity("Existing databases " + infos).build();
		}
	}

	@Operation(summary = "Delete database", description = "Drop an databsse", method = "DELETE", operationId = "#7_3")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "delete database"),
			@ApiResponse(responseCode = "204", description = "innexistent database"),
			@ApiResponse(responseCode = "400", description = "name of database is incorrect")})
	@DELETE
	@Path("delete-database")
	@Produces(MediaType.TEXT_PLAIN)
	public Response dropDatabase(@QueryParam("database-name") String databaseName) throws ResponseException {
		metricRegistry.mark();
		if (!ValidationString.validationString(databaseName, "databaseName")) {
			LOG.error("Value transmited for database name is incorrect");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(),
					"Value transmited for database name is incorrect");
		}
		LOG.info("delete database " + databaseName);

		boolean result = documentDAO.dropDatabase(databaseName);

		if (result) {
			return Response.status(Status.OK).entity("Database " + databaseName + " was deleted").build();
			
		} else {
			return Response.status(Status.NO_CONTENT).entity("Database with name  " + databaseName + " not exist")
					.build();
		}
	}
}
