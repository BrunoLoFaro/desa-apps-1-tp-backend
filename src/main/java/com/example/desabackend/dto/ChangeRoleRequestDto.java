package com.example.desabackend.dto;

import jakarta.validation.constraints.NotNull;

public record ChangeRoleRequestDto(
        @NotNull String role
) {
}
