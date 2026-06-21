package com.ernesto.usermanagerapi.adapter.web.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import com.ernesto.usermanagerapi.adapter.web.config.ErrorCodeHttpMapper;
import com.ernesto.usermanagerapi.application.dto.ApiKeyResponse;
import com.ernesto.usermanagerapi.application.dto.ApiResponse;
import com.ernesto.usermanagerapi.application.dto.GenerateApiKeyRequest;
import com.ernesto.usermanagerapi.application.dto.GenerateApiKeyResponse;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandGenerateApiKeyUseCase;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandRevokeApiKeyUseCase;
import com.ernesto.usermanagerapi.application.ports.drivers.QueryUserApiKeysUseCase;

@RestController
@RequestMapping("api/v1/users/{userId}/api-keys")
@AllArgsConstructor
public class ApiKeyController {

    private final ErrorCodeHttpMapper errorCodeMapper;
    private final CommandGenerateApiKeyUseCase generateApiKeyUseCase;
    private final QueryUserApiKeysUseCase queryUserApiKeysUseCase;
    private final CommandRevokeApiKeyUseCase revokeApiKeyUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<GenerateApiKeyResponse>> generate(
            @PathVariable UUID userId,
            @RequestBody GenerateApiKeyRequest request) {

        Result<GenerateApiKeyResponse, DomainException> result = generateApiKeyUseCase.execute(userId, request);

        if (result.isSuccess()) {
            GenerateApiKeyResponse response = result.getValue();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "API key generated successfully"));
        }

        DomainException error = result.getError();
        HttpStatus status = errorCodeMapper.toHttpStatus(error.getErrorCode());
        return ResponseEntity.status(status).body(ApiResponse.error(error.getMessage()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ApiKeyResponse>>> list(@PathVariable UUID userId) {
        Result<List<ApiKeyResponse>, DomainException> result = queryUserApiKeysUseCase.execute(userId);

        if (result.isSuccess()) {
            List<ApiKeyResponse> response = result.getValue();
            return ResponseEntity.ok(ApiResponse.success(response, "List of API keys"));
        }

        DomainException error = result.getError();
        HttpStatus status = errorCodeMapper.toHttpStatus(error.getErrorCode());
        return ResponseEntity.status(status).body(ApiResponse.error(error.getMessage()));
    }

    @DeleteMapping("/{apiKeyId}")
    public ResponseEntity<ApiResponse<Void>> revoke(
            @PathVariable UUID userId,
            @PathVariable int apiKeyId) {

        Result<Void, DomainException> result = revokeApiKeyUseCase.execute(userId, apiKeyId);

        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        DomainException error = result.getError();
        HttpStatus status = errorCodeMapper.toHttpStatus(error.getErrorCode());
        return ResponseEntity.status(status).body(ApiResponse.error(error.getMessage()));
    }

}
