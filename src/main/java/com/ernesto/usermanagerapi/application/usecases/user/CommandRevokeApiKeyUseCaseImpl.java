package com.ernesto.usermanagerapi.application.usecases.user;

import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import com.ernesto.usermanagerapi.application.ports.drivens.ApiKeyRepository;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandRevokeApiKeyUseCase;
import com.ernesto.usermanagerapi.domain.entities.ApiKey;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

@Service
@Scope("singleton")
@AllArgsConstructor
public class CommandRevokeApiKeyUseCaseImpl implements CommandRevokeApiKeyUseCase {

    private final ApiKeyRepository apiKeyRepository;

    @Override
    public Result<Void, DomainException> execute(UUID userId, int apiKeyId) {

        // 1. Find the API key
        var findResult = apiKeyRepository.findById(apiKeyId);

        if (!findResult.isSuccess()) {
            DomainException notFoundError = findResult.getError();
            return Result.failure(notFoundError);
        }

        ApiKey apiKey = findResult.getValue();

        // 2. Revoke (domain logic)
        apiKey.revoke();

        // 3. Persist
        apiKeyRepository.update(apiKey);

        return Result.success(null);
    }

}
