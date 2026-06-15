package com.ernesto.usermanagerapi.adapter.storage.core.ports.drivers;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ernesto.usermanagerapi.adapter.storage.core.schemas.RoleSchema;

public interface RoleJpaRepository extends JpaRepository<RoleSchema, Integer> {

    public Optional<RoleSchema> findByName(String name);

}
