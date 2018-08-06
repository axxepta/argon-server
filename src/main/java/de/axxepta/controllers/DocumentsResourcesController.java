package de.axxepta.controllers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.codahale.metrics.Meter;

import de.axxepta.exceptions.ResponseException;
import de.axxepta.listeners.RegisterMetricsListener;
import de.axxepta.models.FileModel;
import de.axxepta.services.interfaces.IDocumentsResourceService;
import de.axxepta.tools.ValidationString;

@Path("documents-services")
public class DocumentsResourcesController {

	private static final Logger LOG = Logger.getLogger(DocumentsResourcesController.class);

	private final Meter metricRegistry = RegisterMetricsListener.requests;

	@Inject
	@Named("FilesResourceImplementation")
	private IDocumentsResourceService documentsService;

	@GET
	@Path("local-file-to-url")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public Response localFileUrl(String fileNamePath) throws ResponseException {
		if (!ValidationString.validationString(fileNamePath, "file name path")) {
			metricRegistry.mark();
			
			LOG.error("Value transmited for filename is incorrect");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(),
					"Value transmited for filename is incorrect");
		}
		File file = new File(fileNamePath);
		if (!file.exists()) {
			LOG.error("file not exist");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(),
					"Value transmited for username is incorrect");
		}
		URL fileURL = null;
		try {
			fileURL = file.toURI().toURL();
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage());
		}
		return Response.ok(fileURL).build();
	}
	
	@POST
	@Path("upload-file")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response uploadFile(FileModel fileModel) throws ResponseException {
		LOG.info("upload file");
		metricRegistry.mark();

		if (fileModel == null)
			LOG.error("is not transmited json for user");

		String fileNamePath = fileModel.getFileNamePath();
		String refFile = fileModel.getFileRef();

		if (!ValidationString.validationString(fileNamePath, "fileNamePath")) {
			LOG.error("Not valid file name with path");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(), "Not valid file name with path");
		}

		if (!ValidationString.validationString(refFile, "refFile")) {
			LOG.error("File reference is not valid");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(), "File reference is not valid");
		}
		return null;
	}

	@PUT
	@Path("delete-file")
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteFile(String fileName) throws ResponseException {
		LOG.info("delete file");
		metricRegistry.mark();

		if (!ValidationString.validationString(fileName, "fileName")) {
			LOG.error("Not valid file name");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(), "Not valid file name");
		}

		return Response.status(Status.OK).entity("File  " + fileName + " was deleted").build();
	}

	@GET
	@Path("exist-file")
	@Produces(MediaType.TEXT_PLAIN)
	public Response existFile(String fileName) {
		LOG.info("test if file");
		metricRegistry.mark();
		
		return Response.status(Status.OK).entity("File  " + fileName + " was deleted").build();
	}

	@GET
	@Path("retrieve-file")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_XML)
	public Response retrieveFile(String fileName) throws ResponseException {
		LOG.info("retrieve file");
		metricRegistry.mark();
		
		if (!ValidationString.validationString(fileName, "fileName")) {
			LOG.error("File name is not valid");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(), "File name is not valid");
		}

		return Response.status(Status.OK).entity("File  " + fileName + " was retireved").build();
	}

}
