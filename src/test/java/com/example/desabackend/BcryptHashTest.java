package com.example.desabackend;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptHashTest {
    @Test
    void generateHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("123456");
        System.out.println("===HASH_START===" + hash + "===HASH_END===");
        System.out.println("Matches: " + encoder.matches("123456", hash));
    }
}
