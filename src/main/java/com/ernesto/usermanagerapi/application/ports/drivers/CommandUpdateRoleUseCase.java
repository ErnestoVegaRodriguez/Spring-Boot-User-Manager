package com.ernesto.usermanagerapi.application.ports.drivers;

import com.ernesto.usermanagerapi.application.dto.RoleResponse;
import com.ernesto.usermanagerapi.application.dto.UpdateRoleRequest;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

public interface CommandUpdateRoleUseCase {

    public Result<RoleResponse, DomainException> execute(int roleId, UpdateRoleRequest request);

}
