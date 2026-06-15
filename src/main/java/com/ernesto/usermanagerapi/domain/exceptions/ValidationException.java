package com.ernesto.usermanagerapi.domain.exceptions;

import java.util.List;
import java.util.stream.Collectors;

import com.ernesto.usermanagerapi.domain.enums.ErrorCode;
import com.ernesto.usermanagerapi.domain.patterns.ValidationItem;

public class ValidationException extends DomainException {

    private final List<ValidationItem> errorItems;

    public ValidationException(ErrorCode errorCode, String message, List<ValidationItem> errorItems) {
        super(errorCode, formatMessage(message, errorItems));
        this.errorItems = errorItems;
    }

    public ValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
        this.errorItems = List.of();
    }

    public List<ValidationItem> getErrorItems() {
        return errorItems;
    }

    private static String formatMessage(String baseMessage, List<ValidationItem> items) {
        if (items == null || items.isEmpty()) return baseMessage;

        String details = items.stream()
                .map(item -> item.getDetails().entrySet().stream()
                        .map(e -> e.getKey() + "=" + e.getValue())
                        .collect(Collectors.joining(", ")))
                .collect(Collectors.joining("; "));

        return baseMessage + " [" + details + "]";
    }

}
