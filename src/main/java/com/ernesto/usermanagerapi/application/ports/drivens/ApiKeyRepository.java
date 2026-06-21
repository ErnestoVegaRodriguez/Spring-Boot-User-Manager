package com.ernesto.usermanagerapi.application.ports.drivens;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ernesto.usermanagerapi.domain.entities.ApiKey;
import com.ernesto.usermanagerapi.domain.exceptions.NotFoundException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

public interface ApiKeyRepository {

    Result<ApiKey, NotFoundException> findById(int id);

    Optional<ApiKey> findByKeyHash(String keyHash);

    List<ApiKey> findByUserId(UUID userId);

    ApiKey add(ApiKey entity);

    ApiKey update(ApiKey entity);

    void delete(int id);

}
