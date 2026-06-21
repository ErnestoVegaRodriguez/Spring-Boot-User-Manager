package com.ernesto.usermanagerapi.application.usecases.user;

import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import com.ernesto.usermanagerapi.application.dto.UserResponse;
import com.ernesto.usermanagerapi.application.mappers.UserDtoMapper;
import com.ernesto.usermanagerapi.domain.entities.User;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;
import com.ernesto.usermanagerapi.application.ports.drivens.UserRepository;
import com.ernesto.usermanagerapi.application.ports.drivers.QueryUserByIdUseCase;

@Service
@Scope("singleton")
@AllArgsConstructor
public class QueryUserByIdUseCaseImpl implements QueryUserByIdUseCase {

    private final UserRepository userRepository;
    private final UserDtoMapper mapper;

    @Override
    public Result<UserResponse, DomainException> execute(UUID id) {

        // 1. Find user in repository
        var repoResult = userRepository.findById(id);

        if (!repoResult.isSuccess()) {
            DomainException notFoundError = repoResult.getError();
            return Result.failure(notFoundError);
        }

        User user = repoResult.getValue();

        // 2. Convert to DTO
        UserResponse response = mapper.toDto(user);

        return Result.success(response);
    }

}
