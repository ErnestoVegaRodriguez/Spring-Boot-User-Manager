package com.ernesto.usermanagerapi.application.ports.drivers;

import java.util.List;

import com.ernesto.usermanagerapi.application.dto.UserResponse;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

public interface QueryAllUserUseCase {

    public Result<List<UserResponse>, DomainException> execute();

}
