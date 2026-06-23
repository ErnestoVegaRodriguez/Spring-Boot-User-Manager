package com.ernesto.usermanagerapi.adapter.persistence.adapter.repositories.mappers;

import org.springframework.stereotype.Component;

import com.ernesto.usermanagerapi.adapter.persistence.core.schemas.DeliveryAttemptSchema;
import com.ernesto.usermanagerapi.domain.entities.DeliveryAttempt;

/**
 * Mapper bidireccional entre DeliveryAttempt (dominio) y DeliveryAttemptSchema (JPA).
 *
 * La conversión es plana (campo a campo) porque ambas clases tienen la misma
 * estructura. No hay lógica de negocio aquí — eso pertenece a los factory methods
 * del dominio (create/reconstitute).
 */
@Component
public class WebhookDeliveryAttemptMapper {

    /**
     * Convierte una entidad de dominio a un schema JPA para persistir.
     */
    public DeliveryAttemptSchema toSchema(DeliveryAttempt domain) {
        DeliveryAttemptSchema schema = new DeliveryAttemptSchema();

        // Si el dominio ya tiene id (reconstituido desde BD), lo copiamos
        // para que JPA haga UPDATE en lugar de INSERT.
        // Si es null (recién creado), JPA genera uno nuevo vía @GeneratedValue.
        schema.setId(domain.getId());
        schema.setRequestId(domain.getRequestId());
        schema.setEventType(domain.getEventType());
        schema.setPayload(domain.getPayload());
        schema.setEmittedAt(domain.getEmittedAt());
        schema.setReceivedAt(domain.getReceivedAt());
        schema.setStatus(domain.getStatus());

        return schema;
    }

    /**
     * Convierte un schema JPA a una entidad de dominio.
     * Delega en DeliveryAttempt.reconstitute() para construir la instancia.
     */
    public DeliveryAttempt toDomain(DeliveryAttemptSchema schema) {
        return DeliveryAttempt.reconstitute(
                schema.getId(),
                schema.getRequestId(),
                schema.getEventType(),
                schema.getPayload(),
                schema.getEmittedAt(),
                schema.getReceivedAt(),
                schema.getStatus());
    }

}
