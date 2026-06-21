package com.ernesto.usermanagerapi.application.usecases.role;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import com.ernesto.usermanagerapi.application.dto.RoleResponse;
import com.ernesto.usermanagerapi.application.mappers.RoleDtoMapper;
import com.ernesto.usermanagerapi.domain.entities.Role;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;
import com.ernesto.usermanagerapi.application.ports.drivens.RoleRepository;
import com.ernesto.usermanagerapi.application.ports.drivers.QueryRoleByIdUseCase;

@Service
@Scope("singleton")
@AllArgsConstructor
public class QueryRoleByIdUseCaseImpl implements QueryRoleByIdUseCase {

    private final RoleRepository roleRepository;
    private final RoleDtoMapper mapper;

    @Override
    public Result<RoleResponse, DomainException> execute(int roleId) {

        // 1. Find role in repository
        var repoResult = roleRepository.findById(roleId);

        if (!repoResult.isSuccess()) {
            DomainException notFoundError = repoResult.getError();
            return Result.failure(notFoundError);
        }

        Role role = repoResult.getValue();

        // 2. Convert to DTO
        RoleResponse response = mapper.toDto(role);

        return Result.success(response);
    }

}
