package com.ernesto.usermanagerapi.application.dto;

public record UpdateUserRequest(
        String name,
        String lastName,
        String phoneNumber) {

}
