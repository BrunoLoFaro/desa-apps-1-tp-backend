package com.example.desabackend.services.interfaces;

import com.example.desabackend.dto.LoginResponseDto;
import com.example.desabackend.dto.OtpRegistrationCompleteDto;
import com.example.desabackend.dto.OtpResponseDto;
import com.example.desabackend.dto.PasswordResetConfirmDto;

public interface IOtpService {

    OtpResponseDto requestSignupOtp(String email);

    OtpResponseDto resendSignupOtp(String email);

    LoginResponseDto completeSignupWithOtp(OtpRegistrationCompleteDto request);

    OtpResponseDto requestPasswordReset(String email);

    OtpResponseDto resendPasswordReset(String email);

    LoginResponseDto confirmPasswordReset(PasswordResetConfirmDto request);
}
