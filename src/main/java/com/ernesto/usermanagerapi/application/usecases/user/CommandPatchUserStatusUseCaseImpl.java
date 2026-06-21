package com.ernesto.usermanagerapi.application.usecases.user;

import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import com.ernesto.usermanagerapi.domain.entities.User;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;
import com.ernesto.usermanagerapi.application.ports.drivens.UserRepository;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandPatchUserStatusUseCase;

@Service
@Scope("singleton")
@AllArgsConstructor
public class CommandPatchUserStatusUseCaseImpl implements CommandPatchUserStatusUseCase {

    private final UserRepository userRepository;

    @Override
    public Result<Void, DomainException> execute(UUID id, boolean isActive) {

        // 1. Find existing user
        var findResult = userRepository.findById(id);

        if (!findResult.isSuccess()) {
            DomainException notFoundError = findResult.getError();
            return Result.failure(notFoundError);
        }

        User user = findResult.getValue();

        // 2. Change status
        user.changeStatus(isActive);

        // 3. Persist
        userRepository.update(user);

        return Result.success(null);
    }

}
