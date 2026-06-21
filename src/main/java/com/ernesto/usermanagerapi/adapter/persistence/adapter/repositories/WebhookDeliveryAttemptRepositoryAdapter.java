package com.ernesto.usermanagerapi.adapter.persistence.adapter.repositories;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import lombok.AllArgsConstructor;
import com.ernesto.usermanagerapi.adapter.persistence.adapter.repositories.mappers.WebhookDeliveryAttemptMapper;
import com.ernesto.usermanagerapi.adapter.persistence.core.ports.drivers.WebhookDeliveryAttemptJpaRepository;
import com.ernesto.usermanagerapi.domain.entities.WebhookDeliveryAttempt;
import com.ernesto.usermanagerapi.domain.enums.ErrorCode;
import com.ernesto.usermanagerapi.domain.exceptions.NotFoundException;
import com.ernesto.usermanagerapi.domain.patterns.Result;
import com.ernesto.usermanagerapi.application.ports.drivens.WebhookDeliveryAttemptRepository;

@Repository
@AllArgsConstructor
public class WebhookDeliveryAttemptRepositoryAdapter implements WebhookDeliveryAttemptRepository {

    private final WebhookDeliveryAttemptJpaRepository jpaRepository;
    private final WebhookDeliveryAttemptMapper mapper;

    @Override
    public WebhookDeliveryAttempt add(WebhookDeliveryAttempt attempt) {
        var schema = mapper.toSchema(attempt);
        var saved = jpaRepository.save(schema);
        return mapper.toDomain(saved);
    }

    @Override
    public Result<WebhookDeliveryAttempt, NotFoundException> findByRequestId(UUID requestId) {
        return jpaRepository.findByRequestId(requestId)
                .map(schema -> Result.<WebhookDeliveryAttempt, NotFoundException>success(mapper.toDomain(schema)))
                .orElse(Result.<WebhookDeliveryAttempt, NotFoundException>failure(
                        new NotFoundException(ErrorCode.NOT_FOUND,
                                "Webhook delivery attempt not found for requestId: " + requestId)));
    }
}
