package com.example.desabackend.dto;

public record LoginResponseDto(
        Long userId,
        String email,
        String firstName,
        String lastName,
        String dni,
        String token,
        String refreshToken
) {
}
