package com.ernesto.usermanagerapi.adapter.persistence.core.ports.drivers;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ernesto.usermanagerapi.adapter.persistence.core.schemas.RoleSchema;

public interface RoleJpaRepository extends JpaRepository<RoleSchema, Integer> {

    public Optional<RoleSchema> findByName(String name);

}
