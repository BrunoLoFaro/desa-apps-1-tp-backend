package com.example.desabackend.services.interfaces;

import com.example.desabackend.dto.LoginResponseDto;
import com.example.desabackend.dto.OtpResponseDto;

public interface IOtpService {

    /**
     * Solicita un código OTP para el email proporcionado.
     * Genera un código de 6 dígitos con expiración de 10 minutos.
     *
     * @param email Email del usuario
     * @return OtpResponseDto con mensaje de confirmación
     * @throws IllegalArgumentException si el email está vinculado a una cuenta
     */
    OtpResponseDto requestOtp(String email);

    /**
     * Verifica el código OTP y crea una sesión (JWT) si es correcto.
     *
     * @param email Email del usuario
     * @param code Código OTP de 6 dígitos
     * @return LoginResponseDto con token JWT y datos del usuario
     * @throws IllegalArgumentException si el código es inválido o expirado
     * @throws IllegalStateException si se exceden los intentos fallidos
     */
    LoginResponseDto verifyOtp(String email, String code);

    /**
     * Reenvía el código OTP al email si el anterior expiró o no se recibió.
     *
     * @param email Email del usuario
     * @return OtpResponseDto con mensaje de confirmación
     * @throws IllegalArgumentException si no hay un OTP activo o pendiente para este email
     */
    OtpResponseDto resendOtp(String email);
}
