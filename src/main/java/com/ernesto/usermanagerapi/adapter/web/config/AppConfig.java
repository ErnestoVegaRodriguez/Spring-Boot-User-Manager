package com.ernesto.usermanagerapi.adapter.web.config;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.ernesto.usermanagerapi.adapter.auth.SpringSecurityApiKeyGenerator;
import com.ernesto.usermanagerapi.adapter.hash.SpringSecurityApiKeyHasher;
import com.ernesto.usermanagerapi.adapter.hash.SpringSecurityPasswordHasher;
import com.ernesto.usermanagerapi.application.ports.drivens.ApiKeyGenerator;
import com.ernesto.usermanagerapi.application.ports.drivens.HasherService;

@Configuration
public class AppConfig {

    @Bean
    @Primary
    @Scope("singleton")
    public HasherService passwordHasher() {
        return new SpringSecurityPasswordHasher(new BCryptPasswordEncoder());
    }

    @Bean
    @Scope("singleton")
    public HasherService apiKeyHasher() {
        return new SpringSecurityApiKeyHasher(new BCryptPasswordEncoder());
    }

    @Bean
    @Scope("singleton")
    public ApiKeyGenerator apiKeyGenerator() {
        return new SpringSecurityApiKeyGenerator(
            new SecureRandom(), 
            Base64.getUrlEncoder().withoutPadding(), 
            32);
    }

}
