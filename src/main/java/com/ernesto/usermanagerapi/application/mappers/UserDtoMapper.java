package com.ernesto.usermanagerapi.application.mappers;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ernesto.usermanagerapi.application.dto.RoleResponse;
import com.ernesto.usermanagerapi.application.dto.UserResponse;
import com.ernesto.usermanagerapi.domain.entities.User;

@Component
@Scope("singleton")
public class UserDtoMapper {

    public UserResponse toDto(User user) {
        RoleResponse roleResponse = null;

        if (user.getRole() != null) {
            roleResponse = new RoleResponse(
                    user.getRole().getRoleId(),
                    user.getRole().getName(),
                    user.getRole().getPermissions(),
                    user.getRole().getCreatedAt(),
                    user.getRole().getUpdatedAt());
        }

        return new UserResponse(
                user.getUserId(),
                user.getName(),
                user.getLastName(),
                user.getEmail().getValue(),
                user.getTelephone().getFullNumber(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                roleResponse);
    }

}
