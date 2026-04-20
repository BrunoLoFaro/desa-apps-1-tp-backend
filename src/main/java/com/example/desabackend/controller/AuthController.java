package com.example.desabackend.controller;

import com.example.desabackend.dto.LoginRequestDto;
import com.example.desabackend.dto.LoginResponseDto;
import com.example.desabackend.dto.OtpCodeVerificationDto;
import com.example.desabackend.dto.OtpRequestDto;
import com.example.desabackend.dto.OtpRegistrationCompleteDto;
import com.example.desabackend.dto.OtpResponseDto;
import com.example.desabackend.dto.PasswordResetConfirmDto;
import com.example.desabackend.dto.RefreshRequestDto;
import com.example.desabackend.dto.RefreshResponseDto;
import com.example.desabackend.dto.RegisterRequestDto;
import com.example.desabackend.services.interfaces.IAuthService;
import com.example.desabackend.services.interfaces.IOtpService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final IAuthService authService;
    private final IOtpService otpService;

    public AuthController(IAuthService authService, IOtpService otpService) {
        this.authService = authService;
        this.otpService = otpService;
    }

    @PostMapping("/refresh")
    public RefreshResponseDto refresh(@Valid @RequestBody RefreshRequestDto request) {
        return authService.refresh(request);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public LoginResponseDto register(@Valid @RequestBody RegisterRequestDto request) {
        return authService.register(request);
    }

    @PostMapping("/signup/otp/request")
    public OtpResponseDto requestSignupOtp(@Valid @RequestBody OtpRequestDto request) {
        logger.info("Received signup OTP request for email={}", request.email());
        try {
            OtpResponseDto response = otpService.requestSignupOtp(request.email());
            logger.info("Signup OTP request processed for email={}, message={}", request.email(), response.message());
            return response;
        } catch (Exception e) {
            logger.error("Error processing signup OTP request for email={}: {}", request.email(), e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/signup/otp/resend")
    public OtpResponseDto resendSignupOtp(@Valid @RequestBody OtpRequestDto request) {
        return otpService.resendSignupOtp(request.email());
    }

    @PostMapping("/signup/otp/verify")
    public OtpResponseDto verifySignupOtpCode(@Valid @RequestBody OtpCodeVerificationDto request) {
        return otpService.verifySignupOtpCode(request.email(), request.code());
    }

    @PostMapping("/signup/otp/complete")
    public LoginResponseDto completeSignupWithOtp(@Valid @RequestBody OtpRegistrationCompleteDto request) {
        return otpService.completeSignupWithOtp(request);
    }

    @PostMapping("/password-reset/request")
    public OtpResponseDto requestPasswordReset(@Valid @RequestBody OtpRequestDto request) {
        return otpService.requestPasswordReset(request.email());
    }

    @PostMapping("/password-reset/resend")
    public OtpResponseDto resendPasswordReset(@Valid @RequestBody OtpRequestDto request) {
        return otpService.resendPasswordReset(request.email());
    }

    @PostMapping("/password-reset/verify")
    public OtpResponseDto verifyPasswordResetCode(@Valid @RequestBody OtpCodeVerificationDto request) {
        return otpService.verifyPasswordResetCode(request.email(), request.code());
    }

    @PostMapping("/password-reset/confirm")
    public LoginResponseDto confirmPasswordReset(@Valid @RequestBody PasswordResetConfirmDto request) {
        return otpService.confirmPasswordReset(request);
    }
}
