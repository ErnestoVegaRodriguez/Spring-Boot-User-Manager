package com.ernesto.usermanagerapi.domain.exceptions;

import com.ernesto.usermanagerapi.domain.enums.ErrorCode;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {

    private final ErrorCode errorCode;

    public DomainException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
