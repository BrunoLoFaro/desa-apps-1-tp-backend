package com.example.desabackend;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility to generate BCrypt hashes for seed data.
 * Run: mvn exec:java -Dexec.mainClass="com.example.desabackend.BcryptHashGenerator"
 */
public class BcryptHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("123456");
        System.out.println("BCrypt hash for '123456': " + hash);
        System.out.println("Matches: " + encoder.matches("123456", hash));
    }
}
