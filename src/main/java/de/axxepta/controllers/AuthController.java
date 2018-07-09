package de.axxepta.controllers;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.jvnet.hk2.annotations.Service;

import de.axxepta.services.interfaces.UserServiceI;
import de.axxepta.tools.EncryptAES;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import de.axxepta.models.UserModel;

@Path("auth-services")
@Service
public class AuthController {

	private static final Logger LOG = Logger.getLogger(AuthController.class);
	private static final String KEY = "Argon Server KEY";

	private EncryptAES encrypt;
	
	@Inject
	@Named("FreshImplementation")
	private UserServiceI userService;
	
	@PostConstruct
	public void init()
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException {
		encrypt = new EncryptAES(KEY);
	}
	
	@Operation(summary = "Register user", description = "Register user with JSON data that contain username and password", 
			method = "POST", operationId="#5_1")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "user registered with succes"),
			@ApiResponse(responseCode = "409", description = "error in user registration") })
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
				+ " and password " + encrypt.encrypt(password));
		
		return Response.status(Status.OK).entity("user " + username + " registry").build();
	}

	@Operation(summary = "Login user", description = "Login user with JSON data that contain username and password", 
			method = "POST", operationId="#5_2")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "user login with succes"),
			@ApiResponse(responseCode = "409", description = "error in user login") })
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
				+ " and password " + encrypt.encrypt(password)
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
