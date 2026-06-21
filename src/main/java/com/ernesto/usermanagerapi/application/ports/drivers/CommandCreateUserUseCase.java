package com.ernesto.usermanagerapi.application.ports.drivers;

import com.ernesto.usermanagerapi.application.dto.CreateUserRequest;
import com.ernesto.usermanagerapi.application.dto.UserResponse;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

public interface CommandCreateUserUseCase {

    public Result<UserResponse, DomainException> execute(CreateUserRequest request);

}
