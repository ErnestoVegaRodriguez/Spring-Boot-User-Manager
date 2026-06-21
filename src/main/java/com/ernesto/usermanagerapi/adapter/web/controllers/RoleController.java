package com.ernesto.usermanagerapi.adapter.web.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import com.ernesto.usermanagerapi.adapter.web.config.ErrorCodeHttpMapper;
import com.ernesto.usermanagerapi.application.dto.ApiResponse;
import com.ernesto.usermanagerapi.application.dto.CreateRoleRequest;
import com.ernesto.usermanagerapi.application.dto.RoleResponse;
import com.ernesto.usermanagerapi.application.dto.UpdateRoleRequest;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandCreateRoleUseCase;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandDeleteRoleUseCase;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandUpdateRoleUseCase;
import com.ernesto.usermanagerapi.application.ports.drivers.QueryAllRoleUseCase;
import com.ernesto.usermanagerapi.application.ports.drivers.QueryRoleByIdUseCase;

@RestController
@RequestMapping("api/v1/roles")
@AllArgsConstructor
public class RoleController {

    private final ErrorCodeHttpMapper errorCodeMapper;
    private final CommandCreateRoleUseCase createRoleUseCase;
    private final QueryAllRoleUseCase queryAllRoleUseCase;
    private final QueryRoleByIdUseCase queryRoleByIdUseCase;
    private final CommandUpdateRoleUseCase updateRoleUseCase;
    private final CommandDeleteRoleUseCase deleteRoleUseCase;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<RoleResponse>>> findAll() {
        Result<List<RoleResponse>, DomainException> result = queryAllRoleUseCase.execute();

        if (result.isSuccess()) {
            List<RoleResponse> response = result.getValue();
            return ResponseEntity.ok(ApiResponse.success(response, "GetAll Results"));
        }

        DomainException error = result.getError();
        HttpStatus status = errorCodeMapper.toHttpStatus(error.getErrorCode());
        return ResponseEntity.status(status).body(ApiResponse.error(error.getMessage()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponse>> findById(@PathVariable Integer id) {
        Result<RoleResponse, DomainException> result = queryRoleByIdUseCase.execute(id);

        if (result.isSuccess()) {
            RoleResponse response = result.getValue();
            return ResponseEntity.ok(ApiResponse.success(response, "GetOne Result"));
        }

        DomainException error = result.getError();
        HttpStatus status = errorCodeMapper.toHttpStatus(error.getErrorCode());
        return ResponseEntity.status(status).body(ApiResponse.error(error.getMessage()));
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<RoleResponse>> create(@RequestBody CreateRoleRequest request) {
        Result<RoleResponse, DomainException> result = createRoleUseCase.execute(request);

        if (result.isSuccess()) {
            RoleResponse response = result.getValue();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "Role created successfully"));
        }

        DomainException error = result.getError();
        HttpStatus status = errorCodeMapper.toHttpStatus(error.getErrorCode());
        return ResponseEntity.status(status).body(ApiResponse.error(error.getMessage()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponse>> update(@PathVariable Integer id, @RequestBody UpdateRoleRequest request) {
        Result<RoleResponse, DomainException> result = updateRoleUseCase.execute(id, request);

        if (result.isSuccess()) {
            RoleResponse response = result.getValue();
            return ResponseEntity.ok(ApiResponse.success(response, "Update Result"));
        }

        DomainException error = result.getError();
        HttpStatus status = errorCodeMapper.toHttpStatus(error.getErrorCode());
        return ResponseEntity.status(status).body(ApiResponse.error(error.getMessage()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        Result<Void, DomainException> result = deleteRoleUseCase.execute(id);

        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        DomainException error = result.getError();
        HttpStatus status = errorCodeMapper.toHttpStatus(error.getErrorCode());
        return ResponseEntity.status(status).body(ApiResponse.error(error.getMessage()));
    }

}
