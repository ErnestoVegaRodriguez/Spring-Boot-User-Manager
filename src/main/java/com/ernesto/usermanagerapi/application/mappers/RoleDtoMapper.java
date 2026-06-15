package com.ernesto.usermanagerapi.application.mappers;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ernesto.usermanagerapi.application.dto.CreateRoleRequest;
import com.ernesto.usermanagerapi.application.dto.RoleResponse;
import com.ernesto.usermanagerapi.application.dto.UpdateRoleRequest;
import com.ernesto.usermanagerapi.domain.entities.Role;

@Component
@Scope("singleton")
public class RoleDtoMapper {

    public Role toDomain(CreateRoleRequest request) {
        return new Role(request.name(), request.permissions());
    }

    public Role toDomain(int roleId, UpdateRoleRequest request) {
        return new Role(roleId, request.name(), request.permissions());
    }

    public RoleResponse toDto(Role role) {
        return new RoleResponse(
                role.getRoleId(),
                role.getName(),
                role.getPermissions(),
                role.getCreatedAt(),
                role.getUpdatedAt());
    }

}
