package com.ernesto.usermanagerapi.adapter.auth;

import java.security.SecureRandom;
import java.util.Base64;

import lombok.AllArgsConstructor;
import com.ernesto.usermanagerapi.application.ports.drivens.ApiKeyGenerator;

@AllArgsConstructor
public class SpringSecurityApiKeyGenerator implements ApiKeyGenerator {

    private final SecureRandom secureRandom;
    private final Base64.Encoder encoder;
    private final int keyLengthBytes;

    @Override
    public String generateKey() {
        byte[] randomBytes = new byte[keyLengthBytes];
        secureRandom.nextBytes(randomBytes);
        return encoder.encodeToString(randomBytes);
    }

    @Override
    public String generateSecureKey(int keyLengthBytes) {
        byte[] randomBytes = new byte[keyLengthBytes];
        secureRandom.nextBytes(randomBytes);
        return encoder.encodeToString(randomBytes);
    }

    @Override
    public String generatePrefixedApiKey(String prefix) {
        String generatedKey = this.generateKey();
        String rawKey = String.join("_", prefix, generatedKey);
        return rawKey;
    }

}
