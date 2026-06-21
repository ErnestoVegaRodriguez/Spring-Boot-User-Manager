package com.ernesto.usermanagerapi.application.ports.drivens;

import java.util.List;
import java.util.UUID;

import com.ernesto.usermanagerapi.domain.entities.User;
import com.ernesto.usermanagerapi.domain.exceptions.NotFoundException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

public interface UserRepository {

    public List<User> findAll();

    public Result<User, NotFoundException> findById(UUID id);

    public Result<User, NotFoundException> findByEmail(String email);

    public User add(User entity);

    public User update(User entity);

    public void removeRoleFromAllUsers(int roleId);

}
