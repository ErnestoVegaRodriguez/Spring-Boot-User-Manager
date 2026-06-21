package com.ernesto.usermanagerapi.adapter.storage.core.ports.drivers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ernesto.usermanagerapi.adapter.storage.core.schemas.ApiKeySchema;

public interface ApiKeyJpaRepository extends JpaRepository<ApiKeySchema, Integer> {

    List<ApiKeySchema> findByUserId(UUID userId);

    Optional<ApiKeySchema> findByKeyHash(String keyHash);

}
