package com.ernesto.usermanagerapi.adapter.web.config;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.web.client.RestTemplate;

import com.ernesto.usermanagerapi.adapter.authentication.SpringSecurityApiKeyGenerator;
import com.ernesto.usermanagerapi.adapter.authentication.options.GeneratorSetting;
import com.ernesto.usermanagerapi.adapter.encoding.SpringSecurityApiKeyHasher;
import com.ernesto.usermanagerapi.adapter.encoding.SpringSecurityPasswordHasher;
import com.ernesto.usermanagerapi.application.ports.drivens.ApiKeyGenerator;
import com.ernesto.usermanagerapi.application.ports.drivens.HasherService;

@EnableAsync
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
                new GeneratorSetting()
                        .keyLengthBytes(32)
                        .encoder(Base64.getUrlEncoder().withoutPadding())
                        .secureRandom(new SecureRandom()));
    }

    @Bean
    @Scope("prototype")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
