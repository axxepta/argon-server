package de.axxepta.web.author.resouces;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("testing")
public class SimpleTestResource {

	@GET
	@Path("rest-test-services")
	@Produces(MediaType.TEXT_PLAIN)
	public Response test() {
		return Response.ok("Do a simple test on argon server").build();
	}
}
