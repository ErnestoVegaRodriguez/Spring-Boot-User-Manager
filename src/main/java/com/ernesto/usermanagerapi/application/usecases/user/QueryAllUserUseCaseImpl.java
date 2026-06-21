package com.ernesto.usermanagerapi.application.usecases.user;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import com.ernesto.usermanagerapi.application.dto.UserResponse;
import com.ernesto.usermanagerapi.application.mappers.UserDtoMapper;
import com.ernesto.usermanagerapi.application.ports.drivens.UserRepository;
import com.ernesto.usermanagerapi.application.ports.drivers.QueryAllUserUseCase;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

@Service
@Scope("singleton")
@AllArgsConstructor
public class QueryAllUserUseCaseImpl implements QueryAllUserUseCase {

    private final UserRepository userRepository;
    private final UserDtoMapper mapper;

    @Override
    public Result<List<UserResponse>, DomainException> execute() {

        List<UserResponse> response = userRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();

        return Result.success(response);
    }

}
