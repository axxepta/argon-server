package de.axxepta.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import ro.sync.auth.CsrfFilter;

@WebFilter(filterName = "FilterRestEASY", urlPatterns = { "/*" })
public class SecurityFilter extends CsrfFilter {

	private static final Logger LOG = Logger.getLogger(SecurityFilter.class);

	private static final String[] PATH_NAMES = { "auth-services", "documents-services", "database-services" };

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String pathInfo = ((HttpServletRequest) request).getPathInfo();

		LOG.info("Path request " + pathInfo);
		
		if (pathInfo != null && ((HttpServletRequest) request).getMethod().equalsIgnoreCase("POST")) {
			for (String name : PATH_NAMES) {
				if (pathInfo.contains(name)) {
					String generateRedirectUrl = "/services" + pathInfo;
					LOG.info("Contains rest service " + name + " redirect to " + generateRedirectUrl);

					RequestDispatcher dispatcher = request.getRequestDispatcher(generateRedirectUrl);				
					dispatcher.forward(request, response);
					return;
				}
			}
		}

		super.doFilter(request, response, chain);

	}

}
