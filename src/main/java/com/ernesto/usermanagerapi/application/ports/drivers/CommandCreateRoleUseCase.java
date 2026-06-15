package com.ernesto.usermanagerapi.application.ports.drivers;

import com.ernesto.usermanagerapi.application.dto.CreateRoleRequest;
import com.ernesto.usermanagerapi.application.dto.RoleResponse;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

public interface CommandCreateRoleUseCase {

    public Result<RoleResponse, DomainException> execute(CreateRoleRequest request);

}
