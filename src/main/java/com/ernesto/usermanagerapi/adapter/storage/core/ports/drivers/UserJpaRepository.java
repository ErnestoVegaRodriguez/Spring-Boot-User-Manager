package com.ernesto.usermanagerapi.adapter.storage.core.ports.drivers;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ernesto.usermanagerapi.adapter.storage.core.schemas.UserSchema;

public interface UserJpaRepository extends JpaRepository<UserSchema, UUID> {

    // Spring read the method name and generate this SQL query:
    // SELECT * FROM users WHERE email = ?
    public Optional<UserSchema> findByEmail(String email);

    @Modifying
    @Query(value = "UPDATE UserSchema u SET u.role = null WHERE u.role.roleId = :roleId")
    public void removeRoleFromAllUsers(@Param("roleId") int roleId);

}
