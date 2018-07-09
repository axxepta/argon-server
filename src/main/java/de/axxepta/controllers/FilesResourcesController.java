package de.axxepta.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInputImpl;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import de.schlichtherle.io.File;
import ro.sync.servlet.RESTFileBrowser;
import ro.sync.servlet.admin.RESTAdminLicense;
import ro.sync.servlet.files.RESTFileService;


@Path("files")
public class FilesResourcesController {

	private static final Logger LOG = Logger.getLogger(FilesResourcesController.class);
	
	@Context
	private ResourceContext resourceContext;
	
	@Context
	private HttpServletRequest request;
	
	@POST
	@Path("upload-file")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public Response uploadFile(@QueryParam("path") String path,
			@QueryParam("file_name") String fileName) {
		
		return null;
	}
	
	@GET
	@Path("retrieve-file")
	public Response retrieveFile(@Context HttpHeaders httpHeaders,
			@QueryParam("file_name") String fileName) {
		
		RESTFileBrowser fileBrowser = resourceContext.getResource(RESTFileBrowser.class);
		
		Response response = fileBrowser.retrieveFiles(fileName, httpHeaders, request);
		LOG.info("Retrieve " + fileName + " with headers " + httpHeaders.toString());
		return response;
	}
	
}
