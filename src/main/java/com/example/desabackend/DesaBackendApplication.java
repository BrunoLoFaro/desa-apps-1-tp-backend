package com.example.desabackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot entry point for the XploreNow backend (TP).
 */
@SpringBootApplication
@EnableScheduling
@EnableCaching
public class DesaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesaBackendApplication.class, args);
    }

}
