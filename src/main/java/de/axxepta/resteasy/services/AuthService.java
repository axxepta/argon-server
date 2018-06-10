package de.axxepta.resteasy.services;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.codahale.metrics.annotation.Timed;

/**
 * adding services for authentication
 *
 */
@Path("auth-rest")
public class AuthService {

	private static final Logger LOG = Logger.getLogger(AuthService.class);
	private static final String KEY = "Argon Server KEY";
	
	private Cipher cipher;
	
	public AuthService() 
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			       InvalidKeyException, UnsupportedEncodingException {
		 Key aesKey = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");
         cipher = Cipher.getInstance("AES");
         cipher.init(Cipher.ENCRYPT_MODE, aesKey);
	}
	
	@Path("/registry")
	@Consumes("application/json")
	@Timed
	public Response registryUser(@PathParam("username") String username,
			@PathParam("password") String password) throws IllegalBlockSizeException, BadPaddingException {
		LOG.info("Registry user with username " + username + " and password " 
			+ cipher.doFinal(password.getBytes()));
		return Response.status(200).build();
	}

	@Path("/login")
	@Consumes("application/json")
	@Timed
	public Response loginUser(@PathParam("username") String username,
			@PathParam("password") String password) throws IllegalBlockSizeException, BadPaddingException {
		LOG.info("Logon user with username " + username + " and password " 
				+ cipher.doFinal(password.getBytes()));
		return Response.status(200).build();
	}
}
