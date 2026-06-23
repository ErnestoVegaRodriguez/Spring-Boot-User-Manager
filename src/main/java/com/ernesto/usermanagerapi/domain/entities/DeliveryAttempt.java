package com.ernesto.usermanagerapi.domain.entities;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ernesto.usermanagerapi.domain.enums.DeliveryStatus;

import lombok.Getter;

/**
 * Representa el registro de UN intento de delivery dentro del suscriptor.
 *
 * Mientras que Delivery<TPayload> es el mensaje en tránsito (viaja por HTTP/JSON),
 * DeliveryAttempt es la representación persistida de ese mensaje una vez que
 * el suscriptor lo recibe, lo valida y lo encola para procesamiento asíncrono.
 *
 * Diferencia clave entre id y requestId:
 * - id:  Es el identificador auto-incremental de la base de datos del suscriptor.
 *        Solo existe dentro del sistema del suscriptor. Se genera al hacer INSERT.
 * - requestId: Es el UUID del evento generado por el publisher (Delivery.id).
 *             Viaja con el mensaje y se usa para IDEMPOTENCIA: si el publisher
 *             reintenta (timeout, error de red) y el mismo requestId llega dos
 *             veces, el suscriptor detecta el duplicado y no procesa de nuevo.
 */
@Getter
public class DeliveryAttempt {

    private Long id;
    private UUID requestId;
    private String eventType;
    private String payload;
    private Instant emittedAt;
    private Instant receivedAt;
    private DeliveryStatus status;

    /*
     * Constructor privado de creación. Solo se invoca desde create().
     * Establece receivedAt = ahora (el momento en que el suscriptor recibe el evento)
     * y status = PENDING (aún no procesado por el EventProcessor).
     */
    private DeliveryAttempt(UUID requestId, String eventType, String payload, Instant emittedAt) {
        this.requestId = requestId;
        this.eventType = eventType;
        this.payload = payload;
        this.emittedAt = emittedAt;
        this.receivedAt = Instant.now();
        this.status = DeliveryStatus.PENDING;
    }

    // No-arg constructor requerido por JPA y Jackson como fallback,
    // pero el mecanismo principal de Jackson es @JsonCreator en reconstitute()
    private DeliveryAttempt() {
    }

    /**
     * Factory method de creación. Usar SIEMPRE este método para instancias nuevas.
     * Recibe el emittedAt que viene del Delivery original para preservar la
     * línea de tiempo completa del evento.
     */
    public static DeliveryAttempt create(UUID requestId, String eventType, String payload, Instant emittedAt) {
        return new DeliveryAttempt(requestId, eventType, payload, emittedAt);
    }

    /*
     * Factory method de reconstitución: crea un DeliveryAttempt a partir de datos
     * existentes (desde la BD o desde un mensaje JSON de RabbitMQ).
     *
     * Tiene doble propósito:
     * 1. Reconstituir desde la BD (DeliveryAttemptSchema → DeliveryAttempt)
     * 2. Deserializar desde RabbitMQ vía Jackson (@JsonCreator + @JsonProperty)
     *
     * Se usa @JsonCreator en lugar de un constructor anotado porque los factory
     * methods estáticos son más explícitos y permiten tener lógica de validación
     * en el futuro sin cambiar la firma.
     */
    @JsonCreator
    public static DeliveryAttempt reconstitute(
            @JsonProperty("id") Long id,
            @JsonProperty("requestId") UUID requestId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("payload") String payload,
            @JsonProperty("emittedAt") Instant emittedAt,
            @JsonProperty("receivedAt") Instant receivedAt,
            @JsonProperty("status") DeliveryStatus status) {
        DeliveryAttempt attempt = new DeliveryAttempt();
        attempt.id = id;
        attempt.requestId = requestId;
        attempt.eventType = eventType;
        attempt.payload = payload;
        attempt.emittedAt = emittedAt;
        attempt.receivedAt = receivedAt;
        attempt.status = status;
        return attempt;
    }

    // ─────────────────────────────────────────────────────────────
    // Métodos de transición de estado
    // ─────────────────────────────────────────────────────────────

    /**
     * Marca el attempt como exitoso. Lo llama el EventProcessor
     * después de desencolar y procesar el mensaje correctamente.
     */
    public void markDelivered() {
        this.status = DeliveryStatus.DELIVERED;
    }

    /**
     * Marca el attempt como fallado. Para futuras implementaciones
     * con reintentos o dead-letter queues.
     */
    public void markFailed() {
        this.status = DeliveryStatus.FAILED;
    }

    /**
     * Verifica si este attempt ya fue entregado. Se usa en el servicio
     * de idempotencia para evitar reprocesar eventos duplicados.
     */
    public boolean isAlreadyDelivered() {
        return DeliveryStatus.DELIVERED.equals(status);
    }

}
