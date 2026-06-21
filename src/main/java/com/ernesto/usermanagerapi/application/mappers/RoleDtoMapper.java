package com.ernesto.usermanagerapi.application.mappers;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ernesto.usermanagerapi.application.dto.RoleResponse;
import com.ernesto.usermanagerapi.domain.entities.Role;

@Component
@Scope("singleton")
public class RoleDtoMapper {

    public RoleResponse toDto(Role role) {
        return new RoleResponse(
                role.getRoleId(),
                role.getName(),
                role.getPermissions(),
                role.getCreatedAt(),
                role.getUpdatedAt());
    }

}
