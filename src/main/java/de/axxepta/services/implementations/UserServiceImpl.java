package de.axxepta.services.implementations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.crypto.hash.format.DefaultHashFormatFactory;
import org.apache.shiro.crypto.hash.format.HashFormat;
import org.apache.shiro.crypto.hash.format.Shiro1CryptFormat;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.env.EnvironmentLoader;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.jvnet.hk2.annotations.Service;

import de.axxepta.services.interfaces.IUserService;
import ro.sync.auth.PropertiesRealmWithDefaultUsersFile;

@Service(name = "UserAuthImplementation")
@Singleton
public class UserServiceImpl implements IUserService {

	private static final Logger LOG = Logger.getLogger(UserServiceImpl.class);

	@Context
	private HttpServletRequest request;

	@Override
	public boolean login(String username, String password) {
		try {
			Subject currentUser = SecurityUtils.getSubject();
			currentUser.login(new UsernamePasswordToken(username, password, true));
			LOG.info("Login user with username " + username);
			return true;
		} catch (AuthenticationException e) {}

		return false;
	}

	@Override
	public boolean register(String username, String password) {
		IniWebEnvironment environment = (IniWebEnvironment) request.getServletContext()
				.getAttribute(EnvironmentLoader.ENVIRONMENT_ATTRIBUTE_KEY);
		PropertiesRealmWithDefaultUsersFile realm = (PropertiesRealmWithDefaultUsersFile) environment
				.getObject("usersFileRealm", PropertiesRealmWithDefaultUsersFile.class);
		File configFile = new File(realm.getResourcePath().substring("file:".length()));
		String hashedPassword = hashPassword(password);

		Properties properties = new Properties();
		properties.setProperty("user." + username, hashedPassword + ", admin");
		properties.setProperty("role.admin", "*");
		try (FileOutputStream fos = new FileOutputStream(configFile);) {

			String comments = "Defining the admin user.\n   user.USERNAME = PASSWORD,ROLE";

			properties.store(fos, comments);

			realm.run();

			return true;
		} catch (IOException e) {
			LOG.error("Error in registry " + e.getMessage());
			return false;
		}
	}

	@Override
	public String getActualUsername() {
		String currentUsername = request.getRemoteUser();
		LOG.info("Actual user have username " + currentUsername);
		return currentUsername;
	}

	@Override
	public boolean logout() {
		Subject subject = ThreadContext.getSubject();
		if (getActualUsername() == null) {
			LOG.info("no user logged");
			return false;
		}
		subject.logout();
		LOG.info("Logout user with username " + getActualUsername());
		return true;
	}

	private String hashPassword(String password) {
		SecureRandomNumberGenerator randGenerator = new SecureRandomNumberGenerator();
		SimpleHash hash = new SimpleHash("SHA-256", password.toCharArray(), randGenerator.nextBytes(32));
		HashFormat hashFormat = (new DefaultHashFormatFactory()).getInstance(Shiro1CryptFormat.class.getName());
		return hashFormat.format(hash);
	}

	private String generateString(int size, char firstChar, char lastChar) {
		if((int) firstChar >= (int) lastChar)
    		return null;
		Random rand = new java.util.Random();
		return rand.ints(size, firstChar, lastChar + 1).mapToObj((ch) -> (char) ch)
				.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
	}

	

}
