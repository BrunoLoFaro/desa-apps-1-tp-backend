package com.example.desabackend.services.interfaces;

import com.example.desabackend.dto.LoginResponseDto;
import com.example.desabackend.dto.OtpResponseDto;
import com.example.desabackend.dto.PasswordResetConfirmDto;

public interface IOtpService {

    OtpResponseDto requestSignupOtp(String email);

    OtpResponseDto resendSignupOtp(String email);

    OtpResponseDto resendLoginOtp(String email);

    LoginResponseDto verifySignupOtpCode(String email, String code);

    OtpResponseDto sendLoginOtp(String email);

    /** Verifica el OTP para un usuario ya activo (login OTP). No activa cuentas pendientes. */
    LoginResponseDto verifyLoginOtp(String email, String code);

    OtpResponseDto requestPasswordReset(String email);

    OtpResponseDto resendPasswordReset(String email);

    OtpResponseDto verifyPasswordResetCode(String email, String code);

    LoginResponseDto confirmPasswordReset(PasswordResetConfirmDto request);
}