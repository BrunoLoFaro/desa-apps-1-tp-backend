package com.example.desabackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OtpVerificationDto(
        @Email(message = "Email debe ser válido")
        @NotBlank(message = "Email es requerido")
        String email,

        @NotBlank(message = "Código OTP es requerido")
        @Pattern(regexp = "^\\d{6}$", message = "Código OTP debe ser de 6 dígitos")
        String code
) {}
