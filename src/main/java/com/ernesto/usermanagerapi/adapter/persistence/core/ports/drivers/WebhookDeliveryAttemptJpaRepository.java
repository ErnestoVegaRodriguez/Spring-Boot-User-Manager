package com.ernesto.usermanagerapi.adapter.persistence.core.ports.drivers;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ernesto.usermanagerapi.adapter.persistence.core.schemas.WebhookDeliveryAttemptSchema;

public interface WebhookDeliveryAttemptJpaRepository
        extends JpaRepository<WebhookDeliveryAttemptSchema, Long> {

    Optional<WebhookDeliveryAttemptSchema> findByRequestId(UUID requestId);
}
