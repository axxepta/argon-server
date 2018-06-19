package de.axxepta.resteasy;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;

import de.axxepta.listeners.metrics.AppMetricsCollector;
import de.axxepta.listeners.metrics.MetricsServletContextListener;
import de.axxepta.resteasy.services.AppHealthService;
import de.axxepta.resteasy.services.AppTestServices;
import de.axxepta.resteasy.services.AuthService;

@ApplicationPath("/argon-rest")
public class ArgonServerApplication extends Application{

	private static final Logger LOG = Logger.getLogger(ArgonServerApplication.class);
	
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();
	
    public ArgonServerApplication(@Context ServletContext context) throws InvalidKeyException, NoSuchAlgorithmException, 
    NoSuchPaddingException, UnsupportedEncodingException {
    	LOG.info("Start argon server application");
    	
        singletons.add(new AppTestServices());
        singletons.add(new AppHealthService());
        singletons.add(new AuthService());
        
        AppMetricsCollector.startReport(MetricsServletContextListener.METRIC_REGISTRY);
    }
 
    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
    
    @Override
    public Set<Class<?>> getClasses(){
        return this.empty;
    }  
}
