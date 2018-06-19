package de.axxepta.resteasy.services;

import java.time.LocalDateTime;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.codahale.metrics.annotation.Timed;

@Path("testing")
public class AppTestServices {
	
	private static final Logger LOG = Logger.getLogger(AuthService.class);
	
	@GET
	@Path("test")
	@Produces(MediaType.TEXT_PLAIN)
	@Timed
	public String test() {
		LOG.info("Do a simple test on argon server");

		return "Do a simple test on argon server";
	}
	
	@GET
	@Path("test-date")
	@Produces(MediaType.TEXT_PLAIN)
	@Timed
	public String date() {
		LocalDateTime dateTime = LocalDateTime.now();
		
		LOG.info("Do a simple test on argon server on " + dateTime);

		return "Do a simple test on argon server on "+ dateTime;
	}
}
