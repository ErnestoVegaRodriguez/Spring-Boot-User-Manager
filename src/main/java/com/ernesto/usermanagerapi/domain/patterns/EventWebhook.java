package com.ernesto.usermanagerapi.domain.patterns;

import java.util.UUID;

public class EventWebhook<TValue> {

    private final UUID id;
    private String action;
    private final TValue value;

    private EventWebhook(String action, TValue value) {
        this.id = UUID.randomUUID();
        this.action = action;
        this.value = value;
    }

    public static <TValue> EventWebhook<TValue> newEvent(String action, TValue value) {
        return new EventWebhook<>(action, value);
    }

}
