package de.axxepta.services.implementations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.crypto.hash.format.DefaultHashFormatFactory;
import org.apache.shiro.crypto.hash.format.HashFormat;
import org.apache.shiro.crypto.hash.format.Shiro1CryptFormat;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.env.EnvironmentLoader;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.jvnet.hk2.annotations.Service;

import de.axxepta.dao.implementations.SessionCacheDAOImpl;
import de.axxepta.services.interfaces.IAuthUserService;
import ro.sync.auth.PropertiesRealmWithDefaultUsersFile;
import ro.sync.ecss.extensions.api.webapp.license.UserInfo;
import ro.sync.ecss.extensions.api.webapp.license.UserManager;
import ro.sync.ecss.extensions.api.webapp.license.UserManagerSingleton;

@Service(name = "UserAuthImplementation")
@Singleton
public class AuthUserServiceImpl implements IAuthUserService {

	private static final Logger LOG = Logger.getLogger(AuthUserServiceImpl.class);

	@Context
	private HttpServletRequest request;

	private Map <String , String> tokensMap;
	
	public AuthUserServiceImpl() {
		LOG.info("Startup application from session DAO listener");
		DefaultSecurityManager securityManager = (DefaultSecurityManager) SecurityUtils.getSecurityManager();
		CacheManager cacheManager = new EhCacheManager();
		securityManager.setCacheManager(cacheManager);
		SessionDAO sessionDAO = new SessionCacheDAOImpl();	
		DefaultSessionManager sessionManager = (DefaultSessionManager) securityManager.getSessionManager();
		sessionManager.setSessionDAO(sessionDAO);
		sessionManager.setDeleteInvalidSessions(true);
		sessionManager.setSessionValidationSchedulerEnabled(true);
		sessionManager.setGlobalSessionTimeout(18000000);// set validity to 30min
		sessionManager.validateSessions();
		
		Collection <Session> sessions = sessionManager.getSessionDAO().getActiveSessions();
		tokensMap = new HashMap <>();
		for(Session session : sessions) {
			String username = (String) session.getAttribute("username");
			String password = (String) session.getAttribute("password");		
		    tokensMap.put(username,  password);
		}
	}
	
	@PostConstruct
	private void initAuth() {
		for(Map.Entry<String, String> entry: tokensMap.entrySet()) {
			String username = entry.getKey();
			String password = entry.getValue();
			if(username != null && password != null) {
				register(username, password);
			}
		}
	}
	@Override
	public boolean login(String username, String password) {
		Subject subject = SecurityUtils.getSubject();

		if (!subject.isAuthenticated()) {
			try {
				subject.login(new UsernamePasswordToken(username, password, true));
				LOG.info("Login user with username " + username);
				return true;
			} catch (AuthenticationException e) {
				LOG.error(e.getMessage());
			}
		}

		return false;
	}

	@Override
	public boolean register(String username, String password) {
		IniWebEnvironment environment = (IniWebEnvironment) request.getServletContext()
				.getAttribute(EnvironmentLoader.ENVIRONMENT_ATTRIBUTE_KEY);
		PropertiesRealmWithDefaultUsersFile realm = (PropertiesRealmWithDefaultUsersFile) environment.getObject("usersFileRealm",
				PropertiesRealmWithDefaultUsersFile.class);
		File configFile = new File(realm.getResourcePath().substring("file:".length()));
		String hashedPassword = hashPassword(password);

		Properties properties = new Properties();
		properties.setProperty("user." + username, hashedPassword + ", admin");
		properties.setProperty("role.admin", "*");
		try (FileOutputStream fos = new FileOutputStream(configFile);) {

			String comments = "Admin user from rest services";

			properties.store(fos, comments);

			realm.run();

			
		} catch (IOException e) {
			LOG.error("Error in registry " + e.getMessage());
			return false;
		}
		
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		session.setAttribute("username", username);
		session.setAttribute("password", password);
		return true;
	}

	@Override
	public String getActualUsername() {
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		LOG.info("Originally from " + session.getHost() + " have key " + session.getId().toString());
		if (subject.isAuthenticated()) {
			String currentUsername = request.getRemoteUser();
			LOG.info("Actual user have username " + currentUsername);
			return currentUsername;
		}
		return null;
	}

	@Override
	public Boolean hasRoleActualUser(String role) {
		Subject subject = SecurityUtils.getSubject();
		if (!subject.isAuthenticated()) {
			LOG.info("not exist a current user authenticated");
			return null;
		}
		LOG.info("Check role " + role + " for user with username " + subject.getSession().getAttribute("username"));
		return subject.hasRole(role);
	}

	@RequiresUser
	public String changePasswordActualLoginUser(String password) {
		Subject subject = SecurityUtils.getSubject();
		if (!subject.isAuthenticated()) {
			LOG.info("not exist a current user authenticated");
			return null;
		}
		if (password == null || password.isEmpty())
			password = generateString(15, 'A', 'z');
		String currentUsername = request.getRemoteUser();

		LOG.info("User have username " + currentUsername + " change password to " + password);

		UsernamePasswordToken token = new UsernamePasswordToken(currentUsername, password);
		token.setRememberMe(true);
		subject.logout();
		register(new String(token.getUsername()), new String(token.getPassword()));
		subject.login(token);

		return password;
	}

	@Override
	public boolean logout() {
		Subject subject = ThreadContext.getSubject();
		if (getActualUsername() == null) {
			LOG.info("no user logged");
			return false;
		}
		subject.logout();
		LOG.info("Logout user ");
		return true;
	}

	@Override
	public List <UserInfo> getLoggedUsersId() {
		UserManager userManager = UserManagerSingleton.getInstance();
		return userManager.getAllUsers();
	}
	
	private String hashPassword(String password) {
		SecureRandomNumberGenerator randGenerator = new SecureRandomNumberGenerator();
		SimpleHash hash = new SimpleHash("SHA-256", password.toCharArray(), randGenerator.nextBytes(32));
		HashFormat hashFormat = (new DefaultHashFormatFactory()).getInstance(Shiro1CryptFormat.class.getName());
		return hashFormat.format(hash);
	}

	private String generateString(int size, char firstChar, char lastChar) {
		if ((int) firstChar >= (int) lastChar)
			return null;
		Random rand = new java.util.Random();
		return rand.ints(size, firstChar, lastChar + 1).mapToObj((ch) -> (char) ch)
				.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
	}
	
}
