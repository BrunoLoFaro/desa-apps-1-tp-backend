package com.example.desabackend.security;

/**
 * Authenticated user extracted from a valid JWT token.
 */
public record UserPrincipal(Long userId, String email) {
}
