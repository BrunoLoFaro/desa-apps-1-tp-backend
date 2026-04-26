package com.example.desabackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
        @NotBlank(message = "El correo electrónico es obligatorio.")
        @Email(message = "El correo electrónico no tiene un formato válido.")
        String email,

        @NotBlank(message = "La contraseña es obligatoria.")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres.")
        String password,

        @NotBlank(message = "El nombre es obligatorio.")
        @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres.")
        String firstName,

        @NotBlank(message = "El apellido es obligatorio.")
        @Size(min = 2, max = 80, message = "El apellido debe tener entre 2 y 80 caracteres.")
        String lastName,

        @Size(max = 30, message = "El teléfono no puede superar los 30 caracteres.")
        String phone
) {
}