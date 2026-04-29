package com.example.desabackend.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PasswordConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
