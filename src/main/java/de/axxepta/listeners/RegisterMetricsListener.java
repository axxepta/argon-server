package de.axxepta.listeners;

import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;
import com.codahale.metrics.servlets.MetricsServlet;

@WebListener
public class RegisterMetricsListener implements ServletContextListener {

	private static final Logger LOG = LoggerFactory.getLogger(RegisterMetricsListener.class);

	public static final MetricRegistry metric = new MetricRegistry();
	
	public final HealthCheckRegistry health = new HealthCheckRegistry();
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		LOG.info("Start argon server application");
		
		event.getServletContext().setAttribute(MetricsServlet.METRICS_REGISTRY, metric);
		event.getServletContext().setAttribute(HealthCheckServlet.HEALTH_CHECK_REGISTRY, health);	
		
		startReport();
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		LOG.info("Application shutdown");
	}

	private void startReport() {
		ConsoleReporter reporterConsole = ConsoleReporter.forRegistry(metric).
				convertRatesTo(TimeUnit.SECONDS).filter(MetricFilter.ALL)
				.convertDurationsTo(TimeUnit.MILLISECONDS).build();

		
		reporterConsole.start(30, TimeUnit.SECONDS);

		Slf4jReporter slfReporter = Slf4jReporter.forRegistry(metric).
				outputTo(LOG).convertRatesTo(TimeUnit.SECONDS).filter(MetricFilter.ALL)
				.convertDurationsTo(TimeUnit.MILLISECONDS).build();
		
		slfReporter.start(30, TimeUnit.SECONDS);
		
		LOG.info("Start metric collector");
	}
}
