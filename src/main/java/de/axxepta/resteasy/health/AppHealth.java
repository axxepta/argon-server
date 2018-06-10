package de.axxepta.resteasy.health;

import com.codahale.metrics.health.HealthCheck;

public class AppHealth extends HealthCheck {

    public AppHealth() {
        super();
    }

    @Override
    public Result check() throws Exception {        
        return Result.healthy();
    }
}