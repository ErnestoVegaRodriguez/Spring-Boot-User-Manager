package com.ernesto.usermanagerapi.domain.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import com.ernesto.usermanagerapi.domain.enums.ErrorCode;
import com.ernesto.usermanagerapi.domain.enums.Scope;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.exceptions.ValidationException;
import com.ernesto.usermanagerapi.domain.patterns.Result;
import com.ernesto.usermanagerapi.domain.patterns.ValidationItem;

@Getter
public class ApiKey {

    private int apiKeyId;
    private String keyPrefix;
    private String keyHint;
    private String keyHash;
    private Scope scope;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime revokedAt;

    private ApiKey() {
    }

    private ApiKey(String keyHash, String keyPrefix, String keyHint, Scope scope) {
        this.keyHash = keyHash;
        this.keyPrefix = keyPrefix;
        this.keyHint = keyHint;
        this.scope = scope;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    public static Result<ApiKey, DomainException> fromHashed(String value) {
        if (value == null || value.isBlank()) {
            return Result.failure(
                    new DomainException(ErrorCode.VALIDATION_ERROR, "The key hash field cannot be left blank."));
        }
        ApiKey key = new ApiKey();
        key.keyHash = value;
        key.isActive = true;
        key.createdAt = LocalDateTime.now();
        return Result.success(key);
    }

    public static Result<ApiKey, ValidationException> create(
            String keyHash, String keyPrefix, String keyHint, Scope scope) {

        List<ValidationItem> errors = new ArrayList<>();

        if (keyHash == null || keyHash.isBlank())
            errors.add(ValidationItem.of(Map.of("keyHash", "The key hash cannot be blank.")));

        if (keyPrefix == null || keyPrefix.isBlank())
            errors.add(ValidationItem.of(Map.of("keyPrefix", "The key prefix cannot be blank.")));

        if (keyHint == null || keyHint.isBlank())
            errors.add(ValidationItem.of(Map.of("keyHint", "The key hint cannot be blank.")));

        if (scope == null)
            errors.add(ValidationItem.of(Map.of("scope", "A scope is required.")));

        if (!errors.isEmpty())
            return Result.failure(
                    new ValidationException(ErrorCode.VALIDATION_ERROR, "ApiKey validation failed.", errors));

        return Result.success(new ApiKey(keyHash, keyPrefix, keyHint, scope));
    }

    public static ApiKey reconstitute(
            int apiKeyId,
            String keyPrefix,
            String keyHint,
            String keyHash,
            Scope scope,
            boolean isActive,
            LocalDateTime createdAt,
            LocalDateTime revokedAt) {

        ApiKey key = new ApiKey();
        key.apiKeyId = apiKeyId;
        key.keyPrefix = keyPrefix;
        key.keyHint = keyHint;
        key.keyHash = keyHash;
        key.scope = scope;
        key.isActive = isActive;
        key.createdAt = createdAt;
        key.revokedAt = revokedAt;
        return key;
    }

    public void revoke() {
        this.isActive = false;
        this.revokedAt = LocalDateTime.now();
    }

}
