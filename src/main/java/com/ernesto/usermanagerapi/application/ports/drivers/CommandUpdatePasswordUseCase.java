package com.ernesto.usermanagerapi.application.ports.drivers;

import java.util.UUID;

import com.ernesto.usermanagerapi.application.dto.PasswordUpdateRequest;
import com.ernesto.usermanagerapi.application.dto.UserResponse;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

public interface CommandUpdatePasswordUseCase {

    public Result<UserResponse, DomainException> execute(UUID id, PasswordUpdateRequest request);

}
