package com.ernesto.usermanagerapi.application.services;

import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.ernesto.usermanagerapi.application.ports.drivens.DeliveryAttemptRepository;
import com.ernesto.usermanagerapi.domain.entities.DeliveryAttempt;

/**
 * Servicio de aplicación responsable de recibir un evento entrante, verificar
 * que no sea duplicado (idempotencia), persistirlo como PENDING y encolarlo
 * en RabbitMQ para procesamiento asíncrono.
 *
 * Este servicio NO tiene interfaz (es clase concreta) porque es un servicio
 * de aplicación interno, no un puerto. Los puertos se reservan para
 * abstracciones entre capas (driven/driver ports).
 *
 * Flujo:
 * 1. Recibe datos del evento (requestId, eventType, payload, emittedAt)
 * 2. Busca en BD por requestId → si existe, es duplicado, retorna sin acción
 * 3. Crea DeliveryAttempt con status PENDING y lo persiste
 * 4. Publica el DeliveryAttempt en RabbitMQ con routing key = eventType
 * 5. El EventProcessor (WebhookReceptor) lo desencola y cambia status a DELIVERED
 */
@Service
@Scope("singleton")
public class IdempotentDeliveryService {

    private final DeliveryAttemptRepository attemptRepo;
    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(IdempotentDeliveryService.class);

    public IdempotentDeliveryService(DeliveryAttemptRepository attemptRepo,
                                      RabbitTemplate rabbitTemplate) {
        this.attemptRepo = attemptRepo;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Intenta encolar un delivery entrante.
     *
     * @param requestId  Identificador único del evento (generado por el publisher).
     *                   Es el mismo Delivery.id. Sirve para idempotencia.
     * @param eventType  Tipo de evento como string (ej: "CREATED_USER").
     *                   Se usa como routing key para el exchange direct.
     * @param payload    Payload del evento serializado a JSON (ej: UserResponse).
     * @param emittedAt  Marca de tiempo de emisión del publisher (Delivery.emittedAt).
     *
     * @return true si se encoló correctamente o era duplicado; nunca lanza excepción
     *         controlada para que el controller responda rápido.
     */
    public boolean tryEnqueue(UUID requestId, String eventType, String payload, Instant emittedAt) {

        // ─────────────────────────────────────────────────────────
        // Paso 1: Verificación de idempotencia
        // ─────────────────────────────────────────────────────────
        /*
         * Si el requestId ya existe en BD, significa que este mensaje ya fue
         * recibido antes. Esto puede pasar si el publisher reintenta por timeout
         * o error de red. En lugar de duplicar el mensaje, se ignora.
         *
         * La BD también tiene un UNIQUE CONSTRAINT sobre request_id como
         * capa adicional de defensa, pero la chequeamos primero para evitar
         * excepciones de integridad y logs de error innecesarios.
         */
        var existing = attemptRepo.findByRequestId(requestId);
        if (existing.isSuccess()) {
            log.info("Mensaje duplicado detectado — requestId={}, se ignora", requestId);
            return true;
        }

        // ─────────────────────────────────────────────────────────
        // Paso 2: Creación y persistencia del DeliveryAttempt
        // ─────────────────────────────────────────────────────────
        /*
         * DeliveryAttempt.create() establece:
         * - status = PENDING
         * - receivedAt = Instant.now() (momento exacto de recepción)
         * - id = null (JPA lo generará vía @GeneratedValue)
         */
        var attempt = DeliveryAttempt.create(requestId, eventType, payload, emittedAt);
        attempt = attemptRepo.add(attempt);

        // ─────────────────────────────────────────────────────────
        // Paso 3: Publicación en RabbitMQ
        // ─────────────────────────────────────────────────────────
        /*
         * Se publica el DeliveryAttempt completo en el exchange direct.
         * La routing key es eventType (ej: "CREATED_USER"), que coincide
         * con el binding de la cola "pedidos.creado.queue".
         *
         * RabbitTemplate.convertAndSend() serializa el objeto a JSON usando
         * Jackson2JsonMessageConverter (configurado en RabbitMQConfig).
         *
         * Es importante publicar DESPUÉS de persistir para evitar el escenario
         * donde el mensaje llega a la cola pero el attempt no está en BD
         * (el EventProcessor fallaría al no encontrarlo).
         */
        rabbitTemplate.convertAndSend("webhook.exchange", eventType, attempt);

        log.info("Delivery encolado — requestId={}, eventType={}", requestId, eventType);
        return true;
    }

}
