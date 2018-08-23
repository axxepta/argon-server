package de.axxepta.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
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

	private Map<String, List<String>> mapServicesAcceptedIP;

	@PostConstruct
	public void loadRules() {
		LOG.info("load rules for security filter");
		mapServicesAcceptedIP = new HashMap<>();
		List<String> restricted = new ArrayList<>();
		restricted.add("127.0.0.1");
		mapServicesAcceptedIP.put("auth-services", null);
		mapServicesAcceptedIP.put("document-services", null);
		mapServicesAcceptedIP.put("database-services", restricted);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String pathInfo = ((HttpServletRequest) request).getPathInfo();
		String ip = request.getRemoteAddr();
		LOG.info("Path request " + pathInfo + " from IP " + ip);

		if (pathInfo != null && ((HttpServletRequest) request).getMethod().equalsIgnoreCase("POST")) {
			for (Map.Entry<String, List<String>> entry : mapServicesAcceptedIP.entrySet()) {
				if (pathInfo.contains(entry.getKey())) {
					List<String> restrictedIPList = entry.getValue();

					if (restrictedIPList == null) {
						String generateRedirectUrl = "/services" + pathInfo;

						RequestDispatcher dispatcher = request.getRequestDispatcher(generateRedirectUrl);
						dispatcher.forward(request, response);
						return;
					}
					else {
						for(String ipElem : restrictedIPList) {
							if(ip.equals(ipElem)) {
								String generateRedirectUrl = "/services" + pathInfo;

								RequestDispatcher dispatcher = request.getRequestDispatcher(generateRedirectUrl);
								dispatcher.forward(request, response);
								return;
							}
						}
					}
				}
			}
		}

		super.doFilter(request, response, chain);

	}

}
