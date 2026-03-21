package com.example.desabackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OtpRequestDto(
        @Email(message = "Email debe ser válido")
        @NotBlank(message = "Email es requerido")
        String email
) {}
