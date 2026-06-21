package com.ernesto.usermanagerapi.application.dto;

import com.ernesto.usermanagerapi.domain.enums.Scope;

public record GenerateApiKeyRequest(Scope scope) {

}
