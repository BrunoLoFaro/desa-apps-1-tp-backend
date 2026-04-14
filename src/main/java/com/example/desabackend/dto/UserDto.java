package com.example.desabackend.dto;

public record UserDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String dni,
        String role,
        Boolean enabled
) {
}
