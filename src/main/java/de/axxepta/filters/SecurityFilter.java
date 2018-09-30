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

	private Map<String, Map<String, List<String>>> mapServicesAcceptedIP;

	@PostConstruct
	public void loadRules() {
		LOG.info("load rules for security filter");
		mapServicesAcceptedIP = new HashMap<>();

		Map<String, List<String>> mapMethodsIPUnrestrained = new HashMap<>();
		mapMethodsIPUnrestrained.put("POST", null);

		List<String> restricted = new ArrayList<>();
		restricted.add("127.0.0.1");
		Map<String, List<String>> mapMethodsIPRestricted1 = new HashMap<>();
		mapMethodsIPRestricted1.put("POST", restricted);
		Map<String, List<String>> mapMethodsIPRestricted2 = new HashMap<>();
		mapMethodsIPRestricted2.put("POST", restricted);
		mapMethodsIPRestricted2.put("DELETE", restricted);

		mapServicesAcceptedIP.put("auth-services", mapMethodsIPUnrestrained);
		mapServicesAcceptedIP.put("document-services", mapMethodsIPUnrestrained);
		mapServicesAcceptedIP.put("plugins", mapMethodsIPRestricted1);
		mapServicesAcceptedIP.put("databases-services", mapMethodsIPRestricted2);
		mapServicesAcceptedIP.put("application-directory-services", mapMethodsIPRestricted1);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String pathInfo = ((HttpServletRequest) request).getPathInfo();
		String ip = request.getRemoteAddr();
		LOG.info("Path request " + pathInfo + " from IP " + ip);

		if (pathInfo != null) {
			for (Map.Entry<String, Map<String, List<String>>> entry : mapServicesAcceptedIP.entrySet()) {
				String nameService = entry.getKey();
				Map<String, List<String>> mapMethodsIpRestrictions = entry.getValue();

				if (pathInfo.contains(nameService)) {
					for (Map.Entry<String, List<String>> entryMethodsRestrictions : mapMethodsIpRestrictions.entrySet()) {
						String method = entryMethodsRestrictions.getKey();
						if(((HttpServletRequest) request).getMethod().equalsIgnoreCase(method)) {
							List<String> restrictedIPList = entryMethodsRestrictions.getValue();
							if (restrictedIPList == null) {
								String generateRedirectUrl = "/services" + pathInfo;

								RequestDispatcher dispatcher = request.getRequestDispatcher(generateRedirectUrl);
								dispatcher.forward(request, response);
								return;
							} else {
								for (String ipElem : restrictedIPList) {
									if (ip.equals(ipElem)) {

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
			}
		}

		super.doFilter(request, response, chain);

	}

}
