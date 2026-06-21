package com.ernesto.usermanagerapi.application.ports.drivers;

import java.util.UUID;

import com.ernesto.usermanagerapi.application.dto.GenerateApiKeyRequest;
import com.ernesto.usermanagerapi.application.dto.GenerateApiKeyResponse;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

public interface CommandGenerateApiKeyUseCase {

    Result<GenerateApiKeyResponse, DomainException> execute(UUID userId, GenerateApiKeyRequest request);

}
