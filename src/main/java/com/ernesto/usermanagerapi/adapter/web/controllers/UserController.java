package com.ernesto.usermanagerapi.adapter.web.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import com.ernesto.usermanagerapi.adapter.web.config.ErrorCodeHttpMapper;
import com.ernesto.usermanagerapi.application.dto.ApiResponse;
import com.ernesto.usermanagerapi.application.dto.CreateUserRequest;
import com.ernesto.usermanagerapi.application.dto.PasswordUpdateRequest;
import com.ernesto.usermanagerapi.application.dto.PatchStatusRequest;
import com.ernesto.usermanagerapi.application.dto.RoleAssignmentRequest;
import com.ernesto.usermanagerapi.application.dto.UpdateUserRequest;
import com.ernesto.usermanagerapi.application.dto.UserResponse;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandCreateUserUseCase;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandDeleteUserUseCase;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandGrantRoleUseCase;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandPatchUserStatusUseCase;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandRevokeRoleUseCase;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandUpdatePasswordUseCase;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandUpdateUserUseCase;
import com.ernesto.usermanagerapi.application.ports.drivers.QueryAllUserUseCase;
import com.ernesto.usermanagerapi.application.ports.drivers.QueryUserByIdUseCase;

@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
public class UserController {

    private final ErrorCodeHttpMapper errorCodeMapper;
    private final CommandCreateUserUseCase createUserUseCase;
    private final QueryAllUserUseCase queryAllUserUseCase;
    private final QueryUserByIdUseCase queryUserByIdUseCase;
    private final CommandUpdateUserUseCase updateUserUseCase;
    private final CommandUpdatePasswordUseCase updatePasswordUseCase;
    private final CommandDeleteUserUseCase deleteUserUseCase;
    private final CommandPatchUserStatusUseCase patchUserStatusUseCase;
    private final CommandGrantRoleUseCase grantRoleUseCase;
    private final CommandRevokeRoleUseCase revokeRoleUseCase;

    @PostMapping()
    public ResponseEntity<ApiResponse<UserResponse>> create(@RequestBody CreateUserRequest request) {
        Result<UserResponse, DomainException> result = createUserUseCase.execute(request);

        if (result.isSuccess()) {
            UserResponse response = result.getValue();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "User created successfully"));
        }

        DomainException error = result.getError();
        HttpStatus status = errorCodeMapper.toHttpStatus(error.getErrorCode());
        return ResponseEntity.status(status).body(ApiResponse.error(error.getMessage()));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<UserResponse>>> findAll() {
        Result<List<UserResponse>, DomainException> result = queryAllUserUseCase.execute();

        if (result.isSuccess()) {
            List<UserResponse> response = result.getValue();
            return ResponseEntity.ok(ApiResponse.success(response, "GetAll Results"));
        }

        DomainException error = result.getError();
        HttpStatus status = errorCodeMapper.toHttpStatus(error.getErrorCode());
        return ResponseEntity.status(status).body(ApiResponse.error(error.getMessage()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> findById(@PathVariable UUID id) {
        Result<UserResponse, DomainException> result = queryUserByIdUseCase.execute(id);

        if (result.isSuccess()) {
            UserResponse response = result.getValue();
            return ResponseEntity.ok(ApiResponse.success(response, "GetOne Result"));
        }

        DomainException error = result.getError();
        HttpStatus status = errorCodeMapper.toHttpStatus(error.getErrorCode());
        return ResponseEntity.status(status).body(ApiResponse.error(error.getMessage()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable UUID id, @RequestBody UpdateUserRequest request) {
        Result<UserResponse, DomainException> result = updateUserUseCase.execute(id, request);

        if (result.isSuccess()) {
            UserResponse response = result.getValue();
            return ResponseEntity.ok(ApiResponse.success(response, "Update Result"));
        }

        DomainException error = result.getError();
        HttpStatus status = errorCodeMapper.toHttpStatus(error.getErrorCode());
        return ResponseEntity.status(status).body(ApiResponse.error(error.getMessage()));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<ApiResponse<UserResponse>> updatePassword(@PathVariable UUID id, @RequestBody PasswordUpdateRequest request) {
        Result<UserResponse, DomainException> result = updatePasswordUseCase.execute(id, request);

        if (result.isSuccess()) {
            UserResponse response = result.getValue();
            return ResponseEntity.ok(ApiResponse.success(response, "Password updated successfully"));
        }

        DomainException error = result.getError();
        HttpStatus status = errorCodeMapper.toHttpStatus(error.getErrorCode());
        return ResponseEntity.status(status).body(ApiResponse.error(error.getMessage()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        Result<Void, DomainException> result = deleteUserUseCase.execute(id);

        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        DomainException error = result.getError();
        HttpStatus status = errorCodeMapper.toHttpStatus(error.getErrorCode());
        return ResponseEntity.status(status).body(ApiResponse.error(error.getMessage()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> patchStatus(@PathVariable UUID id, @RequestBody PatchStatusRequest request) {
        Result<Void, DomainException> result = patchUserStatusUseCase.execute(id, request.isActive());

        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        DomainException error = result.getError();
        HttpStatus status = errorCodeMapper.toHttpStatus(error.getErrorCode());
        return ResponseEntity.status(status).body(ApiResponse.error(error.getMessage()));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<ApiResponse<UserResponse>> grantRole(@PathVariable UUID id, @RequestBody RoleAssignmentRequest request) {
        Result<UserResponse, DomainException> result = grantRoleUseCase.execute(id, request.roleId());

        if (result.isSuccess()) {
            UserResponse response = result.getValue();
            return ResponseEntity.ok(ApiResponse.success(response, "Role granted successfully"));
        }

        DomainException error = result.getError();
        HttpStatus status = errorCodeMapper.toHttpStatus(error.getErrorCode());
        return ResponseEntity.status(status).body(ApiResponse.error(error.getMessage()));
    }

    @DeleteMapping("/{id}/role")
    public ResponseEntity<ApiResponse<UserResponse>> revokeRole(@PathVariable UUID id) {
        Result<UserResponse, DomainException> result = revokeRoleUseCase.execute(id);

        if (result.isSuccess()) {
            UserResponse response = result.getValue();
            return ResponseEntity.ok(ApiResponse.success(response, "Role revoked successfully"));
        }

        DomainException error = result.getError();
        HttpStatus status = errorCodeMapper.toHttpStatus(error.getErrorCode());
        return ResponseEntity.status(status).body(ApiResponse.error(error.getMessage()));
    }

}
