package com.ernesto.usermanagerapi.application.ports.drivers;

import java.util.UUID;

import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

public interface CommandDeleteUserUseCase {

    public Result<Void, DomainException> execute(UUID id);

}
