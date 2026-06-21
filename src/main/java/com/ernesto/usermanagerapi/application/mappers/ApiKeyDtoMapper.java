package com.ernesto.usermanagerapi.application.mappers;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ernesto.usermanagerapi.application.dto.ApiKeyResponse;
import com.ernesto.usermanagerapi.domain.entities.ApiKey;

@Component
@Scope("singleton")
public class ApiKeyDtoMapper {

    public ApiKeyResponse toDto(ApiKey apiKey) {
        return new ApiKeyResponse(
                apiKey.getApiKeyId(),
                apiKey.getKeyPrefix(),
                apiKey.getKeyHint(),
                apiKey.getScope(),
                apiKey.isActive(),
                apiKey.getCreatedAt(),
                apiKey.getRevokedAt());
    }

}
