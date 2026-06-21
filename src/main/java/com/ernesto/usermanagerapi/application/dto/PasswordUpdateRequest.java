package com.ernesto.usermanagerapi.application.dto;

public record PasswordUpdateRequest(
        String currentPassword,
        String newPassword) {

}
