package com.ernesto.usermanagerapi.application.dto;

import java.util.List;

import com.ernesto.usermanagerapi.domain.enums.Permission;

public record UpdateRoleRequest(String name, List<Permission> permissions) {

}
