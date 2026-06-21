package com.ernesto.usermanagerapi.application.dto;

import java.time.LocalDateTime;

import com.ernesto.usermanagerapi.domain.enums.Scope;

public record ApiKeyResponse(
        int apiKeyId,
        String keyPrefix,
        String keyHint,
        Scope scope,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime revokedAt) {

}
