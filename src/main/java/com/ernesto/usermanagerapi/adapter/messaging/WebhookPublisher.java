package com.ernesto.usermanagerapi.adapter.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ernesto.usermanagerapi.application.ports.drivens.DeliveryPublisher;
import com.ernesto.usermanagerapi.domain.patterns.Delivery;

/**
 * Publicador de webhooks que envía eventos HTTP al suscriptor.
 * Implementa el puerto driven DeliveryPublisher.
 *
 * @Async permite que el método publish() se ejecute en un hilo separado del
 * caller (CommandCreateUserUseCaseImpl). Esto es clave para el patrón
 * fire-and-forget del webhook: el caso de uso no espera la respuesta HTTP
 * del suscriptor, sigue su flujo inmediatamente.
 *
 * Spring maneja @Async con un TaskExecutor internamente. Si no se configura
 * uno explícito, Spring Boot usa SimpleAsyncTaskExecutor (crea un hilo nuevo
 * por cada tarea). Para entornos productivos conviene configurar un
 * ThreadPoolTaskExecutor con límites definidos.
 *
 * La URL del suscriptor se inyecta via @Value desde application.yaml.
 * @Value resuelve la expresión ${...} en el ApplicationContext, leyendo
 * de application.yaml, variables de entorno, o secrets según el orden de
 * precedencia definido por Spring.
 */
@Service
@Scope("singleton")
public class WebhookPublisher implements DeliveryPublisher {

    private final RestTemplate restTemplate;
    private final String suscriptorUrl;
    private static final Logger log = LoggerFactory.getLogger(WebhookPublisher.class);

    /*
     * Constructor explícito (no @AllArgsConstructor) porque suscriptorUrl
     * requiere @Value para resolverse desde application.properties.
     * Lombok no procesa anotaciones en parámetros generados automáticamente
     * con @AllArgsConstructor, por lo que necesitamos el constructor manual.
     */
    public WebhookPublisher(RestTemplate restTemplate,
                            @Value("${webhook.suscriptor.url}") String suscriptorUrl) {
        this.restTemplate = restTemplate;
        this.suscriptorUrl = suscriptorUrl;
    }

    /**
     * Envía un Delivery<TEvent> al suscriptor vía HTTP POST.
     *
     * @Async hace que este método retorne inmediatamente al caller.
     * La ejecución real ocurre en un hilo separado del pool de Spring.
     * Si el suscriptor responde 200 OK, se loguea éxito.
     * Si hay cualquier excepción (timeout, conexión rechazada, etc.),
     * se loguea el error sin propagarlo para no afectar al flujo principal.
     *
     * @param event El Delivery a enviar. Jackson lo serializa a JSON
     *              automáticamente en el cuerpo de la petición HTTP.
     * @param <TEvent> Tipo del payload del evento
     */
    @Override
    @Async
    public <TEvent> void publish(Delivery<TEvent> event) {
        try {
            restTemplate.postForEntity(suscriptorUrl, event, String.class);
            log.info("Webhook enviado correctamente — eventType={} suscriptor={} eventId={}",
                    event.getType(), suscriptorUrl, event.getId());
        } catch (Exception e) {
            log.error("Error al enviar webhook — eventType={} suscriptor={} eventId={}: {}",
                    event.getType(), suscriptorUrl, event.getId(), e.getMessage());
        }
    }

}
