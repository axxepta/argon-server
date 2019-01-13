package de.axxepta.bind;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;

import com.codahale.metrics.Meter;

public class MeterConfigBinder extends AbstractBinder {

	@Override
	protected void configure() {
		bindFactory(MeterConfigFactory.class).to(Meter.class).proxy(true).proxyForSameScope(false).in(RequestScoped.class);		
	}

}
