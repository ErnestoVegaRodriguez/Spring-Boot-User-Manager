package com.ernesto.usermanagerapi.application.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ernesto.usermanagerapi.domain.enums.Permission;

public record RoleResponse(
        int roleId,
        String name,
        List<Permission> permissions,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

}
