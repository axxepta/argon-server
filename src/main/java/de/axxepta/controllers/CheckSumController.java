package de.axxepta.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.codahale.metrics.Meter;

import de.axxepta.listeners.RegisterMetricsListener;
import de.axxepta.tools.CalculateMD5;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Path("check-sum")
public class CheckSumController {

	private static final Logger LOG = Logger.getLogger(CheckSumController.class);

	private final Meter metricRegistry = RegisterMetricsListener.requests;
	
	@Operation(summary = "Calculate md5 sum for target", description = "Check in a simple way if the application is healthy", 
			method = "GET", operationId="#4_1")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "calculation of md5 sum is complete"),
			@ApiResponse(responseCode = "409", description = "Internal error in md5 sum calculation") })
	@GET
	@Path("md5sum")
	@Produces(MediaType.TEXT_PLAIN)
	public Response computeMD5() {
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
			return Response.status(Status.CONFLICT).entity("Check healthy error: " + e.getMessage()).build();
		} 
	}
}
