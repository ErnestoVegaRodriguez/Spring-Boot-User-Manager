package com.ernesto.usermanagerapi.application.usecases.role;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import com.ernesto.usermanagerapi.domain.entities.Role;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;
import com.ernesto.usermanagerapi.application.ports.drivens.RoleRepository;
import com.ernesto.usermanagerapi.application.ports.drivens.UserRepository;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandDeleteRoleUseCase;

@Service
@Scope("singleton")
@AllArgsConstructor
public class CommandDeleteRoleUseCaseImpl implements CommandDeleteRoleUseCase {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Result<Void, DomainException> execute(int roleId) {

        // 1. Find existing role
        var findResult = roleRepository.findById(roleId);

        if (!findResult.isSuccess()) {
            DomainException notFoundError = findResult.getError().get();
            return Result.failure(notFoundError);
        }

        Role existingRole = findResult.getValue().get();

        // 2. Revoke role from all users that have it assigned
        userRepository.removeRoleFromAllUsers(roleId);

        // 3. Delete the role
        roleRepository.delete(existingRole.getRoleId());

        return Result.success(null);
    }

}
