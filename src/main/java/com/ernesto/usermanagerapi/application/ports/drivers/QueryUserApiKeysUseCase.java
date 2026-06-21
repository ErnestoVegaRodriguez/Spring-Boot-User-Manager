package com.ernesto.usermanagerapi.application.ports.drivers;

import java.util.List;
import java.util.UUID;

import com.ernesto.usermanagerapi.application.dto.ApiKeyResponse;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

public interface QueryUserApiKeysUseCase {

    Result<List<ApiKeyResponse>, DomainException> execute(UUID userId);

}
