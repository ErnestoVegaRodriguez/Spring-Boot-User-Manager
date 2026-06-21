package com.ernesto.usermanagerapi.adapter.persistence.adapter.repositories;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import lombok.AllArgsConstructor;

import com.ernesto.usermanagerapi.domain.entities.Role;
import com.ernesto.usermanagerapi.domain.enums.ErrorCode;
import com.ernesto.usermanagerapi.domain.exceptions.NotFoundException;
import com.ernesto.usermanagerapi.domain.patterns.Result;
import com.ernesto.usermanagerapi.adapter.persistence.adapter.repositories.mappers.RoleSchemaMapper;
import com.ernesto.usermanagerapi.adapter.persistence.core.ports.drivers.RoleJpaRepository;
import com.ernesto.usermanagerapi.adapter.persistence.core.schemas.RoleSchema;
import com.ernesto.usermanagerapi.application.ports.drivens.RoleRepository;

@Repository
@Scope("singleton")
@AllArgsConstructor
public class RoleRepositoryAdapter implements RoleRepository {

    private final RoleJpaRepository jpaRepository;
    private final RoleSchemaMapper roleSchemaMapper;

    @Override
    public Result<Role, NotFoundException> findById(int id) {
        return jpaRepository.findById(id)
                .map(x -> Result.<Role, NotFoundException>success(roleSchemaMapper.toDomain(x)))
                .orElse(Result.<Role, NotFoundException>failure(
                        new NotFoundException(ErrorCode.NOT_FOUND, "Role not found for id: " + id)));
    }

    @Override
    public Role add(Role entity) {
        RoleSchema schema = roleSchemaMapper.toSchema(entity);
        RoleSchema result = jpaRepository.save(schema);
        return roleSchemaMapper.toDomain(result);
    }

    @Override
    public Role update(Role entity) {
        RoleSchema schema = roleSchemaMapper.toSchema(entity);
        RoleSchema updated = jpaRepository.save(schema);
        return roleSchemaMapper.toDomain(updated);
    }

    @Override
    public void delete(int id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Result<Role, NotFoundException> findByName(String name) {
        return jpaRepository.findByName(name)
                .map(x -> Result.<Role, NotFoundException>success(roleSchemaMapper.toDomain(x)))
                .orElse(Result.<Role, NotFoundException>failure(
                        new NotFoundException(ErrorCode.NOT_FOUND, "Role not found: " + name)));
    }

    @Override
    public List<Role> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(x -> roleSchemaMapper.toDomain(x))
                .toList();
    }

}
