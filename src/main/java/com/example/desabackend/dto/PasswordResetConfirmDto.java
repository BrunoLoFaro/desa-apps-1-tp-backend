package com.example.desabackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PasswordResetConfirmDto(
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "^\\d{6}$") String code,
        @NotBlank @Size(min = 6, max = 72) String newPassword
) {
}