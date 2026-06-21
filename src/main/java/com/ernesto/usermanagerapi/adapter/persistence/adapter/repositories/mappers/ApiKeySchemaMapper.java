package com.ernesto.usermanagerapi.adapter.persistence.adapter.repositories.mappers;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ernesto.usermanagerapi.adapter.persistence.core.schemas.ApiKeySchema;
import com.ernesto.usermanagerapi.domain.entities.ApiKey;

@Component
@Scope("singleton")
public class ApiKeySchemaMapper {

    public ApiKey toDomain(ApiKeySchema schema) {
        if (schema == null)
            return null;

        return ApiKey.reconstitute(
                schema.getApiKeyId(),
                schema.getKeyPrefix(),
                schema.getKeyHint(),
                schema.getKeyHash(),
                schema.getScope(),
                schema.isActive(),
                schema.getCreatedAt(),
                schema.getRevokedAt());
    }

    public ApiKeySchema toSchema(ApiKey domain) {
        if (domain == null)
            return null;

        ApiKeySchema schema = new ApiKeySchema();
        schema.setApiKeyId(domain.getApiKeyId());
        schema.setKeyPrefix(domain.getKeyPrefix());
        schema.setKeyHint(domain.getKeyHint());
        schema.setKeyHash(domain.getKeyHash());
        schema.setScope(domain.getScope());
        schema.setActive(domain.isActive());
        schema.setCreatedAt(domain.getCreatedAt());
        schema.setRevokedAt(domain.getRevokedAt());
        return schema;
    }

}
