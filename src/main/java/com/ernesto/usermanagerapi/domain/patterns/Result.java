package com.ernesto.usermanagerapi.domain.patterns;

import java.util.Optional;

import lombok.Getter;

@Getter
public class Result<TValue, TError> {

    private final Optional<TValue> value;
    private final Optional<TError> error;
    private final boolean isSuccess;

    private Result(TValue value, TError error, boolean isSuccess) {
        this.value = Optional.ofNullable(value);
        this.error = Optional.ofNullable(error);
        this.isSuccess = isSuccess;
    }

    public static <TValue, TError> Result<TValue, TError> success(TValue value) {
        return new Result<>(value, null, true);
    }

    public static <TValue, TError> Result<TValue, TError> failure(TError error) {
        return new Result<>(null, error, false);
    }

}
