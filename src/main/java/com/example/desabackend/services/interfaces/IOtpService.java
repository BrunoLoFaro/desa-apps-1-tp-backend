package com.example.desabackend.services.interfaces;

import com.example.desabackend.dto.LoginResponseDto;
import com.example.desabackend.dto.OtpResponseDto;
import com.example.desabackend.dto.PasswordResetConfirmDto;

public interface IOtpService {

    OtpResponseDto requestSignupOtp(String email);

    OtpResponseDto resendSignupOtp(String email);

    LoginResponseDto verifySignupOtpCode(String email, String code);

    OtpResponseDto sendLoginOtp(String email);

    OtpResponseDto requestPasswordReset(String email);

    OtpResponseDto resendPasswordReset(String email);

    OtpResponseDto verifyPasswordResetCode(String email, String code);

    LoginResponseDto confirmPasswordReset(PasswordResetConfirmDto request);
}