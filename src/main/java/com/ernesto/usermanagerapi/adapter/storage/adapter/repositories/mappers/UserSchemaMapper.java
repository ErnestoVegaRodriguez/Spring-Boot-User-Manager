package com.ernesto.usermanagerapi.adapter.storage.adapter.repositories.mappers;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ernesto.usermanagerapi.adapter.storage.core.schemas.ApiKeySchema;
import com.ernesto.usermanagerapi.adapter.storage.core.schemas.TelephoneEmbeddable;
import com.ernesto.usermanagerapi.adapter.storage.core.schemas.UserSchema;
import com.ernesto.usermanagerapi.domain.entities.ApiKey;
import com.ernesto.usermanagerapi.domain.entities.Role;
import com.ernesto.usermanagerapi.domain.entities.User;
import com.ernesto.usermanagerapi.domain.values.Email;
import com.ernesto.usermanagerapi.domain.values.Password;
import com.ernesto.usermanagerapi.domain.values.Telephone;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

@Component
@Scope("singleton")
public class UserSchemaMapper {

    private final RoleSchemaMapper roleSchemaMapper;
    private final ApiKeySchemaMapper apiKeySchemaMapper;

    public UserSchemaMapper(RoleSchemaMapper roleSchemaMapper, ApiKeySchemaMapper apiKeySchemaMapper) {
        this.roleSchemaMapper = roleSchemaMapper;
        this.apiKeySchemaMapper = apiKeySchemaMapper;
    }

    public User toDomain(UserSchema schema) {
        if (schema == null) return null;

        Role role = schema.getRole() != null
            ? roleSchemaMapper.toDomain(schema.getRole())
            : null;

        List<ApiKey> apiKeys = schema.getApiKeys() != null
            ? schema.getApiKeys().stream()
                .map(apiKeySchemaMapper::toDomain)
                .toList()
            : List.of();

        // Build E.164 from stored components: "+" + countryCode + number
        String e164 = "+" + schema.getTelephone().getCountryCode() + schema.getTelephone().getNumber();

        return User.reconstitute(
            schema.getUserId(),
            schema.getName(),
            schema.getLastName(),
            schema.isActive(),
            schema.isDeleted(),
            schema.getCreatedAt(),
            schema.getUpdatedAt(),
            schema.getDeletedAt(),
            Email.fromValue(schema.getEmail()),           
            Password.fromHashed(schema.getPassword()),
            Telephone.reconstitute(e164),
            role,
            apiKeys
        );
    }

    public UserSchema toSchema(User user) {
        if (user == null) return null;

        // Parse E.164 to extract country code and national number
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();
        PhoneNumber phone;

        try {
            phone = util.parse(user.getTelephone().getE164(), null);
        } catch (Exception e) {
            throw new RuntimeException("Invalid E.164 stored in domain: " + user.getTelephone().getE164(), e);
        }

        UserSchema schema = new UserSchema();
        schema.setUserId(user.getUserId());
        schema.setName(user.getName());
        schema.setLastName(user.getLastName());
        schema.setActive(user.isActive());
        schema.setDeleted(user.isDeleted());
        schema.setCreatedAt(user.getCreatedAt());
        schema.setUpdatedAt(user.getUpdatedAt());
        schema.setDeletedAt(user.getDeletedAt());
        schema.setEmail(user.getEmail().getValue());           
        schema.setPassword(user.getPassword().getHashedValue());
        schema.setTelephone(new TelephoneEmbeddable(
            String.valueOf(phone.getCountryCode()),
            String.valueOf(phone.getNationalNumber())
        ));

        if (user.getRole() != null) {
            schema.setRole(roleSchemaMapper.toSchema(user.getRole()));
        }

        if (user.getApiKeys() != null && !user.getApiKeys().isEmpty()) {
            List<ApiKeySchema> apiKeySchemas = user.getApiKeys().stream()
                    .map(apiKeySchemaMapper::toSchema)
                    .toList();
            schema.setApiKeys(apiKeySchemas);
        }

        return schema;
    }

}
