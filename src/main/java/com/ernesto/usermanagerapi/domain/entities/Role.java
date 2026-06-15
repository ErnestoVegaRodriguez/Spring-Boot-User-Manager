package com.ernesto.usermanagerapi.domain.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;

import com.ernesto.usermanagerapi.domain.enums.ErrorCode;
import com.ernesto.usermanagerapi.domain.enums.Permission;
import com.ernesto.usermanagerapi.domain.exceptions.ValidationException;
import com.ernesto.usermanagerapi.domain.patterns.Result;
import com.ernesto.usermanagerapi.domain.patterns.ValidationItem;

@Getter
public class Role {

    private int roleId;
    private String name;
    private List<Permission> permissions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Role() {
    }

    public Role(String name, List<Permission> permissions) {
        this.name = name;
        this.permissions = permissions;
        this.createdAt = LocalDateTime.now();
    }

    public Role(int roleId, String name, List<Permission> permissions) {
        this.roleId = roleId;
        this.name = name;
        this.permissions = permissions;
        this.createdAt = LocalDateTime.now();
    }

    public static Result<Role, ValidationException> create(String name, List<Permission> permissions) {

        List<ValidationItem> errorList = new ArrayList<>();

        if (name == null || name.isEmpty() || name.isBlank())
            errorList.add(ValidationItem.of(Map.of("name", "The name field cannot be left blank.")));

        if (permissions == null || permissions.isEmpty())
            errorList.add(ValidationItem.of(Map.of("permissions", "The permissions field cannot be left blank.")));

        if (!errorList.isEmpty())
            return Result.failure(new ValidationException(ErrorCode.VALIDATION_ERROR, "Role validation failed.", errorList));

        return Result.success(new Role(name, permissions));
    }

    public static Result<Role, ValidationException> create(int roleId, String name, List<Permission> permissions) {

        List<ValidationItem> errorList = new ArrayList<>();

        if (roleId <= 0)
            errorList.add(ValidationItem.of(Map.of("roleId", "The role id must be greater than zero.")));

        if (name == null || name.isEmpty() || name.isBlank())
            errorList.add(ValidationItem.of(Map.of("name", "The name field cannot be left blank.")));

        if (permissions == null || permissions.isEmpty())
            errorList.add(ValidationItem.of(Map.of("permissions", "The permissions field cannot be left blank.")));

        if (!errorList.isEmpty())
            return Result.failure(new ValidationException(ErrorCode.VALIDATION_ERROR, "Role validation failed.", errorList));

        return Result.success(new Role(name, permissions));
    }

    public Result<Void, ValidationException> update(String name, List<Permission> permissions) {

        List<ValidationItem> errorList = new ArrayList<>();

        if (name == null || name.isEmpty() || name.isBlank())
            errorList.add(ValidationItem.of(Map.of("name", "The name field cannot be left blank.")));

        if (permissions == null || permissions.isEmpty())
            errorList.add(ValidationItem.of(Map.of("permissions", "The permissions field cannot be left blank.")));

        if (!errorList.isEmpty())
            return Result.failure(new ValidationException(ErrorCode.VALIDATION_ERROR, "Role update validation failed.", errorList));

        this.name = name;
        this.permissions = permissions;
        this.updatedAt = LocalDateTime.now();

        return Result.success(null);
    }

    public static Role reconstitute(
            int roleId,
            String name,
            List<Permission> permissions,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        Role role = new Role();
        role.roleId = roleId;
        role.name = name;
        role.permissions = permissions;
        role.createdAt = createdAt;
        role.updatedAt = updatedAt;
        return role;
    }

}
