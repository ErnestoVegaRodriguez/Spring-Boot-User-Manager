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
public class Password {

    private final String hashedValue;

    private Password(String hashedValue) {
        this.hashedValue = hashedValue;
    }

    public static Result<Void, ValidationException> validateRaw(String rawPassword) {
        List<ValidationItem> errors = new ArrayList<>();

        if (rawPassword == null || rawPassword.isEmpty()) {
            errors.add(ValidationItem.of(Map.of("password", "The password field cannot be left blank.")));
            return Result.failure(new ValidationException(ErrorCode.VALIDATION_ERROR, "Password validation failed.", errors));
        }

        boolean matches = Regex.PASSWORD_PATTERN.matcher(rawPassword).matches();

        if (!matches)
            errors.add(ValidationItem.of(Map.of("password", "The password format is invalid.")));

        if (!errors.isEmpty())
            return Result.failure(new ValidationException(ErrorCode.VALIDATION_ERROR, "Password validation failed.", errors));

        return Result.success(null);
    }

    public static Password fromHashed(String hashed) {
        return new Password(hashed);
    }

    public boolean isEqual(Password other) {
        return other != null && this.hashedValue.equals(other.hashedValue);
    }

}
