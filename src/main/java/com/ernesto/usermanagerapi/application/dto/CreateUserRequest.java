package com.ernesto.usermanagerapi.application.dto;

public record CreateUserRequest(
        String name,
        String lastName,
        String email,
        String password,
        String phoneNumber) {

}
