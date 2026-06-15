package com.ernesto.usermanagerapi.application.ports.drivers;

import java.util.List;

import com.ernesto.usermanagerapi.application.dto.RoleResponse;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

public interface QueryAllRoleUseCase {

    public Result<List<RoleResponse>, DomainException> execute();

}
