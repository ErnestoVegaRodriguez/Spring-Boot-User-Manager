package com.ernesto.usermanagerapi.adapter.storage.core.schemas;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ernesto.usermanagerapi.domain.enums.Scope;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "identity", name = "api_keys")
@Getter
@Setter
public class ApiKeySchema {

    @Id
    @Column(name = "key_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int apiKeyId;

    @Column(name = "user_id", nullable = true)
    private UUID userId;

    @Column(name = "key_prefix")
    private String keyPrefix;

    @Column(name = "key_hint")
    private String keyHint;

    @Column(name = "key_hash")
    private String keyHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope")
    private Scope scope;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

}
