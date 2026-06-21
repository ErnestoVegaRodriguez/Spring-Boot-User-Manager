package com.ernesto.usermanagerapi.adapter.authentication;

import com.ernesto.usermanagerapi.adapter.authentication.options.GeneratorSetting;
import com.ernesto.usermanagerapi.application.ports.drivens.ApiKeyGenerator;

public class SpringSecurityApiKeyGenerator implements ApiKeyGenerator {

    private final GeneratorSetting options;

    public SpringSecurityApiKeyGenerator(GeneratorSetting options) {
        this.options = options != null ? options : new GeneratorSetting();
    }

    @Override
    public String generateKey() {
        byte[] randomBytes = new byte[options.getKeyLengthBytes()];
        options.getSecureRandom().nextBytes(randomBytes);
        return options.getEncoder().encodeToString(randomBytes);
    }

    @Override
    public String generateSecureKey(int keyLengthBytes) {
        byte[] randomBytes = new byte[keyLengthBytes];
        options.getSecureRandom().nextBytes(randomBytes);
        return options.getEncoder().encodeToString(randomBytes);
    }

    @Override
    public String generatePrefixedApiKey(String prefix) {
        String generatedKey = this.generateKey();
        String rawKey = String.join("_", prefix, generatedKey);
        return rawKey;
    }

}
