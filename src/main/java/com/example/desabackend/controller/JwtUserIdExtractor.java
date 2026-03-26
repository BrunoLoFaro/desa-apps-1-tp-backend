package com.example.desabackend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

final class JwtUserIdExtractor {

    /**
     * Extracts a numeric user id from a JWT payload without validating the signature.
     *
     * This is meant as a compatibility bridge for the TP until Spring Security/JWT validation is in place.
     * In production you should rely on the security layer to validate the token and expose the user id.
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private JwtUserIdExtractor() {
    }

    static Long tryExtractUserIdFromAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null) {
            return null;
        }
        String prefix = "Bearer ";
        if (!authorizationHeader.startsWith(prefix)) {
            return null;
        }
        String token = authorizationHeader.substring(prefix.length()).trim();
        return tryExtractUserIdFromJwt(token);
    }

    static Long tryExtractUserIdFromJwt(String jwt) {
        if (jwt == null || jwt.isBlank()) {
            return null;
        }

        // JWT format: header.payload.signature (Base64URL). We only decode payload.
        String[] parts = jwt.split("\\.");
        if (parts.length < 2) {
            return null;
        }

        try {
            byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
            String payloadJson = new String(payloadBytes, StandardCharsets.UTF_8);
            Map<String, Object> payload = OBJECT_MAPPER.readValue(payloadJson, MAP_TYPE);

            Object userId = payload.get("userId");
            Long parsed = parseLong(userId);
            if (parsed != null) {
                return parsed;
            }

            parsed = parseLong(payload.get("id"));
            if (parsed != null) {
                return parsed;
            }

            // Common fallback for many JWT setups: subject contains user identifier.
            return parseLong(payload.get("sub"));
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Long parseLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number n) {
            return n.longValue();
        }
        if (value instanceof String s) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }
}
