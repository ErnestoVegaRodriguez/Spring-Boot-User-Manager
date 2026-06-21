package com.ernesto.usermanagerapi.application.ports.drivens;

import java.util.UUID;

import com.ernesto.usermanagerapi.domain.entities.WebhookDeliveryAttempt;
import com.ernesto.usermanagerapi.domain.exceptions.NotFoundException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

public interface WebhookDeliveryAttemptRepository {

    WebhookDeliveryAttempt add(WebhookDeliveryAttempt attempt);

    Result<WebhookDeliveryAttempt, NotFoundException> findByRequestId(UUID requestId);
}
