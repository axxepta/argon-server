package de.axxepta.health;

import com.codahale.metrics.health.HealthCheck;

public class HealthCheckImpl extends HealthCheck {

    public HealthCheckImpl() {
        super();
    }

    @Override
    public Result check() throws Exception {        
        return Result.healthy();
    }
}