package com.ernesto.usermanagerapi.adapter.persistence.adapter.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import lombok.AllArgsConstructor;

import com.ernesto.usermanagerapi.domain.entities.ApiKey;
import com.ernesto.usermanagerapi.domain.enums.ErrorCode;
import com.ernesto.usermanagerapi.domain.exceptions.NotFoundException;
import com.ernesto.usermanagerapi.domain.patterns.Result;
import com.ernesto.usermanagerapi.adapter.persistence.adapter.repositories.mappers.ApiKeySchemaMapper;
import com.ernesto.usermanagerapi.adapter.persistence.core.ports.drivers.ApiKeyJpaRepository;
import com.ernesto.usermanagerapi.adapter.persistence.core.schemas.ApiKeySchema;
import com.ernesto.usermanagerapi.application.ports.drivens.ApiKeyRepository;

@Repository
@Scope("singleton")
@AllArgsConstructor
public class ApiKeyRepositoryAdapter implements ApiKeyRepository {

    private final ApiKeyJpaRepository jpaRepository;
    private final ApiKeySchemaMapper mapper;

    @Override
    public Result<ApiKey, NotFoundException> findById(int id) {
        return jpaRepository.findById(id)
                .map(x -> Result.<ApiKey, NotFoundException>success(mapper.toDomain(x)))
                .orElse(Result.<ApiKey, NotFoundException>failure(
                        new NotFoundException(ErrorCode.NOT_FOUND, "ApiKey not found for id: " + id)));
    }

    @Override
    public Optional<ApiKey> findByKeyHash(String keyHash) {
        return jpaRepository.findByKeyHash(keyHash)
                .map(mapper::toDomain);
    }

    @Override
    public List<ApiKey> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public ApiKey add(ApiKey entity) {
        ApiKeySchema schema = mapper.toSchema(entity);
        ApiKeySchema saved = jpaRepository.save(schema);
        return mapper.toDomain(saved);
    }

    @Override
    public ApiKey update(ApiKey entity) {
        ApiKeySchema schema = mapper.toSchema(entity);
        ApiKeySchema updated = jpaRepository.save(schema);
        return mapper.toDomain(updated);
    }

    @Override
    public void delete(int id) {
        jpaRepository.deleteById(id);
    }

}
