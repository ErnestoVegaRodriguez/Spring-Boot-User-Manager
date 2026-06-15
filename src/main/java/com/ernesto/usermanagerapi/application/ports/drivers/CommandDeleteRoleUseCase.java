package com.ernesto.usermanagerapi.application.ports.drivers;

import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

public interface CommandDeleteRoleUseCase {

    public Result<Void, DomainException> execute(int roleId);

}
