package com.ernesto.usermanagerapi.adapter.persistence.core.ports.drivers;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ernesto.usermanagerapi.adapter.persistence.core.schemas.DeliveryAttemptSchema;

public interface DeliveryAttemptJpaRepository
        extends JpaRepository<DeliveryAttemptSchema, Long> {

    Optional<DeliveryAttemptSchema> findByRequestId(UUID requestId);
}
