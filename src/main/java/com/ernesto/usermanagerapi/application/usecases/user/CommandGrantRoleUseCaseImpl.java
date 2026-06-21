package com.ernesto.usermanagerapi.application.usecases.user;

import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import com.ernesto.usermanagerapi.application.dto.UserResponse;
import com.ernesto.usermanagerapi.application.mappers.UserDtoMapper;
import com.ernesto.usermanagerapi.application.ports.drivens.RoleRepository;
import com.ernesto.usermanagerapi.application.ports.drivens.UserRepository;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandGrantRoleUseCase;
import com.ernesto.usermanagerapi.domain.entities.Role;
import com.ernesto.usermanagerapi.domain.entities.User;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

@Service
@Scope("singleton")
@AllArgsConstructor
public class CommandGrantRoleUseCaseImpl implements CommandGrantRoleUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserDtoMapper mapper;

    @Override
    public Result<UserResponse, DomainException> execute(UUID userId, int roleId) {

        // 1. Find user
        var userResult = userRepository.findById(userId);

        if (!userResult.isSuccess()) {
            DomainException notFoundError = userResult.getError();
            return Result.failure(notFoundError);
        }

        User user = userResult.getValue();

        // 2. Find role
        var roleResult = roleRepository.findById(roleId);

        if (!roleResult.isSuccess()) {
            DomainException notFoundError = roleResult.getError();
            return Result.failure(notFoundError);
        }

        Role role = roleResult.getValue();

        // 3. Grant role (domain logic)
        var grantResult = user.grantRole(role);

        if (!grantResult.isSuccess()) {
            DomainException error = grantResult.getError();
            return Result.failure(error);
        }

        // 4. Persist
        User updatedUser = userRepository.update(user);

        // 5. Convert to DTO
        UserResponse response = mapper.toDto(updatedUser);

        return Result.success(response);
    }

}
