package de.axxepta.resources;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import com.codahale.metrics.Meter;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.axxepta.exceptions.ResponseException;
import de.axxepta.models.UserAuthModel;
import de.axxepta.services.interfaces.IDocumentGitService;
import de.axxepta.tools.ValidationString;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Path("git-services")
public class GitResource {

	private static final Logger LOG = Logger.getLogger(GitResource.class);

	private final ExecutorService executor = Executors.newFixedThreadPool(1);

	@Inject
	private Meter metricRegistry;

	@Inject
	@Named("DocumentGitServiceImplementation")
	private IDocumentGitService documentGitService;

	@Operation(summary = "List branches names for git repository", description = "Provide a map of uploaded files", method = "GET", operationId = "#5_1")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "retrieve name of the repository head"),
			@ApiResponse(responseCode = "400", description = "string from url cannot be validate"),
			@ApiResponse(responseCode = "409", description = "null set of names or exception in a list map") })
	@GET
	@Path("branches-names")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRepositoryRemoteNames(
			@Parameter(description = "repository URL", required = true) @QueryParam("repository-url") String url,
			@Parameter(description = "username", required = false) @QueryParam("username") String username,
			@Parameter(description = "password", required = false) @QueryParam("password") String password)
			throws ResponseException {
		metricRegistry.mark();

		if (!ValidationString.validationString(url, "repository-url")) {
			LOG.error("URL string is not a valid one");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(), "URL string is not a valid one");
		}

		UserAuthModel userAuth = new UserAuthModel(username, password);
		List<String> setNames = null;
		
		Callable<List<String>> requestTask = () -> {
		    
				return documentGitService.getRemoteNames(url, userAuth);
		    
		};
		
		Future<List<String>> future = executor.submit(requestTask);
		
		try {
			setNames = future.get();
		} catch (InterruptedException | ExecutionException e) {
			LOG.error(e.getMessage());
			throw new ResponseException(Response.Status.CONFLICT.getStatusCode(), e.getMessage());
		}
		
		if (setNames == null) {
			LOG.error("for URL " + url + " is obtain null set of remote names ");
			throw new ResponseException(Response.Status.CONFLICT.getStatusCode(),
					"for URL " + url + " is obtain null set of remote names");
		}

		final StringWriter stringWriter = new StringWriter();
		final ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(stringWriter, setNames);
		} catch (IOException e) {
			LOG.error(e.getMessage());
			throw new ResponseException(Response.Status.CONFLICT.getStatusCode(), e.getMessage());
		}

		String response = stringWriter.toString();

		LOG.info(response);
		return Response.status(Status.OK).entity(response).build();
	}

	@Operation(summary = "Get lists of directories and files", description = "Provide a map of uploaded files", method = "GET", operationId = "#5_2")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "retrieve name of the repository head"),
			@ApiResponse(responseCode = "400", description = "string from url cannot be validate"),
			@ApiResponse(responseCode = "409", description = "exception in a list map") })
	@GET
	@Path("get-dirs-files")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFilesNames(
			@Parameter(description = "repository URL", required = true) @QueryParam("repository-url") String url,
			@Parameter(description = "username", required = false) @QueryParam("username") String username,
			@Parameter(description = "password", required = false) @QueryParam("password") String password)
			throws ResponseException {
		metricRegistry.mark();

		if (!ValidationString.validationString(url, "repository-url")) {
			LOG.error("URL string is not a valid one");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(), "URL string is not a valid one");
		}

		UserAuthModel userAuth = new UserAuthModel(username, password);

		Pair<List<String>, List<String>> dirsFilesLists = null;

		Callable<Pair<List<String>, List<String>>> requestTask = () -> {
			return documentGitService.getFileNamesFromCommit(url, userAuth);
		};

		Future<Pair<List<String>, List<String>>> future = executor.submit(requestTask);
		
		try {
			dirsFilesLists = future.get();
		} catch (InterruptedException | ExecutionException e) {
			LOG.error(e.getMessage());
			throw new ResponseException(Response.Status.CONFLICT.getStatusCode(), e.getMessage());
		}

		List<String> dirList = dirsFilesLists.getLeft();
		List<String> filesList = dirsFilesLists.getRight();
		StringWriter stringWriter = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();

		try {
			mapper.writeValue(stringWriter, dirList);
		} catch (IOException e) {
			LOG.error(e.getMessage());
			throw new ResponseException(Response.Status.CONFLICT.getStatusCode(), e.getMessage());
		}

		String dirsString = "Directories " + stringWriter.toString();

		stringWriter = new StringWriter();
		mapper = new ObjectMapper();

		try {
			mapper.writeValue(stringWriter, filesList);
		} catch (IOException e) {
			LOG.error(e.getMessage());
			throw new ResponseException(Response.Status.CONFLICT.getStatusCode(), e.getMessage());
		}

		String filesString = "Files " + stringWriter.toString();

		String response = dirsString + " " + filesString;

		LOG.info(response);
		return Response.status(Status.OK).entity(response).build();
	}
}
