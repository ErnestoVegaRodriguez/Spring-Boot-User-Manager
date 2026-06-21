package com.ernesto.usermanagerapi.domain.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;

@Getter
public class WebhookDeliveryAttempt {

    private Long id;
    private UUID requestId;
    private String eventType;
    private String payload;
    private LocalDateTime createdAt;
    private String status;

    private WebhookDeliveryAttempt(UUID requestId, String eventType, String payload) {
        this.requestId = requestId;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    private WebhookDeliveryAttempt() { }

    public static WebhookDeliveryAttempt create(UUID requestId, String eventType, String payload) {
        return new WebhookDeliveryAttempt(requestId, eventType, payload);
    }

    public static WebhookDeliveryAttempt reconstitute(
            Long id, UUID requestId, String eventType, String payload,
            LocalDateTime createdAt, String status) {
        WebhookDeliveryAttempt attempt = new WebhookDeliveryAttempt();
        attempt.id = id;
        attempt.requestId = requestId;
        attempt.eventType = eventType;
        attempt.payload = payload;
        attempt.createdAt = createdAt;
        attempt.status = status;
        return attempt;
    }

    public void markDelivered() {
        this.status = "DELIVERED";
    }

    public void markFailed() {
        this.status = "FAILED";
    }

    public boolean isAlreadyDelivered() {
        return "DELIVERED".equals(status);
    }
}
