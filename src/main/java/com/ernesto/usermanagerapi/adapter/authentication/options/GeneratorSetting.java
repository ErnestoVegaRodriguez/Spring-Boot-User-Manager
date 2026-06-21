package com.ernesto.usermanagerapi.adapter.authentication.options;

import java.security.SecureRandom;
import java.util.Base64;

import lombok.Getter;

@Getter
public class GeneratorSetting {

    private SecureRandom secureRandom = new SecureRandom();
    private Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    private Integer keyLengthBytes = 32;

    public GeneratorSetting secureRandom(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
        return this;
    }

    public GeneratorSetting encoder(Base64.Encoder encoder) {
        this.encoder = encoder;
        return this;
    }

    public GeneratorSetting keyLengthBytes(Integer keyLengthBytes) {
        this.keyLengthBytes = keyLengthBytes;
        return this;
    }

}
