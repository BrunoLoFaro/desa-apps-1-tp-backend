package com.example.desabackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DesaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesaBackendApplication.class, args);
    }

}
