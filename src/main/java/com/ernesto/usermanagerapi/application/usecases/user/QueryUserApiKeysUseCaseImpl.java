package com.ernesto.usermanagerapi.application.usecases.user;

import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import com.ernesto.usermanagerapi.application.dto.ApiKeyResponse;
import com.ernesto.usermanagerapi.application.mappers.ApiKeyDtoMapper;
import com.ernesto.usermanagerapi.application.ports.drivens.UserRepository;
import com.ernesto.usermanagerapi.application.ports.drivers.QueryUserApiKeysUseCase;
import com.ernesto.usermanagerapi.domain.entities.User;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

@Service
@Scope("singleton")
@AllArgsConstructor
public class QueryUserApiKeysUseCaseImpl implements QueryUserApiKeysUseCase {

    private final UserRepository userRepository;
    private final ApiKeyDtoMapper mapper;

    @Override
    public Result<List<ApiKeyResponse>, DomainException> execute(UUID userId) {

        // 1. Find user (loads apiKeys via @OneToMany)
        var userResult = userRepository.findById(userId);

        if (!userResult.isSuccess()) {
            DomainException notFoundError = userResult.getError();
            return Result.failure(notFoundError);
        }

        User user = userResult.getValue();

        // 2. Map all api keys
        List<ApiKeyResponse> response = user.getApiKeys().stream()
                .map(mapper::toDto)
                .toList();

        return Result.success(response);
    }

}
