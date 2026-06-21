package com.ernesto.usermanagerapi.adapter.persistence.adapter.repositories.mappers;

import java.util.ArrayList;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ernesto.usermanagerapi.adapter.persistence.core.schemas.RoleSchema;
import com.ernesto.usermanagerapi.domain.entities.Role;

@Component
@Scope("singleton")
public class RoleSchemaMapper {

    public Role toDomain(RoleSchema schema) {
        if (schema == null) return null;

        return Role.reconstitute(
            schema.getRoleId(),
            schema.getName(),
            schema.getPermissions() != null
                ? new ArrayList<>(schema.getPermissions())
                : new ArrayList<>(),
            schema.getCreatedAt(),
            schema.getUpdatedAt()
        );
    }

    public RoleSchema toSchema(Role role) {
        if (role == null) return null;

        RoleSchema schema = new RoleSchema();
        schema.setRoleId(role.getRoleId());
        schema.setName(role.getName());
        schema.setPermissions(
            role.getPermissions() != null
                ? new ArrayList<>(role.getPermissions())
                : new ArrayList<>()
        );
        schema.setCreatedAt(role.getCreatedAt());
        schema.setUpdatedAt(role.getUpdatedAt());

        return schema;
    }
    
}
