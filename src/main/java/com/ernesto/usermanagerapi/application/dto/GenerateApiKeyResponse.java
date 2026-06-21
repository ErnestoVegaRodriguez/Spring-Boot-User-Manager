package com.ernesto.usermanagerapi.application.dto;

public record GenerateApiKeyResponse(
        String rawKey,
        ApiKeyResponse apiKey) {

}
