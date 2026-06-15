package com.ernesto.usermanagerapi.domain.exceptions;

import com.ernesto.usermanagerapi.domain.enums.ErrorCode;

public class NotFoundException extends DomainException {

    public NotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}
