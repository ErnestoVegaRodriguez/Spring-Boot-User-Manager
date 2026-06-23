package com.ernesto.usermanagerapi.application.ports.drivens;

import java.util.UUID;

import com.ernesto.usermanagerapi.domain.entities.DeliveryAttempt;
import com.ernesto.usermanagerapi.domain.exceptions.NotFoundException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

public interface DeliveryAttemptRepository {

    DeliveryAttempt add(DeliveryAttempt attempt);

    Result<DeliveryAttempt, NotFoundException> findByRequestId(UUID requestId);
}
