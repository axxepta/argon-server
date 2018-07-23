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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.jvnet.hk2.annotations.Service;

import com.codahale.metrics.Meter;

import de.axxepta.services.interfaces.UserServiceI;
import de.axxepta.tools.EncryptAES;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import de.axxepta.exceptions.ResponseException;
import de.axxepta.listeners.RegisterMetricsListener;
import de.axxepta.models.UserModel;

@Path("auth-services")
@Service
public class AuthController {

	private static final Logger LOG = Logger.getLogger(AuthController.class);
	private static final String KEY = "Argon Server KEY";

	private EncryptAES encrypt;

	@Inject
	@Named("UserAuthImplementation")
	private UserServiceI userService;

	private final Meter metricRegistry = RegisterMetricsListener.requests;

	@PostConstruct
	public void init()
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException {
		encrypt = new EncryptAES(KEY);
	}

	@Operation(summary = "Register user", description = "Register user with JSON data that contain username and password", method = "POST", operationId = "#5_1")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "user registered with succes"),
			@ApiResponse(responseCode = "400", description = "error in transmited data for registry"),
			@ApiResponse(responseCode = "403", description = "error in registry") })
	@Path("registry")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	public Response registryUser(UserModel user) throws ResponseException {

		LOG.info("registry service ");

		if (user == null) {
			LOG.error("is not transmited json for user");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(), "is not transmited json for user");
		}

		String username = user.getUsername();
		String password = user.getPassword();

		if (!validate(username, "username")) {
			LOG.error("Value transmited for username is incorrect");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(),
					"Value transmited for username is incorrect");
		}
		if (!validate(password, "password")) {
			LOG.error("Value transmited for username is incorrect");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(),
					"Value transmited for username is incorrect");
		}

		try {
			LOG.info("Registry user with username " + username + " and password " + encrypt.encrypt(password));
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			LOG.error(e.getMessage());
		}

		boolean result = userService.register(username, password);

		metricRegistry.mark();

		if (result) {
			return Response.status(Status.OK)
					.entity("user with username " + username + "and password " + password + " is register").build();
		} else {
			return Response.status(Status.FORBIDDEN)
					.entity("user with username " + username + "and password " + password + " cannot be register")
					.build();
		}
	}

	@Operation(summary = "Login user", description = "Login user with JSON data that contain username and password", method = "POST", operationId = "#5_2")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "user login with succes"),
			@ApiResponse(responseCode = "400", description = "error in transmited data for login"),
			@ApiResponse(responseCode = "403", description = "error in login") })
	@Path("login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	public Response loginUser(UserModel user) throws ResponseException {

		LOG.info("registry service ");

		if (user == null) {
			LOG.error("is not transmited json for user");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(), "is not transmited json for user");
		}

		String username = user.getUsername();
		String password = user.getPassword();

		if (!validate(username, "username")) {
			LOG.error("Value transmited for username is incorrect");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(),
					"Value transmited for username is incorrect");
		}
		if (!validate(password, "password")) {
			LOG.error("Value transmited for username is incorrect");
			throw new ResponseException(Response.Status.BAD_REQUEST.getStatusCode(),
					"Value transmited for username is incorrect");
		}

		boolean result = userService.login(username, password);

		metricRegistry.mark();

		try {
			LOG.info("Logon user with username " + username + " and password " + encrypt.encrypt(password) + " with result "
					+ (result ? "Acceptlogin" : "Not accept"));
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			LOG.error(e.getMessage());
		}
		if (result) {
			return Response.status(Status.OK)
					.entity("user with username " + username + "and password " + password + " login").build();
		} else {
			return Response.status(Status.FORBIDDEN)
					.entity("user with username " + username + "and password " + password + " can not log in").build();
		}
	}

	@Operation(summary = "Get username", description = "Get username uf user from actual session", method = "GET", operationId = "#5_3")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "get username of an existent user for the session"),
			@ApiResponse(responseCode = "202", description = "no user logged") })

	@Path("get-username")
	@Produces(MediaType.TEXT_PLAIN)
	@GET
	public Response getCurrentUsername() {
		String username = userService.getActualUser();
		metricRegistry.mark();
		if (username != null) {
			return Response.status(Status.OK).entity("Loged user with username " + username).build();
		} else {
			return Response.status(Status.ACCEPTED).entity("No user logged").build();
		}
	}

	@Operation(summary = "Logout", description = "Logout user session", method = "POST", operationId = "#5_4")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "logout"),
			@ApiResponse(responseCode = "202", description = "no user logged") })

	@Path("log-out")
	@Produces(MediaType.TEXT_PLAIN)
	@POST
	public Response logout() {
		boolean result = userService.logout();
		metricRegistry.mark();
		if (result) {
			return Response.status(Status.OK).entity("Logout").build();
		} else {
			return Response.status(Status.ACCEPTED).entity("No user logged").build();
		}
	}

	private boolean validate(String string, String name) {
		if (string == null || string.length() == 0) {
			LOG.error("String validation error for " + name);
			return false;
		}
		return true;
	}
}
