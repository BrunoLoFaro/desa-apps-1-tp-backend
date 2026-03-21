package com.example.desabackend.dto;

public record LoginResponseDto(
        Long userId,
        String email,
        String displayName
) {
}
