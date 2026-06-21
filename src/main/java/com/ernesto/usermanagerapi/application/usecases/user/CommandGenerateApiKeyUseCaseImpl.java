package com.ernesto.usermanagerapi.application.usecases.user;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import com.ernesto.usermanagerapi.application.dto.ApiKeyResponse;
import com.ernesto.usermanagerapi.application.dto.GenerateApiKeyRequest;
import com.ernesto.usermanagerapi.application.dto.GenerateApiKeyResponse;
import com.ernesto.usermanagerapi.application.mappers.ApiKeyDtoMapper;
import com.ernesto.usermanagerapi.application.ports.drivens.ApiKeyGenerator;
import com.ernesto.usermanagerapi.application.ports.drivens.HasherService;
import com.ernesto.usermanagerapi.application.ports.drivens.UserRepository;
import com.ernesto.usermanagerapi.application.ports.drivers.CommandGenerateApiKeyUseCase;
import com.ernesto.usermanagerapi.domain.entities.ApiKey;
import com.ernesto.usermanagerapi.domain.entities.User;
import com.ernesto.usermanagerapi.domain.exceptions.DomainException;
import com.ernesto.usermanagerapi.domain.exceptions.ValidationException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

@Service
@Scope("singleton")
@AllArgsConstructor
public class CommandGenerateApiKeyUseCaseImpl implements CommandGenerateApiKeyUseCase {

    private final UserRepository userRepository;
    private final ApiKeyGenerator apiKeyGenerator;
    private final @Qualifier("apiKeyHasher") HasherService hasherService;
    private final ApiKeyDtoMapper mapper;

    private static final String KEY_PREFIX = "wh_";

    @Override
    public Result<GenerateApiKeyResponse, DomainException> execute(UUID userId, GenerateApiKeyRequest request) {

        // 1. Find user
        var userResult = userRepository.findById(userId);

        if (!userResult.isSuccess()) {
            DomainException notFoundError = userResult.getError();
            return Result.failure(notFoundError);
        }

        User user = userResult.getValue();

        // 2. Check limit via domain
        if (!user.canAddApiKey()) {
            return Result.failure(new DomainException(
                    com.ernesto.usermanagerapi.domain.enums.ErrorCode.BAD_REQUEST,
                    "Maximum number of API keys reached (" + user.getMaxApiKeyCount() + ")."));
        }

        // 3. Generate raw key
        String rawKey = apiKeyGenerator.generatePrefixedApiKey(KEY_PREFIX);

        // 4. Create hint (first 12 chars + "...")
        String hint = rawKey.substring(0, Math.min(12, rawKey.length())) + "...";

        // 5. Hash the raw key
        String hashed = hasherService.hash(rawKey);

        // 6. Create domain entity
        var apiKeyResult = ApiKey.create(hashed, KEY_PREFIX, hint, request.scope());

        if (!apiKeyResult.isSuccess()) {
            ValidationException error = apiKeyResult.getError();
            return Result.failure(error);
        }

        ApiKey apiKey = apiKeyResult.getValue();

        // 7. Add to user (domain validates limit again)
        var addResult = user.addApiKey(apiKey);

        if (!addResult.isSuccess()) {
            ValidationException error = addResult.getError();
            return Result.failure(error);
        }

        // 8. Persist (cascade saves the ApiKey)
        User savedUser = userRepository.update(user);

        // 9. Get the saved ApiKey (last one in the list)
        ApiKey savedKey = savedUser.getApiKeys().stream()
                .filter(k -> k.getKeyHash().equals(hashed))
                .findFirst()
                .orElse(apiKey);

        // 10. Build response
        ApiKeyResponse dto = mapper.toDto(savedKey);
        GenerateApiKeyResponse response = new GenerateApiKeyResponse(rawKey, dto);

        return Result.success(response);
    }

}
