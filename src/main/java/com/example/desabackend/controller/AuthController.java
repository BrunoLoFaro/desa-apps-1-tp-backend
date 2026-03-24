package com.example.desabackend.controller;

import com.example.desabackend.dto.LoginRequestDto;
import com.example.desabackend.dto.LoginResponseDto;
import com.example.desabackend.dto.OtpRequestDto;
import com.example.desabackend.dto.OtpResponseDto;
import com.example.desabackend.dto.OtpVerificationDto;
import com.example.desabackend.dto.RegisterRequestDto;
import com.example.desabackend.services.interfaces.IAuthService;
import com.example.desabackend.services.interfaces.IOtpService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final IAuthService authService;
    private final IOtpService otpService;

    public AuthController(IAuthService authService, IOtpService otpService) {
        this.authService = authService;
        this.otpService = otpService;
    }

    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public LoginResponseDto register(@Valid @RequestBody RegisterRequestDto request) {
        return authService.register(request);
    }

    @PostMapping("/otp/request")
    public OtpResponseDto requestOtp(@Valid @RequestBody OtpRequestDto request) {
        return otpService.requestOtp(request.email());
    }

    @PostMapping("/otp/verify")
    public LoginResponseDto verifyOtp(@Valid @RequestBody OtpVerificationDto request) {
        return otpService.verifyOtp(request.email(), request.code());
    }

    @PostMapping("/otp/resend")
    public OtpResponseDto resendOtp(@Valid @RequestBody OtpRequestDto request) {
        return otpService.resendOtp(request.email());
    }
}
