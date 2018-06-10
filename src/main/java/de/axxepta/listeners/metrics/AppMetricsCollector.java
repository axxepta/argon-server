package de.axxepta.listeners.metrics;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class AppMetricsCollector {

    private static final Logger LOG = Logger.getLogger(AppMetricsCollector.class);
    
    public static void startReport(MetricRegistry metricRegistry) {
       ConsoleReporter reporter = ConsoleReporter.forRegistry(metricRegistry)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();

        reporter.start(30, TimeUnit.SECONDS);
        
        LOG.info("Start metric collector");
    }
}
