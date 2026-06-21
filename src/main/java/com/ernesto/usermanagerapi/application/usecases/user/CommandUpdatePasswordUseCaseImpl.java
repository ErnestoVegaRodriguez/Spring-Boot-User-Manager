package com.ernesto.usermanagerapi.application.usecases.user;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import com.ernesto.usermanagerapi.application.dto.PasswordUpdateRequest;
import com.ernesto.usermanagerapi.application.dto.UserResponse;
import com.ernesto.usermanagerapi.application.mappers.UserDtoMapper;
import com.ernesto.usermanagerapi.application.ports.drivens.HasherService;
import com.ernesto.usermanagerapi.application.ports.drivens.UserRepository;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandUpdatePasswordUseCase;
import com.ernesto.usermanagerapi.domain.entities.User;
import com.ernesto.usermanagerapi.domain.enums.ErrorCode;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.exceptions.ValidationException;
import com.ernesto.usermanagerapi.domain.patterns.Result;
import com.ernesto.usermanagerapi.domain.values.Password;

@Service
@Scope("singleton")
@AllArgsConstructor
public class CommandUpdatePasswordUseCaseImpl implements CommandUpdatePasswordUseCase {

    private final UserRepository userRepository;
    private final @Qualifier("passwordHasher") HasherService hasherService;
    private final UserDtoMapper mapper;

    @Override
    public Result<UserResponse, DomainException> execute(UUID id, PasswordUpdateRequest request) {

        // 1. Find user
        var findResult = userRepository.findById(id);

        if (!findResult.isSuccess()) {
            DomainException notFoundError = findResult.getError();
            return Result.failure(notFoundError);
        }

        User user = findResult.getValue();

        // 2. Verify current password
        boolean matches = hasherService.compare(request.currentPassword(), user.getPassword().getHashedValue());

        if (!matches) {
            DomainException badRequest = new DomainException(ErrorCode.BAD_REQUEST, "Current password is incorrect.");
            return Result.failure(badRequest);
        }

        // 3. Validate new password format
        var passwordValidation = Password.validateRaw(request.newPassword());

        if (!passwordValidation.isSuccess()) {
            ValidationException validationError = passwordValidation.getError();
            return Result.failure(validationError);
        }

        // 4. Hash new password
        String hashed = hasherService.hash(request.newPassword());
        Password newPassword = Password.fromHashed(hashed);

        // 5. Update domain entity
        var updateResult = user.updatePassword(newPassword);

        if (!updateResult.isSuccess()) {
            ValidationException validationError = updateResult.getError();
            return Result.failure(validationError);
        }

        // 6. Persist
        User updatedUser = userRepository.update(user);

        // 7. Return DTO
        UserResponse response = mapper.toDto(updatedUser);
        return Result.success(response);
    }

}
