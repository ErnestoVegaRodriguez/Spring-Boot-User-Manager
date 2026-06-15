package com.ernesto.usermanagerapi.application.usecases.role;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import com.ernesto.usermanagerapi.application.dto.CreateRoleRequest;
import com.ernesto.usermanagerapi.application.dto.RoleResponse;
import com.ernesto.usermanagerapi.application.mappers.RoleDtoMapper;
import com.ernesto.usermanagerapi.application.ports.drivens.RoleRepository;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandCreateRoleUseCase;
import com.ernesto.usermanagerapi.domain.entities.Role;
import com.ernesto.usermanagerapi.domain.enums.ErrorCode;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

@Service
@Scope("singleton")
@AllArgsConstructor
public class CommandCreateRoleUseCaseImpl implements CommandCreateRoleUseCase {

    private final RoleRepository roleRepository;
    private final RoleDtoMapper mapper;

    @Override
    public Result<RoleResponse, DomainException> execute(CreateRoleRequest request) {

        // 1. Validate domain rules via entity factory
        var createdResult = Role.create(request.name(), request.permissions());

        if (!createdResult.isSuccess()) {
            DomainException validationError = createdResult.getError().get();
            return Result.failure(validationError);
        }

        Role validRole = createdResult.getValue().get();

        // 2. Check for duplicate name
        var existingResult = roleRepository.findByName(validRole.getName());

        if (existingResult.isSuccess()) {
            DomainException conflictError = new DomainException(ErrorCode.CONFLICT,
                    "The name already exists and cannot be used again.");
            return Result.failure(conflictError);
        }

        // 3. Persist
        Role savedRole = roleRepository.add(validRole);

        // 4. Convert to DTO
        RoleResponse response = mapper.toDto(savedRole);

        return Result.success(response);
    }

}
