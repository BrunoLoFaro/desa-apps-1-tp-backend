package com.example.desabackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OtpCodeVerificationDto(
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "^\\d{6}$") String code
) {
}
