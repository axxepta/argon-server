package de.axxepta.configurator;

import java.io.IOException;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Populator;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ClasspathDescriptorFileFinder;
import org.glassfish.hk2.utilities.DuplicatePostProcessor;
import org.glassfish.jersey.InjectionManagerProvider;
import org.glassfish.jersey.internal.inject.InjectionManager;

@Provider
public class DiscoverableFeature implements Feature {

	private static final Logger LOG = Logger.getLogger(DiscoverableFeature.class);
    
    @Override
    public boolean configure(FeatureContext context) { 
    	LOG.info("Config discovery feature");
    	InjectionManager injectionManager = InjectionManagerProvider.getInjectionManager(context);
    	ServiceLocator locator = injectionManager.getInstance(ServiceLocator.class);
    	DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        Populator populator = dcs.getPopulator();
        try {
            populator.populate(new ClasspathDescriptorFileFinder(this.getClass().getClassLoader()),
                    new DuplicatePostProcessor());
        } catch (IOException | MultiException ex) {
            LOG.error(ex.getMessage());
        }
        
        return true;
    }
    
}
