package com.example.desabackend.dto;

public record RefreshResponseDto(
        String token,
        String refreshToken
) {
}
