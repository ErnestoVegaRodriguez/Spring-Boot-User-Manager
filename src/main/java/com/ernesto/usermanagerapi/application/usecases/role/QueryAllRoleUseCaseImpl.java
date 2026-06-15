package com.ernesto.usermanagerapi.application.usecases.role;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import com.ernesto.usermanagerapi.application.dto.RoleResponse;
import com.ernesto.usermanagerapi.application.mappers.RoleDtoMapper;
import com.ernesto.usermanagerapi.application.ports.drivens.RoleRepository;
import com.ernesto.usermanagerapi.application.ports.drivers.QueryAllRoleUseCase;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

@Service
@Scope("singleton")
@AllArgsConstructor
public class QueryAllRoleUseCaseImpl implements QueryAllRoleUseCase {

    private final RoleRepository roleRepository;
    private final RoleDtoMapper mapper;

    @Override
    public Result<List<RoleResponse>, DomainException> execute() {

        List<RoleResponse> response = roleRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();

        return Result.success(response);
    }

}
