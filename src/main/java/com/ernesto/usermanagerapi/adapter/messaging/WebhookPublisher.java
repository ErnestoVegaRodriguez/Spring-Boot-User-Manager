package com.ernesto.usermanagerapi.adapter.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ernesto.usermanagerapi.application.ports.drivens.EventPublisher;
import com.ernesto.usermanagerapi.domain.patterns.Request;

import lombok.AllArgsConstructor;

@Service
@Scope("singleton")
@AllArgsConstructor
public class WebhookPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(WebhookPublisher.class);

    private final RestTemplate restTemplate;

    @Override
    @Async
    public <TEvent> void notifyEvent(String suscriptor, Request<TEvent> event) {
        try {
            restTemplate.postForEntity(suscriptor, event, String.class);
            log.info("Webhook enviado correctamente — eventType={} suscriptor={} eventId={}",
                    event.getEventType(), suscriptor, event.getId());
        } catch (Exception e) {
            log.error("Error al enviar webhook — eventType={} suscriptor={} eventId={}: {}",
                    event.getEventType(), suscriptor, event.getId(), e.getMessage());
        }
    }

}
