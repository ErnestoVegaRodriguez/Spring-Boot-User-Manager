package com.ernesto.usermanagerapi.domain.values;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import com.ernesto.usermanagerapi.domain.constants.Regex;
import com.ernesto.usermanagerapi.domain.enums.ErrorCode;
import com.ernesto.usermanagerapi.domain.exceptions.ValidationException;
import com.ernesto.usermanagerapi.domain.patterns.Result;
import com.ernesto.usermanagerapi.domain.patterns.ValidationItem;

@Getter
public class Email {

    private final String value;

    private Email(String value) {
        this.value = value;
    }

    public static Result<Email, ValidationException> create(String value) {
        List<ValidationItem> errors = new ArrayList<>();

        if (value == null || value.isEmpty()) {
            errors.add(ValidationItem.of(Map.of("email", "The email field cannot be left blank.")));
        } else if (!Regex.EMAIL_ADDRESS_PATTERN.matcher(value).matches()) {
            errors.add(ValidationItem.of(Map.of("email", "The email format is invalid: " + value)));
        }

        if (!errors.isEmpty())
            return Result.failure(new ValidationException(ErrorCode.VALIDATION_ERROR, "Email validation failed.", errors));

        return Result.success(new Email(value));
    }

    public static Email fromValue(String value) {
        return new Email(value);
    }

    public String domain() {
        return value.substring(value.indexOf('@') + 1);
    }

    public boolean isCorporate(String companyDomain) {
        return domain().equalsIgnoreCase(companyDomain);
    }

    public boolean isEqual(Email other) {
        return other != null && this.value.equals(other.value);
    }

}
