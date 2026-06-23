package com.ernesto.usermanagerapi.adapter.persistence.adapter.repositories;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import lombok.AllArgsConstructor;
import com.ernesto.usermanagerapi.adapter.persistence.adapter.repositories.mappers.WebhookDeliveryAttemptMapper;
import com.ernesto.usermanagerapi.adapter.persistence.core.ports.drivers.DeliveryAttemptJpaRepository;
import com.ernesto.usermanagerapi.domain.entities.DeliveryAttempt;
import com.ernesto.usermanagerapi.domain.enums.ErrorCode;
import com.ernesto.usermanagerapi.domain.exceptions.NotFoundException;
import com.ernesto.usermanagerapi.domain.patterns.Result;
import com.ernesto.usermanagerapi.application.ports.drivens.DeliveryAttemptRepository;

@Repository
@AllArgsConstructor
public class DeliveryAttemptRepositoryAdapter implements DeliveryAttemptRepository {

    private final DeliveryAttemptJpaRepository jpaRepository;
    private final WebhookDeliveryAttemptMapper mapper;

    @Override
    public DeliveryAttempt add(DeliveryAttempt attempt) {
        var schema = mapper.toSchema(attempt);
        var saved = jpaRepository.save(schema);
        return mapper.toDomain(saved);
    }

    @Override
    public Result<DeliveryAttempt, NotFoundException> findByRequestId(UUID requestId) {
        return jpaRepository.findByRequestId(requestId)
                .map(schema -> Result.<DeliveryAttempt, NotFoundException>success(mapper.toDomain(schema)))
                .orElse(Result.<DeliveryAttempt, NotFoundException>failure(
                        new NotFoundException(ErrorCode.NOT_FOUND,
                                "Webhook delivery attempt not found for requestId: " + requestId)));
    }
}
