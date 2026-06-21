package com.ernesto.usermanagerapi.application.usecases.user;

import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import com.ernesto.usermanagerapi.domain.entities.User;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;
import com.ernesto.usermanagerapi.application.ports.drivens.UserRepository;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandDeleteUserUseCase;

@Service
@Scope("singleton")
@AllArgsConstructor
public class CommandDeleteUserUseCaseImpl implements CommandDeleteUserUseCase {

    private final UserRepository userRepository;

    @Override
    public Result<Void, DomainException> execute(UUID id) {

        // 1. Find existing user
        var findResult = userRepository.findById(id);

        if (!findResult.isSuccess()) {
            DomainException notFoundError = findResult.getError();
            return Result.failure(notFoundError);
        }

        User user = findResult.getValue();

        // 2. Apply soft delete
        user.delete();

        // 3. Persist
        userRepository.update(user);

        return Result.success(null);
    }

}
