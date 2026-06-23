package com.ernesto.usermanagerapi.application.usecases.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.ernesto.usermanagerapi.adapter.messaging.WebhookPublisher;
import com.ernesto.usermanagerapi.application.dto.CreateUserRequest;
import com.ernesto.usermanagerapi.application.dto.UserResponse;
import com.ernesto.usermanagerapi.application.mappers.UserDtoMapper;
import com.ernesto.usermanagerapi.application.ports.drivens.HasherService;
import com.ernesto.usermanagerapi.application.ports.drivens.PhoneParser;
import com.ernesto.usermanagerapi.application.ports.drivens.UserRepository;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandCreateUserUseCase;
import com.ernesto.usermanagerapi.domain.entities.User;
import com.ernesto.usermanagerapi.domain.enums.DeliveryType;
import com.ernesto.usermanagerapi.domain.enums.ErrorCode;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.exceptions.ValidationException;
import com.ernesto.usermanagerapi.domain.patterns.Delivery;
import com.ernesto.usermanagerapi.domain.patterns.Result;
import com.ernesto.usermanagerapi.domain.values.Email;
import com.ernesto.usermanagerapi.domain.values.Password;
import com.ernesto.usermanagerapi.domain.values.Telephone;

import lombok.AllArgsConstructor;

@Service
@Scope("singleton")
@AllArgsConstructor
public class CommandCreateUserUseCaseImpl implements CommandCreateUserUseCase {

    private final UserRepository userRepository;
    private final @Qualifier("passwordHasher") HasherService hasherService;
    private final PhoneParser phoneParser;
    private final UserDtoMapper mapper;
    private final WebhookPublisher publisher;

    @Override
    public Result<UserResponse, DomainException> execute(CreateUserRequest request) {

        // 1. Validate raw password format
        var passwordValidation = Password.validateRaw(request.password());

        if (!passwordValidation.isSuccess()) {
            ValidationException error = passwordValidation.getError();
            return Result.failure(error);
        }

        // 2. Create value objects (validation happens inside each factory)
        var emailResult = Email.create(request.email());

        if (!emailResult.isSuccess()) {
            ValidationException error = emailResult.getError();
            return Result.failure(error);
        }

        // 3. Parse and validate phone number via libphonenumber
        var phoneResult = phoneParser.parse(request.phoneNumber(), "CO");

        if (!phoneResult.isSuccess()) {
            ValidationException error = phoneResult.getError();
            return Result.failure(error);
        }

        Email email = emailResult.getValue();
        Telephone telephone = Telephone.fromE164(phoneResult.getValue().e164());

        // 4. Hash the raw password
        String hashed = hasherService.hash(request.password());
        Password password = Password.fromHashed(hashed);

        // 5. Check for duplicate email
        var existingResult = userRepository.findByEmail(email.getValue());

        if (existingResult.isSuccess()) {
            DomainException conflictError = new DomainException(ErrorCode.CONFLICT,
                    "The email " + email.getValue() + " is already registered.");
            return Result.failure(conflictError);
        }

        // 6. Create the domain entity
        var userResult = User.create(request.name(), request.lastName(), email, password, telephone);

        if (!userResult.isSuccess()) {
            ValidationException error = userResult.getError();
            return Result.failure(error);
        }

        User user = userResult.getValue();

        // 7. Persist
        User savedUser = userRepository.add(user);

        // 8. Convert to DTO
        UserResponse response = mapper.toDto(savedUser);

        Delivery<UserResponse> message = Delivery.create(DeliveryType.CREATED_USER, response); 

        publisher.publish(message);

        return Result.success(response);
    }

}