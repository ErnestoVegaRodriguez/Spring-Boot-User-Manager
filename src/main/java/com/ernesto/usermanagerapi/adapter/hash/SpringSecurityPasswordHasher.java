package com.ernesto.usermanagerapi.adapter.hash;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.AllArgsConstructor;
import com.ernesto.usermanagerapi.application.ports.drivens.HasherService;

@AllArgsConstructor
public class SpringSecurityPasswordHasher implements HasherService {

    private final BCryptPasswordEncoder encoder;

    @Override
    public String hash(String value) {
        return encoder.encode(value);
    }

    @Override
    public boolean compare(String value, String hash) {
        return encoder.matches(value, hash);
    }

}
