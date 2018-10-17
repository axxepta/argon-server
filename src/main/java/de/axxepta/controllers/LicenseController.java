package de.axxepta.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.axxepta.exceptions.ResponseException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import ro.sync.servlet.admin.RESTAdminLicense;

@Path("license-services")
public class LicenseController {

	private static final Logger LOG = Logger.getLogger(LicenseController.class);
	
	@Context
	private ResourceContext resourceContext;
	
	@Context 
	private ServletContext servletContext;
	
	@Operation(summary = "get license", description = "Provide available license that are registered", 
			method = "GET", operationId="#3_1")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "license in JSON") })
	@GET
	@Path("get-register-license")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRegisterLicense() {
		
		RESTAdminLicense restAdminLicense = resourceContext.getResource(RESTAdminLicense.class);
		Map <?, ?> licenseInfo = restAdminLicense.getLicenseInfo();
		if(licenseInfo == null) {
			LOG.info("Is not added a license");
			
			return Response.status(Status.OK).entity("{\"license \": \"Is not added a license\"}").build();
		}
		else {
			LOG.info("Get license info " + licenseInfo);
			return Response.status(Status.OK).entity(licenseInfo).build();
		}
		
	}
	
	@Operation(summary = "Register license", description = "Set register license that was send as JSON", 
			method = "PUT", operationId="#3_2")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "the license registration process went without error"),
			@ApiResponse(responseCode = "409", description = "appear an error in license registration") })
	@PUT
	@Path("set-register-license")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response setRegisterLicense(@QueryParam("license") String licenseInfo) throws ResponseException {
		RESTAdminLicense restAdminLicense = resourceContext.getResource(RESTAdminLicense.class);
		Map<String, String> mapLicense = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapLicense = mapper.readValue(licenseInfo, new TypeReference<Map<String, String>>(){});
			boolean r = restAdminLicense.setLicenseInfo(mapLicense, servletContext);
			String message = "License " + (r ? "is registered" : "is not registered");
			LOG.info(message);
			return Response.ok(message).build();
		} catch (IOException e) {
			LOG.error("Error in register license " + e.getMessage());
			throw new ResponseException(Response.Status.CONFLICT.getStatusCode(),
					"Error in register license " + e.getMessage());
		}	
	}
}
