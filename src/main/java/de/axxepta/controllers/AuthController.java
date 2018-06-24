package de.axxepta.controllers;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.jvnet.hk2.annotations.Service;

import de.axxepta.services.interfaces.UserServiceI;
import de.axxepta.models.UserModel;

@Path("auth-services")
@Service
public class AuthController {

	private static final Logger LOG = Logger.getLogger(AuthController.class);
	private static final String KEY = "Argon Server KEY";

	private Cipher cipher;
	
	@Inject
	@Named("FreshImplementation")
	private UserServiceI userService;
	
	@PostConstruct
	public void init()
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException {
		Key aesKey = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");
		cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, aesKey);
	}

	@GET
	@Path("test")
	@Produces(MediaType.TEXT_PLAIN)
	public Response test() {		
		LOG.info("Do a simple test for auth services");
		if(userService != null)
			LOG.info("injection load");
		
		return Response.ok("Do a simple test for auth services").build();
	}
	
	@Path("registry")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	public Response registryUser(UserModel user) throws IllegalBlockSizeException, BadPaddingException {

		LOG.info("registry service ");

		if (user == null) {
			LOG.error("is not transmited json for user");
			return Response.status(Status.BAD_REQUEST).build();
		}

		String username = user.getUsername();
		String password = user.getPassword();

		if (!validate(username, "username"))
			return Response.status(Status.BAD_REQUEST).entity("Value transmited for username is incorrect").build();

		if (!validate(password, "password"))
			return Response.status(Status.BAD_REQUEST).entity("Value transmited for username is incorrect").build();

		LOG.info("Registry user with username " + username 
				+ " and password " + cipher.doFinal(password.getBytes()));
		
		return Response.status(Status.OK).entity("user " + username + " registry").build();
	}

	@Path("login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	public Response loginUser(UserModel user) throws IllegalBlockSizeException, BadPaddingException {

		LOG.info("registry service ");

		if (user == null) {
			LOG.error("is not transmited json for user");
			return Response.status(Status.BAD_REQUEST).build();
		}

		String username = user.getUsername();
		String password = user.getPassword();

		if (!validate(username, "username"))
			return Response.status(Status.BAD_REQUEST).entity("Value transmited for username is incorrect").build();

		if (!validate(password, "password"))
			return Response.status(Status.BAD_REQUEST).entity("Value transmited for username is incorrect").build();

		boolean result = userService.login(username, password);
		
		LOG.info("Logon user with username " + username 
				+ " and password " + cipher.doFinal(password.getBytes())
				+ " with result " + (result ? "Acceptlogin" : "Not accept"));
		
		return Response.status(Status.OK).entity("user " + username + " login").build();
	}

	private boolean validate(String string, String name) {
		if (string == null || string.length() == 0) {
			LOG.error("String validation error for " + name);
			return false;
		}
		return true;
	}
}
