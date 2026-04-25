package com.example.desabackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OtpRegistrationCompleteDto(
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "^\\d{6}$") String code,
        @NotBlank @Size(min = 6, max = 72) String password,
        @NotBlank @Size(min = 2, max = 80) String firstName,
        @NotBlank @Size(min = 2, max = 80) String lastName
) {
}