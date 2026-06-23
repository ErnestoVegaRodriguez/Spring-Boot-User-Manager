package com.ernesto.usermanagerapi.adapter.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ernesto.usermanagerapi.application.ports.drivens.DeliveryAttemptRepository;
import com.ernesto.usermanagerapi.application.ports.drivens.DeliveryReceptor;
import com.ernesto.usermanagerapi.domain.entities.DeliveryAttempt;

/**
 * Procesador de mensajes de la cola RabbitMQ.
 * Implementa DeliveryReceptor para mantener la coherencia con el puerto driven.
 *
 * @RabbitListener: anotación que registra un consumer en RabbitMQ para la cola
 * especificada. Cuando un mensaje llega a "pedidos.creado.queue", Spring AMQP:
 *
 * 1. Recibe el mensaje del exchange (entrega push, no polling)
 * 2. Deserializa el JSON usando Jackson2JsonMessageConverter (configurado en
 *    RabbitMQConfig), que convierte el cuerpo del mensaje a DeliveryAttempt
 * 3. Llama automáticamente al método anotado con el objeto ya hidratado
 * 4. Confirma el mensaje (auto-ack) si el método no lanza excepción
 *
 * Ventajas del consumer basado en @RabbitListener:
 * - Sin loops, sin polling, sin manejo manual de conexiones
 * - RabbitMQ notifica al consumer cuando hay mensajes disponibles
 * - Si el procesador falla, el mensaje NO se confirma y queda en la cola
 *   (con auto-ack por defecto; se puede configurar manual ack para más control)
 * - Si el procesador está caído, los mensajes se acumulan en la cola y se
 *   entregan cuando vuelve a estar disponible
 */
@Component
public class WebhookReceptor implements DeliveryReceptor {

    private final DeliveryAttemptRepository attemptRepo;
    private static final Logger log = LoggerFactory.getLogger(WebhookReceptor.class);

    public WebhookReceptor(DeliveryAttemptRepository attemptRepo) {
        this.attemptRepo = attemptRepo;
    }

    /**
     * Procesa un DeliveryAttempt recibido de la cola RabbitMQ.
     * Cambia el estado a DELIVERED, persiste el cambio y loguea la información
     * del evento ya procesado.
     *
     * @param attempt El DeliveryAttempt deserializado desde el JSON de la cola.
     *                Jackson2JsonMessageConverter lo reconstruye usando
     *                DeliveryAttempt.reconstitute() con @JsonCreator.
     */
    @Override
    @RabbitListener(queues = "pedidos.creado.queue")
    public void procesar(DeliveryAttempt attempt) {

        // ─────────────────────────────────────────────────────────
        // Paso 1: Marcar como entregado
        // ─────────────────────────────────────────────────────────
        /*
         * Cambia el estado interno del objeto a DELIVERED.
         * El objeto ya fue persistido como PENDING por IdempotentDeliveryService
         * antes de encolarlo. Ahora actualizamos el registro existente vía JPA save()
         * que hace un UPDATE porque el DeliveryAttempt ya tiene id.
         */
        attempt.markDelivered();

        // ─────────────────────────────────────────────────────────
        // Paso 2: Persistir el cambio de estado
        // ─────────────────────────────────────────────────────────
        /*
         * attemptRepo.add() con un attempt que ya tiene id (no null) hace un
         * UPDATE en lugar de INSERT gracias a JPA's save() que verifica
         * si la entidad existe por su ID.
         *
         * Es importante que el DeliveryAttempt que viaja por RabbitMQ incluya
         * el id generado por la BD, y eso ocurre porque IdempotentDeliveryService
         * persiste primero (genera el id) y después encola el attempt con id incluido.
         */
        attemptRepo.add(attempt);

        // ─────────────────────────────────────────────────────────
        // Paso 3: Log del evento procesado
        // ─────────────────────────────────────────────────────────
        /*
         * Se registra la información completa del evento para trazabilidad.
         * En un entorno productivo esto podría disparar alertas, alimentar
         * un dashboard o generar métricas.
         */
        log.info("Evento procesado — requestId={}, eventType={}, emittedAt={}, receivedAt={}, status={}",
                attempt.getRequestId(),
                attempt.getEventType(),
                attempt.getEmittedAt(),
                attempt.getReceivedAt(),
                attempt.getStatus());
    }

}
