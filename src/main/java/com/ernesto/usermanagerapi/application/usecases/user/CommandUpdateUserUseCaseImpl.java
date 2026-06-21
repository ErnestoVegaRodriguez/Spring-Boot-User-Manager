package com.ernesto.usermanagerapi.application.usecases.user;

import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import com.ernesto.usermanagerapi.application.dto.UpdateUserRequest;
import com.ernesto.usermanagerapi.application.dto.UserResponse;
import com.ernesto.usermanagerapi.application.mappers.UserDtoMapper;
import com.ernesto.usermanagerapi.application.ports.drivens.PhoneParser;
import com.ernesto.usermanagerapi.application.ports.drivens.UserRepository;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandUpdateUserUseCase;
import com.ernesto.usermanagerapi.domain.entities.User;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.exceptions.ValidationException;
import com.ernesto.usermanagerapi.domain.patterns.Result;
import com.ernesto.usermanagerapi.domain.values.Telephone;

@Service
@Scope("singleton")
@AllArgsConstructor
public class CommandUpdateUserUseCaseImpl implements CommandUpdateUserUseCase {

    private final UserRepository userRepository;
    private final PhoneParser phoneParser;
    private final UserDtoMapper mapper;

    @Override
    public Result<UserResponse, DomainException> execute(UUID id, UpdateUserRequest request) {

        // 1. Find existing user
        var findResult = userRepository.findById(id);

        if (!findResult.isSuccess()) {
            DomainException notFoundError = findResult.getError();
            return Result.failure(notFoundError);
        }

        User user = findResult.getValue();

        // 2. Apply name/lastName update via domain validation
        var updateResult = user.update(request.name(), request.lastName());

        if (!updateResult.isSuccess()) {
            ValidationException validationError = updateResult.getError();
            return Result.failure(validationError);
        }

        // 3. Update phone if provided
        if (request.phoneNumber() != null && !request.phoneNumber().isBlank()) {
            var phoneResult = phoneParser.parse(request.phoneNumber(), "CO");

            if (!phoneResult.isSuccess()) {
                ValidationException error = phoneResult.getError();
                return Result.failure(error);
            }

            Telephone telephone = Telephone.fromE164(phoneResult.getValue().e164());

            var phoneUpdateResult = user.updatePhone(telephone);

            if (!phoneUpdateResult.isSuccess()) {
                ValidationException error = phoneUpdateResult.getError();
                return Result.failure(error);
            }
        }

        // 4. Persist changes
        User updatedUser = userRepository.update(user);

        // 5. Convert to DTO
        UserResponse response = mapper.toDto(updatedUser);

        return Result.success(response);
    }

}