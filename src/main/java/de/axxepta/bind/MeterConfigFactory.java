package de.axxepta.bind;


import org.glassfish.hk2.api.Factory;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

import de.axxepta.listeners.RegisterMetricsListener;

public class MeterConfigFactory implements Factory<Meter> {

	private final MetricRegistry metrics = RegisterMetricsListener.metric;
	
	public final Meter meter;
	
	public MeterConfigFactory() {
		meter = metrics.meter("requests");
		meter.mark(0);
	}
	
	@Override
	public Meter provide() {
		return  meter;
	}

	@Override
	public void dispose(Meter instance) {
	}
	
}
