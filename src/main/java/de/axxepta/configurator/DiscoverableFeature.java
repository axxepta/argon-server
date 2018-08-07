package de.axxepta.configurator;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Populator;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ClasspathDescriptorFileFinder;
import org.glassfish.hk2.utilities.DuplicatePostProcessor;

public class DiscoverableFeature implements Feature {

	private final ServiceLocator scopedLocator;

	@Inject
    private DiscoverableFeature(ServiceLocator scopedLocator) {
        this.scopedLocator = scopedLocator;
    }
    
    @Override
    public boolean configure(FeatureContext context) {     
        DynamicConfigurationService dcs = scopedLocator.getService(DynamicConfigurationService.class);
        Populator populator = dcs.getPopulator();
        try {
            populator.populate(new ClasspathDescriptorFileFinder(this.getClass().getClassLoader()),
                    new DuplicatePostProcessor());
        } catch (IOException | MultiException ex) {
            Logger.getLogger(DiscoverableFeature.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
}
