package com.ernesto.usermanagerapi.adapter.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StartupLogger implements ApplicationListener<WebServerInitializedEvent> {

    private static final Logger log = LoggerFactory.getLogger(StartupLogger.class);

    @Value("${server.port:8080}")
    private int port;

    @Value("${spring.application.name:app}")
    private String appName;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        log.info("==================================================");
        log.info("  Aplicación : {}", appName);
        log.info("  URL Local  : http://localhost:{}", port);
        log.info("  URL Docker : http://spring-api:{}", port);
        log.info("  Swagger UI : http://localhost:{}/swagger-ui.html", port);
        log.info("==================================================");
    }
}
