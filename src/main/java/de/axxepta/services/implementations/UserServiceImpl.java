package de.axxepta.services.implementations;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.jvnet.hk2.annotations.Service;

import de.axxepta.services.interfaces.UserServiceI;

@Service(name = "UserAuthImplementation")
@Singleton
public class UserServiceImpl implements UserServiceI {

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
		} catch (AuthenticationException e) {

		}

		return false;
	}

	@Override
	public boolean register(String username, String password) {
		Subject currentUser = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		token.setRememberMe(true);
		return false;
	}

	@Override
	public String getActualUser() {
		String currentUsername = request.getRemoteUser();
		LOG.info("Actual user have username " + currentUsername);
		return currentUsername;
	}

	@Override
	public boolean logout() {
		Subject subject = ThreadContext.getSubject();
		if (subject == null) {
			LOG.info("no user logged");
			return false;
		}
		subject.logout();
		LOG.info("Logout user with username " + getActualUser());
		return true;
	}

}
