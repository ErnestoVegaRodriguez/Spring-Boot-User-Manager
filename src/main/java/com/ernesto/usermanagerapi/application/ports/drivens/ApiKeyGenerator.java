package com.ernesto.usermanagerapi.application.ports.drivens;

public interface ApiKeyGenerator {

    public String generateKey();

    public String generateSecureKey(int keyLengthBytes);

    public String generatePrefixedApiKey(String prefix);
    
}
