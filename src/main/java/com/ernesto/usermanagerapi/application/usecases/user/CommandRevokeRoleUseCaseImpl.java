package com.ernesto.usermanagerapi.application.usecases.user;

import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import com.ernesto.usermanagerapi.application.dto.UserResponse;
import com.ernesto.usermanagerapi.application.mappers.UserDtoMapper;
import com.ernesto.usermanagerapi.application.ports.drivens.UserRepository;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandRevokeRoleUseCase;
import com.ernesto.usermanagerapi.domain.entities.User;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

@Service
@Scope("singleton")
@AllArgsConstructor
public class CommandRevokeRoleUseCaseImpl implements CommandRevokeRoleUseCase {

    private final UserRepository userRepository;
    private final UserDtoMapper mapper;

    @Override
    public Result<UserResponse, DomainException> execute(UUID userId) {

        // 1. Find user
        var userResult = userRepository.findById(userId);

        if (!userResult.isSuccess()) {
            DomainException notFoundError = userResult.getError();
            return Result.failure(notFoundError);
        }

        User user = userResult.getValue();

        // 2. Revoke role (domain logic)
        user.revokeRole();

        // 3. Persist
        User updatedUser = userRepository.update(user);

        // 4. Convert to DTO
        UserResponse response = mapper.toDto(updatedUser);

        return Result.success(response);
    }

}
