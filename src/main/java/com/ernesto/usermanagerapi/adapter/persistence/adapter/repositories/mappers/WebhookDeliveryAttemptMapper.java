package com.ernesto.usermanagerapi.adapter.persistence.adapter.repositories.mappers;

import org.springframework.stereotype.Component;

import com.ernesto.usermanagerapi.adapter.persistence.core.schemas.WebhookDeliveryAttemptSchema;
import com.ernesto.usermanagerapi.domain.entities.WebhookDeliveryAttempt;

@Component
public class WebhookDeliveryAttemptMapper {

    public WebhookDeliveryAttemptSchema toSchema(WebhookDeliveryAttempt domain) {
        WebhookDeliveryAttemptSchema schema = new WebhookDeliveryAttemptSchema();
        schema.setRequestId(domain.getRequestId());
        schema.setEventType(domain.getEventType());
        schema.setPayload(domain.getPayload());
        schema.setCreatedAt(domain.getCreatedAt());
        schema.setStatus(domain.getStatus());
        return schema;
    }

    public WebhookDeliveryAttempt toDomain(WebhookDeliveryAttemptSchema schema) {
        return WebhookDeliveryAttempt.reconstitute(
                schema.getId(),
                schema.getRequestId(),
                schema.getEventType(),
                schema.getPayload(),
                schema.getCreatedAt(),
                schema.getStatus());
    }
}
