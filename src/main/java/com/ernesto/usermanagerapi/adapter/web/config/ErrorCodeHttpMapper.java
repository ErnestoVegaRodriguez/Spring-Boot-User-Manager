package com.ernesto.usermanagerapi.adapter.web.config;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.ernesto.usermanagerapi.domain.enums.ErrorCode;

@Component
public class ErrorCodeHttpMapper {
    public HttpStatus toHttpStatus(ErrorCode code) {
        return switch (code) {
            case NOT_FOUND      -> HttpStatus.NOT_FOUND;
            case VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
            case CONFLICT       -> HttpStatus.CONFLICT;
            case BAD_REQUEST    -> HttpStatus.BAD_REQUEST;
            case UNAUTHORIZED   -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN      -> HttpStatus.FORBIDDEN;
            case INVALID_STATE  -> HttpStatus.CONFLICT;
            case INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
