package com.ernesto.usermanagerapi.domain.patterns;

import java.util.Map;

import lombok.Getter;

@Getter
public class ValidationItem {

    private final Map<String, Object> details;

    private ValidationItem(Map<String, Object> details) {
        this.details = details;
    }

    public static ValidationItem of(Map<String, Object> details) {
        return new ValidationItem(details);
    }

}
