package com.ernesto.usermanagerapi.domain.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import com.ernesto.usermanagerapi.domain.enums.ErrorCode;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.exceptions.ValidationException;
import com.ernesto.usermanagerapi.domain.patterns.Result;
import com.ernesto.usermanagerapi.domain.patterns.ValidationItem;
import com.ernesto.usermanagerapi.domain.values.Email;
import com.ernesto.usermanagerapi.domain.values.Password;
import com.ernesto.usermanagerapi.domain.values.Telephone;

@Getter
public class User {

    // properties
    private UUID userId;
    private String name;
    private String lastName;
    private boolean isActive;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private final int maxApiKeyCount = 2;

    // value objects
    private Email email;
    private Password password;
    private Telephone telephone;
    private List<ApiKey> apiKeys;

    // relationships
    private Role role;

    private User() { }

    private User(
        String name, 
        String lastName, 
        Email email, 
        Password password, 
        Telephone telephone) {
        
        this.userId = UUID.randomUUID();
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.telephone = telephone;
        this.isActive = true;
        this.isDeleted = false;
        this.createdAt = LocalDateTime.now();
        this.apiKeys = new ArrayList<>();
    }

    public static Result<User, ValidationException> create(
        String name, 
        String lastName, 
        Email  email, 
        Password password, 
        Telephone telephone) {

        List<ValidationItem> errors = new ArrayList<>();

        if (name == null || name.isEmpty() || name.isBlank())
            errors.add(ValidationItem.of(Map.of("name", "The name field cannot be left blank.")));

        if (lastName == null || lastName.isEmpty() || lastName.isBlank())
            errors.add(ValidationItem.of(Map.of("lastName", "The last name field cannot be left blank.")));

        if (email == null)
            errors.add(ValidationItem.of(Map.of("email", "The email field cannot be left blank.")));

        if (password == null)
            errors.add(ValidationItem.of(Map.of("password", "The password field cannot be left blank.")));

        if (telephone == null)
            errors.add(ValidationItem.of(Map.of("telephone", "The telephone field cannot be left blank.")));

        if (!errors.isEmpty())
            return Result.failure(new ValidationException(ErrorCode.VALIDATION_ERROR, "User validation failed.", errors));

        return Result.success(new User(name, lastName, email, password, telephone));
    }

    public static User reconstitute(
        UUID userId,
        String name,
        String lastName,
        boolean isActive,
        boolean isDeleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt,
        Email email,
        Password password,
        Telephone telephone,
        Role role,
        List<ApiKey> apiKeys) {

        User user = new User();
        user.userId = userId;
        user.name = name;
        user.lastName = lastName;
        user.isActive = isActive;
        user.isDeleted = isDeleted;
        user.createdAt = createdAt;
        user.updatedAt = updatedAt;
        user.deletedAt = deletedAt;
        user.email = email;
        user.password = password;
        user.telephone = telephone;
        user.role = role;
        user.apiKeys = apiKeys != null ? new ArrayList<>(apiKeys) : new ArrayList<>();
        return user;
    }

    public Result<Void, ValidationException> update(String name, String lastName) {
        List<ValidationItem> errors = new ArrayList<>();

        if (name == null || name.isEmpty() || name.isBlank())
            errors.add(ValidationItem.of(Map.of("name", "The name field cannot be left blank.")));

        if (lastName == null || lastName.isEmpty() || lastName.isBlank())
            errors.add(ValidationItem.of(Map.of("lastName", "The last name field cannot be left blank.")));

        if (!errors.isEmpty())
            return Result.failure(new ValidationException(ErrorCode.VALIDATION_ERROR, "User update validation failed.", errors));

        this.name = name;
        this.lastName = lastName;
        this.updatedAt = LocalDateTime.now();

        return Result.success(null);
    }

    public Result<Void, ValidationException> updatePassword(Password password) {
        if (password == null)
            return Result.failure(new ValidationException(ErrorCode.VALIDATION_ERROR, "User update password failed.",
                    List.of(ValidationItem.of(Map.of("password", "The password field cannot be left blank.")))));

        this.password = password;
        this.updatedAt = LocalDateTime.now();

        return Result.success(null);
    }

    public Result<Void, ValidationException> updatePhone(Telephone telephone) {
        if (telephone == null)
            return Result.failure(new ValidationException(ErrorCode.VALIDATION_ERROR, "User update phone failed.",
                    List.of(ValidationItem.of(Map.of("telephone", "The telephone field cannot be left blank.")))));

        this.telephone = telephone;
        this.updatedAt = LocalDateTime.now();

        return Result.success(null);
    }

    public Result<Void, DomainException> grantRole(Role role) {
        if (role == null)
            return Result.failure(new DomainException(ErrorCode.BAD_REQUEST, "The role cannot be null."));

        this.role = role;
        this.updatedAt = LocalDateTime.now();

        return Result.success(null);
    }

    public void revokeRole() {
        this.role = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeStatus(boolean isActive) {
        this.isActive = isActive;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.isActive = false;
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public boolean canAddApiKey() {
        return apiKeys.size() < maxApiKeyCount;
    }

    public Result<Void, ValidationException> addApiKey(ApiKey apiKey) {
        if (apiKey == null)
            return Result.failure(new ValidationException(ErrorCode.VALIDATION_ERROR, "Add api key failed.",
                    List.of(ValidationItem.of(Map.of("apiKey", "The api key cannot be null.")))));

        if (!canAddApiKey())
            return Result.failure(new ValidationException(ErrorCode.VALIDATION_ERROR, "Add api key failed.",
                    List.of(ValidationItem.of(Map.of("apiKey", "Maximum number of API keys reached (" + maxApiKeyCount + ").")))));

        this.apiKeys.add(apiKey);
        this.updatedAt = LocalDateTime.now();
        return Result.success(null);
    }

}
