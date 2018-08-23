package de.axxepta.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;

import de.axxepta.exceptions.ResponseException;
import de.axxepta.listeners.RegisterMetricsListener;
import de.axxepta.models.FileModel;
import de.axxepta.services.interfaces.IDatabaseResourceService;
import de.axxepta.tools.ValidationString;

@Path("document-services")
public class DocumentsResourcesController {

	private static final Logger LOG = Logger.getLogger(DocumentsResourcesController.class);

	private final Meter metricRegistry = RegisterMetricsListener.requests;

	@Inject
	@Named("DatabaseBaseXServiceImplementation")
	private IDatabaseResourceService documentsService;

	private List<File> tmpFileList = new ArrayList<>();

	@PreDestroy
	private void init() {
		for (File tmpFile : tmpFileList) {
			tmpFile.deleteOnExit();
			LOG.info(tmpFileList + " put on deleted");
		}
	}

	@POST
	@Path("local-file-to-url")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public Response uploadFile(@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileMetaData) throws ResponseException {
		metricRegistry.mark();
		Timer timer = new Timer();
		if (fileInputStream == null || fileMetaData == null) {
			LOG.error("At least one of the parameters is missing");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(),
					"At least one of the parameters is missing");
		}

		Timer.Context timerContext = timer.time();

		String prefixFile = fileMetaData.getFileName();
		File tempFileUpload;
		try {
			tempFileUpload = File.createTempFile(prefixFile, ".tmp");
		} catch (IOException e) {
			LOG.error("Temp file cannot be created " + e.getMessage());
			throw new ResponseException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					"Temp file cannot be created");
		}

		try {
			int read = 0;
			byte[] buffer = new byte[1024];

			OutputStream outStream = new FileOutputStream(tempFileUpload);
			while ((read = fileInputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, read);
			}
			outStream.flush();
			outStream.close();
		} catch (IOException e) {
			LOG.error(e.getMessage());
			throw new ResponseException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					"Internal error in file uploading");
		}

		tmpFileList.add(tempFileUpload);

		URL fileURL = null;
		try {
			fileURL = tempFileUpload.toURI().toURL();
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage());
			return null;
		}

		timerContext.stop();
		LOG.info("URL for uploaded file is " + tempFileUpload.getPath() + " was obtained in " + timer.getCount());
		return Response.ok(fileURL.toString()).build();
	}

	@POST
	@Path("upload-file")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response uploadFile(FileModel fileModel) throws ResponseException {
		LOG.info("upload file service");
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

		return Response.status(Status.OK).entity("File  " + fileName + " was deleted").build();
	}

	@GET
	@Path("exist-file")
	public Response existFile(@QueryParam("filename") String fileName) {
		LOG.info("test if file service");
		metricRegistry.mark();

		return Response.status(Status.OK).entity("File  " + fileName + " was deleted").build();
	}

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

		return Response.status(Status.OK).entity("File  " + fileName + " was retireved").build();
	}

}
