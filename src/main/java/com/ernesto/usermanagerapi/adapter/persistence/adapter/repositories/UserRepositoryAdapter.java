package com.ernesto.usermanagerapi.adapter.persistence.adapter.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import lombok.AllArgsConstructor;

import com.ernesto.usermanagerapi.domain.entities.User;
import com.ernesto.usermanagerapi.domain.enums.ErrorCode;
import com.ernesto.usermanagerapi.domain.exceptions.NotFoundException;
import com.ernesto.usermanagerapi.domain.patterns.Result;
import com.ernesto.usermanagerapi.adapter.persistence.adapter.repositories.mappers.UserSchemaMapper;
import com.ernesto.usermanagerapi.adapter.persistence.core.ports.drivers.UserJpaRepository;
import com.ernesto.usermanagerapi.adapter.persistence.core.schemas.UserSchema;
import com.ernesto.usermanagerapi.application.ports.drivens.UserRepository;

@Repository
@Scope("singleton")
@AllArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserSchemaMapper userSchemaMapper;

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(x -> userSchemaMapper.toDomain(x))
                .toList();
    }

    @Override
    public Result<User, NotFoundException> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(x -> Result.<User, NotFoundException>success(userSchemaMapper.toDomain(x)))
                .orElse(Result.<User, NotFoundException>failure(
                        new NotFoundException(ErrorCode.NOT_FOUND, "User not found for id: " + id)));
    }

    @Override
    public Result<User, NotFoundException> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(x -> Result.<User, NotFoundException>success(userSchemaMapper.toDomain(x)))
                .orElse(Result.<User, NotFoundException>failure(
                        new NotFoundException(ErrorCode.NOT_FOUND, "User not found for email: " + email)));
    }

    @Override
    public User add(User entity) {
        UserSchema schema = userSchemaMapper.toSchema(entity);
        UserSchema saved = jpaRepository.save(schema);
        return userSchemaMapper.toDomain(saved);
    }

    @Override
    public User update(User entity) {
        UserSchema schema = userSchemaMapper.toSchema(entity);
        UserSchema updated = jpaRepository.save(schema);
        return userSchemaMapper.toDomain(updated);
    }

    @Override
    public void removeRoleFromAllUsers(int roleId) {
        jpaRepository.removeRoleFromAllUsers(roleId);
    }

}
