package com.ernesto.usermanagerapi.adapter.persistence.core.schemas;

import java.time.Instant;
import java.util.UUID;

import com.ernesto.usermanagerapi.domain.enums.DeliveryStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Schema JPA para la tabla webhook_delivery_attempts.
 * Mapea uno a uno los campos de DeliveryAttempt del dominio.
 *
 * No confundir con Delivery<TPayload> del dominio. Este es el modelo
 * de base de datos del suscriptor, no el mensaje en tránsito.
 */
@Entity
@Table(schema = "identity", name = "webhook_delivery_attempts",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_webhook_request_id",
           columnNames = "request_id"))
@NoArgsConstructor
@Getter
@Setter
public class DeliveryAttemptSchema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /*
     * request_id tiene unique constraint para garantizar idempotencia
     * a nivel de base de datos. Si el mismo requestId llega dos veces,
     * la BD rechaza el duplicado como capa adicional de seguridad.
     */
    @Column(name = "request_id", nullable = false, unique = true)
    private UUID requestId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    /*
     * payload se almacena como TEXT porque el contenido serializado
     * puede ser extenso (todo un UserResponse en JSON).
     */
    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    /*
     * emitted_at: momento en que el publisher emitió el evento (copia de Delivery.emittedAt).
     * received_at: momento en que el suscriptor recibió la request HTTP.
     * Ambos se almacenan como TIMESTAMP WITH TIME ZONE gracias a que
     * Instant se mapea automáticamente a TIMESTAMP_UTC en Hibernate 6+.
     */
    @Column(name = "emitted_at", nullable = false)
    private Instant emittedAt;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    /*
     * @Enumerated(EnumType.STRING) almacena el nombre del enum ("PENDING",
     * "DELIVERED", "FAILED") en la base de datos en lugar de un número ordinal.
     *
     * STRING es preferible a ORDINAL porque:
     * - La columna es legible directamente en la BD
     * - Agregar nuevos valores al enum no cambia los ordinales existentes
     * - Si se reordenan los valores del enum, los datos existentes no se corrompen
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeliveryStatus status;

}
