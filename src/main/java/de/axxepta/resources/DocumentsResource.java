package de.axxepta.resources;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.codehaus.plexus.util.FileUtils;

import com.codahale.metrics.Meter;

import de.axxepta.exceptions.ResponseException;
import de.axxepta.models.FileDescriptionModel;
import de.axxepta.models.FileDisplayModel;
import de.axxepta.services.interfaces.IFileResourceService;
import de.axxepta.tools.ValidationString;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Path("document-services")
public class DocumentsResource {

	private static final Logger LOG = Logger.getLogger(DocumentsResource.class);

	@Inject
	private Meter metricRegistry;

	@Inject
	@Named("FileServiceImplementation")
	private IFileResourceService fileService;

	@Operation(summary = "List uploaded files", description = "Provide a map of uploaded files", method = "GET", operationId = "#4_1")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "retrieve of json of uploaded files"),
			@ApiResponse(responseCode = "409", description = "uploaded directory stored as constant not exist") })
	@GET
	@Path("list-uploaded-files")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listUploadedFiles() throws ResponseException {
		metricRegistry.mark();
		List<File> filesList = fileService.listUploadedFiles();
		if (filesList == null) {
			throw new ResponseException(Response.Status.CONFLICT.getStatusCode(), "Upload directory not exist");
		}

		Map<String, FileDisplayModel> filesResponseMap = new HashMap<>();
		for (File file : filesList) {
			String key = FileUtils.basename(file.getName());
			FileDisplayModel value = null;
			try {
				value = new FileDisplayModel(file);
				filesResponseMap.put(key, value);
			} catch (IOException e) {
				LOG.error(e.getMessage());
			}
		}

		return Response.status(Status.OK).entity(filesResponseMap).build();
	}

	@Operation(summary = "Check if file exist", description = "Retrieve head for an repository", method = "GET", operationId = "#4_2")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "file exist"),
			@ApiResponse(responseCode = "202", description = "file not exist"),
			@ApiResponse(responseCode = "400", description = "name of file cannot be valided") })
	@GET
	@Path("exist-file")
	public Response existFile(@QueryParam("filename") String fileName) throws ResponseException {
		LOG.info("test if file service");
		metricRegistry.mark();
		if (!ValidationString.validationString(fileName, "fileName")) {
			LOG.error("Name of file is null or empty");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(),
					"Name of file cannot be validated");
		}
		boolean fileExists = fileService.existFileStored(fileName);
		if (fileExists)
			return Response.status(Status.OK).entity("File  with name " + fileName + " exist").build();
		else
			return Response.status(Status.ACCEPTED).entity("File  with name " + fileName + " not exist").build();
	}

	@Operation(summary = "Upload file", description = "Upload file as an temporal one", method = "POST", operationId = "#4_3")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "upload file succes and return URL for temporal file and his type in JSON"),
			@ApiResponse(responseCode = "400", description = "at least one parameter in the request is missing"),
			@ApiResponse(responseCode = "409", description = "temporal file cannot be created") })
	@POST
	@Path("upload")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadFileLocal(@FormParam("file-url") String fileURLString,
			@DefaultValue("true") @FormParam("as-temp-file") boolean isTmpFile) throws ResponseException {
		metricRegistry.mark();

		if (!ValidationString.validationString(fileURLString, "file url")) {
			LOG.error("At least one of the parameters is missing");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(),
					"At least one of the parameters is missing");
		}

		URL fileURL = null;
		try {
			fileURL = new URL(fileURLString);
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage());
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(),
					"String does not determine a valid URL");
		}
		FileDescriptionModel fileDescription = fileService.uploadLocalFile(fileURL, isTmpFile);

		if (fileDescription == null)
			throw new ResponseException(Response.Status.CONFLICT.getStatusCode(), "The file could not be uploaded");
		return Response.ok(fileDescription).build();
	}

	@Operation(summary = "Delete a file", description = "Delete an file from uploaded set", method = "DELETE", operationId = "#4_4")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "the file was successfully deleted"),
			@ApiResponse(responseCode = "202", description = "file not exist"),
			@ApiResponse(responseCode = "409", description = "name of file cannot be validated") })
	@DELETE
	@Path("delete-file")
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteFile(String fileName) throws ResponseException {
		LOG.info("delete file service");
		metricRegistry.mark();

		if (!ValidationString.validationString(fileName, "fileName")) {
			LOG.error("Not valid file name");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(), "Not valid file name");
		}
		boolean hasDeleted = fileService.deleteFile(fileName);
		if (hasDeleted)
			return Response.status(Status.OK).entity("File  with name " + fileName + " was deleted").build();
		else
			return Response.status(Status.ACCEPTED).entity("File with name " + fileName + "has not been deleted")
					.build();
	}

	@Operation(summary = "Retrieve file", description = "Retrieve file as an binary one", method = "GET", operationId = "#4_5")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "return file content"),
			@ApiResponse(responseCode = "409", description = "name of file cannot be validated") })
	@GET
	@Path("retrieve-file")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response retrieveFile(@QueryParam("filename") String fileName) throws ResponseException {
		LOG.info("retrieve file service");
		metricRegistry.mark();

		if (!ValidationString.validationString(fileName, "fileName")) {
			LOG.error("File name is not valid");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(), "File name is not valid");
		}

		byte[] bytesFile = fileService.readingFile(fileName);
		ByteArrayOutputStream boStream = new ByteArrayOutputStream(bytesFile.length);
		boStream.write(bytesFile, 0, bytesFile.length);
		return Response.ok(boStream, "image/png").header("content-disposition", "attachment; filename = " + fileName)
				.build();
	}
}
