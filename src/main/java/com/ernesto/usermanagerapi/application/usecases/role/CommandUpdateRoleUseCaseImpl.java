package com.ernesto.usermanagerapi.application.usecases.role;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import com.ernesto.usermanagerapi.application.dto.RoleResponse;
import com.ernesto.usermanagerapi.application.dto.UpdateRoleRequest;
import com.ernesto.usermanagerapi.application.mappers.RoleDtoMapper;
import com.ernesto.usermanagerapi.domain.entities.Role;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;
import com.ernesto.usermanagerapi.application.ports.drivens.RoleRepository;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandUpdateRoleUseCase;

@Service
@Scope("singleton")
@AllArgsConstructor
public class CommandUpdateRoleUseCaseImpl implements CommandUpdateRoleUseCase {

    private final RoleRepository roleRepository;
    private final RoleDtoMapper mapper;

    @Override
    public Result<RoleResponse, DomainException> execute(int roleId, UpdateRoleRequest request) {

        // 1. Find existing role
        var findResult = roleRepository.findById(roleId);

        if (!findResult.isSuccess()) {
            DomainException notFoundError = findResult.getError().get();
            return Result.failure(notFoundError);
        }

        Role existingRole = findResult.getValue().get();

        // 2. Apply domain validation via update
        var updateResult = existingRole.update(request.name(), request.permissions());

        if (!updateResult.isSuccess()) {
            DomainException validationError = updateResult.getError().get();
            return Result.failure(validationError);
        }

        // 3. Persist changes
        Role updatedRole = roleRepository.update(existingRole);

        // 4. Convert to DTO
        RoleResponse response = mapper.toDto(updatedRole);

        return Result.success(response);
    }

}
